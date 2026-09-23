package org.powernukkitx.level.format;

import org.powernukkitx.Player;
import org.powernukkitx.Server;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockAir;
import org.powernukkitx.block.BlockLightProperties;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.blockentity.BlockEntity;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityFlyable;
import org.powernukkitx.entity.mob.EntityPillager;
import org.powernukkitx.level.DimensionData;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.biome.BiomeID;
import org.powernukkitx.level.entity.spawners.SpawnRule;
import org.powernukkitx.level.generator.ChunkGenerationState;
import org.powernukkitx.level.generator.densityfunction.DensityCommon;
import org.powernukkitx.level.lighting.ChunkLightingState;
import org.powernukkitx.level.format.bitarray.BitArrayVersion;
import org.powernukkitx.level.format.palette.Palette;
import org.powernukkitx.level.structure.AabbVolumes;
import org.powernukkitx.level.structure.spawn.SpawnCategory;
import org.powernukkitx.level.structure.spawn.SpawnOverrideState;
import org.powernukkitx.level.structure.spawn.StructureSpawnHandler;
import org.powernukkitx.level.structure.spawn.StructureSpawnerData;
import org.powernukkitx.level.structure.spawn.StructureSpawnerEntry;
import org.powernukkitx.math.BlockVector3;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.registry.Registries;
import org.powernukkitx.scheduler.BlockUpdateScheduler;
import org.powernukkitx.scheduler.RandomBlockUpdateScheduler;
import org.powernukkitx.utils.Utils;
import org.powernukkitx.utils.collection.nb.Long2ObjectNonBlockingMap;
import com.google.common.base.Preconditions;

import org.jetbrains.annotations.ApiStatus;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.StampedLock;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Cool_Loong
 */
@Slf4j
public class Chunk implements IChunk {
    private static final StructureSpawnHandler STRUCTURE_SPAWN_HANDLER = new StructureSpawnHandler(Registries.STRUCTURE_SPAWN_OVERRIDE);
    private static final String ILLAGER_CAPTAIN_EVENT = "minecraft:spawn_as_illager_captain";

    private volatile int x;
    private volatile int z;
    private volatile long hash;
    protected final AtomicReference<ChunkFinalizationState> finalizationState;
    protected final AtomicReference<ChunkGenerationState> generationState;
    protected final AtomicReference<ChunkLightingState> lightingState;
    protected final ChunkSection[] sections;
    protected final Palette<Integer>[] biomeSections;
    protected final short[] heightMap; // 256 size Values start at 0 and are 0-384 for the Overworld range
    protected final short[] renderHeightMap;
    protected final short[] rainHeightMap;
    protected final AtomicLong changes;

    protected final Long2ObjectNonBlockingMap<Entity> entities;
    /**
     * Live entity count. The non-blocking map computes size()/isEmpty() by summing a
     * striped counter, which is too slow for the per-chunk-per-tick emptiness checks
     * in {@code Level.tickChunks}.
     */
    protected final AtomicInteger entityCount = new AtomicInteger();
    protected final Long2ObjectNonBlockingMap<BlockEntity> tiles;//block entity id -> block entity
    protected final Long2ObjectNonBlockingMap<BlockEntity> tileList;//block entity position hash index -> block entity
    private static final int SNOW_RANDOM_INITIAL = 42184323;
    private static final int SNOW_RANDOM_ADDEND = 1013904223;

    protected final BlockUpdateScheduler blockUpdateScheduler;
    protected final RandomBlockUpdateScheduler randomBlockUpdateScheduler;
    private final AtomicInteger snowRandomValue = new AtomicInteger(SNOW_RANDOM_INITIAL);
    //delay load block entity and entity
    protected CompoundTag extraData;
    protected AabbVolumes aabbVolumes;
    protected BiomeState biomeState;
    protected LevelChunkMetaData levelChunkMetaData;
    private volatile DensityCommon.ChunkCache densityChunkCache;
    protected final StampedLock blockLock;
    protected final StampedLock heightAndBiomeLock;
    protected final StampedLock lightLock;
    protected final LevelProvider provider;
    protected volatile boolean isInit;
    private volatile boolean available;
    private volatile boolean discarded;
    private volatile boolean storageResolved = true;
    protected boolean isInitializing;
    protected List<CompoundTag> blockEntityNBT;
    protected List<CompoundTag> entityNBT;

    private final boolean[] borderBlockMap;

    @SuppressWarnings("unchecked")
    private static Palette<Integer>[] createBiomeSections(int sectionCount) {
        Palette<Integer>[] biomeSections = (Palette<Integer>[]) new Palette<?>[sectionCount];

        for (int i = 0; i < sectionCount; i++) {
            biomeSections[i] = new Palette<>(BiomeID.PLAINS, BitArrayVersion.V0);
        }

        return biomeSections;
    }

    private Chunk(final int chunkX, final int chunkZ, final LevelProvider levelProvider) {
        this.finalizationState = new AtomicReference<>(ChunkFinalizationState.NEEDS_INSTATICKING);
        this.generationState = new AtomicReference<>(ChunkGenerationState.NEEDS_GENERATION);
        this.lightingState = new AtomicReference<>(ChunkLightingState.NEEDS_LIGHTING);

        this.x = chunkX;
        setZ(chunkZ);
        this.provider = levelProvider;
        this.sections = new ChunkSection[levelProvider.getDimensionData().getChunkSectionCount()];
        this.biomeSections = createBiomeSections(levelProvider.getDimensionData().getChunkSectionCount());
        this.heightMap = new short[256];
        this.renderHeightMap = new short[256];
        this.rainHeightMap = ChunkRainHeightMap.create();
        this.entities = new Long2ObjectNonBlockingMap<>();
        this.tiles = new Long2ObjectNonBlockingMap<>();
        this.tileList = new Long2ObjectNonBlockingMap<>();
        this.blockUpdateScheduler = new BlockUpdateScheduler(this, levelProvider.getCurrentTick());
        this.randomBlockUpdateScheduler = new RandomBlockUpdateScheduler(this, 0);
        this.entityNBT = new ArrayList<>();
        this.blockEntityNBT = new ArrayList<>();
        this.extraData = new CompoundTag();
        this.aabbVolumes = AabbVolumes.empty();
        this.biomeState = new BiomeState();
        this.levelChunkMetaData = LevelChunkMetaData.uninitialized();
        this.borderBlockMap = new boolean[256];
        this.changes = new AtomicLong();
        this.blockLock = new StampedLock();
        this.heightAndBiomeLock = new StampedLock();
        this.lightLock = new StampedLock();
    }

