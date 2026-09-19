package org.powernukkitx.level.format;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.blockentity.BlockEntity;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.level.DimensionData;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.format.palette.Palette;
import org.powernukkitx.level.generator.ChunkGenerationState;
import org.powernukkitx.level.structure.AabbVolumes;
import org.powernukkitx.math.BlockVector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.scheduler.BlockUpdateScheduler;
import org.powernukkitx.scheduler.RandomBlockUpdateScheduler;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Range;

import java.io.IOException;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * @author Cool_Loong
 */
public interface IChunk {
    int VERSION = 42;

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

    /**
     * Returns the per-section biome palettes.
     *
     * @return biome section palettes
     */
    Palette<Integer>[] getBiomeSections();

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

    Level getLevel();

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

    /**
     * Provides block-state reads while the chunk block read lock is held.
     */
    @ApiStatus.Internal
    @FunctionalInterface
    interface BlockStateReader {

        /**
         * Returns a block state from the locked chunk view.
         */
        BlockState getBlockState(int x, int y, int z, int layer);

        /**
         * Returns a layer-zero block state from the locked chunk view.
         */
        default BlockState getBlockState(int x, int y, int z) {
            return getBlockState(x, y, z, 0);
        }
    }

    /**
     * Executes block-state reads under one chunk block read lock.
     */
    @ApiStatus.Internal
    <T> T readBlockStates(Function<BlockStateReader, T> action);

    BlockState getAndSetBlockState(int x, int y, int z, BlockState blockstate, int layer);

    default BlockState getAndSetBlockState(int x, int y, int z, BlockState blockstate) {
        return getAndSetBlockState(x, y, z, blockstate, 0);
    }

    void setBlockState(int x, int y, int z, BlockState blockstate, int layer);

    default void setBlockState(int x, int y, int z, BlockState blockstate) {
        setBlockState(x, y, z, blockstate, 0);
    }

    /**
     * Gets the first free Y above the highest precipitation obstruction.
     *
     * @param x the x 0~15
     * @param z the z 0~15
     * @return the first free precipitation Y
     */
    int getRainHeight(int x, int z);

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
     * Gets the first free Y above the highest heightmap blocker, matching BDS heightmap semantics.
     *
     * @param x the x 0~15
     * @param z the z 0~15
     * @return the first free Y
     */
    int getHeightMap(int x, int z);

    /**
     * Sets the first free Y above the highest heightmap blocker.
     *
     * @param x     the x 0~15
     * @param z     the z 0~15
     * @param value the first free Y
     */
    void setHeightMap(int x, int z, int value);

    /**
     * Gets the render height map for this coordinate.
     *
     * @param x the x 0~15
     * @param z the z 0~15
     * @return the render height map
     */
    int getRenderHeightMap(int x, int z);

    /**
     * Sets the render height map for this coordinate.
     *
     * @param x     the x 0~15
     * @param z     the z 0~15
     * @param value the value
     */
    void setRenderHeightMap(int x, int z, int value);

    /**
     * Recalculate height map for this chunk.
     */
    void recalculateHeightMap();

    /**
     * Recalculate a column height map of chunk
     */
    int recalculateHeightMapColumn(@Range(from = 0, to = 15) int x, @Range(from = 0, to = 15) int z);

    /**
     * Performs the legacy direct skylight population pass.
     *
     * @deprecated Initial lighting is managed by the chunk lighting pipeline.
     */
    @Deprecated(since = "3.1.0", forRemoval = true)
    void populateSkyLight();

    /**
     * Gets the biome id at a specific position within the sub-chunk
     *
     * @param x 0~15
     * @param z 0~15
     * @return the biome id at the specific position
     */
    int getBiomeId(int x, int y, int z);

    void setBiomeId(int x, int y, int z, int biomeId);

    boolean isLightPopulated();

