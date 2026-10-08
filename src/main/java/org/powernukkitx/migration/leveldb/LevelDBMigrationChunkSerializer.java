package org.powernukkitx.migration.leveldb;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import org.cloudburstmc.nbt.NBTInputStream;
import org.cloudburstmc.nbt.NBTOutputStream;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtUtils;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.powernukkitx.block.BlockAir;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.level.DimensionData;
import org.powernukkitx.level.biome.BiomeID;
import org.powernukkitx.level.format.Chunk;
import org.powernukkitx.level.format.ChunkFinalizationState;
import org.powernukkitx.level.format.ChunkSection;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.format.IChunkBuilder;
import org.powernukkitx.level.format.LevelProvider;
import org.powernukkitx.level.format.leveldb.LevelDBChunkSerializer;
import org.powernukkitx.level.format.palette.Palette;
import org.powernukkitx.level.util.LevelDBKeyUtil;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.ListTag;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Provides LevelDB chunk discovery, decoding and NBT serialization helpers used by offline migrations.
 *
 * @author Curse
 */
public final class LevelDBMigrationChunkSerializer {
    private LevelDBMigrationChunkSerializer() {}

    /**
     * Collects all stored chunk coordinates for one dimension.
     */
    public static List<ChunkCoordinate> collectChunks(DB db, int dimensionId) throws IOException {
        List<ChunkCoordinate> chunks = new ArrayList<>();
        Set<Long> seen = new HashSet<>();
        try (DBIterator iterator = db.iterator()) {
            for (iterator.seekToFirst(); iterator.hasNext(); iterator.next()) {
                byte[] key = iterator.peekNext().getKey();
                boolean actorDigestKey = isActorDigestKey(key);
                if (!actorDigestKey && !isChunkVersionKey(key)) {
                    continue;
                }

                int keyDimension = actorDigestKey ? getDimensionFromActorDigestKey(key) : getDimensionFromChunkKey(key);
                if (keyDimension != dimensionId) {
                    continue;
                }

                int chunkOffset = actorDigestKey ? 4 : 0;
                int chunkX = readIntLittleEndian(key, chunkOffset);
                int chunkZ = readIntLittleEndian(key, chunkOffset + 4);
                long chunkKey = getChunkKey(chunkX, chunkZ);
                if (seen.add(chunkKey)) {
                    chunks.add(new ChunkCoordinate(chunkX, chunkZ));
                }
            }
        }

        chunks.sort(Comparator.comparingInt(ChunkCoordinate::x).thenComparingInt(ChunkCoordinate::z));
        return chunks;
    }

    /**
     * Reads consecutive little-endian compound NBT values.
     */
    public static List<CompoundTag> readLittleEndianCompounds(byte[] data) throws IOException {
        List<CompoundTag> result = new ArrayList<>();
        ByteBuf buffer = Unpooled.wrappedBuffer(data);
        try {
            while (buffer.isReadable()) {
                try (ByteBufInputStream inputStream = new ByteBufInputStream(buffer);
                     NBTInputStream nbtInputStream = NbtUtils.createReaderLE(inputStream)) {
                    Object tag = nbtInputStream.readTag();
                    if (!(tag instanceof NbtMap map)) {
                        throw new IOException("Invalid little-endian compound NBT");
                    }

                    result.add(CompoundTag.fromNetwork(map));
                }
            }
        } finally {
            buffer.release();
        }

        return result;
    }

