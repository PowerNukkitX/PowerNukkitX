package cn.nukkit.level.format;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockState;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.DimensionData;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Range;

import java.io.IOException;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * @author Cool_Loong
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
     */
    void setSection(int fY, ChunkSection section);

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

    default BlockState getBlockState(int x, int y, int z) {
        return getBlockState(x, y, z, 0);
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
    BlockState getBlockState(int x, int y, int z, int layer);

    BlockState getAndSetBlockState(int x, int y, int z, BlockState blockstate, int layer);

    default BlockState getAndSetBlockState(int x, int y, int z, BlockState blockstate) {
        return getAndSetBlockState(x, y, z, blockstate, 0);
    }

    void setBlockState(int x, int y, int z, BlockState blockstate, int layer);

    default void setBlockState(int x, int y, int z, BlockState blockstate) {
        setBlockState(x, y, z, blockstate, 0);
    }

    /**
     * @param x the x 0~15
     * @param y the y
     * @param z the z 0~15
     * @return The block skylight at this location
     */
    int getBlockSkyLight(int x, int y, int z);

    void setBlockSkyLight(int x, int y, int z, int level);

    /**
     * @param x the x 0~15
     * @param y the y
     * @param z the z 0~15
     * @return The block light at this location
     */
    int getBlockLight(int x, int y, int z);

    /**
     * Sets block light.
     *
     * @param x     the x 0~15
     * @param y     the y
     * @param z     the z 0~15
     * @param level the level 0~15
     */
    void setBlockLight(int x, int y, int z, int level);

    /**
     * Get a heightmap in this section coordinates, which is the highest block height
     *
     * @param x the x 0~15
     * @param z the z 0~15
     * @return the height map
     */
    int getHeightMap(int x, int z);

    /**
     * Sets height map for this coordinate,which is the highest block height
     *
     * @param x     the x 0~15
     * @param z     the z 0~15
     * @param value the value
     */
    void setHeightMap(int x, int z, int value);

    /**
     * Recalculate height map for this chunk.
     */
    void recalculateHeightMap();

    /**
     * Recalculate a column height map of chunk
     */
    int recalculateHeightMapColumn(@Range(from = 0, to = 15) int x, @Range(from = 0, to = 15) int z);

    void populateSkyLight();

    /**
     * 获取子区块中某个特定位置的生物群系id
     *
     * @param x 0~15
     * @param z 0~15
     * @return 特定位置的生物群系id
     */
    int getBiomeId(int x, int y, int z);

    void setBiomeId(int x, int y, int z, int biomeId);

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

    @ApiStatus.Experimental
    void batchProcess(Consumer<UnsafeChunk> unsafeChunkConsumer);

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

    short[] getHeightMapArray();

    CompoundTag getExtraData();

    boolean hasChanged();

    void setChanged();

    void setChanged(boolean changed);

    long getChanges();

    long getSectionBlockChanges(int sectionY);

    /**
     * Used to handle with deny and allow blocks
     *
     * @return the boolean
     */
    boolean isBlockChangeAllowed(int chunkX, int chunkY, int chunkZ);

    Stream<Block> scanBlocks(BlockVector3 min, BlockVector3 max, BiPredicate<BlockVector3, BlockState> condition);

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

    default boolean isGenerated() {
        return this.getChunkState().ordinal() >= ChunkState.GENERATED.ordinal();
    }

    default boolean isPopulated() {
        return this.getChunkState().ordinal() >= ChunkState.POPULATED.ordinal();
    }

    default boolean isFinished() {
        return this.getChunkState().ordinal() == ChunkState.FINISHED.ordinal();
    }

    default void setGenerated() {
        setChunkState(ChunkState.GENERATED);
    }

    default void setPopulated() {
        setChunkState(ChunkState.POPULATED);
    }
}
