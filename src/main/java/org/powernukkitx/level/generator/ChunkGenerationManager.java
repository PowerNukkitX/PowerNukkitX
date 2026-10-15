package org.powernukkitx.level.generator;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import org.powernukkitx.Player;
import org.powernukkitx.level.ChunkBuildOrderPolicy;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.format.Chunk;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.tickingarea.TickingArea;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import org.jetbrains.annotations.ApiStatus;
import lombok.extern.slf4j.Slf4j;

/**
 * Schedules and tracks chunk generation work for a level provider. It limits concurrent jobs, resolves stage
 * dependencies, and exposes generation state for pending and active chunks.
 *
 * @author Curse
 */
@Slf4j
public final class ChunkGenerationManager {
    private final Level level;
    private final Generator generator;
    private final ForkJoinPool executor;
    private final int refillLowWater;
    private final int refillHighWater;
    private final ChunkGenerationState terrainReadyState;
    private final AtomicInteger inFlight = new AtomicInteger();
    private final AtomicInteger pendingCount = new AtomicInteger();
    private final AtomicBoolean schedulerRequested = new AtomicBoolean();
    private final ReentrantLock schedulerLock = new ReentrantLock();
    private final ReentrantLock generationQueueLock = new ReentrantLock();
    private final Map<ChunkGenerationState, ChunkGenerationTask> tasksByState = new EnumMap<>(ChunkGenerationState.class);
    private final ConcurrentHashMap<Long, GenerationDemand> demands = new ConcurrentHashMap<>();
    private final Long2ObjectOpenHashMap<List<GenerationDemand>> drivenChunks = new Long2ObjectOpenHashMap<>();
    private final ConcurrentHashMap<GenerationRequestKey, GenerationRequest> requests = new ConcurrentHashMap<>();
    private final ConcurrentLinkedDeque<GenerationRequest> pending = new ConcurrentLinkedDeque<>();

    /**
     * Creates a chunk generation manager.
     *
     * @param level managed level
     * @param generator chunk generator
     */
    public ChunkGenerationManager(Level level, Generator generator) {
        this.level = level;
        this.generator = generator;
        this.executor = level.getServer().getComputeThreadPool();

        final int workerCount = Math.max(1, executor.getParallelism());
        final int generationQueueSize = level.getServer().getSettings().chunkSettings().generationQueueSize();
        Preconditions.checkArgument(generationQueueSize >= -1, "generationQueueSize must be -1 or greater");

        if (generationQueueSize <= 0) {
            this.refillLowWater = workerCount * 2;
            this.refillHighWater = workerCount * 6;
        } else {
            this.refillLowWater = generationQueueSize;
            this.refillHighWater = generationQueueSize;
        }

        for (ChunkGenerationTask task : generator.getGenerationTasks()) {
            ChunkGenerationTask previous = tasksByState.put(task.stableState(), task);
            if (previous != null) {
                throw new IllegalStateException("Generator '" + generator.getName()
                        + "' declares more than one generation task for state " + task.stableState());
            }
        }

        ChunkGenerationTask lightingTask = new ChunkGenerationTask(
                "Initial Lighting",
                ChunkGenerationState.NEEDS_LIGHTING,
                ChunkGenerationState.LIGHTING,
                ChunkGenerationState.NEEDS_COMPLETION,
                ChunkGenerationState.NEEDS_LIGHTING,
                null,
                null,
                ChunkGenerationDependency.NEIGHBORHOOD_STATE_8
        );

        if (tasksByState.putIfAbsent(lightingTask.stableState(), lightingTask) != null) {
            throw new IllegalStateException("Generator '" + generator.getName()
                    + "' declares a generation task for " + lightingTask.stableState());
        }

        ChunkGenerationTask terrainTask = tasksByState.get(ChunkGenerationState.NEEDS_GENERATION);
        if (terrainTask == null) {
            throw new IllegalStateException("Generator '" + generator.getName()
                    + "' does not declare a terrain generation task");
        }

        this.terrainReadyState = terrainTask.successState();
    }

    /**
     * Requests chunk generation.
     * <p>
     * A chunk only completes once the chunks around it have caught up. An unforced request leaves
     * driving those to whatever already keeps them loaded, such as a player's view, so it may never
     * complete for a chunk nobody can see. A forced request drives them itself, following the
     * dependencies stage by stage: whenever a driven chunk waits for a neighbour, that neighbour is
     * loaded and driven to the state the stage needs and no further. Driven chunks stay loaded until
     * the chunk is complete.
     *
     * @param chunkX chunk X
     * @param chunkZ chunk Z
     * @param force whether to drive the neighbourhood of the chunk so the request completes
     * @return completes with the chunk once it is complete
     */
    public CompletableFuture<IChunk> request(int chunkX, int chunkZ, boolean force) {
        return requestToState(chunkX, chunkZ, ChunkGenerationState.COMPLETE, force);
    }

    /**
     * Requests terrain generation.
     *
     * @param chunkX value for this API
     * @param chunkZ value for this API
     * @return the requested value
     */
    public CompletableFuture<IChunk> requestTerrain(int chunkX, int chunkZ) {
        return requestToState(chunkX, chunkZ, terrainReadyState, false);
    }