    /**
     * Writes consecutive compounds using little-endian NBT.
     */
    public static byte[] writeLittleEndianCompounds(List<CompoundTag> tags) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             NBTOutputStream nbtOutputStream = NbtUtils.createWriterLE(outputStream)) {
            for (CompoundTag tag : tags) {
                nbtOutputStream.writeTag(tag.toNetwork());
            }

            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Writes one compound using little-endian NBT.
     */
    public static byte[] writeLittleEndianCompound(CompoundTag tag) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             NBTOutputStream nbtOutputStream = NbtUtils.createWriterLE(outputStream)) {
            nbtOutputStream.writeTag(tag.toNetwork());
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Reads one big-endian compound NBT value.
     */
    public static CompoundTag readBigEndianCompound(byte[] data) {
        if (data == null) return null;

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
             NBTInputStream nbtInputStream = NbtUtils.createReader(inputStream)) {
            Object tag = nbtInputStream.readTag();
            if (!(tag instanceof NbtMap map)) {
                return null;
            }

            return CompoundTag.fromNetwork(map);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Writes one compound using big-endian NBT.
     */
    public static byte[] writeBigEndianCompound(CompoundTag tag) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             NBTOutputStream nbtOutputStream = NbtUtils.createWriter(outputStream)) {
            nbtOutputStream.writeTag(tag.toNetwork());
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static boolean isChunkVersionKey(byte[] key) {
        if (key.length == 9) {
            byte suffix = key[8];
            return suffix == (byte) ',' || suffix == (byte) 'v';
        }

        if (key.length == 13) {
            byte suffix = key[12];
            return suffix == (byte) ',' || suffix == (byte) 'v';
        }

        return false;
    }

    private static boolean isActorDigestKey(byte[] key) {
        return (key.length == 12 || key.length == 16)
                && key[0] == (byte) 'd'
                && key[1] == (byte) 'i'
                && key[2] == (byte) 'g'
                && key[3] == (byte) 'p';
    }

    private static int getDimensionFromChunkKey(byte[] key) {
        if (key.length == 9) {
            return 0;
        }

        if (key.length == 13) {
            return readIntLittleEndian(key, 8);
        }

        throw new IllegalArgumentException("Invalid chunk key length: " + key.length);
    }

    private static int getDimensionFromActorDigestKey(byte[] key) {
        if (key.length == 12) {
            return 0;
        }

        if (key.length == 16) {
            return readIntLittleEndian(key, 12);
        }

        throw new IllegalArgumentException("Invalid actor digest key length: " + key.length);
    }

    private static long getChunkKey(int chunkX, int chunkZ) {
        return ((long) chunkX & 0xffffffffL) << 32 | ((long) chunkZ & 0xffffffffL);
    }

    private static int readIntLittleEndian(byte[] data, int offset) {
        return (data[offset] & 0xff)
                | (data[offset + 1] & 0xff) << 8
                | (data[offset + 2] & 0xff) << 16
                | (data[offset + 3] & 0xff) << 24;
    }

    /**
     * Reads a stored chunk using migration-compatible BDS or legacy PNX decoding.
     *
     * @param pnxManagedStorage whether the chunk contained PNX extra data before migration
     */
    public static IChunk readChunk(
            DB db,
            int chunkX,
            int chunkZ,
            LevelProvider provider,
            CompoundTag extraData,
            boolean pnxManagedStorage
    ) throws IOException {
        IChunkBuilder builder = Chunk.builder().chunkX(chunkX).chunkZ(chunkZ).levelProvider(provider);
        byte[] versionValue = db.get(LevelDBKeyUtil.VERSION.getKey(chunkX, chunkZ, provider.getDimensionData()));
        if (versionValue == null) {
            versionValue = db.get(LevelDBKeyUtil.LEGACY_VERSION.getKey(chunkX, chunkZ, provider.getDimensionData()));
        }

        if (versionValue == null) {
            return null;
        }

        builder.extraData(extraData);
        byte[] finalized = db.get(LevelDBKeyUtil.CHUNK_FINALIZED_STATE.getKey(chunkX, chunkZ, provider.getDimensionData()));
        builder.finalizationState(decodeLegacyFinalizationState(finalized));
        boolean legacyPnxStorage = pnxManagedStorage && (versionValue[0] & 0xff) < (IChunk.VERSION & 0xff);
        if (legacyPnxStorage) {
            initializeBiomeSections(builder);
            LevelDBChunkSerializer.INSTANCE.deserializeBlock(db, builder);
            deserializeLegacyHeightAndBiome(db, builder);
        } else {
            deserializeHeightAndBiome(db, builder);
            LevelDBChunkSerializer.INSTANCE.deserializeBlock(db, builder);
        }

        return builder.build();
    }

    /**
     * Reads legacy scheduled ticks stored in PNX chunk extra data.
     */
    public static List<ScheduledTickInfo> readLegacyScheduledTicks(IChunk chunk, CompoundTag extraData) {
        if (extraData == null || !extraData.contains("pendingScheduledTicks")) {
            return List.of();
        }

        ListTag<CompoundTag> scheduledTicks = extraData.getList("pendingScheduledTicks", CompoundTag.class);
        List<ScheduledTickInfo> result = new ArrayList<>();
        for (CompoundTag tag : scheduledTicks.getAll()) {
            int x = tag.getInt("x");
            int y = tag.getInt("y");
            int z = tag.getInt("z");
            long delay = Math.max(1, tag.getInt("delay"));
            int layer = tag.getInt("layer");
            int sectionIndex = (y >> 4) - chunk.getProvider().getDimensionData().getMinSectionY();
            BlockState blockState = BlockAir.STATE;
            ChunkSection[] sections = chunk.getSections();
            if (sectionIndex >= 0 && sectionIndex < sections.length) {
                ChunkSection section = sections[sectionIndex];
                if (section != null && layer >= 0 && layer < ChunkSection.LAYER_COUNT) {
                    blockState = section.getBlockState(x & 0x0f, y & 0x0f, z & 0x0f, layer);
                }
            }

            result.add(new ScheduledTickInfo(x, y, z, delay, CompoundTag.fromNetwork(blockState.getBlockStateTag())));
        }

        return result;
    }

    /**
     * Decodes the legacy persisted chunk finalization state.
     */
    public static ChunkFinalizationState decodeLegacyFinalizationState(byte[] finalized) {
        if (finalized == null) {
            return ChunkFinalizationState.DONE;
        }

        ByteBuf byteBuf = Unpooled.wrappedBuffer(finalized);
        int storageValue = byteBuf.readableBytes() >= Integer.BYTES ? byteBuf.readIntLE() : byteBuf.readByte();
        return switch (storageValue) {
            case -1, 0 -> ChunkFinalizationState.NEEDS_INSTATICKING;
            case 1 -> ChunkFinalizationState.NEEDS_POPULATION;
            case 2, 3 -> ChunkFinalizationState.DONE;
            default -> ChunkFinalizationState.fromStorageValue(storageValue);
        };
    }

    @SuppressWarnings("unchecked")
    private static void initializeBiomeSections(IChunkBuilder builder) {
        int sectionCount = builder.getDimensionData().getChunkSectionCount();
        Palette<Integer>[] biomeSections = (Palette<Integer>[]) new Palette<?>[sectionCount];
        for (int i = 0; i < sectionCount; i++) {
            biomeSections[i] = new Palette<>(BiomeID.PLAINS);
        }

        builder.biomeSections(biomeSections);
    }

    private static void deserializeLegacyHeightAndBiome(DB db, IChunkBuilder builder) {
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
                ChunkSection[] sections = builder.getSections();
                Palette<Integer>[] biomeSections = builder.getBiomeSections();
                for (int i = 0; i < sections.length; i++) {
                    if (sections[i] == null) continue;

                    Palette<Integer> biomePalette = biomeSections[i];
                    biomePalette.readFromStorageRuntime(heightAndBiomesBuffer, Integer::valueOf, lastPalette);
                    lastPalette = biomePalette;
                }

                return;
            }

            byte[] bytes2D = db.get(LevelDBKeyUtil.DATA_2D.getKey(builder.getChunkX(), builder.getChunkZ(), dimensionInfo));
            if (bytes2D == null) return;

            heightAndBiomesBuffer = Unpooled.wrappedBuffer(bytes2D);
            short[] heights = new short[256];
            for (int i = 0; i < 256; i++) {
                heights[i] = heightAndBiomesBuffer.readShortLE();
            }

            builder.heightMap(heights);
            byte[] biomes = new byte[256];
            heightAndBiomesBuffer.readBytes(biomes);
            ChunkSection[] sections = builder.getSections();
            Palette<Integer>[] biomeSections = builder.getBiomeSections();
            for (int i = 0; i < sections.length; i++) {
                if (sections[i] == null) continue;

                Palette<Integer> biomePalette = biomeSections[i];
                fillLegacy2DBiomes(biomePalette, biomes);
            }
        } finally {
            if (heightAndBiomesBuffer != null) {
                heightAndBiomesBuffer.release();
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static void deserializeHeightAndBiome(DB db, IChunkBuilder builder) {
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

            byte[] bytes2D = db.get(LevelDBKeyUtil.DATA_2D.getKey(builder.getChunkX(), builder.getChunkZ(), dimensionInfo));
            if (bytes2D == null) return;

            heightAndBiomesBuffer = Unpooled.wrappedBuffer(bytes2D);
            short[] heights = new short[256];
            for (int i = 0; i < 256; i++) {
                heights[i] = heightAndBiomesBuffer.readShortLE();
            }

            builder.heightMap(heights);
            byte[] biomes = new byte[256];
            heightAndBiomesBuffer.readBytes(biomes);
            for (Palette<Integer> biomePalette : biomeSections) {
                fillLegacy2DBiomes(biomePalette, biomes);
            }
        } finally {
            if (heightAndBiomesBuffer != null) {
                heightAndBiomesBuffer.release();
            }
        }
    }

    private static void fillLegacy2DBiomes(Palette<Integer> biomePalette, byte[] biomes) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int sy = 0; sy < 16; sy++) {
                    biomePalette.set(IChunk.index(x, sy, z), (int) biomes[x + 16 * z]);
                }
            }
        }
    }

    /**
     * Represents one stored LevelDB chunk coordinate discovered during migration.
     */
    public record ChunkCoordinate(int x, int z) {
    }

    /**
     * Represents one scheduled tick decoded from legacy chunk storage.
     */
    public record ScheduledTickInfo(int x, int y, int z, long delay, CompoundTag blockState) {
    }
}
