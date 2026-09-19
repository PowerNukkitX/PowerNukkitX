package org.powernukkitx.level.format.leveldb;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockAir;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.BlockUnknown;
import org.powernukkitx.blockentity.BlockEntity;
import org.powernukkitx.level.DimensionData;
import org.powernukkitx.level.biome.BiomeID;
import org.powernukkitx.level.format.BiomeState;
import org.powernukkitx.level.format.Chunk;
import org.powernukkitx.level.format.ChunkSection;
import org.powernukkitx.level.format.ChunkFinalizationState;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.format.IChunkBuilder;
import org.powernukkitx.level.format.UnsafeChunk;
import org.powernukkitx.level.format.bitarray.BitArrayVersion;
import org.powernukkitx.level.format.palette.BlockPalette;
import org.powernukkitx.level.format.palette.Palette;
import org.powernukkitx.level.util.LevelDBKeyUtil;
import org.powernukkitx.migration.leveldb.LevelDBMigrationVersionStore;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.registry.Registries;
import org.powernukkitx.utils.BlockUpdateEntry;
import org.powernukkitx.utils.Utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.ints.Int2ByteOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import org.cloudburstmc.nbt.NBTInputStream;
import org.cloudburstmc.nbt.NBTOutputStream;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtUtils;
import lombok.extern.slf4j.Slf4j;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.WriteBatch;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Allay Project 8/23/2023
 *
 * @author Cool_Loong
 */
@Slf4j
public class LevelDBChunkSerializer {
    public static final LevelDBChunkSerializer INSTANCE = new LevelDBChunkSerializer();

    private LevelDBChunkSerializer() {
    }

    public void serialize(WriteBatch writeBatch, IChunk chunk) {

        //Spawning block entities requires call the getSpawnPacket method,
        //which is easy to call Level#getBlock, which can cause a deadlock,
        //so handle it without locking

        serializeTileAndEntity(writeBatch, chunk);
        chunk.batchProcess(unsafeChunk -> {
            writeBatch.put(LevelDBKeyUtil.VERSION.getKey(unsafeChunk.getX(), unsafeChunk.getZ(), unsafeChunk.getProvider().getDimensionData()), new byte[]{IChunk.VERSION});
            writeBatch.put(
                    LevelDBKeyUtil.CHUNK_FINALIZED_STATE.getKey(unsafeChunk.getX(), unsafeChunk.getZ(), unsafeChunk.getDimensionData()),
                    Utils.intToLittleEndian(unsafeChunk.getFinalizationState().getStorageValue())
            );
            serializeBlock(writeBatch, unsafeChunk);
            serializeHeightAndBiome(writeBatch, unsafeChunk);
            serializeBiomeState(writeBatch, unsafeChunk);
            serializeBlockTicks(writeBatch, unsafeChunk);
            serializeBorderBlocks(writeBatch, unsafeChunk);
            serializeAabbVolumes(writeBatch, unsafeChunk);

            LevelDBMigrationVersionStore.writeRuntimeVersions(unsafeChunk.getExtraData());

            writeBatch.put(
                    LevelDBKeyUtil.PNX_EXTRA_DATA.getKey(unsafeChunk.getX(), unsafeChunk.getZ(), unsafeChunk.getDimensionData()),
                    this.writeBigEndian(unsafeChunk.getExtraData())
            );
        });

    }

    /**
     * Serializes chunk terrain, heightmap and biome data into a LevelDB batch.
     *
     * @param writeBatch target write batch
     * @param chunk chunk to serialize
     */
    public void serializeTerrain(WriteBatch writeBatch, IChunk chunk) {
        chunk.batchProcess(unsafeChunk -> {
            serializeBlock(writeBatch, unsafeChunk);
            serializeHeightAndBiome(writeBatch, unsafeChunk);
        });
    }