    private Chunk(
            final ChunkFinalizationState finalizationState,
            final int chunkX,
            final int chunkZ,
            final LevelProvider levelProvider,
            final ChunkSection[] sections,
            final Palette<Integer>[] biomeSections,
            final short[] heightMap,
            final List<CompoundTag> entityNBT,
            final List<CompoundTag> blockEntityNBT,
            final CompoundTag extraData,
            final AabbVolumes aabbVolumes,
            final BiomeState biomeState,
            final LevelChunkMetaData levelChunkMetaData,
            final boolean[] borderBlockMap
    ) {
        this.finalizationState = new AtomicReference<>(finalizationState);
        this.generationState = new AtomicReference<>(ChunkGenerationState.NEEDS_GENERATION);
        this.lightingState = new AtomicReference<>(ChunkLightingState.NEEDS_LIGHTING);
        this.x = chunkX;
        setZ(chunkZ);
        this.provider = levelProvider;
        this.sections = sections;
        this.biomeSections = biomeSections;
        this.heightMap = heightMap;
        this.renderHeightMap = new short[256];
        this.rainHeightMap = ChunkRainHeightMap.create();
        this.entities = new Long2ObjectNonBlockingMap<>();
        this.tiles = new Long2ObjectNonBlockingMap<>();
        this.tileList = new Long2ObjectNonBlockingMap<>();
        this.blockUpdateScheduler = new BlockUpdateScheduler(this, levelProvider.getCurrentTick());
        this.randomBlockUpdateScheduler = new RandomBlockUpdateScheduler(this, 0);
        this.entityNBT = entityNBT;
        this.blockEntityNBT = blockEntityNBT;
        this.extraData = extraData;
        this.aabbVolumes = aabbVolumes;
        this.biomeState = biomeState;
        this.levelChunkMetaData = levelChunkMetaData;
        this.borderBlockMap = borderBlockMap;
        this.changes = new AtomicLong();
        this.blockLock = new StampedLock();
        this.heightAndBiomeLock = new StampedLock();
        this.lightLock = new StampedLock();

        new UnsafeChunk(this).recalculateRenderHeightMap();
    }

    @Override
    public boolean isSectionEmpty(int fY) {
        ChunkSection section = this.getSection(fY - getDimensionData().getMinSectionY());
        return section == null || section.isEmpty();
    }

    public DensityCommon.ChunkCache getOrCreateDensityChunkCache() {
        DensityCommon.ChunkCache cache = this.densityChunkCache;
        if (cache == null) {
            synchronized (this) {
                cache = this.densityChunkCache;
                if (cache == null) {
                    cache = new DensityCommon.ChunkCache();
                    this.densityChunkCache = cache;
                }
            }
        }
        return cache;
    }

    public void releaseDensityChunkCache() {
        DensityCommon.ChunkCache cache = this.densityChunkCache;
        this.densityChunkCache = null;
        if (cache != null) {
            cache.clear();
        }
    }

    @Override
    public ChunkSection getSection(int fY) {
        long stamp = blockLock.tryOptimisticRead();
        try {
            for (; ; stamp = blockLock.readLock()) {
                if (stamp == 0L) continue;
                ChunkSection section = this.sections[fY - getDimensionData().getMinSectionY()];
                if (!blockLock.validate(stamp)) continue;
                return section;
            }
        } finally {
            if (StampedLock.isReadLockStamp(stamp)) blockLock.unlockRead(stamp);
        }
    }

    private ChunkSection getSectionInternal(int fY) {
        return this.sections[fY - getDimensionData().getMinSectionY()];
    }

    /**
     * Returns or creates the section required by the lighting pipeline.
     *
     * @param fY section Y
     * @return chunk section, or {@code null} when outside the dimension range
     */
    @ApiStatus.Internal
    public ChunkSection getOrCreateSectionForLighting(int fY) {
        long blockStamp = blockLock.writeLock();
        long biomeStamp = heightAndBiomeLock.readLock();

        try {
            int index = fY - getDimensionData().getMinSectionY();

            if (index < 0 || index >= sections.length) return null;

            ChunkSection section = sections[index];

            if (section == null) {
                section = new ChunkSection((byte) fY, biomeSections[index]);
                sections[index] = section;
            }

            return section;
        } finally {
            heightAndBiomeLock.unlockRead(biomeStamp);
            blockLock.unlockWrite(blockStamp);
        }
    }

    @Override
    @ApiStatus.Internal
    public ChunkGenerationState getGenerationState() {
        return generationState.get();
    }

    /**
     * Atomically transitions the transient generation state.
     *
     * @param expected expected current state
     * @param state replacement state
     * @return whether the state was changed
     */
    @ApiStatus.Internal
    public boolean compareAndSetGenerationState(ChunkGenerationState expected, ChunkGenerationState state) {
        return generationState.compareAndSet(expected, state);
    }

    /**
     * Returns whether persisted chunk data has been resolved.
     *
     * @return whether storage resolution has completed
     */
    @ApiStatus.Internal
    public boolean isStorageResolved() {
        return storageResolved;
    }

    /**
     * Marks this chunk as awaiting persisted-data resolution.
     */
    @ApiStatus.Internal
    public void markStoragePending() {
        Preconditions.checkState(!this.isInit, "Cannot mark an initialized chunk as storage-pending");
        this.storageResolved = false;
    }

    /**
     * Marks persisted-data resolution as complete.
     */
    @ApiStatus.Internal
    public void markStorageResolved() {
        this.storageResolved = true;
    }

    /**
     * Applies persisted data from a detached chunk while preserving this chunk's runtime identity and generation state.
     *
     * @param source loaded persisted chunk
     */
    @ApiStatus.Internal
    public void applyPersistentData(Chunk source) {
        Preconditions.checkNotNull(source);
        Preconditions.checkArgument(source != this, "Source chunk must be detached");
        Preconditions.checkArgument(source.provider == this.provider, "Chunk provider does not match");
        Preconditions.checkArgument(source.x == this.x && source.z == this.z, "Chunk position does not match");
        Preconditions.checkState(!this.isInit, "Cannot hydrate an initialized chunk");
        Preconditions.checkState(!this.storageResolved, "Chunk storage is already resolved");

        Preconditions.checkState(source.sections.length == this.sections.length, "Chunk section count does not match");
        Preconditions.checkState(
                source.biomeSections.length == this.biomeSections.length,
                "Chunk biome section count does not match"
        );

        long blockStamp = blockLock.writeLock();
        long heightAndBiomeStamp = heightAndBiomeLock.writeLock();
        long lightStamp = lightLock.writeLock();

        try {
            System.arraycopy(source.sections, 0, this.sections, 0, this.sections.length);
            System.arraycopy(source.biomeSections, 0, this.biomeSections, 0, this.biomeSections.length);
            System.arraycopy(source.heightMap, 0, this.heightMap, 0, this.heightMap.length);
            System.arraycopy(source.renderHeightMap, 0, this.renderHeightMap, 0, this.renderHeightMap.length);
            System.arraycopy(source.rainHeightMap, 0, this.rainHeightMap, 0, this.rainHeightMap.length);
            System.arraycopy(source.borderBlockMap, 0, this.borderBlockMap, 0, this.borderBlockMap.length);

            this.finalizationState.set(source.finalizationState.get());
            this.lightingState.set(source.lightingState.get());
            this.entityNBT = source.entityNBT;
            this.blockEntityNBT = source.blockEntityNBT;
            this.extraData = source.extraData;
            this.aabbVolumes = source.aabbVolumes;
            this.biomeState = source.biomeState;
            this.levelChunkMetaData = source.levelChunkMetaData;
            this.densityChunkCache = null;
            this.changes.set(0);
            this.storageResolved = true;
        } finally {
            lightLock.unlockWrite(lightStamp);
            heightAndBiomeLock.unlockWrite(heightAndBiomeStamp);
            blockLock.unlockWrite(blockStamp);
        }
    }

    /**
     * Returns whether transient generation has completed.
     *
     * @return whether generation is complete
     */
    @ApiStatus.Internal
    public boolean isGenerationComplete() {
        return generationState.get() == ChunkGenerationState.COMPLETE;
    }

    /**
     * Returns whether this chunk is admitted to the active runtime lifecycle.
     *
     * @return whether the chunk is available
     */
    @ApiStatus.Internal
    public boolean isAvailable() {
        return available && !discarded;
    }

    /**
     * Updates this chunk's runtime availability.
     *
     * @param available availability state
     */
    @ApiStatus.Internal
    public synchronized void setAvailable(boolean available) {
        if (available && discarded) return;
        this.available = available;
    }

