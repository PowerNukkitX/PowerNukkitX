package org.powernukkitx.level.format;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockAir;
import org.powernukkitx.block.BlockLightProperties;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.biome.BiomeID;
import org.powernukkitx.level.format.bitarray.BitArrayVersion;
import org.powernukkitx.level.format.palette.BlockPalette;
import org.powernukkitx.level.format.palette.Palette;
import org.powernukkitx.level.util.NibbleArray;
import org.powernukkitx.math.BlockVector3;
import org.powernukkitx.registry.Registries;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiPredicate;

import static org.powernukkitx.level.format.IChunk.index;

/**
 * Allay Project 2023/5/30
 *
 * @author Cool_Loong
 */
@NotThreadSafe
public record ChunkSection(byte y, BlockPalette[] blockLayer, Palette<Integer> biomes, SubChunkLightData lighting, AtomicLong blockChanges) {
    public static final int SIZE = 16 * 16 * 16;
    public static final int LAYER_COUNT = 2;
    public static final int VERSION = 9;

    public ChunkSection(byte sectionY) {
        this(sectionY, new Palette<>(BiomeID.PLAINS, BitArrayVersion.V0));
    }

    public ChunkSection(byte sectionY, Palette<Integer> biomes) {
        this(sectionY,
                new BlockPalette[]{new BlockPalette(BlockAir.STATE, new ReferenceArrayList<>(16), BitArrayVersion.V2),
                        new BlockPalette(BlockAir.STATE, new ReferenceArrayList<>(16), BitArrayVersion.V2)},
                biomes,
                new SubChunkLightData(),
                new AtomicLong(0));
    }

    public ChunkSection(byte sectionY, BlockPalette[] blockLayer) {
        this(sectionY, blockLayer, new Palette<>(BiomeID.PLAINS, BitArrayVersion.V0));
    }

    public ChunkSection(byte sectionY, BlockPalette[] blockLayer, Palette<Integer> biomes) {
        this(sectionY, blockLayer,
                biomes,
                new SubChunkLightData(),
                new AtomicLong(0));
    }

    public ChunkSection(byte sectionY, BlockPalette[] blockLayer, Palette<Integer> biomes, NibbleArray blockLights, NibbleArray skyLights, AtomicLong blockChanges) {
        this(sectionY, blockLayer, biomes, new SubChunkLightData(blockLights, skyLights), blockChanges);
    }

    public BlockState getBlockState(int x, int y, int z) {
        return getBlockState(x, y, z, 0);
    }

    public BlockState getBlockState(int x, int y, int z, int layer) {
        return blockLayer[layer].get(index(x, y, z));
    }

    /**
     * Returns packed combined lighting properties for a section-local block.
     *
     * @param x local X
     * @param y local Y
     * @param z local Z
     * @return packed lighting properties
     */
    public int getCombinedLightProperties(int x, int y, int z) {
        int blockIndex = IChunk.index(x, y, z);
        byte[] cache = lighting.getCombinedLightPropertiesCache();

        if (cache != null) {
            int compact = cache[blockIndex] & 0xff;
            return (compact & 0x0f) | (compact & 0xf0) << 4;
        }

        return calculateCombinedLightProperties(blockIndex);
    }

    /**
     * Returns the cached compact lighting-property array, creating it when required.
     *
     * @return compact lighting properties for all section cells
     */
    public byte[] getCombinedLightingProperties() {
        byte[] cache = lighting.getCombinedLightPropertiesCache();

        if (cache != null) return cache;

        /*
         * Multiple InitialSubChunkLighters can share the same neighboring
         * ChunkSection. Build this persistent 4096-cell cache only once.
         */
        synchronized (lighting) {
            cache = lighting.getCombinedLightPropertiesCache();
            if (cache != null) return cache;
            byte[] created = new byte[SIZE];
            final boolean layer0Empty = blockLayer[0].isEmpty();
            final boolean layer1Empty = blockLayer[1].isEmpty();

            if (layer0Empty && layer1Empty) {
                return lighting.installCombinedLightPropertiesCache(created);
            }

            if (layer1Empty) {
                for (int blockIndex = 0; blockIndex < SIZE; blockIndex++) {
                    created[blockIndex] = compactCombinedLightProperties(blockLayer[0].getLightingProperties(blockIndex));
                }

                return lighting.installCombinedLightPropertiesCache(created);
            }

            if (layer0Empty) {
                for (int blockIndex = 0; blockIndex < SIZE; blockIndex++) {
                    created[blockIndex] = compactCombinedLightProperties(blockLayer[1].getLightingProperties(blockIndex));
                }

                return lighting.installCombinedLightPropertiesCache(created);
            }

            for (int blockIndex = 0; blockIndex < SIZE; blockIndex++) {
                created[blockIndex] = compactCombinedLightProperties(calculateCombinedLightProperties(blockIndex));
            }

            return lighting.installCombinedLightPropertiesCache(created);
        }
    }