    public boolean deserialize(DB db, IChunkBuilder builder) throws IOException {
        byte[] versionValue = db.get(LevelDBKeyUtil.VERSION.getKey(builder.getChunkX(), builder.getChunkZ(), builder.getDimensionData()));

        if (versionValue == null) return false;

        byte[] finalized = db.get(LevelDBKeyUtil.CHUNK_FINALIZED_STATE.getKey(builder.getChunkX(), builder.getChunkZ(), builder.getDimensionData()));
        byte[] extraData = db.get(LevelDBKeyUtil.PNX_EXTRA_DATA.getKey(builder.getChunkX(), builder.getChunkZ(), builder.getDimensionData()));

        CompoundTag pnxExtraData = null;

        if (extraData != null) {
            pnxExtraData = this.readBigEndian(extraData);

            if (!LevelDBMigrationVersionStore.isRuntimeCurrent(pnxExtraData)) {
                throw new IOException("PNX chunk [" + builder.getChunkX() + "," + builder.getChunkZ() + "] has pending or unsupported storage migrations");
            }

            builder.extraData(pnxExtraData);
        }

        builder.finalizationState(decodeFinalizationState(finalized));
        deserializeHeightAndBiome(db, builder);

        BiomeState biomeState = deserializeBiomeState(db, builder);
        rebuildBiomeState(builder.getBiomeSections(), biomeState);
        builder.biomeState(biomeState);

        deserializeBlock(db, builder);
        deserializeBorderBlocks(db, builder);
        deserializeTileAndEntity(db, builder, pnxExtraData);
        deserializeBlockTicks(db, pnxExtraData, builder);
        deserializeAabbVolumes(db, builder);

        return true;
    }

    private void serializeBiomeState(WriteBatch writeBatch, UnsafeChunk chunk) {
        BiomeState biomeState = chunk.getBiomeState();
        if (!biomeState.hasStorageChanges()) return;

        byte[] key = LevelDBKeyUtil.BIOME_STATE.getKey(chunk.getX(), chunk.getZ(), chunk.getDimensionData());
        var entries = biomeState.snowAccumulation();

        if (entries.isEmpty()) {
            writeBatch.delete(key);
            return;
        }

        ByteBuf buffer = ByteBufAllocator.DEFAULT.heapBuffer(Short.BYTES + entries.size() * 3);
        try {
            buffer.writeShortLE(entries.size());
            for (var entry : entries.int2ByteEntrySet()) {
                buffer.writeShortLE(entry.getIntKey());
                buffer.writeByte(entry.getByteValue());
            }
            writeBatch.put(key, Utils.convertByteBuf2Array(buffer));
        } finally {
            buffer.release();
        }
    }

    private BiomeState deserializeBiomeState(DB db, IChunkBuilder builder) throws IOException {
        byte[] data = db.get(LevelDBKeyUtil.BIOME_STATE.getKey(builder.getChunkX(), builder.getChunkZ(), builder.getDimensionData()));
        if (data == null) return new BiomeState();

        if (data.length < Short.BYTES) {
            throw new IOException("Invalid BiomeState data: expected at least 2 bytes, got " + data.length);
        }

        ByteBuf buffer = Unpooled.wrappedBuffer(data);
        int count = buffer.readUnsignedShortLE();
        int expectedLength = Short.BYTES + count * 3;

        if (data.length != expectedLength) {
            throw new IOException("Invalid BiomeState length: expected " + expectedLength + ", got " + data.length);
        }

        Int2ByteOpenHashMap entries = new Int2ByteOpenHashMap(count);
        for (int i = 0; i < count; i++) {
            entries.put(buffer.readUnsignedShortLE(), buffer.readByte());
        }

        return new BiomeState(entries);
    }

    private void rebuildBiomeState(Palette<Integer>[] biomeSections, BiomeState biomeState) {
        var biomeIds = new IntOpenHashSet();

        for (Palette<Integer> biomeSection : biomeSections) {
            for (int index = 0; index < ChunkSection.SIZE; index++) {
                biomeIds.add(biomeSection.get(index));
            }
        }

        var iterator = biomeIds.iterator();
        while (iterator.hasNext()) {
            if (!BiomeState.isTrackedBiome(iterator.nextInt())) {
                iterator.remove();
            }
        }

        biomeState.retainTrackedBiomes(biomeIds);
    }