    /**
     * Returns whether this chunk has entered unload processing.
     *
     * @return whether the chunk is being discarded
     */
    @ApiStatus.Internal
    public boolean isDiscarded() {
        return discarded;
    }

    /**
     * Returns the transient lighting state.
     *
     * @return lighting state
     */
    @ApiStatus.Internal
    public ChunkLightingState getLightingState() {
        return lightingState.get();
    }

    /**
     * Atomically transitions the transient lighting state.
     *
     * @param expected expected current state
     * @param state replacement state
     * @return whether the state was changed
     */
    @ApiStatus.Internal
    public boolean compareAndSetLightingState(ChunkLightingState expected, ChunkLightingState state) {
        return lightingState.compareAndSet(expected, state);
    }

    /**
     * Returns whether the chunk is waiting for lighting.
     *
     * @return whether lighting is required
     */
    @ApiStatus.Internal
    public boolean needsLighting() {
        return lightingState.get() == ChunkLightingState.NEEDS_LIGHTING;
    }

    /**
     * Returns whether the chunk is currently being lit.
     *
     * @return whether lighting is in progress
     */
    @ApiStatus.Internal
    public boolean isLighting() {
        return lightingState.get() == ChunkLightingState.LIGHTING;
    }

    /**
     * Returns whether lighting computation has finished.
     *
     * @return whether lighting computation is finished
     */
    @ApiStatus.Internal
    public boolean isLightingFinished() {
        return lightingState.get() == ChunkLightingState.LIGHTING_FINISHED;
    }

    /**
     * Returns whether lighting is fully loaded and ready for use.
     *
     * @return whether lighting is ready
     */
    @ApiStatus.Internal
    public boolean isLightingReady() {
        return lightingState.get() == ChunkLightingState.LOADED;
    }

    @Override
    public void setSection(int fY, ChunkSection section) {
        long blockStamp = blockLock.writeLock();
        long biomeStamp = heightAndBiomeLock.writeLock();
        try {
            int index = fY - getDimensionData().getMinSectionY();
            this.sections[index] = section;

            if (section != null) {
                this.biomeSections[index] = section.biomes();
            }

            ChunkRainHeightMap.invalidateAll(this);
            setChanged();
        } finally {
            heightAndBiomeLock.unlockWrite(biomeStamp);
            blockLock.unlockWrite(blockStamp);
        }
    }

    @Override
    @ApiStatus.Internal
    public ChunkSection[] getSections() {
        long stamp = blockLock.readLock();
        try {
            return this.sections;
        } finally {
            blockLock.unlockRead(stamp);
        }
    }

