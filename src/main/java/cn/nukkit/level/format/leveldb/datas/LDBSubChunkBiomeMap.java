package cn.nukkit.level.format.leveldb.datas;

import cn.nukkit.level.format.leveldb.palette.IntPalette;

public final class LDBSubChunkBiomeMap implements Cloneable {
    private IntPalette palette;
    private int[] biomes = new int[4096];


    public LDBSubChunkBiomeMap(IntPalette palette) {
        this.palette = palette;
    }

    public IntPalette getPalette() {
        return this.palette;
    }

    /**
     * Retrieve the biome data at a specific coordinate.
     * @param x x coordinate
     * @param y y coordinate
     * @param z z coordinate
     * @return the biome
     */
    public int getBiomeAt(int x, int y, int z) {
        return this.biomes[getChunkPosIndex(x, y, z)];
    }

    /**
     * Set the biome data at a specific location.
     * @param x x coordinate
     * @param y y coordinate
     * @param z z coordinate
     * @param biome the new biome we are changing this block column to
     */
    public void setBiomeAt(int x, int y, int z, int biome) {
        this.palette.addEntry(biome);
        this.biomes[getChunkPosIndex(x, y, z)] = biome;
    }

    @Override
    public LDBSubChunkBiomeMap clone() {
        try {
            LDBSubChunkBiomeMap subChunkBiomeMap = (LDBSubChunkBiomeMap) super.clone();
            subChunkBiomeMap.palette = this.palette.clone();

            int[] copiedBiomes = new int[4096];
            System.arraycopy(this.biomes, 0, copiedBiomes, 0, copiedBiomes.length);
            subChunkBiomeMap.biomes = copiedBiomes;

            return subChunkBiomeMap;
        } catch (CloneNotSupportedException exception) {
            throw new AssertionError("Clone threw exception");
        }
    }

    private static int getChunkPosIndex(int x, int y, int z) {
        return x << 8 | x << 4 | y;
    }
}