    /**
     * Sets the legacy light-populated state directly.
     *
     * @deprecated Use the chunk lighting state lifecycle instead.
     */
    @Deprecated(since = "3.1.0", forRemoval = true)
    void setLightPopulated(boolean value);

    /**
     * Marks the legacy light-populated state as complete.
     *
     * @deprecated Use the chunk lighting state lifecycle instead.
     */
    @Deprecated(since = "3.1.0", forRemoval = true)
    void setLightPopulated();

    /**
     * Returns the transient runtime generation-tree state.
     *
     * @return runtime generation state
     */
    @ApiStatus.Internal
    ChunkGenerationState getGenerationState();

    /**
     * Returns the persisted chunk finalization state.
     *
     * @return finalization state
     */
    ChunkFinalizationState getFinalizationState();

    /**
     * Sets the persisted chunk finalization state.
     *
     * @param finalizationState finalization state
     */
    void setFinalizationState(
            ChunkFinalizationState finalizationState
    );

    @Deprecated(since = "3.1.0", forRemoval = true)
    default ChunkState getChunkState() {
        return ChunkState.fromFinalizationState(this.getFinalizationState());
    }

    @Deprecated(since = "3.1.0", forRemoval = true)
    default void setChunkState(ChunkState chunkState) {
        this.setFinalizationState(chunkState.toFinalizationState());
    }

    void addEntity(Entity entity);

    void removeEntity(Entity entity);

    void addBlockEntity(BlockEntity blockEntity);

    void removeBlockEntity(BlockEntity blockEntity);

    Map<Long, Entity> getEntities();

    default boolean hasEntities() {
        return !getEntities().isEmpty();
    }

    void doMobSpawning();

    BlockUpdateScheduler getBlockUpdateScheduler();

    /**
     * Returns the scheduler for persisted random block updates.
     *
     * @return random block update scheduler
     */
    RandomBlockUpdateScheduler getRandomBlockUpdateScheduler();

    /**
     * Advances and returns the Bedrock per-chunk snow random value.
     */
    int nextSnowRandomValue();

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

    boolean isInitiated();

    short[] getHeightMapArray();

    CompoundTag getExtraData();

    void setExtraData(CompoundTag extraData);

    /**
     * Returns this chunk's LevelChunkMetaData reference state.
     */
    LevelChunkMetaData getLevelChunkMetaData();

    /**
     * Replaces this chunk's LevelChunkMetaData reference state.
     */
    void setLevelChunkMetaData(LevelChunkMetaData levelChunkMetaData);

    /**
     * Returns the AABBVolumes data associated with this chunk.
     */
    AabbVolumes getAabbVolumes();

    /**
     * Replaces the AABBVolumes data associated with this chunk.
     */
    void setAabbVolumes(AabbVolumes aabbVolumes);

    /**
     * Returns the persisted Bedrock biome state associated with this chunk.
     */
    BiomeState getBiomeState();

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

    @Deprecated(since = "3.1.0", forRemoval = true)
    default boolean isGenerated() {
        return this.getFinalizationState() != ChunkFinalizationState.NEEDS_INSTATICKING;
    }

    @Deprecated(since = "3.1.0", forRemoval = true)
    default boolean isPopulated() {
        return this.getFinalizationState() == ChunkFinalizationState.DONE;
    }

    @Deprecated(since = "3.1.0", forRemoval = true)
    default boolean isFinished() {
        return this.getFinalizationState() == ChunkFinalizationState.DONE;
    }

    @Deprecated(since = "3.1.0", forRemoval = true)
    default void setGenerated() {
        setFinalizationState(ChunkFinalizationState.NEEDS_POPULATION);
    }

    @Deprecated(since = "3.1.0", forRemoval = true)
    default void setPopulated() {
        setFinalizationState(ChunkFinalizationState.DONE);
    }

    /**
     * Returns whether the specified local X/Z column contains a Border Block.
     */
    boolean hasBorderBlock(int localX, int localZ);
}