    /**
     * Copies compact lighting properties for all section cells into the supplied array.
     *
     * @param target destination array with exactly {@link #SIZE} cells
     */
    public void fillCombinedLightProperties(byte[] target) {
        if (target == null || target.length != SIZE) {
            throw new IllegalArgumentException("Lighting property array must contain exactly " + SIZE + " cells");
        }

        System.arraycopy(getCombinedLightingProperties(), 0, target, 0, SIZE);
    }

    private int calculateCombinedLightProperties(int blockIndex) {
        int packed0 = blockLayer[0].getLightingProperties(blockIndex);
        int packed1 = blockLayer[1].getLightingProperties(blockIndex);
        int emission = Math.max(BlockLightProperties.lightLevel(packed0), BlockLightProperties.lightLevel(packed1));
        int filter = Math.min(15, Math.max(BlockLightProperties.lightFilter(packed0), BlockLightProperties.lightFilter(packed1)));

        return emission | filter << 8;
    }

    private static byte compactCombinedLightProperties(int packed) {
        int emission = packed & 0x0f;
        int filter = packed >>> 8 & 0x0f;

        return (byte) (emission | filter << 4);
    }

    private void refreshCombinedLightProperties(int blockIndex) {
        if (lighting.getCombinedLightPropertiesCache() == null) return;

        lighting.updateCombinedLightPropertiesCache(blockIndex, compactCombinedLightProperties(calculateCombinedLightProperties(blockIndex)));
    }

    public void setBlockState(int x, int y, int z, BlockState blockState, int layer) {
        blockChanges.addAndGet(1);
        int blockIndex = index(x, y, z);
        blockLayer[layer].set(blockIndex, blockState);
        refreshCombinedLightProperties(blockIndex);
    }

    void setBlockState(int x, int y, int z, BlockState blockState, int layer, BlockState previousState) {
        blockChanges.addAndGet(1);
        if (previousState == blockState) return;

        int blockIndex = index(x, y, z);
        blockLayer[layer].set(blockIndex, blockState, previousState);
        refreshCombinedLightProperties(blockIndex);
    }

    public BlockState getAndSetBlockState(int x, int y, int z, BlockState blockstate, int layer) {
        blockChanges.addAndGet(1);
        int blockIndex = index(x, y, z);
        BlockState result = blockLayer[layer].get(blockIndex);
        blockLayer[layer].set(blockIndex, blockstate, result);
        refreshCombinedLightProperties(blockIndex);

        return result;
    }

    private static final Integer[] BIOME_ID_BOX = new Integer[1024];

    static {
        for (int i = 0; i < BIOME_ID_BOX.length; i++) {
            BIOME_ID_BOX[i] = i;
        }
    }

    static Integer boxBiomeId(int biomeId) {
        return biomeId >= 0 && biomeId < BIOME_ID_BOX.length ? BIOME_ID_BOX[biomeId] : Integer.valueOf(biomeId);
    }

    public void setBiomeId(int x, int y, int z, int biomeId) {
        biomes.set(index(x, y, z), boxBiomeId(biomeId));
    }

    public int getBiomeId(int x, int y, int z) {
        return biomes.get(index(x, y, z));
    }

    public byte getBlockLight(int x, int y, int z) {
        return lighting.getBlockLight(index(x, y, z));
    }

    public byte getBlockSkyLight(int x, int y, int z) {
        return lighting.getSkyLight(index(x, y, z));
    }

    public void setBlockLight(int x, int y, int z, byte light) {
        lighting.setBlockLight(index(x, y, z), light);
    }

