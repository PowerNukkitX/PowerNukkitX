package cn.nukkit.level.newformat;

import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.DimensionData;
import cn.nukkit.level.biome.Biome;
import cn.nukkit.nbt.tag.CompoundTag;

import java.io.IOException;
import java.util.Map;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public interface IChunk {
    int VERSION = 40;

    /**
     * Get Palette index
     *
     * @param x the x
     * @param y the y
     * @param z the z
     * @return the int
     */
    static int index(int x, int y, int z) {
        //The bedrock chunk order is xzy,the chunk order of java version is yzx
        return (x << 8) + (z << 4) + y;
    }

    /**
     * Is section empty.
     *
     * @param fY range -4 ~ 19 for Overworld
     * @return the boolean
     */
    boolean isSectionEmpty(int fY);

    /**
     * Gets section.
     *
     * @param fY range -4 ~ 19 for Overworld
     * @return the section
     */
    ChunkSection getSection(int fY);

    /**
     * Sets section.
     *
     * @param fY      range -4 ~ 19 for Overworld
     * @param section the section
     * @return the section
     */
    boolean setSection(int fY, ChunkSection section);

    ChunkSection[] getSections();

    int getX();

    void setX(int x);

    int getZ();

    void setZ(int z);

    default void setPosition(int x, int z) {
        setX(x);
        setZ(z);
    }

    long getIndex();

    LevelProvider getProvider();

    default DimensionData getDimensionData() {
        return getProvider().getDimensionData();
    }

    default Block getBlock(int x, int y, int z) {
        return getBlock(x, y, z, 0);
    }

    /**
     * Gets block.
     *
     * @param x     the x
     * @param y     the chunk y range -64 ~ 319 for OVERWORLD
     * @param z     the z
     * @param layer the layer
     * @return the block
     */
    Block getBlock(int x, int y, int z, int layer);

    Block getAndSetBlock(Block block, int layer);

    default Block getAndSetBlock(Block block) {
        return getAndSetBlock(block, 0);
    }

    boolean setBlock(Block block);

    boolean setBlock(Block block, int layer);

    int getBlockSkyLight(int x, int y, int z);

    void setBlockSkyLight(int x, int y, int z, int level);

    int getBlockLight(int x, int y, int z);

    void setBlockLight(int x, int y, int z, int level);

    int getHighestBlockAt(int x, int z);

    int getHighestBlockAt(int x, int z, boolean cache);

    int getHeightMap(int x, int z);

    void setHeightMap(int x, int z, int value);

    void recalculateHeightMap();

    int recalculateHeightMapColumn(int chunkX, int chunkZ);

    void populateSkyLight();

    /**
     * 获取子区块中某个特定位置的生物群系id
     *
     * @param x [0, 16)
     * @param y [0, 16)
     * @param z [0, 16)
     * @return 特定位置的生物群系id
     */
    int getBiomeId(int x, int y, int z);

    void setBiomeId(int x, int y, int z, int biomeId);

    Biome getBiome(int x, int y, int z);

    /**
     * 设置子区块中某个特定位置的生物群系id
     *
     * @param x [0, 16)
     * @param y [0, 16)
     * @param z [0, 16)
     */
    void setBiome(int x, int y, int z, Biome biome);

    boolean isLightPopulated();

    void setLightPopulated(boolean value);

    void setLightPopulated();

    ChunkState getChunkState();

    void setChunkState(ChunkState chunkState);

    void addEntity(Entity entity);

    void removeEntity(Entity entity);

    void addBlockEntity(BlockEntity blockEntity);

    void removeBlockEntity(BlockEntity blockEntity);

    Map<Long, Entity> getEntities();

    Map<Long, BlockEntity> getBlockEntities();

    BlockEntity getTile(int x, int y, int z);

    boolean isLoaded();

    boolean load() throws IOException;

    boolean load(boolean generate) throws IOException;

    boolean unload();

    boolean unload(boolean save);

    boolean unload(boolean save, boolean safe);

    /**
     * Init chunk.Load block entity and entity NBT
     */
    void initChunk();

    byte[] getBiomeIdArray();

    short[] getHeightMapArray();

    byte[] getBlockSkyLightArray();

    byte[] getBlockLightArray();

    CompoundTag getExtraData();

    boolean hasChanged();

    void setChanged();

    void setChanged(boolean changed);

    long getBlockChanges();

    boolean isBlockChangeAllowed(int x, int y, int z);

    default void reObfuscateChunk() {
    }

    default boolean isOverWorld() {
        return getProvider().isOverWorld();
    }

    default boolean isNether() {
        return getProvider().isNether();
    }

    default boolean isTheEnd() {
        return getProvider().isTheEnd();
    }

    class Entry {
        public final int chunkX;
        public final int chunkZ;

        public Entry(int chunkX, int chunkZ) {
            this.chunkX = chunkX;
            this.chunkZ = chunkZ;
        }
    }
}