    private void serializeBorderBlocks(WriteBatch writeBatch, UnsafeChunk chunk) {
        byte[] key = LevelDBKeyUtil.BORDER_BLOCKS.getKey(chunk.getX(), chunk.getZ(), chunk.getDimensionData());
        byte[] data = new byte[256];
        int count = 0;

        outer:
        for (int localX = 0; localX < 16; localX++) {
            for (int localZ = 0; localZ < 16; localZ++) {
                if (!chunk.hasBorderBlock(localX, localZ)) continue;
                if (count >= 255) break outer;

                data[++count] = (byte) ((localX << 4) | localZ);
            }
        }

        if (count == 0) {
            writeBatch.delete(key);
            return;
        }

        data[0] = (byte) count;
        writeBatch.put(key, Arrays.copyOf(data, count + 1));
    }

    private void deserializeBorderBlocks(DB db, IChunkBuilder builder) throws IOException {
        byte[] data = db.get(LevelDBKeyUtil.BORDER_BLOCKS.getKey(builder.getChunkX(), builder.getChunkZ(), builder.getDimensionData()));
        if (data == null) return;

        if (data.length == 0) {
            throw new IOException("Invalid BorderBlocks data: empty value");
        }

        int count = Byte.toUnsignedInt(data[0]);
        if (data.length != count + 1) {
            throw new IOException("Invalid BorderBlocks length: expected " + (count + 1) + ", got " + data.length);
        }

        boolean[] borderBlockMap = new boolean[256];
        for (int i = 0; i < count; i++) {
            borderBlockMap[Byte.toUnsignedInt(data[i + 1])] = true;
        }

        builder.borderBlockMap(borderBlockMap);
    }

    private void serializeAabbVolumes(WriteBatch writeBatch, UnsafeChunk chunk) {
        byte[] key = LevelDBKeyUtil.AABB_VOLUMES.getKey(chunk.getX(), chunk.getZ(), chunk.getDimensionData());

        var aabbVolumes = chunk.getChunk().getAabbVolumes();

        if (aabbVolumes.isEmpty()) {
            writeBatch.delete(key);
            return;
        }

        writeBatch.put(key, LevelDBAabbVolumesCodec.encode(aabbVolumes));
    }

    private void deserializeAabbVolumes(DB db, IChunkBuilder builder) throws IOException {
        byte[] bytes = db.get(LevelDBKeyUtil.AABB_VOLUMES.getKey(builder.getChunkX(), builder.getChunkZ(), builder.getDimensionData()));

        if (bytes != null) {
            builder.aabbVolumes(LevelDBAabbVolumesCodec.decode(bytes));
        }
    }

    static ChunkFinalizationState decodeFinalizationState(byte[] finalized) {
        if (finalized == null) return ChunkFinalizationState.DONE;

        if (finalized.length != Integer.BYTES) {
            throw new IllegalStateException("Invalid chunk finalization state length: " + finalized.length);
        }

        ByteBuf byteBuf = Unpooled.wrappedBuffer(finalized);

        return ChunkFinalizationState.fromStorageValue(byteBuf.readIntLE());
    }

