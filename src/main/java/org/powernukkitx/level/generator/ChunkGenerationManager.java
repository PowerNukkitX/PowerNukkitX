package org.powernukkitx.level.generator;
import org.powernukkitx.Player;
import org.powernukkitx.level.ChunkBuildOrderPolicy;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.format.Chunk;
import org.powernukkitx.level.format.IChunk;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

/**
 * Schedules and tracks chunk generation work for a level provider. It limits concurrent jobs, resolves stage
 * dependencies, and exposes generation state for pending and active chunks.
 *
 * @author Curse
 */
@Slf4j
public final class ChunkGenerationManager {
    private static final float CHUNK_VIEW_CELL_PADDING = 1.7320508f;
    private static final int NO_VIEW_SOURCE_PRIORITY = Integer.MAX_VALUE / 2;
    private final Level level;
    private final Generator generator;
    private final Consumer<IChunk> completionCallback;
    private final ForkJoinPool executor;
    private final int maxInFlight;
    private final int candidateWindow;
    private final ChunkGenerationState terrainReadyState;
    private final AtomicInteger inFlight = new AtomicInteger();
    private final AtomicLong requestSequence = new AtomicLong();
    private final ReentrantLock schedulerLock = new ReentrantLock();
    private final Map<ChunkGenerationState, ChunkGenerationTask> tasksByState = new EnumMap<>(ChunkGenerationState.class);
    private final ConcurrentHashMap<Long, GenerationRequest> requests = new ConcurrentHashMap<>();
    private final ConcurrentLinkedDeque<GenerationRequest> pending = new ConcurrentLinkedDeque<>();
    private final ConcurrentHashMap<Long, CompletableFuture<IChunk>> dependencyLoads = new ConcurrentHashMap<>();

    /**
     * Creates a new ChunkGenerationManager instance.
     *
     * @param level value for this API
     * @param generator value for this API
     * @param completionCallback value for this API
     */
    public ChunkGenerationManager(Level level, Generator generator, Consumer<IChunk> completionCallback) {
        this.level = level;
        this.generator = generator;
        this.completionCallback = completionCallback;
        this.executor = level.getServer().getComputeThreadPool();
        final int workerCount = Math.max(1, executor.getParallelism());
        /*
         * BDS:
         *     in-flight task ceiling = 2 * workers
         *     candidate window       = 6 * workers
         */
        this.maxInFlight = workerCount * 2;
        this.candidateWindow = workerCount * 6;
        for (ChunkGenerationTask task : generator.getGenerationTasks()) {
            ChunkGenerationTask previous = tasksByState.put(task.stableState(), task);
            if (previous != null) {
                throw new IllegalStateException("Generator '" + generator.getName() + "' declares more than one generation task for state " + task.stableState());
            }
        }

        ChunkGenerationTask terrainTask = tasksByState.get(ChunkGenerationState.NEEDS_GENERATION);
        if (terrainTask == null) {
            throw new IllegalStateException("Generator '" + generator.getName() + "' does not declare a terrain generation task");
        }

        this.terrainReadyState = terrainTask.successState();
    }