    @Override
    @ApiStatus.Internal
    public Palette<Integer>[] getBiomeSections() {
        long stamp = heightAndBiomeLock.readLock();
        try {
            return this.biomeSections;
        } finally {
            heightAndBiomeLock.unlockRead(stamp);
        }
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public void setX(int x) {
        this.x = x;
        this.hash = Level.chunkHash(x, getZ());
    }

    @Override
    public int getZ() {
        return z;
    }

    @Override
    public void setZ(int z) {
        this.z = z;
        this.hash = Level.chunkHash(getX(), z);
    }

    @Override
    public final long getIndex() {
        return this.hash;
    }

    @Override
    public LevelProvider getProvider() {
        return provider;
    }

    @Override
    public Level getLevel() {
        return getProvider().getLevel();
    }

    @Override
    public BlockState getBlockState(int x, int y, int z, int layer) {
        long stamp = blockLock.tryOptimisticRead();
        try {
            for (; ; stamp = blockLock.readLock()) {
                if (stamp == 0L) continue;
                BlockState result = getBlockStateInternal(x, y, z, layer);
                if (!blockLock.validate(stamp)) continue;
                return result;
            }
        } finally {
            if (StampedLock.isReadLockStamp(stamp)) blockLock.unlockRead(stamp);
        }
    }

    private BlockState getBlockStateInternal(int x, int y, int z, int layer) {
        ChunkSection sectionInternal = getSectionInternal(y >> 4);
        return sectionInternal == null ? BlockAir.STATE : sectionInternal.getBlockState(x, y & 0x0f, z, layer);
    }

    @Override
    @ApiStatus.Internal
    public <T> T readBlockStates(Function<BlockStateReader, T> action) {
        long stamp = blockLock.readLock();
        try {
            return action.apply(this::getBlockStateInternal);
        } finally {
            blockLock.unlockRead(stamp);
        }
    }

    /**
     * Returns packed combined block-light properties for a block position.
     *
     * @param x local X
     * @param y world Y
     * @param z local Z
     * @return packed light properties
     */
    @ApiStatus.Internal
    public int getCombinedLightProperties(int x, int y, int z) {
        long stamp = blockLock.tryOptimisticRead();

        try {
            for (; ; stamp = blockLock.readLock()) {
                if (stamp == 0L) continue;

                ChunkSection sectionInternal = getSectionInternal(y >> 4);

                if (sectionInternal == null) {
                    if (!blockLock.validate(stamp)) continue;
                    return 0;
                }

                int result = sectionInternal.getCombinedLightProperties(x, y & 0x0f, z);
                if (!blockLock.validate(stamp)) continue;

                return result;
            }
        } finally {
            if (StampedLock.isReadLockStamp(stamp)) {
                blockLock.unlockRead(stamp);
            }
        }
    }

    @Override
    public BlockState getAndSetBlockState(int x, int y, int z, BlockState blockstate, int layer) {
        long blockStamp = blockLock.writeLock();
        long heightAndBiomeStamp = 0L;

        try {
            setChanged();
            ChunkSection section = getOrCreateSection(y >> 4);
            int localY = y & 0x0f;
            BlockState oldState = section.getBlockState(x, localY, z, layer);
            int oldHeightMask = 0;
            int newHeightMask = 0;

            /*
             * Terrain generation owns its heightmap while the chunk is still NEEDS_INSTATICKING.
             * Once GeneratedStage has run, population/runtime mutations keep the cached heightmaps incrementally synchronized.
             */
            if (oldState != blockstate && finalizationState.get() != ChunkFinalizationState.NEEDS_INSTATICKING) {
                long heightMasks = ChunkHeightMap.cellMasksWithStateChange(section, x, localY, z, layer, oldState, blockstate);
                oldHeightMask = (int) (heightMasks >>> 32);
                newHeightMask = (int) heightMasks;

                if (oldHeightMask != newHeightMask) {
                    heightAndBiomeStamp = heightAndBiomeLock.writeLock();
                }
            }

            section.setBlockState(x, localY, z, blockstate, layer, oldState);

            if (oldState != blockstate) {
                ChunkRainHeightMap.invalidateAfterBlockChange(this, x, y, z);
            }

            if (heightAndBiomeStamp != 0L) {
                ChunkHeightMap.updateAfterMaskChange(this, x, y, z, oldHeightMask, newHeightMask);
            }

            updateBorderBlockMap(x, z, oldState, blockstate);

            return oldState;
        } finally {
            if (heightAndBiomeStamp != 0L) {
                heightAndBiomeLock.unlockWrite(heightAndBiomeStamp);
            }

            blockLock.unlockWrite(blockStamp);
            removeInvalidTile(x, y, z);
        }
    }

    @Override
    public void setBlockState(int x, int y, int z, BlockState blockstate, int layer) {
        long blockStamp = blockLock.writeLock();
        long heightAndBiomeStamp = 0L;

        try {
            setChanged();
            ChunkSection section = getOrCreateSection(y >> 4);
            int localY = y & 0x0f;
            BlockState oldState = section.getBlockState(x, localY, z, layer);
            int oldHeightMask = 0;
            int newHeightMask = 0;

            if (oldState != blockstate && finalizationState.get() != ChunkFinalizationState.NEEDS_INSTATICKING) {
                oldHeightMask = ChunkHeightMap.cellMask(section, x, localY, z);
                newHeightMask = ChunkHeightMap.cellMaskWithState(section, x, localY, z, layer, blockstate);

                if (oldHeightMask != newHeightMask) {
                    heightAndBiomeStamp = heightAndBiomeLock.writeLock();
                }
            }

            section.setBlockState(x, localY, z, blockstate, layer, oldState);

            if (oldState != blockstate) {
                ChunkRainHeightMap.invalidateAfterBlockChange(this, x, y, z);
            }

            if (heightAndBiomeStamp != 0L) {
                ChunkHeightMap.updateAfterMaskChange(this, x, y, z, oldHeightMask, newHeightMask);
            }

            updateBorderBlockMap(x, z, oldState, blockstate);
        } finally {
            if (heightAndBiomeStamp != 0L) {
                heightAndBiomeLock.unlockWrite(heightAndBiomeStamp);
            }

            blockLock.unlockWrite(blockStamp);
            removeInvalidTile(x, y, z);
        }
    }

    @Override
    public int getBlockSkyLight(int x, int y, int z) {
        long stamp = lightLock.tryOptimisticRead();

        try {
            for (; ; stamp = lightLock.readLock()) {
                if (stamp == 0L) continue;

                ChunkSection sectionInternal = getSectionInternal(y >> 4);

                if (sectionInternal != null) {
                    int result = sectionInternal.getBlockSkyLight(x, y & 0x0f, z);
                    if (!lightLock.validate(stamp)) continue;
                    return result;
                }

                if (!lightLock.validate(stamp)) continue;

                break;
            }
        } finally {
            if (StampedLock.isReadLockStamp(stamp)) {
                lightLock.unlockRead(stamp);
            }
        }

        if (!isOverWorld()) return 0;

        int minHeight = getDimensionData().getMinHeight();
        int height = getHeightMap(x, z);

        return height == minHeight || y >= height ? 15 : 0;
    }

    @Override
    public void setBlockSkyLight(int x, int y, int z, int level) {
        long stamp = lightLock.writeLock();
        try {
            setChanged();
            getOrCreateSection(y >> 4).setBlockSkyLight(x, y & 0x0f, z, (byte) level);
        } finally {
            lightLock.unlockWrite(stamp);
        }
    }

    @Override
    public int getBlockLight(int x, int y, int z) {
        long stamp = lightLock.tryOptimisticRead();
        try {
            for (; ; stamp = lightLock.readLock()) {
                if (stamp == 0L) continue;
                ChunkSection sectionInternal = getSectionInternal(y >> 4);
                if (sectionInternal == null) return 0;
                int result = sectionInternal.getBlockLight(x, y & 0x0f, z);
                if (!lightLock.validate(stamp)) continue;
                return result;
            }
        } finally {
            if (StampedLock.isReadLockStamp(stamp)) lightLock.unlockRead(stamp);
        }
    }

    @Override
    public void setBlockLight(int x, int y, int z, int level) {
        long stamp = lightLock.writeLock();
        try {
            setChanged();
            getOrCreateSection(y >> 4).setBlockLight(x, y & 0x0f, z, (byte) level);
        } finally {
            lightLock.unlockWrite(stamp);
        }
    }

    @Override
    public int getRainHeight(int x, int z) {
        long stamp = blockLock.writeLock();
        try {
            return ChunkRainHeightMap.get(this, x, z);
        } finally {
            blockLock.unlockWrite(stamp);
        }
    }

    @Override
    public int getHeightMap(int x, int z) {
        long stamp = heightAndBiomeLock.tryOptimisticRead();
        try {
            for (; ; stamp = heightAndBiomeLock.readLock()) {
                if (stamp == 0L) continue;
                int result = this.heightMap[(z << 4) | x] + getDimensionData().getMinHeight();
                if (!heightAndBiomeLock.validate(stamp)) continue;
                return result;
            }
        } finally {
            if (StampedLock.isReadLockStamp(stamp)) heightAndBiomeLock.unlockRead(stamp);
        }
    }

    @Override
    public void setHeightMap(int x, int z, int value) {
        //Bedrock Edition 3d-data saves the height map start from index of 0, so need to subtract the world minimum height here, see for details:
        //https://github.com/bedrock-dev/bedrock-level/blob/main/src/include/data_3d.h#L115
        long stamp = heightAndBiomeLock.writeLock();
        try {
            this.heightMap[(z << 4) | x] = (short) (value - getDimensionData().getMinHeight());
        } finally {
            heightAndBiomeLock.unlockWrite(stamp);
        }
    }

    @Override
    public int getRenderHeightMap(int x, int z) {
        long stamp = heightAndBiomeLock.tryOptimisticRead();
        try {
            for (; ; stamp = heightAndBiomeLock.readLock()) {
                if (stamp == 0L) continue;
                int result = this.renderHeightMap[(z << 4) | x] + getDimensionData().getMinHeight();
                if (!heightAndBiomeLock.validate(stamp)) continue;
                return result;
            }
        } finally {
            if (StampedLock.isReadLockStamp(stamp)) heightAndBiomeLock.unlockRead(stamp);
        }
    }

    @Override
    public void setRenderHeightMap(int x, int z, int value) {
        long stamp = heightAndBiomeLock.writeLock();
        try {
            this.renderHeightMap[(z << 4) | x] = (short) (value - getDimensionData().getMinHeight());
        } finally {
            heightAndBiomeLock.unlockWrite(stamp);
        }
    }

    @Override
    public void recalculateHeightMap() {
        long blockStamp = blockLock.readLock();
        long heightAndBiomeStamp = heightAndBiomeLock.writeLock();

        try {
            ChunkHeightMap.recalculateAll(this);
        } finally {
            heightAndBiomeLock.unlockWrite(heightAndBiomeStamp);
            blockLock.unlockRead(blockStamp);
        }
    }

    @Override
    public int recalculateHeightMapColumn(int x, int z) {
        long blockStamp = blockLock.readLock();
        long heightAndBiomeStamp = heightAndBiomeLock.writeLock();

        try {
            return ChunkHeightMap.recalculateColumn(this, x, z);
        } finally {
            heightAndBiomeLock.unlockWrite(heightAndBiomeStamp);
            blockLock.unlockRead(blockStamp);
        }
    }

    @Deprecated(since = "3.1.0", forRemoval = true)
    @Override
    public void populateSkyLight() {
        batchProcess(unsafe -> {
            // basic light calculation; properties are read from the shared precomputed table (no per-cell Block alloc)
            for (int z = 0; z < 16; ++z) {
                for (int x = 0; x < 16; ++x) { // iterating over all columns in chunk
                    int level = 15;
                    for (int y = getDimensionData().getMaxHeight(); y >= getDimensionData().getMinHeight(); y--) {
                        BlockState state = unsafe.getBlockState(x, y, z);
                        int packed = BlockLightProperties.packed(state);
                        if (!BlockLightProperties.isTransparent(packed)) {
                            level = 0;
                        } else if (BlockLightProperties.diffusesSkyLight(packed)) {
                            level--;
                        } else {
                            level -= BlockLightProperties.lightLevel(packed);
                        }
                        if (level <= 0) break;
                        unsafe.setBlockSkyLight(x, y, z, level);
                    }
                }
            }
        });
    }

    public void batchProcess(Consumer<UnsafeChunk> unsafeChunkConsumer) {
        long stamp1 = blockLock.writeLock();
        long stamp2 = heightAndBiomeLock.writeLock();
        long stamp3 = lightLock.writeLock();
        try {
            unsafeChunkConsumer.accept(new UnsafeChunk(this));
        } catch (Exception e) {
            e.printStackTrace();
            log.error("An error occurred while executing chunk batch operation", e);
        } finally {
            blockLock.unlockWrite(stamp1);
            heightAndBiomeLock.unlockWrite(stamp2);
            lightLock.unlockWrite(stamp3);
        }
    }

    @Override
    public int getBiomeId(int x, int y, int z) {
        long stamp = heightAndBiomeLock.tryOptimisticRead();
        try {
            for (; ; stamp = heightAndBiomeLock.readLock()) {
                if (stamp == 0L) continue;

                int sectionIndex = (y >> 4) - getDimensionData().getMinSectionY();
                if (sectionIndex < 0 || sectionIndex >= biomeSections.length) {
                    return BiomeID.PLAINS;
                }

                int result = biomeSections[sectionIndex].get(IChunk.index(x, y & 0x0f, z));
                if (!heightAndBiomeLock.validate(stamp)) continue;
                return result;
            }
        } finally {
            if (StampedLock.isReadLockStamp(stamp)) heightAndBiomeLock.unlockRead(stamp);
        }
    }

    @Override
    public void setBiomeId(int x, int y, int z, int biomeId) {
        long stamp = heightAndBiomeLock.writeLock();
        try {
            int sectionIndex = (y >> 4) - getDimensionData().getMinSectionY();
            if (sectionIndex < 0 || sectionIndex >= biomeSections.length) return;

            setChanged();
            biomeSections[sectionIndex].set(IChunk.index(x, y & 0x0f, z), ChunkSection.boxBiomeId(biomeId));
            biomeState.updateBiome(biomeId);
        } finally {
            heightAndBiomeLock.unlockWrite(stamp);
        }
    }

    @Override
    public boolean isLightPopulated() {
        return isLightingReady();
    }

    @Deprecated(since = "3.1.0", forRemoval = true)
    @Override
    public void setLightPopulated(boolean value) {
        lightingState.set(value ? ChunkLightingState.LOADED : ChunkLightingState.NEEDS_LIGHTING);
    }

    @Deprecated(since = "3.1.0", forRemoval = true)
    @Override
    public void setLightPopulated() {
        setLightPopulated(true);
    }

    @Override
    public ChunkFinalizationState getFinalizationState() {
        return this.finalizationState.get();
    }

    @Override
    public void setFinalizationState(ChunkFinalizationState finalizationState) {
        this.finalizationState.set(finalizationState);
    }

    @Override
    public void addEntity(Entity entity) {
        if (this.entities.put(entity.runtimeId(), entity) == null) {
            this.entityCount.incrementAndGet();
        }
        if (!(entity instanceof Player) && this.isInit) {
            this.setChanged();
        }
    }

    @Override
    public void removeEntity(Entity entity) {
        if (entity.runtimeId() < 0) return;
        if (this.entities != null) {
            synchronized (this.entities) {
                if (this.entities.remove(entity.runtimeId()) != null) {
                    this.entityCount.decrementAndGet();
                }
                if (!(entity instanceof Player) && this.isInit) {
                    this.setChanged();
                }
            }
        }
    }

    @Override
    public boolean hasEntities() {
        return this.entityCount.get() > 0;
    }

    @Override
    public void addBlockEntity(BlockEntity blockEntity) {
        this.tiles.put(blockEntity.getId(), blockEntity);
        int index = ((blockEntity.getFloorZ() & 0x0f) << 16) | ((blockEntity.getFloorX() & 0x0f) << 12) | (ensureY(blockEntity.getFloorY()) + 64);
        BlockEntity existing = this.tileList.put(index, blockEntity);
        if (existing != null && !existing.equals(blockEntity)) {
            this.tiles.remove(existing.getId());
            existing.close();
        }
        if (this.isInit) {
            this.setChanged();
        }
    }

    @Override
    public void removeBlockEntity(BlockEntity blockEntity) {
        if (this.tiles != null) {
            this.tiles.remove(blockEntity.getId());
            int index = ((blockEntity.getFloorZ() & 0x0f) << 16) | ((blockEntity.getFloorX() & 0x0f) << 12) | (ensureY(blockEntity.getFloorY()) + 64);
            this.tileList.remove(index, blockEntity);
            if (this.isInit) {
                this.setChanged();
            }
        }
    }

    @Override
    public Map<Long, Entity> getEntities() {
        return entities;
    }

    @Override
    public void doMobSpawning() {
        Level level = getProvider().getLevel();
        if (!isLoaded() || getFinalizationState() != ChunkFinalizationState.DONE || !isLightPopulated()) return;

        // Structure-spawn cadence: Random::nextInt(2000) <= 10.
        if (Utils.rand(0, 1999) <= 10) {
            tryStructureMobSpawning(level);
        }

        if (Utils.rand(0, 50) != 0) return;

        var chunkEntities = level.getChunkEntities(getX(), getZ());
        Collection<Entity> spawnedEntities = chunkEntities.values().stream().filter(
                e -> e.despawnable && e.isAlive() && !e.closed
        ).toList();

        // Spawning
        if (Utils.rand(0, 4) == 0 && spawnedEntities.size() < Server.getInstance().getSettings().chunkSettings().spawnLimit()) {
            int x = Utils.rand(0, 16);
            int z = Utils.rand(0, 16);
            int chunkX = getX(), chunkZ = getZ();
            int absX = (chunkX << 4) + x;
            int absZ = (chunkZ << 4) + z;

            int spawnedEntityCount = spawnedEntities.size();
            int maxEntityCount = Server.getInstance().getSettings().chunkSettings().spawnLimit();

            DimensionData data = getDimensionData();
            SpawnRule[] spawnRules = Registries.ENTITY.getSpawnRules().toArray(new SpawnRule[0]);

            Vector3 lookVec = new Vector3();
            for (int y = data.getMaxHeight(); y > data.getMinHeight(); y--) {
                lookVec.setComponents(absX, y, absZ);
                Block block = level.getBlock(lookVec, true);

                List<SpawnRule> applicableRules = null;
                for (SpawnRule rule : spawnRules) {
                    if (rule.evaluate(block)) {
                        if (applicableRules == null) applicableRules = new ArrayList<>();
                        applicableRules.add(rule);
                    }
                }

                if (applicableRules != null && !applicableRules.isEmpty()) {
                    if (!isMobSpawnPlayerDistanceAllowed(level, absX, y, absZ)) continue;

                    int totalWeight = 0;
                    for (SpawnRule rule : applicableRules) {
                        totalWeight += rule.getWeight();
                    }

                    int selectedWeight = Utils.rand(1, totalWeight);
                    SpawnRule spawnRule = applicableRules.get(applicableRules.size() - 1);
                    for (SpawnRule rule : applicableRules) {
                        selectedWeight -= rule.getWeight();
                        if (selectedWeight <= 0) {
                            spawnRule = rule;
                            break;
                        }
                    }

                    int herd = Utils.rand(spawnRule.getHerdMin(), spawnRule.getHerdMax());

                    for (int i = 0; i < herd; i++) {
                        Vector3 spawnPos = lookVec;
                        if (!EntityFlyable.class.isAssignableFrom(Registries.ENTITY.getEntityClass(spawnRule.getEntityId()))) {
                            Vector3 scattered = i == 0 ? lookVec : lookVec.add(Utils.rand(-4, 4), 0, Utils.rand(-4, 4));
                            Vector3 safe = level.getSafeSpawn(scattered, 2, true);
                            if (safe == null || safe.distanceSquared(lookVec) > 64) {
                                safe = level.getSafeSpawn(lookVec, 1, true);
                            }
                            if (safe == null) {
                                continue;
                            }
                            spawnPos = safe.add(Utils.rand(0.3, 0.7), 0, Utils.rand(0.3, 0.7));
                        }
                        if (spawnedEntityCount >= maxEntityCount) {
                            break;
                        }

                        Entity entity = Registries.ENTITY.provideEntity(spawnRule.getEntityId(), this, Entity.getDefaultNBT(spawnPos));
                        if (entity != null) {
                            spawnedEntityCount++;
                            entity.getNbt().putString("SpawnReason", "NATURAL");
                            entity.despawnable = true;
                            entity.spawnToAll();
                        }
                    }
                }
            }
        }
    }

    private void tryStructureMobSpawning(Level level) {
        if (aabbVolumes.isEmpty()) return;

        int localX = Utils.rand(0, 15);
        int localZ = Utils.rand(0, 15);
        int absX = (getX() << 4) + localX;
        int absZ = (getZ() << 4) + localZ;

        DimensionData dimension = getDimensionData();
        boolean surfacePass = true;
        for (int supportY = dimension.getMaxHeight() - 1; supportY >= dimension.getMinHeight(); supportY--) {
            Block support = level.getBlock(absX, supportY, absZ, false);
            if (!isStructureSpawnSupport(support)) continue;

            int spawnY = supportY + 1;
            Block spawnBlock = level.getBlock(absX, spawnY, absZ, false);
            Block headBlock = level.getBlock(absX, spawnY + 1, absZ, false);
            if (spawnBlock.isSolid() || headBlock.isSolid()) continue;

            boolean underwater = isStructureSpawnWater(spawnBlock);
            boolean surface = !underwater && surfacePass;
            surfacePass = false;

            BlockVector3 blockPosition = new BlockVector3(absX, spawnY, absZ);
            List<StructureSpawnerEntry> entries = findStructureSpawnerEntries(blockPosition);
            if (entries == null) continue;

            // A present but empty structure override suppresses this structure spawn category.
            if (entries.size() == 0) return;
            if (!isMobSpawnPlayerDistanceAllowed(level, absX, spawnY, absZ)) continue;

            StructureSpawnerData spawner = selectStructureSpawner(entries);
            if (spawner == null || !isStructureSpawnLocationAllowed(spawner, surface, underwater)) continue;
            if (!isStructureSpawnBrightnessAllowed(level, spawner, absX, spawnY, absZ)) continue;

            double herdRoll = Utils.random.nextDouble();
            int herd = spawner.minCount() + (int) Math.round(herdRoll * herdRoll * (spawner.maxCount() - spawner.minCount()));
            Vector3 spawnPosition = new Vector3(absX + 0.5, spawnY, absZ + 0.5);

            for (int i = 0; i < herd; i++) {
                if (!isStructureSpawnPopulationAllowed(level, spawner, surface)) break;

                CompoundTag nbt = Entity.getDefaultNBT(spawnPosition);
                nbt.putBoolean("NaturalSpawn", true);
                nbt.putBoolean("Surface", surface);
                nbt.putString("SpawnReason", "NATURAL");

                Entity entity = Registries.ENTITY.provideEntity(spawner.entityId(), this, nbt);
                if (entity == null) continue;
                if (!applyStructureSpawnInitializationEvent(entity, spawner.initializationEvent())) {
                    entity.close();
                    continue;
                }

                entity.despawnable = true;
                entity.spawnToAll();
            }
        }
    }

    private static boolean applyStructureSpawnInitializationEvent(Entity entity, String initializationEvent) {
        if (initializationEvent.isEmpty()) return true;

        if (ILLAGER_CAPTAIN_EVENT.equals(initializationEvent) && entity instanceof EntityPillager pillager) {
            pillager.setIllagerCaptain();
            return true;
        }

        return false;
    }

    private List<StructureSpawnerEntry> findStructureSpawnerEntries(BlockVector3 position) {
        return STRUCTURE_SPAWN_HANDLER.findSpawnerEntries(aabbVolumes, position, SpawnCategory.MONSTER);
    }

    private static boolean isMobSpawnPlayerDistanceAllowed(Level level, int x, int y, int z) {
        int chunkTickRadius = level.getChunkTickRadius();
        if (chunkTickRadius < 4) return false;

        int maxDistance = chunkTickRadius == 4 ? 44 : 128;
        double nearestDistanceSquared = Double.MAX_VALUE;

        for (Player player : level.getPlayers().values()) {
            if (!player.isOnline()) continue;

            double dx = player.x - x;
            double dy = player.y - y;
            double dz = player.z - z;
            nearestDistanceSquared = Math.min(nearestDistanceSquared, dx * dx + dy * dy + dz * dz);
        }

        return nearestDistanceSquared >= 24 * 24 && nearestDistanceSquared < maxDistance * maxDistance;
    }

    private static StructureSpawnerData selectStructureSpawner(List<StructureSpawnerEntry> entries) {
        int totalWeight = 0;
        for (StructureSpawnerEntry entry : entries) {
            totalWeight += Math.max(0, entry.data().probabilityWeight());
        }
        if (totalWeight <= 0) return null;

        int selectedWeight = Utils.rand(1, totalWeight);
        for (StructureSpawnerEntry entry : entries) {
            selectedWeight -= Math.max(0, entry.data().probabilityWeight());
            if (selectedWeight <= 0) return entry.data();
        }

        return null;
    }

    private static boolean isStructureSpawnLocationAllowed(StructureSpawnerData spawner, boolean surface, boolean underwater) {
        if (underwater) return spawner.underwater() == SpawnOverrideState.YES;
        return surface ? spawner.surface() == SpawnOverrideState.YES : spawner.underground() == SpawnOverrideState.YES;
    }

    private static boolean isStructureSpawnBrightnessAllowed(Level level, StructureSpawnerData spawner, int x, int y, int z) {
        StructureSpawnerData.Brightness brightness = spawner.brightness();
        if (brightness == null) return true;

        int light = brightness.raw() ? level.getBlockLightAt(x, y, z) : level.getFullLight(new Vector3(x, y, z));

        return light >= brightness.min() && light <= brightness.max();
    }

    private boolean isStructureSpawnPopulationAllowed(Level level, StructureSpawnerData spawner, boolean surface) {
        StructureSpawnerData.Population population = spawner.population();
        if (population == null) return true;

        int limit = surface ? population.surface() : population.underground();
        if (limit <= 0) return false;

        int count = 0;
        for (int chunkX = getX() - 4; chunkX <= getX() + 4; chunkX++) {
            for (int chunkZ = getZ() - 4; chunkZ <= getZ() + 4; chunkZ++) {
                for (Entity entity : level.getChunkEntities(chunkX, chunkZ, false).values()) {
                    if (entity.closed || !entity.isAlive() || !spawner.entityId().equals(entity.getIdentifier())) continue;

                    CompoundTag nbt = entity.getNbt();
                    if (!nbt.getBoolean("NaturalSpawn") || nbt.getBoolean("Surface") != surface) continue;
                    if (entity instanceof EntityPillager pillager && pillager.isIllagerCaptain() != ILLAGER_CAPTAIN_EVENT.equals(spawner.initializationEvent())) {
                        continue;
                    }
                    if (++count >= limit) return false;
                }
            }
        }

        return true;
    }

    private static boolean isStructureSpawnWater(Block block) {
        String id = block.getId();
        return BlockID.WATER.equals(id) || BlockID.FLOWING_WATER.equals(id);
    }

    private static boolean isStructureSpawnSupport(Block block) {
        String id = block.getId();

        if (BlockID.AIR.equals(id) || BlockID.WATER.equals(id) || BlockID.FLOWING_WATER.equals(id) || BlockID.LAVA.equals(id) || BlockID.FLOWING_LAVA.equals(id)) {
            return false;
        }

        return block.isSolid();
    }

    @Override
    public BlockUpdateScheduler getBlockUpdateScheduler() {
        return blockUpdateScheduler;
    }

    @Override
    public RandomBlockUpdateScheduler getRandomBlockUpdateScheduler() {
        return randomBlockUpdateScheduler;
    }

    @Override
    public int nextSnowRandomValue() {
        return snowRandomValue.updateAndGet(value -> value * 3 + SNOW_RANDOM_ADDEND);
    }

    @Override
    public Map<Long, BlockEntity> getBlockEntities() {
        return tiles;
    }

    @Override
    public BlockEntity getTile(int x, int y, int z) {
        return this.tileList.get(((long) z << 16) | ((long) x << 12) | (y + 64));
    }

    @Override
    public boolean isLoaded() {
        return this.getProvider() != null && this.getProvider().isChunkLoaded(this.getX(), this.getZ());
    }

    @Override
    public boolean load() throws IOException {
        return this.load(true);
    }

    @Override
    public boolean load(boolean generate) throws IOException {
        return this.getProvider() != null && this.getProvider().getChunk(this.getX(), this.getZ(), true) != null;
    }

    @Override
    public boolean unload() {
        return this.unload(true, true);
    }

    @Override
    public boolean unload(boolean save) {
        return this.unload(save, true);
    }

    @Override
    public boolean unload(boolean save, boolean safe) {
        LevelProvider provider = this.getProvider();
        if (provider == null) {
            return true;
        }
        if (save && this.changes.get() != 0 && isInit) {
            provider.saveChunk(this.getX(), this.getZ());
        }
        if (safe) {
            for (Entity entity : this.getEntities().values()) {
                if (entity instanceof Player) {
                    return false;
                }
            }
        }

        synchronized (this) {
            this.available = false;
            this.discarded = true;
        }

        for (Entity entity : new ArrayList<>(this.getEntities().values())) {
            if (entity instanceof Player) {
                continue;
            }
            entity.close();
        }

        for (BlockEntity blockEntity : new ArrayList<>(this.getBlockEntities().values())) {
            blockEntity.close();
        }
        return true;
    }

    @Override
    public synchronized void initChunk() {
        if (this.getProvider() == null || this.isInit || this.isInitializing) {
            return;
        }

        this.isInitializing = true;
        try {
            boolean changed = false;
            if (this.entityNBT != null) {
                for (CompoundTag nbt : entityNBT) {
                    if (!nbt.contains("identifier")) {
                        this.setChanged();
                        continue;
                    }
                    long actorUniqueId = nbt.contains("UniqueID") ? nbt.getLong("UniqueID") : 0;
                    try {
                        Entity entity = Entity.createEntity(nbt.getString("identifier"), this, nbt);
                        if (entity != null || actorUniqueId != 0 && this.getLevel().getEntityByUniqueId(actorUniqueId) != null) {
                            changed = true;
                        }
                    } catch (Exception e) {
                        log.error("Failed to spawn blockentity", e);
                    }
                }
                this.entityNBT = null;
            }

            if (this.blockEntityNBT != null) {
                for (CompoundTag nbt : blockEntityNBT) {
                    if (nbt != null) {
                        if (!nbt.contains("id")) {
                            changed = true;
                            log.warn("BlockEntity tag without id");
                            continue;
                        }
                        if ((nbt.getInt("x") >> 4) != this.getX() || ((nbt.getInt("z") >> 4) != this.getZ())) {
                            changed = true;
                            log.warn("BlockEntity tag position does not match chunk!");
                            continue;
                        }
                        BlockEntity blockEntity = BlockEntity.createBlockEntity(nbt.getString("id"), this, nbt);
                        if (blockEntity == null) {
                            changed = true;
                        }
                    }
                }
                this.blockEntityNBT = null;
            }
            if (changed) {
                this.setChanged();
            }

            this.isInit = true;
            this.getProvider().onChunkInitialized(this);
        } finally {
            this.isInitializing = false;
        }
    }

    @Override
    public boolean isInitiated() {
        return isInit;
    }

    @Override
    public short[] getHeightMapArray() {
        return heightMap;
    }

    @Override
    public CompoundTag getExtraData() {
        return this.extraData;
    }

    @Override
    public void setExtraData(CompoundTag extraData) {
        this.extraData = extraData;
    }

    @Override
    public LevelChunkMetaData getLevelChunkMetaData() {
        return this.levelChunkMetaData;
    }

    @Override
    public void setLevelChunkMetaData(LevelChunkMetaData levelChunkMetaData) {
        this.levelChunkMetaData = Preconditions.checkNotNull(levelChunkMetaData);
    }

    @Override
    public AabbVolumes getAabbVolumes() {
        return this.aabbVolumes;
    }

    @Override
    public void setAabbVolumes(AabbVolumes aabbVolumes) {
        this.aabbVolumes = Preconditions.checkNotNull(aabbVolumes);
        if (this.isInit) {
            this.setChanged();
        }
    }

    /**
     * Returns the persisted Bedrock biome state.
     */
    public BiomeState getBiomeState() {
        return biomeState;
    }

    @Override
    public boolean hasChanged() {
        return this.changes.get() != 0;
    }

    @Override
    public void setChanged() {
        this.changes.incrementAndGet();
    }

    @Override
    public void setChanged(boolean changed) {
        if (changed) {
            setChanged();
        } else {
            changes.set(0);
        }
    }

    @Override
    public long getChanges() {
        return changes.get();
    }

    @Override
    public long getSectionBlockChanges(int sectionY) {
        if (sectionY < 0 || sectionY >= getDimensionData().getChunkSectionCount()) {
            return 0L;
        }
        if (sections[sectionY] == null) return 0L;
        return sections[sectionY].blockChanges().get();
    }

    @Override
    public boolean isBlockChangeAllowed(int chunkX, int chunkY, int chunkZ) {
        //todo complete
        return true;
    }

    @Override
    public Stream<Block> scanBlocks(BlockVector3 min, BlockVector3 max, BiPredicate<BlockVector3, BlockState> condition) {
        int offsetX = getX() << 4;
        int offsetZ = getZ() << 4;
        return IntStream.rangeClosed(0, getDimensionData().getChunkSectionCount() - 1)
                .mapToObj(sectionY -> sections[sectionY])
                .filter(section -> section != null && !section.isEmpty()).parallel()
                .map(section -> section.scanBlocks(getProvider(), offsetX, offsetZ, min, max, condition))
                .flatMap(Collection::stream);
    }

    /**
     * Gets or create section.
     *
     * @param sectionY the section y range -4 ~ 19
     * @return the or create section
     */
    protected ChunkSection getOrCreateSection(int sectionY) {
        int minSectionY = this.getDimensionData().getMinSectionY();
        int offsetY = sectionY - minSectionY;
        if (this.sections[offsetY] == null) {
            this.sections[offsetY] = new ChunkSection((byte) (offsetY + minSectionY), this.biomeSections[offsetY]);
        }
        return sections[offsetY];
    }

    private void removeInvalidTile(int x, int y, int z) {
        BlockEntity entity = getTile(x, y, z);
        if (entity != null) {
            try {
                if (!entity.closed && entity.isBlockEntityValid()) return;
            } catch (Exception e) {
                try {
                    log.warn("Block entity validation of {} at {}, {} {} {} failed, removing as invalid.",
                            entity.getClass().getName(), getProvider().getLevel().getName(), entity.x, entity.y, entity.z, e);
                } catch (Exception e2) {
                    e.addSuppressed(e2);
                    log.warn("Block entity validation failed", e);
                }
            }
            entity.close();
        }
    }

    @Override
    public void reObfuscateChunk() {
        for (var section : getSections()) {
            if (section != null) {
                section.setNeedReObfuscate();
            }
        }
    }

    private int ensureY(final int y) {
        final int minHeight = getDimensionData().getMinHeight();
        final int maxHeight = getDimensionData().getMaxHeight();
        return Math.max(Math.min(y, maxHeight), minHeight);
    }


    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return "Chunk{" + "x=" + x + ", z=" + z + '}';
    }

