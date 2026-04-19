package cn.nukkit.level.format.leveldb;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockState;
import cn.nukkit.block.BlockUnknown;
import cn.nukkit.block.customblock.CustomBlockDefinition;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.DimensionData;
import cn.nukkit.level.format.Chunk;
import cn.nukkit.level.format.ChunkSection;
import cn.nukkit.level.format.ChunkState;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.format.IChunkBuilder;
import cn.nukkit.level.format.UnsafeChunk;
import cn.nukkit.level.format.bitarray.BitArrayVersion;
import cn.nukkit.level.format.palette.BlockPalette;
import cn.nukkit.level.format.palette.Palette;
import cn.nukkit.level.util.LevelDBKeyUtil;
import cn.nukkit.registry.BlockRegistry;
import cn.nukkit.registry.Registries;
import cn.nukkit.utils.BlockUpdateEntry;
import cn.nukkit.utils.Utils;
import com.google.common.base.Predicates;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import org.cloudburstmc.nbt.NBTInputStream;
import org.cloudburstmc.nbt.NBTOutputStream;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtType;
import org.cloudburstmc.nbt.NbtUtils;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.WriteBatch;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Allay Project 8/23/2023
 *
 * @author Cool_Loong
 */
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
            writeBatch.put(LevelDBKeyUtil.CHUNK_FINALIZED_STATE.getKey(unsafeChunk.getX(), unsafeChunk.getZ(), unsafeChunk.getDimensionData()), Utils.intToLittleEndian(unsafeChunk.getChunkState().ordinal() - 1));
            serializeBlock(writeBatch, unsafeChunk);
            serializeHeightAndBiome(writeBatch, unsafeChunk);
            serializeLight(writeBatch, unsafeChunk);
            serializeBlockTicks(unsafeChunk);
            writeBatch.put(LevelDBKeyUtil.PNX_EXTRA_DATA.getKey(unsafeChunk.getX(), unsafeChunk.getZ(), unsafeChunk.getDimensionData()), this.write(unsafeChunk.getExtraData()));
        });

    }

    public void deserialize(DB db, IChunkBuilder builder) throws IOException {
        byte[] versionValue = db.get(LevelDBKeyUtil.VERSION.getKey(builder.getChunkX(), builder.getChunkZ(), builder.getDimensionData()));
        if (versionValue == null) {
            versionValue = db.get(LevelDBKeyUtil.LEGACY_VERSION.getKey(builder.getChunkX(), builder.getChunkZ(), builder.getDimensionData()));
        }
        if (versionValue == null) {
            return;
        }
        byte[] finalized = db.get(LevelDBKeyUtil.CHUNK_FINALIZED_STATE.getKey(builder.getChunkX(), builder.getChunkZ(), builder.getDimensionData()));
        if (finalized == null) {
            builder.state(ChunkState.FINISHED);
        } else {
            ByteBuf byteBuf = Unpooled.wrappedBuffer(finalized);
            final int i = byteBuf.readableBytes() >= 4 ? byteBuf.readIntLE() : byteBuf.readByte();
            builder.state(ChunkState.values()[i + 1]);
        }
        byte[] extraData = db.get(LevelDBKeyUtil.PNX_EXTRA_DATA.getKey(builder.getChunkX(), builder.getChunkZ(), builder.getDimensionData()));
        NbtMap pnxExtraData = null;
        if (extraData != null) {
            builder.extraData(pnxExtraData = this.read(extraData));
        }
        deserializeBlock(db, builder, pnxExtraData);
        deserializeHeightAndBiome(db, builder, pnxExtraData);
        deserializeTileAndEntity(db, builder, pnxExtraData);
        deserializeLight(db, builder, pnxExtraData);
        deserializeBlockTicks(pnxExtraData, builder);
    }

    //serialize chunk section light
    private void serializeLight(WriteBatch writeBatch, UnsafeChunk chunk) {
        final int chunkX = chunk.getX();
        final int chunkZ = chunk.getZ();
        final var dimensionData = chunk.getProvider().getDimensionData();
        ChunkSection[] sections = chunk.getSections();
        for (var section : sections) {
            if (section == null) {
                continue;
            }
            ByteBuf buffer = ByteBufAllocator.DEFAULT.heapBuffer();
            try {
                byte[] blockLights = section.blockLights().getData();
                buffer.writeInt(blockLights.length);
                buffer.writeBytes(blockLights);
                byte[] skyLights = section.skyLights().getData();
                buffer.writeInt(skyLights.length);
                buffer.writeBytes(skyLights);
                writeBatch.put(LevelDBKeyUtil.PNX_LIGHT.getKey(chunkX, chunkZ, section.y(), dimensionData), Utils.convertByteBuf2Array(buffer));
            } finally {
                buffer.release();
            }
        }
    }

    private void deserializeLight(DB db, IChunkBuilder builder, NbtMap pnxExtraData) {
        DimensionData dimensionInfo = builder.getDimensionData();
        var minSectionY = dimensionInfo.getMinSectionY();
        for (int ySection = minSectionY; ySection <= dimensionInfo.getMaxSectionY(); ySection++) {
            byte[] bytes = db.get(LevelDBKeyUtil.PNX_LIGHT.getKey(builder.getChunkX(), builder.getChunkZ(), ySection, dimensionInfo));
            if (bytes != null) {
                ByteBuf byteBuf = ByteBufAllocator.DEFAULT.ioBuffer();
                try {
                    byteBuf.writeBytes(bytes);
                    int i = byteBuf.readInt();
                    byte[] blockLights = new byte[i];
                    byteBuf.readBytes(blockLights);
                    int i2 = byteBuf.readInt();
                    byte[] skyLights = new byte[i2];
                    byteBuf.readBytes(skyLights);
                    final ChunkSection section = builder.getSections()[ySection - minSectionY];
                    section.blockLights().copyFrom(blockLights);
                    section.skyLights().copyFrom(skyLights);
                } finally {
                    byteBuf.release();
                }
            }
        }
    }

    //serialize chunk section
    private void serializeBlock(WriteBatch writeBatch, UnsafeChunk chunk) {
        final int chunkX = chunk.getX();
        final int chunkZ = chunk.getZ();
        final var dimensionData = chunk.getProvider().getDimensionData();
        ChunkSection[] sections = chunk.getSections();
        for (var section : sections) {
            if (section == null) {
                continue;
            }
            final var blockLayers = section.blockLayer();
            ByteBuf buffer = ByteBufAllocator.DEFAULT.heapBuffer();
            try {
                buffer.writeByte(ChunkSection.VERSION);
                buffer.writeByte(ChunkSection.LAYER_COUNT);
                buffer.writeByte(section.y());
                for (int i = 0; i < ChunkSection.LAYER_COUNT; i++) {
                    blockLayers[i].writeToStoragePersistent(buffer, BlockState::getBlockStateTag);
                }
                writeBatch.put(LevelDBKeyUtil.CHUNK_SECTION_PREFIX.getKey(chunkX, chunkZ, section.y(), dimensionData), Utils.convertByteBuf2Array(buffer));
            } finally {
                buffer.release();
            }
        }
    }

    //serialize chunk section
    private void deserializeBlock(DB db, IChunkBuilder builder, NbtMap pnxExtraData) {
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
                            ChunkSection section;
                            if (layers <= 2) {
                                section = new ChunkSection((byte) ySection);
                            } else {
                                BlockPalette[] palettes = new BlockPalette[layers];
                                Arrays.fill(palettes, new BlockPalette(BlockAir.STATE, new ReferenceArrayList<>(16), BitArrayVersion.V2));
                                section = new ChunkSection((byte) ySection, palettes);
                            }
                            for (int layer = 0; layer < layers; layer++) {
                                section.blockLayer()[layer].readFromStoragePersistent(byteBuf, hash -> {
                                    BlockState blockState = Registries.BLOCKSTATE.get(hash);
                                    if (blockState == null) {
                                        return BlockUnknown.PROPERTIES.getDefaultState();
                                    }
                                    return blockState;
                                });
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
        ByteBuf heightAndBiomesBuffer = ByteBufAllocator.DEFAULT.heapBuffer(heightMap.length * Short.BYTES);
        try {
            for (short height : heightMap) {
                heightAndBiomesBuffer.writeShortLE(height);
            }
            Palette<Integer> biomePalette = null;
            for (int ySection = dimensionData.getMinSectionY(); ySection <= dimensionData.getMaxSectionY(); ySection++) {
                ChunkSection section = chunk.getSection(ySection);
                if (section == null) {
                    continue;
                }
                section.biomes().writeToStorageRuntime(heightAndBiomesBuffer, Integer::intValue, biomePalette);
                biomePalette = section.biomes();
            }
            if (heightAndBiomesBuffer.readableBytes() > 0) {
                writeBatch.put(LevelDBKeyUtil.DATA_3D.getKey(chunkX, chunkZ, dimensionData), Utils.convertByteBuf2Array(heightAndBiomesBuffer));
            }
        } finally {
            heightAndBiomesBuffer.release();
        }
    }

    //read biomeAndHeight
    private void deserializeHeightAndBiome(DB db, IChunkBuilder builder, NbtMap pnxExtraData) {
        ByteBuf heightAndBiomesBuffer = null;
        try {
            DimensionData dimensionInfo = builder.getDimensionData();
            byte[] bytes = db.get(LevelDBKeyUtil.DATA_3D.getKey(builder.getChunkX(), builder.getChunkZ(), dimensionInfo));
            if (bytes != null) {
                heightAndBiomesBuffer = Unpooled.wrappedBuffer(bytes);
                short[] heights = new short[256];
                for (int i = 0; i < 256; i++) {
                    heights[i] = heightAndBiomesBuffer.readShortLE();
                }
                builder.heightMap(heights);
                Palette<Integer> lastPalette = null;
                var minSectionY = builder.getDimensionData().getMinSectionY();
                for (int y = minSectionY; y <= builder.getDimensionData().getMaxSectionY(); y++) {
                    ChunkSection section = builder.getSections()[y - minSectionY];
                    if (section == null) continue;
                    section.biomes().readFromStorageRuntime(heightAndBiomesBuffer, Integer::valueOf, lastPalette);
                    lastPalette = section.biomes();
                }
            } else {
                byte[] bytes2D = db.get(LevelDBKeyUtil.DATA_2D.getKey(builder.getChunkX(), builder.getChunkZ(), dimensionInfo));
                if (bytes2D != null) {
                    heightAndBiomesBuffer = Unpooled.wrappedBuffer(bytes2D);
                    short[] heights = new short[256];
                    for (int i = 0; i < 256; i++) {
                        heights[i] = heightAndBiomesBuffer.readShortLE();
                    }
                    builder.heightMap(heights);
                    byte[] biomes = new byte[256];
                    heightAndBiomesBuffer.readBytes(biomes);

                    var minSectionY = builder.getDimensionData().getMinSectionY();
                    for (int y = minSectionY; y <= builder.getDimensionData().getMaxSectionY(); y++) {
                        ChunkSection section = builder.getSections()[y - minSectionY];
                        if (section == null) continue;
                        final Palette<Integer> biomePalette = section.biomes();
                        for (int x = 0; x < 16; x++) {
                            for (int z = 0; z < 16; z++) {
                                for (int sy = 0; sy < 16; sy++) {
                                    biomePalette.set(IChunk.index(x, sy, z), (int) biomes[x + 16 * z]);
                                }
                            }
                        }
                    }
                }
            }
        } finally {
            if (heightAndBiomesBuffer != null) {
                heightAndBiomesBuffer.release();
            }
        }
    }

    private void deserializeTileAndEntity(DB db, IChunkBuilder builder, NbtMap pnxExtraData) {
        DimensionData dimensionInfo = builder.getDimensionData();
        byte[] tileBytes = db.get(LevelDBKeyUtil.BLOCK_ENTITIES.getKey(builder.getChunkX(), builder.getChunkZ(), dimensionInfo));
        if (tileBytes != null) {
            List<NbtMap> blockEntityTags = new ArrayList<>();
            try (BufferedInputStream stream = new BufferedInputStream(new ByteArrayInputStream(tileBytes))) {
                while (stream.available() > 0) {
                    blockEntityTags.add(this.read(stream.readAllBytes()));
                }
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
            builder.blockEntities(blockEntityTags);
        }

        byte[] key = LevelDBKeyUtil.ENTITIES.getKey(builder.getChunkX(), builder.getChunkZ(), dimensionInfo);
        byte[] entityBytes = db.get(key);
        if (entityBytes == null) return;
        List<NbtMap> entityTags = new ArrayList<>();
        try (BufferedInputStream stream = new BufferedInputStream(new ByteArrayInputStream(entityBytes))) {
            while (stream.available() > 0) {
                entityTags.add(this.read(stream.readAllBytes()));
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        if (pnxExtraData == null) {
            db.delete(key);
            List<NbtMap> list = entityTags.stream().map(BDSEntityTranslator::translate).filter(Predicates.notNull()).toList();
            builder.entities(list);
        } else {
            builder.entities(entityTags);
        }
    }

    private void serializeTileAndEntity(WriteBatch writeBatch, IChunk chunk) {
        //Write blockEntities
        Collection<BlockEntity> blockEntities = chunk.getBlockEntities().values();
        ByteBuf tileBuffer = ByteBufAllocator.DEFAULT.ioBuffer();
        try (final ByteBufOutputStream bufOutputStream = new ByteBufOutputStream(tileBuffer);
             final NBTOutputStream nbtOutputStream = NbtUtils.createWriterLE(bufOutputStream)) {
            byte[] key = LevelDBKeyUtil.BLOCK_ENTITIES.getKey(chunk.getX(), chunk.getZ(), chunk.getProvider().getDimensionData());
            if (blockEntities.isEmpty()) writeBatch.delete(key);
            else {
                for (BlockEntity blockEntity : blockEntities) {
                    blockEntity.saveNBT();
                    nbtOutputStream.writeTag(blockEntity.namedTag);
                }
                writeBatch.put(key, Utils.convertByteBuf2Array(tileBuffer));
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } finally {
            tileBuffer.release();
        }

        Collection<Entity> entities = chunk.getEntities().values();
        ByteBuf entityBuffer = ByteBufAllocator.DEFAULT.ioBuffer();
        try (final ByteBufOutputStream bufOutputStream = new ByteBufOutputStream(entityBuffer);
             final NBTOutputStream nbtOutputStream = NbtUtils.createWriterLE(bufOutputStream)) {
            byte[] key = LevelDBKeyUtil.ENTITIES.getKey(chunk.getX(), chunk.getZ(), chunk.getProvider().getDimensionData());
            if (entities.isEmpty()) {
                writeBatch.delete(key);
            } else {
                for (Entity e : entities) {
                    if (!(e instanceof Player) && !e.closed && e.canBeSavedWithChunk()) {
                        e.saveNBT();
                        nbtOutputStream.writeTag(e.namedTag);
                    }
                }
                writeBatch.put(key, Utils.convertByteBuf2Array(entityBuffer));
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } finally {
            entityBuffer.release();
        }
    }

    private void serializeBlockTicks(UnsafeChunk unsafe) {
        List<NbtMap> scheduledTicks = new ObjectArrayList<>();
        List<NbtMap> normalTickBlocks = new ObjectArrayList<>();

        Chunk chunk = unsafe.getChunk();
        Set<BlockUpdateEntry> pending = chunk.getLevel().getPendingBlockUpdates(chunk);
        pending.parallelStream().forEach(blockUpdateEntry -> {
            Block block = blockUpdateEntry.block;
            String id = block.getId();
            int x = block.getFloorX();
            int y = block.getFloorY();
            int z = block.getFloorZ();
            handleCustomTickableBlock(id, x, y, z, scheduledTicks);
            handleVanillaTickableBlock(id, x, y, z, normalTickBlocks);
        });

        // Save both lists to extraData
        unsafe.updateExtraData(
                unsafe.getExtraData().toBuilder()
                        .putList("pendingScheduledTicks", NbtType.COMPOUND, scheduledTicks)
                        .putList("pendingNormalTickBlocks", NbtType.COMPOUND, normalTickBlocks)
                        .build()
        );
    }

    private void handleCustomTickableBlock(String id, int x, int y, int z, List<NbtMap> scheduledTicks) {
        CustomBlockDefinition def = BlockRegistry.getCustomBlockDefinition(id);
        if (def == null || def.tickSettings() == null) return;

        NbtMap tag = NbtMap.builder()
                .putInt("x", x)
                .putInt("y", y)
                .putInt("z", z)
                .putString("id", id)
                .putInt("layer", 0)
                .putInt("delay", def.tickSettings().minTicks())
                .putInt("priority", 0)
                .putBoolean("checkBlockWhenUpdate", true)
                .build();

        scheduledTicks.add(tag);
    }

    private void handleVanillaTickableBlock(String id, int x, int y, int z, List<NbtMap> normalTickBlocks) {
        switch (id) {
            case BlockID.DAYLIGHT_DETECTOR, BlockID.DAYLIGHT_DETECTOR_INVERTED,
                 BlockID.REDSTONE_WIRE, BlockID.REDSTONE_TORCH,
                 BlockID.POWERED_REPEATER, BlockID.UNPOWERED_REPEATER,
                 BlockID.POWERED_COMPARATOR, BlockID.UNPOWERED_COMPARATOR,
                 BlockID.PISTON, BlockID.STICKY_PISTON -> {
                NbtMap tag = NbtMap.builder()
                        .putInt("x", x)
                        .putInt("y", y)
                        .putInt("z", z)
                        .putString("id", id)
                        .build();
                normalTickBlocks.add(tag);
            }
        }
    }

    public static class ScheduledTickInfo {
        public int x, y, z, layer, delay, priority;
        public String id;
        public boolean checkBlockWhenUpdate;
    }

    public static class NormalTickInfo {
        public int x, y, z;
        public String id;
    }

    public static void deserializeBlockTicks(NbtMap extraData, IChunkBuilder builder) {
        if (extraData == null) return;
        long chunkKey = ((long) builder.getChunkX() & 0xffffffffL) << 32 | ((long) builder.getChunkZ() & 0xffffffffL);

        // --- Scheduled Ticks ---
        if (extraData.containsKey("pendingScheduledTicks")) {
            List<NbtMap> scheduledTicks = extraData.getList("pendingScheduledTicks", NbtType.COMPOUND);
            List<ScheduledTickInfo> scheduledList = new ArrayList<>();
            for (NbtMap tag : scheduledTicks) {
                ScheduledTickInfo info = new ScheduledTickInfo();
                info.x = tag.getInt("x");
                info.y = tag.getInt("y");
                info.z = tag.getInt("z");
                info.id = tag.getString("id");
                info.layer = tag.getInt("layer");
                info.delay = tag.getInt("delay");
                info.priority = tag.getInt("priority");
                info.checkBlockWhenUpdate = tag.getBoolean("checkBlockWhenUpdate");
                scheduledList.add(info);
            }
            if (!scheduledList.isEmpty()) {
                LevelDBProvider.getScheduledTicksMap().put(chunkKey, scheduledList);
            }
        }

        // --- Normal Tick Blocks ---
        if (extraData.containsKey("pendingNormalTickBlocks")) {
            List<NbtMap> normalTicks = extraData.getList("pendingNormalTickBlocks", NbtType.COMPOUND);
            List<NormalTickInfo> normalList = new ArrayList<>();
            for (NbtMap tag : normalTicks) {
                NormalTickInfo info = new NormalTickInfo();
                info.x = tag.getInt("x");
                info.y = tag.getInt("y");
                info.z = tag.getInt("z");
                info.id = tag.getString("id");
                normalList.add(info);
            }
            if (!normalList.isEmpty()) {
                LevelDBProvider.getNormalTicksMap().put(chunkKey, normalList);
            }
        }
    }

    private NbtMap read(byte[] data) {
        try (final ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
             final NBTInputStream nbtInputStream = NbtUtils.createNetworkReader(inputStream)) {
            return (NbtMap) nbtInputStream.readTag();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read network nbt");
        }
    }

    public byte[] write(NbtMap nbtMap) {
        try (final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             final NBTOutputStream nbtOutputStream = NbtUtils.createNetworkWriter(outputStream)) {
            nbtOutputStream.writeTag(nbtMap);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to write network nbt");
        }
    }
}