    /**
     * Requests generation while the coordinate remains owned by a physical chunk view.
     *
     * @param chunkX chunk X
     * @param chunkZ chunk Z
     */
    @ApiStatus.Internal
    public void requestView(int chunkX, int chunkZ) {
        final long chunkHash = Level.chunkHash(chunkX, chunkZ);
        if (!level.isChunkViewRetained(chunkHash)) return;

        IChunk loaded = level.getPhysicalChunkIfLoaded(chunkX, chunkZ);
        if (loaded == null) {
            try {
                loaded = level.acquireGenerationChunk(chunkX, chunkZ);
            } catch (Throwable throwable) {
                log.error("Could not acquire view chunk ({}, {}) in level '{}'",
                        chunkX, chunkZ, level.getFolderName(), throwable);
                return;
            }
        }

        if (!(loaded instanceof Chunk chunk) || !isCurrentChunk(chunk)) return;

        if (chunk.getGenerationState() == ChunkGenerationState.COMPLETE) {
            chunk.setAvailable(true);
            return;
        }

        onChunkViewAcquired(chunk);
        schedule();
    }

    private CompletableFuture<IChunk> requestToState(
            int chunkX, int chunkZ, ChunkGenerationState targetState, boolean force) {
        final long chunkHash = Level.chunkHash(chunkX, chunkZ);
        final GenerationDemand demand = demands.compute(
                chunkHash,
                (hash, existing) -> {
                    if (existing != null) {
                        existing.upgradeTarget(targetState);
                        return existing;
                    }
                    return new GenerationDemand(chunkX, chunkZ, chunkHash, targetState);
                });

        IChunk loaded = level.getPhysicalChunkIfLoaded(chunkX, chunkZ);
        if (loaded instanceof Chunk chunk) {
            completeReachedWaiters(demand, chunk);
            if (hasReachedTarget(chunk, demand)) {
                completeDemand(demand, chunk);
            } else {
                if (force && targetState == ChunkGenerationState.COMPLETE) {
                    driveDemand(demand);
                }
                tryEnqueueStableState(chunk);
                schedule();
            }
        } else if (loaded != null) {
            failDemand(demand, new IllegalStateException(
                    "Chunk generation manager requires Chunk, got " + loaded.getClass().getName()));
        } else {
            if (force && targetState == ChunkGenerationState.COMPLETE) {
                driveDemand(demand);
            }
            ensureDemandChunkAcquired(demand);
        }

        return targetState == ChunkGenerationState.COMPLETE ? demand.completionFuture : demand.terrainFuture;
    }

    /**
     * Starts driving a forced complete demand. Without this a chunk that no player can see never
     * completes, because nothing else drives its neighbours, and whoever waits for it waits forever.
     * <p>
     * Skipped on generation workers: a stage that demands another chunk is already inside chunks
     * that are being driven, and driving from there would spread without bound.
     */
    private void driveDemand(GenerationDemand demand) {
        if (isGenerationWorker() || !retainDriven(demand, demand.chunkHash, ChunkGenerationState.COMPLETE.getNativeId())) {
            return;
        }
        if (demands.get(demand.chunkHash) != demand) {
            releaseDriven(demand);
        }
    }

    /**
     * Drives the neighbours a driven chunk waits for at its current state, for every demand that
     * still needs the chunk past that state. A neighbour that is not loaded yet, or not far enough,
     * is loaded and driven to the state the stage needs.
     */
    private void driveDependencies(Chunk chunk) {
        final int state = chunk.getGenerationState().getNativeId();
        final GenerationDemand[] owners = demandsDrivingPast(chunk.getIndex(), state);
        if (owners == null) return;

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) continue;

                final int required = requiredNeighborState(chunk.getX(), chunk.getZ(), state, dx, dz);
                if (required < 0) return;

                final int neighborX = chunk.getX() + dx;
                final int neighborZ = chunk.getZ() + dz;
                IChunk loaded = level.getPhysicalChunkIfLoaded(neighborX, neighborZ);
                if (loaded instanceof Chunk neighbor && !neighbor.isDiscarded()
                        && neighbor.getGenerationState().getNativeId() >= required) {
                    continue;
                }

