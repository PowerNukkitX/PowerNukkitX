package cn.nukkit.level.newformat;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.biome.Biome;
import cn.nukkit.level.format.ChunkSection;
import cn.nukkit.level.format.LevelProvider;

import java.io.IOException;
import java.util.Map;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public interface Chunk {
    boolean isSectionEmpty(int fY);

    ChunkSection getSection(int fY);

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

    void setProvider(LevelProvider provider);

    default BlockState getBlockState(int x, int y, int z) {
        return getBlockState(x, y, z, 0);
    }

    default BlockState getBlockState(int x, int y, int z, int layer) {
        return null;
    }

    BlockState getAndSetBlockState(int x, int y, int z, int layer, BlockState state);

    default BlockState getAndSetBlockState(int x, int y, int z, BlockState state) {
        return getAndSetBlockState(x, y, z, 0, state);
    }

    boolean setBlockState(int x, int y, int z, BlockState state);

    boolean setBlockState(int x, int y, int z, BlockState state, int layer);

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

    boolean isPopulated();

    void setPopulated(boolean value);

    void setPopulated();

    boolean isGenerated();

    void setGenerated(boolean value);

    void setGenerated();

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

    boolean unload() throws Exception;

    boolean unload(boolean save) throws Exception;

    boolean unload(boolean save, boolean safe) throws Exception;

    void initChunk();

    byte[] getBiomeIdArray();

    short[] getHeightMapArray();

    byte[] getBlockSkyLightArray();

    byte[] getBlockLightArray();

    byte[] toBinary();

    byte[] toFastBinary();

    boolean hasChanged();

    void setChanged();

    void setChanged(boolean changed);

    long getBlockChanges();

    boolean isBlockChangeAllowed(int x, int y, int z);

    default void reObfuscateChunk() {
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    default boolean isOverWorld() {
        return getProvider().isOverWorld();
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    default boolean isNether() {
        return getProvider().isNether();
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
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