    //serialize chunk section
    private void serializeBlock(WriteBatch writeBatch, UnsafeChunk chunk) {
        final int chunkX = chunk.getX();
        final int chunkZ = chunk.getZ();
        final var dimensionData = chunk.getProvider().getDimensionData();
        ChunkSection[] sections = chunk.getSections();

        for (int ySection = dimensionData.getMinSectionY(); ySection <= dimensionData.getMaxSectionY(); ySection++) {
            final byte sectionY = (byte) ySection;
            final byte[] key = LevelDBKeyUtil.CHUNK_SECTION_PREFIX.getKey(chunkX, chunkZ, sectionY, dimensionData);
            final ChunkSection section = sections[ySection - dimensionData.getMinSectionY()];

            if (section == null || section.isEmpty()) {
                writeBatch.delete(key);
                continue;
            }

            final var blockLayers = section.blockLayer();
            final int layerCount = blockLayers[1].isEmpty() ? 1 : 2;

            ByteBuf buffer = ByteBufAllocator.DEFAULT.heapBuffer();
            try {
                buffer.writeByte(ChunkSection.VERSION);
                buffer.writeByte(layerCount);
                buffer.writeByte(sectionY);

                for (int i = 0; i < layerCount; i++) {
                    blockLayers[i].writeToStoragePersistent(buffer, BlockState::getBlockStateTag);
                }

                writeBatch.put(key, Utils.convertByteBuf2Array(buffer));
            } finally {
                buffer.release();
            }
        }
    }

    /**
     * Deserializes chunk subchunk block and biome storage.
     *
     * @param db source LevelDB
     * @param builder target chunk builder
     */
    public void deserializeBlock(DB db, IChunkBuilder builder) {
        DimensionData dimensionInfo = builder.getDimensionData();
        ChunkSection[] sections = new ChunkSection[dimensionInfo.getChunkSectionCount()];
        var minSectionY = dimensionInfo.getMinSectionY();
        for (int ySection = minSectionY; ySection <= dimensionInfo.getMaxSectionY(); ySection++) {
            byte[] bytes = db.get(LevelDBKeyUtil.CHUNK_SECTION_PREFIX.getKey(builder.getChunkX(), builder.getChunkZ(), ySection, dimensionInfo));
            if (bytes != null) {
                ByteBuf byteBuf = ByteBufAllocator.DEFAULT.ioBuffer();
                try {
                    byteBuf.writeBytes(bytes);
                    byte subChunkVersion = byteBuf.readByte();
                    int layers = 2;
                    switch (subChunkVersion) {
                        case 8, 9:
                            layers = byteBuf.readByte();//layers
                            if (subChunkVersion == 9) {
                                byteBuf.readByte();//sectionY not use
                            }
                        case 1:
                            final Palette<Integer> biomePalette =
                                    builder.getBiomeSections()[ySection - minSectionY];

                            ChunkSection section;
                            if (layers <= 2) {
                                section = new ChunkSection((byte) ySection, biomePalette);
                            } else {
                                BlockPalette[] palettes = new BlockPalette[layers];
                                for (int i = 0; i < layers; i++) {
                                    palettes[i] = new BlockPalette(BlockAir.STATE, new ReferenceArrayList<>(16), BitArrayVersion.V2);
                                }
                                section = new ChunkSection((byte) ySection, palettes, biomePalette);
                            }
                            final int sectionY = ySection;
                            for (int layer = 0; layer < layers; layer++) {
                                final int currentLayer = layer;
                                final Supplier<String> locationHint = log.isDebugEnabled()
                                        ? () -> "level=" + builder.getLevelProvider().getName()
                                                + " dim=" + dimensionInfo.getDimensionName()
                                                + " chunk=(" + builder.getChunkX() + "," + builder.getChunkZ() + ")"
                                                + " sectionY=" + sectionY + " layer=" + currentLayer
                                        : null;
                                section.blockLayer()[layer].readFromStoragePersistent(byteBuf, hash -> {
                                    BlockState blockState = Registries.BLOCKSTATE.get(hash);
                                    if (blockState == null) {
                                        return BlockUnknown.PROPERTIES.getDefaultState();
                                    }
                                    return blockState;
                                }, locationHint);
                            }
                            sections[ySection - minSectionY] = section;
                    }
                } finally {
                    byteBuf.release();
                }
            }
            builder.sections(sections);
        }
    }