                for (GenerationDemand owner : owners) {
                    driveChunk(owner, neighborX, neighborZ, required);
                }
            }
        }
    }

    private static int requiredNeighborState(int chunkX, int chunkZ, int state, int dx, int dz) {
        return switch (state) {
            case 2, 6, 8 -> state;
            case 4 -> populationNeighborState(chunkX, chunkZ, dx, dz);
            default -> -1;
        };
    }

    /**
     * Loads a chunk a demand depends on and drives it. A chunk that cannot be loaded fails the
     * demand, which releases everything it drives: the chunk is never driven for the demand again,
     * so the demand would otherwise wait forever.
     */
    private void driveChunk(GenerationDemand demand, int chunkX, int chunkZ, int targetNativeId) {
        if (!retainDriven(demand, Level.chunkHash(chunkX, chunkZ), targetNativeId)) return;

        final IChunk acquired;
        try {
            acquired = level.acquireGenerationChunk(chunkX, chunkZ);
        } catch (Throwable throwable) {
            failDemand(demand, new IllegalStateException("Could not load chunk (" + chunkX + ", " + chunkZ
                    + ") needed by demanded chunk (" + demand.chunkX + ", " + demand.chunkZ + ")", throwable));
            return;
        }

        if (!(acquired instanceof Chunk)) {
            failDemand(demand, new IllegalStateException("Chunk generation manager requires Chunk, got "
                    + (acquired == null ? "null" : acquired.getClass().getName())));
            return;
        }

        onChunkViewAcquired(acquired);
    }

    /**
     * Records that a demand needs a chunk driven to at least the given state, keeping the chunk
     * loaded the first time.
     *
     * @return whether the demand needs the chunk further than before
     */
    private boolean retainDriven(GenerationDemand demand, long chunkHash, int targetNativeId) {
        synchronized (drivenChunks) {
            if (demand.drivenReleased) return false;

            final int previous = demand.driven.get(chunkHash);
            if (previous >= targetNativeId) return false;

            demand.driven.put(chunkHash, targetNativeId);
            if (previous < 0) {
                drivenChunks.computeIfAbsent(chunkHash, ignored -> new ArrayList<>(1)).add(demand);
                level.retainGenerationTask(chunkHash);
            }
            return true;
        }
    }

    private void releaseDriven(GenerationDemand demand) {
        final long[] released;
        synchronized (drivenChunks) {
            if (demand.drivenReleased) return;
            demand.drivenReleased = true;

            released = demand.driven.keySet().toLongArray();
            for (long chunkHash : released) {
                final List<GenerationDemand> owners = drivenChunks.get(chunkHash);
                if (owners != null && owners.remove(demand) && owners.isEmpty()) {
                    drivenChunks.remove(chunkHash);
                }
            }
            demand.driven.clear();
        }

        for (long chunkHash : released) {
            level.releaseGenerationTask(chunkHash);
        }
    }

    private GenerationDemand[] demandsDrivingPast(long chunkHash, int state) {
        synchronized (drivenChunks) {
            final List<GenerationDemand> owners = drivenChunks.get(chunkHash);
            if (owners == null) return null;

            GenerationDemand[] driving = null;
            int count = 0;
            for (GenerationDemand owner : owners) {
                if (owner.driven.get(chunkHash) <= state) continue;
                if (driving == null) driving = new GenerationDemand[owners.size()];
                driving[count++] = owner;
            }
            return driving == null || count == driving.length ? driving : Arrays.copyOf(driving, count);
        }
    }

    private boolean isDriven(long chunkHash) {
        synchronized (drivenChunks) {
            return drivenChunks.containsKey(chunkHash);
        }
    }

    private boolean isGenerationWorker() {
        return Thread.currentThread() instanceof ForkJoinWorkerThread worker && worker.getPool() == executor;
    }

    private static boolean hasReachedTarget(Chunk chunk, ChunkGenerationState targetState) {
        return chunk.getGenerationState().getNativeId() >= targetState.getNativeId();
    }

    private static boolean hasReachedTarget(Chunk chunk, GenerationDemand demand) {
        return chunk.getGenerationState().getNativeId() >= demand.targetNativeId.get();
    }

    private void ensureDemandChunkAcquired(GenerationDemand demand) {
        final IChunk acquired;

        try {
            acquired = level.acquireGenerationChunk(demand.chunkX, demand.chunkZ);
        } catch (Throwable throwable) {
            failDemand(demand, throwable);
            return;
        }

        if (!(acquired instanceof Chunk chunk)) {
            failDemand(demand, new IllegalStateException(
                    "Chunk generation manager requires Chunk, got "
                            + (acquired == null ? "null" : acquired.getClass().getName())));
            return;
        }

        completeReachedWaiters(demand, chunk);
        if (hasReachedTarget(chunk, demand)) {
            completeDemand(demand, chunk);
            return;
        }

        onChunkViewAcquired(chunk);
        schedule();
    }

    /**
     * Returns whether the chunk is generating.
     *
     * @param chunkX value for this API
     * @param chunkZ value for this API
     * @return the requested value
     */
    public boolean isGenerating(int chunkX, int chunkZ) {
        final long chunkHash = Level.chunkHash(chunkX, chunkZ);
        if (demands.containsKey(chunkHash)) return true;

        IChunk loaded = level.getPhysicalChunkIfLoaded(chunkX, chunkZ);
        if (!(loaded instanceof Chunk chunk)) return false;

        ChunkGenerationState state = chunk.getGenerationState();
        return state.isActive() || requests.containsKey(new GenerationRequestKey(chunkHash, state));
    }

    /**
     * Returns the active generation job count.
     * @return the requested value
     */
    public int getInFlightCount() {
        return inFlight.get();
    }

    /**
     * Returns the queued generation job count.
     * @return the requested value
     */
    public int getPendingCount() {
        return pendingCount.get();
    }

    /**
     * Returns the generation dispatch high-water mark.
     *
     * @return generation dispatch high-water mark
     */
    public int getMaxInFlight() {
        return refillHighWater;
    }

    /**
     * Generates terrain on the current thread.
     *
     * @param inputChunk value for this API
     * @return the requested value
     */
    public IChunk generateTerrainSynchronously(IChunk inputChunk) {
        if (!(inputChunk instanceof Chunk chunk)) {
            throw new IllegalArgumentException("Chunk generation manager requires Chunk, got " + inputChunk.getClass().getName());
        }

        if (hasReachedTarget(chunk, terrainReadyState)) {
            notifySynchronousProgress(chunk);
            return chunk;
        }

        if (chunk.getGenerationState().isActive()) {
            return chunk;
        }

        ChunkGenerationTask task = tasksByState.get(chunk.getGenerationState());
        if (hasReachedTarget(chunk, terrainReadyState)) {
            notifySynchronousProgress(chunk);
            return chunk;
        }

        if (task == null || task.stableState() != ChunkGenerationState.NEEDS_GENERATION) {
            throw new IllegalStateException(
                    "Cannot synchronously generate terrain for chunk ("+ chunk.getX() + ", " + chunk.getZ() + ") in generator '" + generator.getName() + "' from state " + chunk.getGenerationState());
        }

        if (!chunk.compareAndSetGenerationState(task.stableState(), task.activeState())) {
            return chunk;
        }

        final ChunkGenerationState resultState;
        try {
            resultState = executeTask(chunk, task);
        } catch (Throwable throwable) {
            if (chunk.compareAndSetGenerationState(task.activeState(), task.failureState())) {
                enqueueStableState(chunk);
            }

            GenerationDemand demand = demands.get(chunk.getIndex());
            if (demand != null) {
                failDemand(demand, throwable);
            }

            schedule();
            throw new IllegalStateException(
                    "Synchronous terrain generation failed for chunk (" + chunk.getX() + ", " + chunk.getZ()
                            + ") in level '" + level.getFolderName() + "'", throwable);
        }

        if (resultState == null) {
            if (chunk.compareAndSetGenerationState(task.activeState(), task.failureState())) {
                enqueueStableState(chunk);
            }

            schedule();
            throw new IllegalStateException(
                    "Synchronous terrain generation returned failure for chunk ("
                            + chunk.getX() + ", " + chunk.getZ() + ")");
        }

        if (!chunk.compareAndSetGenerationState(task.activeState(), resultState)) {
            throw new IllegalStateException(
                    "Synchronous terrain generation completed for chunk (" + chunk.getX() + ", " + chunk.getZ()
                            + ") but state changed from " + task.activeState() + " to " + chunk.getGenerationState());
        }

        if (!chunk.isInitiated()) {
            level.initializeAcquiredChunk(chunk);
        }

        notifySynchronousProgress(chunk);
        return chunk;
    }

    private void notifySynchronousProgress(Chunk chunk) {
        GenerationDemand demand = demands.get(chunk.getIndex());
        if (demand != null) {
            completeReachedWaiters(demand, chunk);
            if (hasReachedTarget(chunk, demand)) {
                completeDemand(demand, chunk);
            }
        }

        reevaluateAround(chunk.getX(), chunk.getZ());
        schedule();
    }

    /**
     * Re-evaluates generation when a chunk becomes loaded.
     *
     * @param loadedChunk loaded chunk
     */
    @ApiStatus.Internal
    public void onChunkLoaded(IChunk loadedChunk) {
        if (!(loadedChunk instanceof Chunk chunk) || chunk.isDiscarded() || level.isClosing()) return;

        if (chunk.getGenerationState() == ChunkGenerationState.COMPLETE) {
            chunk.setAvailable(true);
        }

        GenerationDemand demand = demands.get(chunk.getIndex());
        if (demand != null) {
            completeReachedWaiters(demand, chunk);
            if (hasReachedTarget(chunk, demand)) {
                completeDemand(demand, chunk);
            }
        }

        if (chunk.getGenerationState() == ChunkGenerationState.NEEDS_GENERATION) {
            enqueueStableState(chunk);
        } else {
            reevaluateAround(chunk.getX(), chunk.getZ());
        }

        schedule();
    }

    /**
     * Restores generation eligibility when a physical view acquires an existing chunk.
     *
     * @param loadedChunk acquired chunk
     */
    @ApiStatus.Internal
    public void onChunkViewAcquired(IChunk loadedChunk) {
        if (!(loadedChunk instanceof Chunk chunk) || chunk.isDiscarded() || level.isClosing()) return;
        if (!isCurrentChunk(chunk)) return;

        if (chunk.getGenerationState() == ChunkGenerationState.COMPLETE) {
            chunk.setAvailable(true);
        }

        if (chunk.getGenerationState() == ChunkGenerationState.NEEDS_GENERATION) {
            enqueueStableState(chunk);
        } else {
            reevaluateAround(chunk.getX(), chunk.getZ());
        }
    }

    /**
     * Runs generation scheduling after a physical view acquisition batch.
     */
    @ApiStatus.Internal
    public void onChunkViewAcquisitionComplete() {
        if (!level.isClosing()) {
            schedule();
        }
    }

    private void enqueue(GenerationRequest request) {
        generationQueueLock.lock();
        try {
            enqueueLocked(request);
        } finally {
            generationQueueLock.unlock();
        }
    }

    private void enqueueLocked(GenerationRequest request) {
        if (requests.get(request.key) != request) return;
        if (request.queued.get()) return;

        request.queued.set(true);
        pendingCount.incrementAndGet();
        pending.offerLast(request);
    }

    private void enqueueStableState(Chunk chunk) {
        if (!isCurrentChunk(chunk)) return;

        ChunkGenerationState state = chunk.getGenerationState();
        if (state.isActive() || !tasksByState.containsKey(state)) return;

        GenerationRequestKey key = new GenerationRequestKey(chunk.getIndex(), state);

        generationQueueLock.lock();
        try {
            GenerationRequest request = requests.computeIfAbsent(
                    key,
                    ignored -> new GenerationRequest(key, chunk.getX(), chunk.getZ(), state));
            enqueueLocked(request);
        } finally {
            generationQueueLock.unlock();
        }
    }

    private void tryEnqueueStableState(Chunk chunk) {
        if (!isCurrentChunk(chunk)) return;

        ChunkGenerationState state = chunk.getGenerationState();
        if (state.isActive() || !tasksByState.containsKey(state)) return;
        if (!dependenciesSatisfied(chunk, state)) {
            driveDependencies(chunk);
            return;
        }
        enqueueStableState(chunk);
    }

    private GenerationRequest pollPending() {
        generationQueueLock.lock();
        try {
            GenerationRequest request = pending.pollFirst();
            if (request != null) {
                pendingCount.decrementAndGet();
                request.queued.set(false);
            }
            return request;
        } finally {
            generationQueueLock.unlock();
        }
    }

    private boolean isRegisteredRequest(GenerationRequest request) {
        generationQueueLock.lock();
        try {
            return requests.get(request.key) == request;
        } finally {
            generationQueueLock.unlock();
        }
    }

    private boolean removeRequest(GenerationRequest request) {
        generationQueueLock.lock();
        try {
            return requests.remove(request.key, request);
        } finally {
            generationQueueLock.unlock();
        }
    }

    private void discardRequestUnlessRequeued(GenerationRequest request) {
        generationQueueLock.lock();
        try {
            if (!request.queued.get()) {
                requests.remove(request.key, request);
            }
        } finally {
            generationQueueLock.unlock();
        }
    }

    private void reevaluateAround(int chunkX, int chunkZ) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                IChunk loaded = level.getPhysicalChunkIfLoaded(chunkX + dx, chunkZ + dz);
                if (loaded instanceof Chunk chunk) {
                    tryEnqueueStableState(chunk);
                }
            }
        }
    }

    private boolean dependenciesSatisfied(Chunk center, ChunkGenerationState state) {
        return switch (state.getNativeId()) {
            case 0 -> true;
            case 2, 6, 8 -> hasNeighborhoodState(center.getX(), center.getZ(), state.getNativeId());
            case 4 -> hasPopulationNeighborhoodState(center);
            default -> true;
        };
    }

    private boolean hasNeighborhoodState(int chunkX, int chunkZ, int minimumState) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                IChunk loaded = level.getPhysicalChunkIfLoaded(chunkX + dx, chunkZ + dz);
                if (!(loaded instanceof Chunk chunk) || chunk.isDiscarded()) return false;
                if (chunk.getGenerationState().getNativeId() < minimumState) return false;
            }
        }

        return true;
    }

    private boolean hasPopulationNeighborhoodState(Chunk center) {
        final int chunkX = center.getX();
        final int chunkZ = center.getZ();

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) continue;

                IChunk loaded = level.getPhysicalChunkIfLoaded(chunkX + dx, chunkZ + dz);
                if (!(loaded instanceof Chunk chunk) || chunk.isDiscarded()) return false;

                int requiredState = populationNeighborState(chunkX, chunkZ, dx, dz);
                if (chunk.getGenerationState().getNativeId() < requiredState) return false;
            }
        }

        return true;
    }

    private static int populationNeighborState(int chunkX, int chunkZ, int dx, int dz) {
        final boolean oddX = (chunkX & 1) != 0;
        final boolean oddZ = (chunkZ & 1) != 0;

        if (!oddX && !oddZ) return 4;
        if (!oddX) return 6;
        if (!oddZ) return dz == 0 ? 6 : 4;
        return dz == 0 ? 4 : 6;
    }

    private boolean hasPhysicalNeighborhood(int chunkX, int chunkZ) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                IChunk loaded = level.getPhysicalChunkIfLoaded(chunkX + dx, chunkZ + dz);
                if (!(loaded instanceof Chunk chunk) || chunk.isDiscarded()) return false;
            }
        }

        return true;
    }

    private void schedule() {
        schedulerRequested.set(true);
        if (!schedulerLock.tryLock()) return;

        try {
            do {
                schedulerRequested.set(false);
                refill();
            } while (schedulerRequested.get());
        } finally {
            schedulerLock.unlock();
            if (schedulerRequested.get()) schedule();
        }
    }

    private void refill() {
        final int active = inFlight.get();
        if (active >= refillLowWater || pending.isEmpty()) return;

        int dispatchAllowance = refillHighWater - active;
        if (dispatchAllowance <= 0) return;

        final List<ViewSource> viewSources = snapshotViewSources();
        final List<ChunkBuildOrderPolicy.RegionSource> regionSources = snapshotRegionSources();
        final List<Candidate> candidates = new ArrayList<>();
        final List<GenerationRequest> deferred = new ArrayList<>();
        List<Chunk> blocked = null;

        GenerationRequest request;
        while ((request = pollPending()) != null) {
            if (!isRegisteredRequest(request)) continue;

            Chunk chunk = resolveChunk(request);
            if (chunk == null) {
                discardRequestUnlessRequeued(request);
                continue;
            }

            if (chunk.getGenerationState() != request.state) {
                removeRequest(request);
                continue;
            }

            ChunkGenerationTask task = tasksByState.get(request.state);
            if (task == null) {
                removeRequest(request);
                failDemand(request.key.chunkHash(), new IllegalStateException(
                        "No generation task for chunk (" + request.chunkX + ", " + request.chunkZ
                                + ") in generator '" + generator.getName() + "' at state " + request.state));
                continue;
            }

            if (!dependenciesSatisfied(chunk, request.state)
                    || request.state.getNativeId() >= 2 && !hasPhysicalNeighborhood(request.chunkX, request.chunkZ)) {
                removeRequest(request);
                if (blocked == null) blocked = new ArrayList<>();
                blocked.add(chunk);
                continue;
            }

            candidates.add(new Candidate(request, chunk, task, calculatePriority(request, viewSources, regionSources)));
        }

        candidates.sort(Candidate.COMPARATOR);

        int index = 0;
        while (index < candidates.size() && dispatchAllowance > 0) {
            Candidate candidate = candidates.get(index++);
            GenerationRequest candidateRequest = candidate.request();

            if (!removeRequest(candidateRequest)) continue;

            if (launch(candidate)) {
                dispatchAllowance--;
            }
        }

        while (index < candidates.size()) {
            GenerationRequest candidateRequest = candidates.get(index++).request();
            if (isRegisteredRequest(candidateRequest)) {
                deferred.add(candidateRequest);
            }
        }

        for (GenerationRequest deferredRequest : deferred) {
            enqueue(deferredRequest);
        }

        if (blocked != null) {
            for (Chunk chunk : blocked) {
                driveDependencies(chunk);
            }
        }

        if (inFlight.get() == 0 && !pending.isEmpty()) {
            schedulerRequested.set(true);
        }
    }

    private Chunk resolveChunk(GenerationRequest request) {
        IChunk loaded = level.getPhysicalChunkIfLoaded(request.chunkX, request.chunkZ);
        if (loaded == null) return null;

        if (!(loaded instanceof Chunk chunk)) {
            removeRequest(request);
            failDemand(request.key.chunkHash(), new IllegalStateException(
                    "Chunk generation manager requires Chunk, got " + loaded.getClass().getName()));
            return null;
        }

        return isCurrentChunk(chunk) ? chunk : null;
    }

    private boolean isSameChunkIdentity(Chunk chunk) {
        return !chunk.isDiscarded()
                && level.getPhysicalChunkIfLoaded(chunk.getX(), chunk.getZ()) == chunk;
    }

    private boolean isCurrentChunk(Chunk chunk) {
        return isSameChunkIdentity(chunk)
                && (level.isChunkViewRetained(chunk.getIndex()) || demands.containsKey(chunk.getIndex())
                || isDriven(chunk.getIndex()));
    }

    private boolean launch(Candidate candidate) {
        final GenerationRequest request = candidate.request();
        final Chunk chunk = candidate.chunk();
        final ChunkGenerationTask task = candidate.task();
        if (!isCurrentChunk(chunk)) return false;
        if (!chunk.compareAndSetGenerationState(task.stableState(), task.activeState())) return false;

        inFlight.incrementAndGet();
        level.retainGenerationTask(chunk.getIndex());

        try {
            executor.execute(() -> runTask(request, chunk, task));
            return true;
        } catch (Throwable throwable) {
            if (chunk.compareAndSetGenerationState(task.activeState(), task.failureState())) {
                enqueueStableState(chunk);
            }

            inFlight.decrementAndGet();
            level.releaseGenerationTask(chunk.getIndex());
            failDemand(request.key.chunkHash(), throwable);
            schedule();
            return false;
        }
    }

    private void runTask(GenerationRequest request, Chunk chunk, ChunkGenerationTask task) {
        if (!isSameChunkIdentity(chunk)) {
            finishTask(request);
            return;
        }

        final ChunkGenerationState resultState;
        try {
            resultState = executeTask(chunk, task);
        } catch (Throwable throwable) {
            if (!isSameChunkIdentity(chunk)) {
                finishTask(request);
                return;
            }

            boolean rolledBack = chunk.compareAndSetGenerationState(task.activeState(), task.failureState());
            if (rolledBack && isCurrentChunk(chunk)) {
                enqueueStableState(chunk);
            } else if (!rolledBack) {
                log.error("Could not roll generation state back for chunk ({}, {}) in level '{}' after task '{}': "
                                + "expected {}, actual {}", request.chunkX, request.chunkZ, level.getFolderName(),
                        task.name(), task.activeState(), chunk.getGenerationState());
            }

            log.error("Chunk generation task '{}' failed for chunk ({}, {}) in level '{}'; rolled back {} -> {}",
                    task.name(), request.chunkX, request.chunkZ, level.getFolderName(), task.activeState(),
                    task.failureState(), throwable);
            failDemand(request.key.chunkHash(), throwable);
            finishTask(request);
            return;
        }

        if (!isSameChunkIdentity(chunk)) {
            finishTask(request);
            return;
        }

        if (resultState == null) {
            if (chunk.compareAndSetGenerationState(task.activeState(), task.failureState()) && isCurrentChunk(chunk)) {
                enqueueStableState(chunk);
            }
            finishTask(request);
            return;
        }

        if (!chunk.compareAndSetGenerationState(task.activeState(), resultState)) {
            failDemand(request.key.chunkHash(), new IllegalStateException(
                    "Chunk (" + request.chunkX + ", " + request.chunkZ + ") completed generation task '"
                            + task.name() + "' but state changed from " + task.activeState() + " to "
                            + chunk.getGenerationState()));
            finishTask(request);
            return;
        }

        if (resultState == ChunkGenerationState.NEEDS_COMPLETION) {
            submitCompletion(request, chunk);
        }

        if (!isCurrentChunk(chunk)) {
            finishTask(request);
            return;
        }

        if (!chunk.isInitiated()) {
            level.initializeAcquiredChunk(chunk);
        }

        GenerationDemand demand = demands.get(request.key.chunkHash());
        if (demand != null) {
            completeReachedWaiters(demand, chunk);
            if (hasReachedTarget(chunk, demand)) {
                completeDemand(demand, chunk);
            }
        }

        reevaluateAround(request.chunkX, request.chunkZ);
        finishTask(request);
    }

    private ChunkGenerationState executeTask(Chunk chunk, ChunkGenerationTask task) {
        if (task.stableState() == ChunkGenerationState.NEEDS_GENERATION && !chunk.isStorageResolved()) {
            boolean loadedFromStorage = chunk.getProvider().loadPersistentChunk(chunk);
            if (!isSameChunkIdentity(chunk)) return null;

            if (!chunk.isStorageResolved()) {
                throw new IllegalStateException(
                        "Provider did not resolve storage for chunk (" + chunk.getX() + ", " + chunk.getZ() + ")");
            }

            if (loadedFromStorage) {
                return ChunkGenerationState.NEEDS_CFRD;
            }
        } else if (!chunk.isInitiated()) {
            level.initializeAcquiredChunk(chunk);
        }

        if (task.stableState() == ChunkGenerationState.NEEDS_LIGHTING) {
            return level.getInitialLightingManager().process(chunk) ? task.successState() : null;
        }

        if (task.firstStage() == null) return task.successState();

        generator.runGenerationTask(chunk, task);
        return task.successState();
    }

    private void submitCompletion(GenerationRequest request, Chunk chunk) {
        long chunkHash = request.key.chunkHash();
        level.retainGenerationTask(chunkHash);

        try {
            executor.execute(() -> {
                try {
                    completeChunk(request, chunk);
                } finally {
                    level.releaseGenerationTask(chunkHash);
                }
            });
        } catch (Throwable throwable) {
            level.releaseGenerationTask(chunkHash);
            failDemand(chunkHash, throwable);
        }
    }

    private void completeChunk(GenerationRequest request, Chunk chunk) {
        if (!isSameChunkIdentity(chunk)) {
            schedule();
            return;
        }

        boolean transitioned = chunk.compareAndSetGenerationState(
                ChunkGenerationState.NEEDS_COMPLETION, ChunkGenerationState.COMPLETE);

        if (!transitioned && chunk.getGenerationState() != ChunkGenerationState.COMPLETE) {
            failDemand(request.key.chunkHash(), new IllegalStateException(
                    "Chunk (" + request.chunkX + ", " + request.chunkZ
                            + ") could not complete generation from state " + chunk.getGenerationState()));
            return;
        }

        if (!isSameChunkIdentity(chunk)) {
            schedule();
            return;
        }

        if (transitioned) {
            Level.GENERATED_CHUNK_COUNT.incrementAndGet();
        }

        if (isCurrentChunk(chunk)) {
            chunk.setAvailable(true);

            GenerationDemand demand = demands.get(request.key.chunkHash());
            if (demand != null) {
                completeReachedWaiters(demand, chunk);
                if (hasReachedTarget(chunk, demand)) {
                    completeDemand(demand, chunk);
                }
            }

            reevaluateAround(request.chunkX, request.chunkZ);
        }

        schedule();
    }

    private void finishTask(GenerationRequest request) {
        inFlight.decrementAndGet();
        level.releaseGenerationTask(request.key.chunkHash());
        schedule();
    }

    private void completeReachedWaiters(GenerationDemand demand, Chunk chunk) {
        final int state = chunk.getGenerationState().getNativeId();
        if (state >= terrainReadyState.getNativeId()) demand.terrainFuture.complete(chunk);
        if (state >= ChunkGenerationState.COMPLETE.getNativeId()) demand.completionFuture.complete(chunk);
    }

    private void completeDemand(GenerationDemand demand, Chunk chunk) {
        if (!demands.remove(demand.chunkHash, demand)) return;
        completeReachedWaiters(demand, chunk);
        releaseDriven(demand);
    }

    private void failDemand(long chunkHash, Throwable throwable) {
        GenerationDemand demand = demands.remove(chunkHash);
        if (demand != null) {
            releaseDriven(demand);
            demand.terrainFuture.completeExceptionally(throwable);
            demand.completionFuture.completeExceptionally(throwable);
        }
        failDemandsDriving(chunkHash, throwable);
    }

    /**
     * Fails every forced demand that drives the failed chunk. Such a demand waits for that chunk to
     * reach a later stage, which it no longer will, so it would wait forever and keep the chunks it
     * drives loaded.
     */
    private void failDemandsDriving(long chunkHash, Throwable throwable) {
        final GenerationDemand[] owners;
        synchronized (drivenChunks) {
            final List<GenerationDemand> driving = drivenChunks.get(chunkHash);
            if (driving == null) return;
            owners = driving.toArray(new GenerationDemand[0]);
        }

        final int chunkX = Level.getHashX(chunkHash);
        final int chunkZ = Level.getHashZ(chunkHash);
        for (GenerationDemand demand : owners) {
            failDemand(demand, new IllegalStateException(
                    "Chunk (" + chunkX + ", " + chunkZ + ") needed by demanded chunk (" + demand.chunkX + ", "
                            + demand.chunkZ + ") failed to generate", throwable));
        }
    }

    private void failDemand(GenerationDemand demand, Throwable throwable) {
        if (!demands.remove(demand.chunkHash, demand)) return;

        releaseDriven(demand);
        demand.terrainFuture.completeExceptionally(throwable);
        demand.completionFuture.completeExceptionally(throwable);
    }

    private List<ViewSource> snapshotViewSources() {
        final List<ViewSource> sources = new ArrayList<>(level.getPlayers().size());
        for (Player player : level.getPlayers().values()) {
            if (!player.isConnected()) continue;

            final float yawRadians = (float) Math.toRadians(player.getYaw());
            final float pitchRadians = (float) Math.toRadians(player.getPitch());
            final float horizontal = (float) Math.cos(pitchRadians);
            final float directionX = -(float) Math.sin(yawRadians) * horizontal;
            final float directionYAbs = Math.abs((float) Math.sin(pitchRadians));
            final float directionZ = (float) Math.cos(yawRadians) * horizontal;
            sources.add(new ViewSource(player.getChunkX(), player.getChunkZ(), directionX, directionYAbs, directionZ));
        }

        return sources;
    }

    private List<ChunkBuildOrderPolicy.RegionSource> snapshotRegionSources() {
        final var manager = level.getServer().getTickingAreaManager();
        if (manager == null || !manager.hasAreas()) return List.of();

        final List<ChunkBuildOrderPolicy.RegionSource> sources = new ArrayList<>();
        for (TickingArea area : manager.getTickingAreas(level)) {
            if (area.getDimensionId() != level.getDimensionData().getDimensionId() || area.getChunks().isEmpty()) continue;

            final List<TickingArea.ChunkPos> bounds = area.minAndMaxChunkPos();
            final TickingArea.ChunkPos min = bounds.get(0);
            final TickingArea.ChunkPos max = bounds.get(1);

            sources.add(ChunkBuildOrderPolicy.createRegionSource(
                    area.getUuid(), min.x, min.z, max.x, max.z, area.isCircle(), area.isPreload()));
        }

        return sources;
    }

    private int calculatePriority(
            GenerationRequest request,
            List<ViewSource> viewSources,
            List<ChunkBuildOrderPolicy.RegionSource> regionSources) {
        if (viewSources.isEmpty() && regionSources.isEmpty()) return 0;

        int best = Integer.MAX_VALUE;

        for (ViewSource source : viewSources) {
            ChunkBuildOrderPolicy.Priority priority = ChunkBuildOrderPolicy.calculate(
                    request.chunkX,
                    request.chunkZ,
                    source.chunkX(),
                    source.chunkZ(),
                    source.directionX(),
                    source.directionYAbs(),
                    source.directionZ());
            best = Math.min(best, priority.score());
        }

        for (ChunkBuildOrderPolicy.RegionSource source : regionSources) {
            best = Math.min(best, ChunkBuildOrderPolicy.calculateRegion(request.chunkX, request.chunkZ, source));
        }

        return best;
    }

    private record ViewSource(
            int chunkX,
            int chunkZ,
            float directionX,
            float directionYAbs,
            float directionZ) {}

    private record Candidate(GenerationRequest request, Chunk chunk, ChunkGenerationTask task, int priority) {
        private static final Comparator<Candidate> COMPARATOR =
                Comparator.comparingInt(Candidate::priority);
    }

    /**
     * Immutable key for one stable generation-state request.
     */
    private record GenerationRequestKey(long chunkHash, ChunkGenerationState state) {}

    /**
     * Tracks one immutable stable-state scheduling request.
     */
    private static final class GenerationRequest {
        private final GenerationRequestKey key;
        private final int chunkX;
        private final int chunkZ;
        private final ChunkGenerationState state;
        private final AtomicBoolean queued = new AtomicBoolean();

        private GenerationRequest(
                GenerationRequestKey key,
                int chunkX,
                int chunkZ,
                ChunkGenerationState state) {
            this.key = key;
            this.chunkX = chunkX;
            this.chunkZ = chunkZ;
            this.state = state;
        }
    }

    /**
     * Tracks API completion demand independently from scheduler stage requests.
     */
    private static final class GenerationDemand {
        private final int chunkX;
        private final int chunkZ;
        private final long chunkHash;
        private final CompletableFuture<IChunk> terrainFuture = new CompletableFuture<>();
        private final CompletableFuture<IChunk> completionFuture = new CompletableFuture<>();
        private final AtomicInteger targetNativeId;
        /** The state each chunk this demand drives has to reach; guarded by {@code drivenChunks}. */
        private final Long2IntOpenHashMap driven = new Long2IntOpenHashMap();
        /** Whether the driven chunks were released; guarded by {@code drivenChunks}. */
        private boolean drivenReleased;

        private GenerationDemand(
                int chunkX,
                int chunkZ,
                long chunkHash,
                ChunkGenerationState targetState) {
            this.chunkX = chunkX;
            this.chunkZ = chunkZ;
            this.chunkHash = chunkHash;
            this.targetNativeId = new AtomicInteger(targetState.getNativeId());
            this.driven.defaultReturnValue(-1);
        }

        private void upgradeTarget(ChunkGenerationState targetState) {
            targetNativeId.accumulateAndGet(targetState.getNativeId(), Math::max);
        }
    }
}