    /**
     * Requests chunk generation.
     *
     * @param chunkX value for this API
     * @param chunkZ value for this API
     * @param force value for this API
     * @return the requested value
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

    private CompletableFuture<IChunk> requestToState(
            int chunkX, int chunkZ, ChunkGenerationState targetState, boolean force) {
        final long chunkHash = Level.chunkHash(chunkX, chunkZ);
        IChunk loaded = level.getChunkIfLoaded(chunkX, chunkZ);
        if (loaded instanceof Chunk chunk && hasReachedTarget(chunk, targetState)) {
            return CompletableFuture.completedFuture(chunk);
        }

        GenerationRequest request =
                requests.compute(
                        chunkHash,
                        (hash, existing) -> {
                            if (existing != null) {
                                existing.upgradeTarget(targetState);
                                if (force) existing.force = true;
                                return existing;
                            }
                            return new GenerationRequest(chunkX, chunkZ, chunkHash, requestSequence.getAndIncrement(), force, targetState);
                        });
        enqueue(request);
        schedule();
        if (targetState == ChunkGenerationState.COMPLETE) {
            return request.completionFuture;
        }

        return request.terrainFuture;
    }

    private static boolean hasReachedTarget(Chunk chunk, ChunkGenerationState targetState) {
        return chunk.getGenerationState().getNativeId() >= targetState.getNativeId();
    }

    private static boolean hasReachedTarget(Chunk chunk, GenerationRequest request) {
        return chunk.getGenerationState().getNativeId() >= request.targetNativeId.get();
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
        if (requests.containsKey(chunkHash)) return true;

        IChunk chunk = level.getChunkIfLoaded(chunkX, chunkZ);
        return chunk instanceof Chunk concreteChunk && concreteChunk.getGenerationState().isActive();
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
        return requests.size();
    }

    /**
     * Returns the generation concurrency limit.
     * @return the requested value
     */
    public int getMaxInFlight() {
        return maxInFlight;
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

        ChunkGenerationTask task = resolveTask(chunk);
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

        try {
            generator.runGenerationTask(chunk, task);
        } catch (Throwable throwable) {
            chunk.compareAndSetGenerationState(task.activeState(), task.failureState());
            GenerationRequest request = requests.get(chunk.getIndex());
            if (request != null) {
                failRequest(request, throwable);
            }

            throw new IllegalStateException(
                    "Synchronous terrain generation failed for chunk (" + chunk.getX() + ", " + chunk.getZ() + ") in level '" + level.getFolderName() + "'", throwable);
        }

        if (!chunk.compareAndSetGenerationState(task.activeState(), task.successState())) {
            throw new IllegalStateException(
                    "Synchronous terrain generation completed for chunk (" + chunk.getX() + ", " + chunk.getZ() + ") but state changed from " + task.activeState() + " to " + chunk.getGenerationState());
        }

        if (task.successState() == ChunkGenerationState.COMPLETE) {
            Level.GENERATED_CHUNK_COUNT.incrementAndGet();
            try {
                completionCallback.accept(chunk);
            } catch (Throwable throwable) {
                log.error("Chunk generation completion callback failed for synchronously generated chunk ({}, {}) in level '{}'",
                            chunk.getX(), chunk.getZ(), level.getFolderName(), throwable);
            }
        }

        notifySynchronousProgress(chunk);
        return chunk;
    }

    private void notifySynchronousProgress(Chunk chunk) {
        GenerationRequest request = requests.get(chunk.getIndex());
        if (request != null) {
            completeReachedWaiters(request, chunk);
            if (hasReachedTarget(chunk, request)) {
                completeRequest(request, chunk, false);
            } else {
                enqueue(request);
            }
        }

        wakeNeighborRequests(chunk.getX(), chunk.getZ());
        schedule();
    }

    private void enqueue(GenerationRequest request) {
        if (requests.get(request.chunkHash) != request) return;
        if (!request.queued.compareAndSet(false, true)) return;

        pending.offerLast(request);
    }