    //write biomeAndHeight
    private void serializeHeightAndBiome(WriteBatch writeBatch, UnsafeChunk chunk) {
        final var dimensionData = chunk.getProvider().getDimensionData();
        final int chunkX = chunk.getX();
        final int chunkZ = chunk.getZ();
        final short[] heightMap = chunk.getHeightMapArray();
        final Palette<Integer>[] biomeSections = chunk.getBiomeSections();

        ByteBuf heightAndBiomesBuffer = ByteBufAllocator.DEFAULT.heapBuffer(heightMap.length * Short.BYTES);
        try {
            for (short height : heightMap) {
                heightAndBiomesBuffer.writeShortLE(height);
            }

            Palette<Integer> lastPalette = null;
            for (Palette<Integer> biomePalette : biomeSections) {
                biomePalette.writeToStorageRuntime(heightAndBiomesBuffer, Integer::intValue, lastPalette);
                lastPalette = biomePalette;
            }

            writeBatch.put(
                    LevelDBKeyUtil.DATA_3D.getKey(chunkX, chunkZ, dimensionData),
                    Utils.convertByteBuf2Array(heightAndBiomesBuffer)
            );
        } finally {
            heightAndBiomesBuffer.release();
        }
    }

    //read biomeAndHeight
    @SuppressWarnings("unchecked")
    private void deserializeHeightAndBiome(DB db, IChunkBuilder builder) {
        ByteBuf heightAndBiomesBuffer = null;
        try {
            DimensionData dimensionInfo = builder.getDimensionData();
            int sectionCount = dimensionInfo.getChunkSectionCount();

            Palette<Integer>[] biomeSections = (Palette<Integer>[]) new Palette<?>[sectionCount];

            for (int i = 0; i < sectionCount; i++) {
                biomeSections[i] = new Palette<>(BiomeID.PLAINS);
            }

            builder.biomeSections(biomeSections);

            byte[] bytes = db.get(LevelDBKeyUtil.DATA_3D.getKey(builder.getChunkX(), builder.getChunkZ(), dimensionInfo));

            if (bytes != null) {
                heightAndBiomesBuffer = Unpooled.wrappedBuffer(bytes);

                short[] heights = new short[256];
                for (int i = 0; i < 256; i++) {
                    heights[i] = heightAndBiomesBuffer.readShortLE();
                }
                builder.heightMap(heights);

                Palette<Integer> lastPalette = null;

                for (Palette<Integer> biomePalette : biomeSections) {
                    biomePalette.readFromStorageRuntime(heightAndBiomesBuffer, Integer::valueOf, lastPalette);
                    lastPalette = biomePalette;
                }

                return;
            }
        } finally {
            if (heightAndBiomesBuffer != null) {
                heightAndBiomesBuffer.release();
            }
        }
    }

    private void deserializeTileAndEntity(DB db, IChunkBuilder builder, CompoundTag pnxExtraData) {
        DimensionData dimensionInfo = builder.getDimensionData();
        byte[] tileBytes = db.get(LevelDBKeyUtil.BLOCK_ENTITIES.getKey(builder.getChunkX(), builder.getChunkZ(), dimensionInfo));

        if (tileBytes != null) {
            List<CompoundTag> blockEntityTags = new ArrayList<>();
            final ByteBuf buffer = Unpooled.wrappedBuffer(tileBytes);

            try {
                while (buffer.isReadable()) {
                    blockEntityTags.add(this.read(buffer));
                }
            } finally {
                buffer.release();
            }

            builder.blockEntities(blockEntityTags);
        }

        byte[] digestBytes = db.get(LevelDBActorStorage.getDigestKey(builder.getChunkX(), builder.getChunkZ(), dimensionInfo));

        if (digestBytes == null) return;

        List<CompoundTag> entityTags = new ArrayList<>();

        for (long actorStorageKey : LevelDBActorStorage.readDigest(digestBytes)) {
            byte[] actorBytes = db.get(LevelDBActorStorage.getActorKey(actorStorageKey));

            if (actorBytes == null) {
                log.warn("Missing actorprefix record for storage key {} in chunk [{},{}]",
                                Long.toUnsignedString(actorStorageKey), builder.getChunkX(), builder.getChunkZ());
                continue;
            }

            final ByteBuf actorBuffer = Unpooled.wrappedBuffer(actorBytes);

            try {
                CompoundTag tag = this.read(actorBuffer);

                if (!tag.contains("UniqueID") || tag.getLong("UniqueID") == 0) {

                    log.warn("Actorprefix record for storage key {} in chunk [{},{}] is missing UniqueID",
                                Long.toUnsignedString(actorStorageKey), builder.getChunkX(), builder.getChunkZ());
                    continue;
                }

                entityTags.add(tag);
            } finally {
                actorBuffer.release();
            }
        }

        builder.entities(entityTags);
    }