    @Override
    public boolean hasBorderBlock(int localX, int localZ) {
        long stamp = blockLock.tryOptimisticRead();
        try {
            for (; ; stamp = blockLock.readLock()) {
                if (stamp == 0L) continue;
                boolean result = hasBorderBlockInternal(localX, localZ);
                if (!blockLock.validate(stamp)) continue;
                return result;
            }
        } finally {
            if (StampedLock.isReadLockStamp(stamp)) blockLock.unlockRead(stamp);
        }
    }

    boolean hasBorderBlockInternal(int localX, int localZ) {
        return this.borderBlockMap[(localX << 4) | localZ];
    }

    void updateBorderBlockMap(int localX, int localZ, BlockState oldState, BlockState newState) {
        if (oldState == newState) return;

        int index = (localX << 4) | localZ;
        if (isBorderBlock(newState)) {
            this.borderBlockMap[index] = true;
            return;
        }

        if (!isBorderBlock(oldState)) return;

        this.borderBlockMap[index] = hasBorderBlockInColumnInternal(localX, localZ);
    }

    private boolean hasBorderBlockInColumnInternal(int localX, int localZ) {
        for (ChunkSection section : this.sections) {
            if (section == null || section.isEmpty()) {
                continue;
            }

            for (int localY = 0; localY < 16; localY++) {
                BlockState state = section.getBlockState(localX, localY, localZ);
                if (isBorderBlock(state)) {
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean isBorderBlock(BlockState state) {
        return state != null && BlockID.BORDER_BLOCK.equals(state.getIdentifier());
    }

    public static class Builder implements IChunkBuilder {
        ChunkFinalizationState finalizationState;
        int chunkZ;
        int chunkX;
        LevelProvider levelProvider;
        ChunkSection[] sections;
        Palette<Integer>[] biomeSections;
        short[] heightMap;
        List<CompoundTag> entities;
        List<CompoundTag> blockEntities;
        CompoundTag extraData;
        AabbVolumes aabbVolumes;
        BiomeState biomeState;
        LevelChunkMetaData levelChunkMetaData;
        boolean[] borderBlockMap;

        private Builder() {
        }

        @Override
        public Builder chunkX(int chunkX) {
            this.chunkX = chunkX;
            return this;
        }

        @Override
        public int getChunkX() {
            return chunkX;
        }

        @Override
        public Builder chunkZ(int chunkZ) {
            this.chunkZ = chunkZ;
            return this;
        }

        @Override
        public int getChunkZ() {
            return chunkZ;
        }

        @Override
        public Builder finalizationState(ChunkFinalizationState finalizationState) {
            this.finalizationState = finalizationState;
            return this;
        }

        @Override
        @Deprecated(since = "3.1.0", forRemoval = true)
        public Builder state(ChunkState state) {
            this.finalizationState = state.toFinalizationState();
            return this;
        }

        @Override
        public Builder levelProvider(LevelProvider levelProvider) {
            this.levelProvider = levelProvider;
            return this;
        }

        @Override
        public LevelProvider getLevelProvider() {
            return levelProvider;
        }

        @Override
        public DimensionData getDimensionData() {
            Preconditions.checkNotNull(levelProvider);
            return levelProvider.getDimensionData();
        }

        public Builder sections(ChunkSection[] sections) {
            this.sections = sections;
            return this;
        }

        @Override
        public ChunkSection[] getSections() {
            return sections;
        }

        @Override
        public Builder biomeSections(Palette<Integer>[] biomeSections) {
            this.biomeSections = biomeSections;
            return this;
        }

        @Override
        public Palette<Integer>[] getBiomeSections() {
            return biomeSections;
        }

        public Builder heightMap(short[] heightMap) {
            this.heightMap = heightMap;
            return this;
        }

        @Override
        public Builder entities(List<CompoundTag> entities) {
            this.entities = entities;
            return this;
        }

        @Override
        public Builder blockEntities(List<CompoundTag> blockEntities) {
            this.blockEntities = blockEntities;
            return this;
        }

        @Override
        public IChunkBuilder extraData(CompoundTag extraData) {
            this.extraData = extraData;
            return this;
        }

        @Override
        public Builder levelChunkMetaData(LevelChunkMetaData levelChunkMetaData) {
            this.levelChunkMetaData = Preconditions.checkNotNull(levelChunkMetaData);
            return this;
        }

        @Override
        public Builder aabbVolumes(AabbVolumes aabbVolumes) {
            this.aabbVolumes = aabbVolumes;
            return this;
        }

        @Override
        public Builder biomeState(BiomeState biomeState) {
            this.biomeState = Preconditions.checkNotNull(biomeState);
            return this;
        }

        @Override
        public Builder borderBlockMap(boolean[] borderBlockMap) {
            Preconditions.checkNotNull(borderBlockMap);
            Preconditions.checkArgument(borderBlockMap.length == 256, "Border Block map must contain 256 columns");
            this.borderBlockMap = borderBlockMap.clone();
            return this;
        }

        public Chunk build() {
            Preconditions.checkNotNull(levelProvider);
            if (finalizationState == null) {
                finalizationState = ChunkFinalizationState.NEEDS_INSTATICKING;
            }
            if (sections == null) sections = new ChunkSection[levelProvider.getDimensionData().getChunkSectionCount()];

            if (biomeSections == null) {
                biomeSections = createBiomeSections(levelProvider.getDimensionData().getChunkSectionCount());

                for (int i = 0; i < sections.length; i++) {
                    if (sections[i] != null) biomeSections[i] = sections[i].biomes();
                }
            }

            if (heightMap == null) heightMap = new short[256];
            if (entities == null) entities = new ArrayList<>();
            if (blockEntities == null) blockEntities = new ArrayList<>();
            if (extraData == null) extraData = new CompoundTag();
            if (aabbVolumes == null) aabbVolumes = AabbVolumes.empty();
            if (biomeState == null) biomeState = new BiomeState();
            if (levelChunkMetaData == null) levelChunkMetaData = LevelChunkMetaData.uninitialized();
            if (borderBlockMap == null) borderBlockMap = new boolean[256];
            return new Chunk(
                    finalizationState,
                    chunkX,
                    chunkZ,
                    levelProvider,
                    sections,
                    biomeSections,
                    heightMap,
                    entities,
                    blockEntities,
                    extraData,
                    aabbVolumes,
                    biomeState,
                    levelChunkMetaData,
                    borderBlockMap
            );
        }

        public Chunk emptyChunk(int chunkX, int chunkZ) {
            Preconditions.checkNotNull(levelProvider);
            return new Chunk(chunkX, chunkZ, levelProvider);
        }
    }
}