    public void setBlockSkyLight(int x, int y, int z, byte light) {
        lighting.setSkyLight(index(x, y, z), light);
    }

    /**
     * Returns or creates the block-light storage.
     *
     * @return block-light storage
     */
    public NibbleArray blockLights() {
        return lighting.getOrCreateBlockLightStorage();
    }

    /**
     * Returns or creates the skylight storage.
     *
     * @return skylight storage
     */
    public NibbleArray skyLights() {
        return lighting.getOrCreateSkyLightStorage();
    }

    /**
     * Returns whether every cell has maximum skylight.
     *
     * @return whether skylight is uniformly maximum
     */
    public boolean hasMaxSkyLight() {
        return lighting.hasMaxSkyLight();
    }

    /**
     * Sets skylight for every section cell.
     *
     * @param light skylight value
     */
    public void setAllSkyLight(byte light) {
        lighting.setAllSkyLight(light);
    }

    /**
     * Sets block light for every section cell.
     *
     * @param light block-light value
     */
    public void setAllBlockLight(byte light) {
        lighting.setAllBlockLight(light);
    }

    /**
     * Returns whether this section requires initial lighting.
     *
     * @return whether initial lighting is required
     */
    public boolean needsInitLighting() {
        return lighting.needsInitLighting();
    }

    /**
     * Sets whether this section requires initial lighting.
     *
     * @param value whether initial lighting is required
     */
    public void setNeedsInitLighting(boolean value) {
        lighting.setNeedsInitLighting(value);
    }

    /**
     * Returns whether client lighting data must be sent.
     *
     * @return whether client lighting is required
     */
    public boolean needsClientLighting() {
        return lighting.needsClientLighting();
    }

    /**
     * Sets whether client lighting data must be sent.
     *
     * @param value whether client lighting is required
     */
    public void setNeedsClientLighting(boolean value) {
        lighting.setNeedsClientLighting(value);
    }

    public List<Block> scanBlocks(LevelProvider provider, int offsetX, int offsetZ, BlockVector3 min, BlockVector3 max, BiPredicate<BlockVector3, BlockState> condition) {
        final List<Block> results = new ArrayList<>();
        final BlockVector3 current = new BlockVector3();
        int offsetY = y << 4;
        int minX = Math.max(0, min.x - offsetX);
        int minY = Math.max(0, min.y - offsetY);
        int minZ = Math.max(0, min.z - offsetZ);
        for (int x = Math.min(max.x - offsetX, 15); x >= minX; x--) {
            current.x = offsetX + x;
            for (int z = Math.min(max.z - offsetZ, 15); z >= minZ; z--) {
                current.z = offsetZ + z;
                for (int y = Math.min(max.y - offsetY, 15); y >= minY; y--) {
                    current.y = offsetY + y;
                    BlockState state = blockLayer[0].get(index(x, y, z));
                    if (condition.test(current, state)) {
                        results.add(Registries.BLOCK.get(state, current.x, current.y, current.z, provider.getLevel()));
                    }
                }
            }
        }
        return results;
    }

    public boolean isEmpty() {
        for (BlockPalette palette : blockLayer) {
            if (!palette.isEmpty()) return false;
        }

        return true;
    }

    public void setNeedReObfuscate() {
        blockLayer[0].setNeedReObfuscate();
        blockLayer[1].setNeedReObfuscate();
    }

    public void writeToBuf(ByteBuf byteBuf) {
        byteBuf.writeByte(VERSION);
        //block layer count
        byteBuf.writeByte(LAYER_COUNT);
        byteBuf.writeByte(y);

        blockLayer[0].writeToNetwork(byteBuf, BlockState::blockStateHash);
        blockLayer[1].writeToNetwork(byteBuf, BlockState::blockStateHash);
    }

    public void writeObfuscatedToBuf(Level level, ByteBuf byteBuf) {
        byteBuf.writeByte(VERSION);
        //block layer count
        byteBuf.writeByte(LAYER_COUNT);
        byteBuf.writeByte(y);

        blockLayer[0].writeObfuscatedToNetwork(level, blockChanges, byteBuf, BlockState::blockStateHash);
        blockLayer[1].writeObfuscatedToNetwork(level, blockChanges, byteBuf, BlockState::blockStateHash);
    }
}