    private void serializeTileAndEntity(WriteBatch writeBatch, IChunk chunk) {
        List<BlockEntity> blockEntitySnapshot = new ArrayList<>(chunk.getBlockEntities().values());
        ByteBuf tileBuffer = ByteBufAllocator.DEFAULT.ioBuffer();
        try (final ByteBufOutputStream bufOutputStream = new ByteBufOutputStream(tileBuffer);
             final NBTOutputStream nbtOutputStream = NbtUtils.createWriterLE(bufOutputStream)) {
            byte[] key = LevelDBKeyUtil.BLOCK_ENTITIES.getKey(chunk.getX(), chunk.getZ(), chunk.getProvider().getDimensionData());
            if (blockEntitySnapshot.isEmpty()) writeBatch.delete(key);
            else {
                for (BlockEntity blockEntity : blockEntitySnapshot) {
                    try {
                        CompoundTag snapshot = blockEntity.serializationSnapshot;
                        CompoundTag tag;

                        if (snapshot != null) {
                            blockEntity.serializationSnapshot = null;
                            tag = blockEntity.getStorageNBT(snapshot);
                        } else {
                            tag = blockEntity.getStorageNBT();
                        }

                        nbtOutputStream.writeTag(tag.toNetwork());
                    } catch (Exception e) {
                        log.error("Failed to serialize block entity {} at {},{},{} in chunk [{},{}]",
                                blockEntity.getSaveId(), (int) blockEntity.x, (int) blockEntity.y, (int) blockEntity.z,
                                chunk.getX(), chunk.getZ(), e);
                    }
                }
                writeBatch.put(key, Utils.convertByteBuf2Array(tileBuffer));
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } finally {
            tileBuffer.release();
        }
    }

    private void serializeBlockTicks(WriteBatch writeBatch, UnsafeChunk unsafe) {
        Chunk chunk = unsafe.getChunk();
        Map<BlockUpdateEntry, Long> pending = chunk.getBlockUpdateScheduler().getPendingBlockUpdatesWithTime();
        byte[] pendingTicksKey = LevelDBKeyUtil.PENDING_TICKS.getKey(chunk.getX(), chunk.getZ(), chunk.getProvider().getDimensionData());

        if (pending.isEmpty()) {
            writeBatch.delete(pendingTicksKey);
        } else {
            ListTag<CompoundTag> tickList = new ListTag<>();

            for (Map.Entry<BlockUpdateEntry, Long> pendingEntry : pending.entrySet()) {
                BlockUpdateEntry blockUpdateEntry = pendingEntry.getKey();
                Block block = blockUpdateEntry.block;
                CompoundTag blockState = CompoundTag.fromNetwork(block.getBlockState().getBlockStateTag());

                CompoundTag tick = new CompoundTag()
                        .putCompound("blockState", blockState)
                        .putLong("time", Math.max(pendingEntry.getValue(), chunk.getBlockUpdateScheduler().getLastTick() + 1))
                        .putInt("x", blockUpdateEntry.pos.getFloorX())
                        .putInt("y", blockUpdateEntry.pos.getFloorY())
                        .putInt("z", blockUpdateEntry.pos.getFloorZ());

                tickList.add(tick);
            }

            long currentTick = chunk.getBlockUpdateScheduler().getLastTick();

            CompoundTag pendingTicks = new CompoundTag()
                    .putInt("currentTick", (int) currentTick)
                    .putList("tickList", tickList);

            writeBatch.put(pendingTicksKey, writeLittleEndianCompound(pendingTicks));
        }

        Map<BlockUpdateEntry, Long> randomPending = chunk.getRandomBlockUpdateScheduler().getPendingBlockUpdatesWithTime();
        byte[] randomTicksKey = LevelDBKeyUtil.RANDOM_TICKS.getKey(chunk.getX(), chunk.getZ(), chunk.getProvider().getDimensionData());

        if (randomPending.isEmpty()) {
            writeBatch.delete(randomTicksKey);
        } else {
            ListTag<CompoundTag> tickList = new ListTag<>();

            for (Map.Entry<BlockUpdateEntry, Long> pendingEntry : randomPending.entrySet()) {
                BlockUpdateEntry blockUpdateEntry = pendingEntry.getKey();
                Block block = blockUpdateEntry.block;
                CompoundTag blockState = CompoundTag.fromNetwork(block.getBlockState().getBlockStateTag());

                CompoundTag tick = new CompoundTag()
                        .putCompound("blockState", blockState)
                        .putLong("time", Math.max(pendingEntry.getValue(), chunk.getRandomBlockUpdateScheduler().getLastTick() + 1))
                        .putInt("x", blockUpdateEntry.pos.getFloorX())
                        .putInt("y", blockUpdateEntry.pos.getFloorY())
                        .putInt("z", blockUpdateEntry.pos.getFloorZ());

                tickList.add(tick);
            }

            long currentTick = chunk.getRandomBlockUpdateScheduler().getLastTick();

            CompoundTag randomTicks = new CompoundTag()
                    .putInt("currentTick", (int) currentTick)
                    .putList("tickList", tickList);

            writeBatch.put(randomTicksKey, writeLittleEndianCompound(randomTicks));
        }
    }

    public static class ScheduledTickInfo {
        public int x, y, z;
        public long delay;
        public CompoundTag blockState;
    }

    public static class RandomTickInfo {
        public int x, y, z;
        public long time;
        public CompoundTag blockState;
    }

    public static class RandomTickData {
        public long currentTick;
        public List<RandomTickInfo> ticks = new ArrayList<>();
    }

    public static class NormalTickInfo {
        public int x, y, z, layer, neighbor = -1;
        public String id;
    }

    /**
     * Deserializes persisted scheduled, random and normal block ticks for a chunk.
     *
     * @param db source LevelDB
     * @param extraData PNX chunk extra data
     * @param builder target chunk builder
     */
    public static void deserializeBlockTicks(DB db, CompoundTag extraData, IChunkBuilder builder) {
        long chunkKey = ((long) builder.getChunkX() & 0xffffffffL) << 32 | ((long) builder.getChunkZ() & 0xffffffffL);
        LevelDBProvider provider = null;

        if (builder.getLevelProvider() instanceof LevelDBProvider p) {
            provider = p;
        }

        if (provider == null) return;

        byte[] pendingTicksBytes = db.get(LevelDBKeyUtil.PENDING_TICKS.getKey(builder.getChunkX(), builder.getChunkZ(), builder.getDimensionData()));

        if (pendingTicksBytes != null) {
            CompoundTag pendingTicks = readLittleEndianCompound(pendingTicksBytes);
            int currentTick = pendingTicks.getInt("currentTick");
            ListTag<CompoundTag> tickList = pendingTicks.getList("tickList", CompoundTag.class);
            List<ScheduledTickInfo> scheduledList = new ArrayList<>();

            for (CompoundTag tag : tickList.getAll()) {
                ScheduledTickInfo info = new ScheduledTickInfo();
                info.x = tag.getInt("x");
                info.y = tag.getInt("y");
                info.z = tag.getInt("z");
                info.blockState = tag.getCompound("blockState");
                long scheduledTime = tag.getLong("time");
                info.delay = Math.max(1L, scheduledTime - currentTick);
                scheduledList.add(info);
            }

            if (!scheduledList.isEmpty()) {
                provider.getScheduledTicksMap().put(chunkKey, scheduledList);
            }
        }

        byte[] randomTicksBytes = db.get(LevelDBKeyUtil.RANDOM_TICKS.getKey(builder.getChunkX(), builder.getChunkZ(), builder.getDimensionData()));

        if (randomTicksBytes != null) {
            CompoundTag randomTicks = readLittleEndianCompound(randomTicksBytes);
            RandomTickData randomTickData = new RandomTickData();
            randomTickData.currentTick = randomTicks.getInt("currentTick");
            ListTag<CompoundTag> tickList = randomTicks.getList("tickList", CompoundTag.class);

            for (CompoundTag tag : tickList.getAll()) {
                RandomTickInfo info = new RandomTickInfo();

                info.x = tag.getInt("x");
                info.y = tag.getInt("y");
                info.z = tag.getInt("z");
                info.time = tag.getLong("time");
                info.blockState = tag.getCompound("blockState");

                randomTickData.ticks.add(info);
            }

            provider.getRandomTicksMap().put(chunkKey, randomTickData);
        }

        if (extraData == null) return;

        if (extraData.contains("pendingNormalTickBlocks")) {
            ListTag<CompoundTag> normalTicks = extraData.getList("pendingNormalTickBlocks", CompoundTag.class);
            List<NormalTickInfo> normalList = new ArrayList<>();

            for (CompoundTag tag : normalTicks.getAll()) {
                NormalTickInfo info = new NormalTickInfo();

                info.x = tag.getInt("x");
                info.y = tag.getInt("y");
                info.z = tag.getInt("z");
                info.id = tag.getString("id");
                info.layer = tag.getInt("layer");

                if (tag.contains("neighbor")) {
                    info.neighbor = tag.getInt("neighbor");
                }

                normalList.add(info);
            }

            if (!normalList.isEmpty()) {
                provider.getNormalTicksMap().put(chunkKey, normalList);
            }
        }
    }

    private CompoundTag read(ByteBuf buffer) {
        try (final ByteBufInputStream inputStream = new ByteBufInputStream(buffer);
             final NBTInputStream nbtInputStream = NbtUtils.createReaderLE(inputStream)) {
            return CompoundTag.fromNetwork((NbtMap) nbtInputStream.readTag());
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read nbt", e);
        }
    }

    private CompoundTag readBigEndian(byte[] data) {
        try (final ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
             final NBTInputStream nbtInputStream = NbtUtils.createReader(inputStream)) {
            Object tag = nbtInputStream.readTag();
            if (!(tag instanceof NbtMap map)) {
                return null;
            }
            return CompoundTag.fromNetwork(map);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read big endian nbt", e);
        }
    }

    public byte[] writeBigEndian(CompoundTag nbtMap) {
        try (final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             final NBTOutputStream nbtOutputStream = NbtUtils.createWriter(outputStream)) {
            nbtOutputStream.writeTag(nbtMap.toNetwork());
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to write big endian nbt", e);
        }
    }

    private static CompoundTag readLittleEndianCompound(byte[] data) {
        try (final ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
             final NBTInputStream nbtInputStream = NbtUtils.createReaderLE(inputStream)) {
            return CompoundTag.fromNetwork((NbtMap) nbtInputStream.readTag());
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read little endian NBT", e);
        }
    }

    private static byte[] writeLittleEndianCompound(CompoundTag tag) {
        try (final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             final NBTOutputStream nbtOutputStream = NbtUtils.createWriterLE(outputStream)) {
            nbtOutputStream.writeTag(tag.toNetwork());
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to write little endian NBT", e);
        }
    }

}