    private void wakeNeighborRequests(int chunkX, int chunkZ) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) continue;

                GenerationRequest request = requests.get(Level.chunkHash(chunkX + dx, chunkZ + dz));
                if (request != null) enqueue(request);
            }
        }
    }

    private void schedule() {
        if (!schedulerLock.tryLock()) return;

        try {
            int availableSlots = maxInFlight - inFlight.get();
            if (availableSlots <= 0) return;
            if (pending.isEmpty()) return;

            final List<ViewSource> viewSources = snapshotViewSources();
            final List<Candidate> candidates = new ArrayList<>(candidateWindow);
            final List<GenerationRequest> deferred = new ArrayList<>(candidateWindow);
            int scanned = 0;
            while (scanned < candidateWindow) {
                GenerationRequest request = pending.pollFirst();
                if (request == null) break;

                request.queued.set(false);
                scanned++;
                if (requests.get(request.chunkHash) != request) continue;

                Chunk chunk = resolveChunk(request);
                if (chunk == null) continue;

                if (hasReachedTarget(chunk, request)) {
                    completeRequest(request, chunk, false);
                    continue;
                }

                ChunkGenerationState state = chunk.getGenerationState();
                if (state.isActive()) continue;

                ChunkGenerationTask task = resolveTask(chunk);
                if (hasReachedTarget(chunk, request)) {
                    completeRequest(request, chunk, false);
                    continue;
                }

                if (task == null) {
                    state = chunk.getGenerationState();
                    if (state.isActive()) continue;

                    failRequest(request, new IllegalStateException("No generation task for chunk (" + request.chunkX + ", " + request.chunkZ + ") in generator '" + generator.getName() + "' at state " + state));
                    continue;
                }

                DependencyStatus dependencyStatus = checkDependencies(request, task);
                if (dependencyStatus == DependencyStatus.WAITING_FOR_LOAD) continue;

                if (dependencyStatus == DependencyStatus.WAITING_FOR_STATE) {
                    deferred.add(request);
                    continue;
                }

                candidates.add(new Candidate(request, chunk, task, calculatePriority(request, viewSources)));
            }

            candidates.sort(Candidate.COMPARATOR);
            int launched = 0;
            for (Candidate candidate : candidates) {
                if (launched >= availableSlots) {
                    deferred.add(candidate.request());
                    continue;
                }

                if (launch(candidate)) {
                    launched++;
                } else {
                    deferred.add(candidate.request());
                }
            }

            for (GenerationRequest request : deferred) {
                enqueue(request);
            }
        } finally {
            schedulerLock.unlock();
        }
    }

    private Chunk resolveChunk(GenerationRequest request) {
        IChunk loaded = level.getChunkIfLoaded(request.chunkX, request.chunkZ);
        if (loaded != null) {
            if (!(loaded instanceof Chunk chunk)) {
                failRequest(request, new IllegalStateException("Chunk generation manager requires Chunk, got " + loaded.getClass().getName()));
                return null;
            }

            return chunk;
        }

        CompletableFuture<IChunk> future = request.chunkLoadFuture;
        if (future == null) {
            synchronized (request) {
                future = request.chunkLoadFuture;
                if (future == null) {
                    future = level.getChunkAsync(request.chunkX, request.chunkZ, true);
                    request.chunkLoadFuture = future;
                    future.whenComplete(
                            (chunk, throwable) -> {
                                if (throwable != null) {
                                    failRequest(request, throwable);
                                    return;
                                }

                                enqueue(request);
                                wakeNeighborRequests(request.chunkX, request.chunkZ);
                                schedule();
                            });
                }
            }
        }

        if (!future.isDone() || future.isCompletedExceptionally()) {
            return null;
        }

        IChunk chunk = future.getNow(null);
        if (chunk == null) {
            return null;
        }

        if (!(chunk instanceof Chunk concreteChunk)) {
            failRequest(request, new IllegalStateException("Chunk generation manager requires Chunk, got " + chunk.getClass().getName()));
            return null;
        }

        return concreteChunk;
    }

    /**
     * ChunkFinalizationState only persists:<p>
     * <p>
     * NEEDS_INSTATICKING<p>
     * NEEDS_POPULATION<p>
     * DONE
     * <p>
     * NEEDS_POPULATION therefore restores runtime state 2.<p>
     * <p>
     * Overworld has an actual Structure PP state at 2.<p>
     * Nether and TheEnd do not, so state 2 must be normalized to their first post-terrain state 4.
     */
    private ChunkGenerationTask resolveTask(Chunk chunk) {
        ChunkGenerationState state = chunk.getGenerationState();
        ChunkGenerationTask task = tasksByState.get(state);
        if (task != null) return task;

        if (state == ChunkGenerationState.NEEDS_STRUCTURE_PP) {
            ChunkGenerationTask populationTask = tasksByState.get(ChunkGenerationState.NEEDS_POPULATION);
            if (populationTask != null) {
                if (chunk.compareAndSetGenerationState(ChunkGenerationState.NEEDS_STRUCTURE_PP, ChunkGenerationState.NEEDS_POPULATION)) {
                    return populationTask;
                }
                return tasksByState.get(chunk.getGenerationState());
            }
        }

        return null;
    }

    private DependencyStatus checkDependencies(GenerationRequest request, ChunkGenerationTask task) {
        if (task.dependency() == ChunkGenerationDependency.NONE) return DependencyStatus.READY;

        boolean waitingForLoad = false;
        boolean waitingForState = false;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                final int neighborX = request.chunkX + dx;
                final int neighborZ = request.chunkZ + dz;
                IChunk neighbor = level.getChunkIfLoaded(neighborX, neighborZ);
                if (neighbor == null) {
                    if (task.dependency() == ChunkGenerationDependency.NEIGHBORHOOD_GENERATED) {
                        ensureGenerationDemand(neighborX, neighborZ, terrainReadyState);
                    } else {
                        ensureDependencyLoaded(request, neighborX, neighborZ);
                    }

                    waitingForLoad = true;
                    continue;
                }

                if (task.dependency() == ChunkGenerationDependency.NEIGHBORHOOD_GENERATED) {
                    if (!(neighbor instanceof Chunk neighborChunk)) {
                        failRequest(request, new IllegalStateException("Neighbor chunk (" + neighborX + ", " + neighborZ + ") is not a Chunk"));
                        return DependencyStatus.WAITING_FOR_LOAD;
                    }

                    if (neighborChunk.getGenerationState().getNativeId() < terrainReadyState.getNativeId()) {
                        ensureGenerationDemand(neighborX, neighborZ, terrainReadyState);
                        waitingForState = true;
                    }

                    continue;
                }

                if (task.dependency() != ChunkGenerationDependency.NEIGHBORHOOD_STATE_8) continue;

                if (!(neighbor instanceof Chunk neighborChunk)) {
                    failRequest(request, new IllegalStateException("Neighbor chunk (" + neighborX + ", " + neighborZ + ") is not a Chunk"));
                    return DependencyStatus.WAITING_FOR_LOAD;
                }

                if (neighborChunk.getGenerationState().getNativeId() < 8) {
                    ensureGenerationDemand(neighborX, neighborZ, ChunkGenerationState.NEEDS_NEIGHBOR_UPGRADE);
                    waitingForState = true;
                }
            }
        }

        if (waitingForLoad) {
            return DependencyStatus.WAITING_FOR_LOAD;
        }

        if (waitingForState) {
            return DependencyStatus.WAITING_FOR_STATE;
        }

        return DependencyStatus.READY;
    }

    private void ensureDependencyLoaded(GenerationRequest owner, int chunkX, int chunkZ) {
        final long chunkHash = Level.chunkHash(chunkX, chunkZ);
        CompletableFuture<IChunk> future = dependencyLoads.get(chunkHash);
        if (future == null) {
            CompletableFuture<IChunk> created = level.getChunkAsync(chunkX, chunkZ, true);
            CompletableFuture<IChunk> existing = dependencyLoads.putIfAbsent(chunkHash, created);
            if (existing == null) {
                future = created;
                final CompletableFuture<IChunk> registered = created;
                registered.whenComplete((chunk, throwable) -> dependencyLoads.remove(chunkHash, registered));
            } else {
                future = existing;
            }
        }

        if (!owner.waitingLoads.add(chunkHash)) return;

        future.whenComplete(
                (chunk, throwable) -> {
                    owner.waitingLoads.remove(chunkHash);
                    if (throwable != null) {
                        failRequest(owner, throwable);
                        return;
                    }

                    wakeNeighborRequests(chunkX, chunkZ);
                    schedule();
                });
    }

    private void ensureGenerationDemand(int chunkX, int chunkZ, ChunkGenerationState targetState) {
        final long chunkHash = Level.chunkHash(chunkX, chunkZ);
        IChunk loaded = level.getChunkIfLoaded(chunkX, chunkZ);
        if (loaded instanceof Chunk chunk && hasReachedTarget(chunk, targetState)) return;

        GenerationRequest request =
                requests.compute(
                        chunkHash,
                        (hash, existing) -> {
                            if (existing != null) {
                                existing.upgradeTarget(targetState);
                                return existing;
                            }

                            return new GenerationRequest(
                                    chunkX,
                                    chunkZ,
                                    chunkHash,
                                    requestSequence.getAndIncrement(),
                                    false,
                                    targetState);
                        });
        enqueue(request);
    }

    private boolean launch(Candidate candidate) {
        final GenerationRequest request = candidate.request();
        final Chunk chunk = candidate.chunk();
        final ChunkGenerationTask task = candidate.task();
        if (!chunk.compareAndSetGenerationState(task.stableState(), task.activeState())) return false;

        inFlight.incrementAndGet();
        try {
            executor.execute(() -> runTask(request, chunk, task));
            return true;
        } catch (Throwable throwable) {
            chunk.compareAndSetGenerationState(task.activeState(), task.failureState());
            inFlight.decrementAndGet();
            failRequest(request, throwable);
            return false;
        }
    }

    private void runTask(GenerationRequest request, Chunk chunk, ChunkGenerationTask task) {
        Throwable failure = null;
        try {
            generator.runGenerationTask(chunk, task);
        } catch (Throwable throwable) {
            failure = throwable;
        }

        if (failure != null) {
            boolean rolledBack =
                    chunk.compareAndSetGenerationState(task.activeState(), task.failureState());
            if (!rolledBack) {
                log.error("Could not roll generation state back for chunk ({}, {}) in level '{}' after task '{}': expected {}, actual {}",
                            request.chunkX, request.chunkZ, level.getFolderName(), task.name(), task.activeState(), chunk.getGenerationState());
            }

            log.error("Chunk generation task '{}' failed for chunk ({}, {}) in level '{}'; rolled back {} -> {}",
                        task.name(), request.chunkX, request.chunkZ, level.getFolderName(), task.activeState(), task.failureState(), failure);
            failRequest(request, failure);
            finishTask();
            return;
        }

        if (!chunk.compareAndSetGenerationState(task.activeState(), task.successState())) {
            IllegalStateException stateFailure =
                    new IllegalStateException("Chunk (" + request.chunkX + ", " + request.chunkZ + ") completed generation task '" + task.name() + "' but state changed from " + task.activeState() + " to " + chunk.getGenerationState());
            failRequest(request, stateFailure);
            finishTask();
            return;
        }

        completeReachedWaiters(request, chunk);
        if (task.successState() == ChunkGenerationState.COMPLETE) {
            Level.GENERATED_CHUNK_COUNT.incrementAndGet();
            completeRequest(request, chunk, true);
        } else if (hasReachedTarget(chunk, request)) {
            completeRequest(request, chunk, false);
        } else {
            enqueue(request);
        }

        wakeNeighborRequests(request.chunkX, request.chunkZ);
        finishTask();
    }

    /**
     * Immediately re-enters generation scheduling when a task
     * completes instead of waiting for the next server tick.
     */
    private void finishTask() {
        inFlight.decrementAndGet();
        schedule();
    }

    private void completeReachedWaiters(GenerationRequest request, Chunk chunk) {
        final int state = chunk.getGenerationState().getNativeId();
        if (state >= terrainReadyState.getNativeId()) request.terrainFuture.complete(chunk);
        if (state >= ChunkGenerationState.COMPLETE.getNativeId()) request.completionFuture.complete(chunk);
    }

    private void completeRequest(GenerationRequest request, Chunk chunk, boolean notifyCompletion) {
        if (!requests.remove(request.chunkHash, request)) return;

        if (notifyCompletion) {
            try {
                completionCallback.accept(chunk);
            } catch (Throwable throwable) {
                log.error("Chunk generation completion callback failed for chunk ({}, {}) in level '{}'",
                            request.chunkX, request.chunkZ, level.getFolderName(), throwable);
            }
        }

        completeReachedWaiters(request, chunk);
    }

    private void failRequest(GenerationRequest request, Throwable throwable) {
        if (!requests.remove(request.chunkHash, request)) return;

        request.terrainFuture.completeExceptionally(throwable);
        request.completionFuture.completeExceptionally(throwable);
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
            sources.add(new ViewSource(player.getChunkX(), player.getChunkZ(), player.getViewDistance(), directionX, directionYAbs, directionZ));
        }

        return sources;
    }

    private GenerationPriority calculatePriority(GenerationRequest request, List<ViewSource> viewSources) {
        ChunkBuildOrderPolicy.Priority best = null;
        for (ViewSource source : viewSources) {
            final int dx = request.chunkX - source.chunkX();
            final int dz = request.chunkZ - source.chunkZ();
            if (!isInsideView(dx, dz, source.viewDistance())) continue;

            ChunkBuildOrderPolicy.Priority priority = ChunkBuildOrderPolicy.calculate(request.chunkX, request.chunkZ, source.chunkX(), source.chunkZ(), source.directionX(), source.directionYAbs(), source.directionZ());
            if (best == null || priority.compareTo(best) < 0) {
                best = priority;
            }
        }

        if (best == null) {
            return new GenerationPriority(NO_VIEW_SOURCE_PRIORITY, Long.MAX_VALUE);
        }

        return new GenerationPriority(best.score(), best.squaredDistance());
    }

    private static boolean isInsideView(int dx, int dz, int viewDistance) {
        final float effectiveRadius = (viewDistance * 2 + 1) * 0.5f + CHUNK_VIEW_CELL_PADDING;
        final float distanceSquared = (float) dx * dx + (float) dz * dz;
        return distanceSquared < effectiveRadius * effectiveRadius;
    }

    private enum DependencyStatus {
        READY,
        WAITING_FOR_LOAD,
        WAITING_FOR_STATE
    }

    private record ViewSource(
            int chunkX,
            int chunkZ,
            int viewDistance,
            float directionX,
            float directionYAbs,
            float directionZ) {}

    private record GenerationPriority(int score, long squaredDistance) {}

    private record Candidate(GenerationRequest request, Chunk chunk, ChunkGenerationTask task, GenerationPriority priority) {
        private static final Comparator<Candidate> COMPARATOR =
                (first, second) -> {
                    int comparison = Integer.compare(first.priority.score(), second.priority.score());
                    if (comparison != 0)  return comparison;

                    /*
                     * Same spatial priority:
                     * finish the chunk that has progressed farther first.
                     */
                    comparison = Integer.compare(second.task.stableState().getNativeId(), first.task.stableState().getNativeId());
                    if (comparison != 0) return comparison;

                    comparison = Long.compare(first.priority.squaredDistance(), second.priority.squaredDistance());
                    if (comparison != 0) return comparison;

                    return Long.compare(first.request.sequence, second.request.sequence);
                };
    }

    private static final class GenerationRequest {
        private final int chunkX;
        private final int chunkZ;
        private final long chunkHash;
        private final long sequence;
        private final CompletableFuture<IChunk> terrainFuture = new CompletableFuture<>();
        private final CompletableFuture<IChunk> completionFuture = new CompletableFuture<>();
        private final AtomicBoolean queued = new AtomicBoolean();
        private final AtomicInteger targetNativeId;
        private final Set<Long> waitingLoads = ConcurrentHashMap.newKeySet();
        private volatile boolean force;
        private volatile CompletableFuture<IChunk> chunkLoadFuture;
        private GenerationRequest(
                int chunkX,
                int chunkZ,
                long chunkHash,
                long sequence,
                boolean force,
                ChunkGenerationState targetState) {
            this.chunkX = chunkX;
            this.chunkZ = chunkZ;
            this.chunkHash = chunkHash;
            this.sequence = sequence;
            this.force = force;
            this.targetNativeId = new AtomicInteger(targetState.getNativeId());
        }

        private void upgradeTarget(ChunkGenerationState targetState) {
            targetNativeId.accumulateAndGet(targetState.getNativeId(), Math::max);
        }
    }
}
