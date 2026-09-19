package org.powernukkitx.level;

import org.powernukkitx.Player;
import org.powernukkitx.Server;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.event.player.PlayerChunkRequestEvent;
import org.powernukkitx.event.player.PlayerPreChunkRequestEvent;
import org.powernukkitx.level.format.Chunk;
import org.powernukkitx.level.format.ChunkFinalizationState;
import org.powernukkitx.level.format.IChunk;

import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongArrayPriorityQueue;
import it.unimi.dsi.fastutil.longs.LongComparator;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.netty.channel.raknet.config.RakChannelOption;
import org.cloudburstmc.protocol.bedrock.packet.NetworkChunkPublisherUpdatePacket;
import org.powernukkitx.network.RakNetNetworkMetrics;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
public final class PlayerChunkManager {
    /**
     * Padding used by BDS when rasterizing a circular block-space view into chunk grid cells.
     */
    private static final float CHUNK_VIEW_CELL_PADDING = 1.7320508f;
    /**
     * Normal BDS minimum movement before rebuilding the chunk view region.
     */
    private static final int CHUNK_VIEW_MOVE_MIN_DISTANCE = 16;
    private static final long CHUNK_VIEW_MOVE_MIN_DISTANCE_SQUARED = (long) CHUNK_VIEW_MOVE_MIN_DISTANCE * CHUNK_VIEW_MOVE_MIN_DISTANCE;
    private static final float CHUNK_BUILD_DIRECTION_EPSILON = 0.0001f;
    /**
     * Timeout for asynchronously loading a chunk before retrying or generating it, in microseconds.
     */
    private static final long CHUNK_LOAD_TIMEOUT_MICROS = 10L;
    /*
     * BDS NetworkChunkPublisher suppresses publisher-region movement and
     * reports zero chunks sent while server chunks used by the client-side
     * generation path have been queued within the previous 250 ms.
     */
    private static final long SERVER_CHUNK_WAIT_NANOS = TimeUnit.MILLISECONDS.toNanos(250L);
    /*
     * BDS NetworkChunkPublisher keeps an additional 46-block acceptance/
     * retention skirt around the published radius.
     */
    private static final int CHUNK_PUBLISHER_RETENTION_PADDING = 46;
    private static final int INITIAL_SPAWN_CHUNK_RADIUS = 4;
    private static final int INITIAL_SPAWN_CHUNK_MANHATTAN_LIMIT = 5;
    /*
     * A normal SubChunk request usually follows its LevelChunk skeleton very
     * quickly. A singleton bottom-section request arriving much later is a
     * strong indication that the client is trying to complete a stale chunk
     * transaction while entering its ticking area.
     */
    private static final long STALE_CHUNK_RECOVERY_MIN_AGE_NANOS = TimeUnit.SECONDS.toNanos(10);
    /*
     * Bedrock requests all required SubChunks when a LevelChunk gets within
     * approximately four chunks of the player's client-side position.
     */
    private static final int STALE_CHUNK_RECOVERY_MAX_DISTANCE = 4;
    private int comparatorLoaderChunkX;
    private int comparatorLoaderChunkZ;
    private float comparatorDirX;
    private float comparatorDirYAbs;
    private float comparatorDirZ;
    private int levelChunksSentSinceStart;
    private long lastGenerationRequestQueuedNanos;
    private boolean waitingForServerChunks;
    private volatile Vector3i publisherPosition;
    private volatile int publisherRadius;
    private final LongComparator chunkBuildOrderComparator =
            new LongComparator() {
                @Override
                public int compare(long chunkHash1, long chunkHash2) {
                    int chunkX1 = Level.getHashX(chunkHash1);
                    int chunkZ1 = Level.getHashZ(chunkHash1);
                    int chunkX2 = Level.getHashX(chunkHash2);
                    int chunkZ2 = Level.getHashZ(chunkHash2);
                    int dx1 = chunkX1 - comparatorLoaderChunkX;
                    int dz1 = chunkZ1 - comparatorLoaderChunkZ;
                    int dx2 = chunkX2 - comparatorLoaderChunkX;
                    int dz2 = chunkZ2 - comparatorLoaderChunkZ;
                    int priority1 = getBdsChunkBuildPriority(dx1, dz1);
                    int priority2 = getBdsChunkBuildPriority(dx2, dz2);
                    if (priority1 != priority2) {
                        return Integer.compare(priority1, priority2);
                    }

                    long squaredDist1 = (long) dx1 * dx1 + (long) dz1 * dz1;
                    long squaredDist2 = (long) dx2 * dx2 + (long) dz2 * dz2;
                    return Long.compare(squaredDist1, squaredDist2);
                }

                private int getBdsChunkBuildPriority(int dx, int dz) {
                    final int squaredDistance = dx * dx + dz * dz;
                    if (squaredDistance == 0) {
                        return 0;
                    }

                    final float distance = (float) Math.sqrt((float) squaredDistance);
                    final float horizontalDirectionLength = (float) Math.sqrt(comparatorDirX * comparatorDirX + comparatorDirZ * comparatorDirZ);
                    float dot = 0.0f;
                    if (horizontalDirectionLength >= CHUNK_BUILD_DIRECTION_EPSILON && distance >= CHUNK_BUILD_DIRECTION_EPSILON) {
                        final float toChunkX = dx / distance;
                        final float toChunkZ = dz / distance;
                        final float directionX = comparatorDirX / horizontalDirectionLength;
                        final float directionZ = comparatorDirZ / horizontalDirectionLength;
                        dot = toChunkX * directionX + toChunkZ * directionZ;
                    }

                    float directionFactor = 1.0f;
                    if (dot > 0.0f) {
                        directionFactor = 0.25f + (1.0f - dot) * 0.75f;
                    }

                    final float priorityFactor = directionFactor + comparatorDirYAbs * (1.0f - directionFactor);
                    return (int) (distance * priorityFactor);
                }
            };
    private final Player player;
    // holds all chunk hash values already sent in the previous tick
    private final @NotNull LongOpenHashSet sentChunks;
    // holds all chunk hash values to be sent this tick
    private final @NotNull LongOpenHashSet inRadiusChunks;
    private final int trySendChunkCountPerTick;
    private final LongArrayPriorityQueue chunkSendQueue;
    private final Long2ObjectOpenHashMap<CompletableFuture<IChunk>> chunkLoadingQueue;
    private final LongArrayPriorityQueue chunkReadyToSend;
    private final LongOpenHashSet inFlightChunks;
    private final LongOpenHashSet pendingChunkLoaders;
    private final LongOpenHashSet requeueScratch;
    private final LongOpenHashSet pruneScratch;
    private final LongOpenHashSet generationDemandChunks;
    private final LongOpenHashSet generationDemandScratch;
    private long lastGenerationDemandChunkHash = Long.MIN_VALUE;
    private int lastGenerationDemandViewDistance = -1;
    private int lastGenerationDemandPublisherX = Integer.MIN_VALUE;
    private int lastGenerationDemandPublisherZ = Integer.MIN_VALUE;
    /*
     * Wall-clock timestamp of the most recent actual LevelChunk send for each
     * chunk still tracked by this player's publisher.
     */
    private final Long2LongOpenHashMap levelChunkSentAtNanos;
    /*
     * Chunks whose request-mode transaction has been explicitly re-armed.
     *
     * Keep this concurrent because SubChunk requests may be handled outside
     * the publisher's synchronized tick path.
     */
    private final Set<Long> recoveryChunks = ConcurrentHashMap.newKeySet();
    private int lastRegionUpdateX = Integer.MIN_VALUE;
    private int lastRegionUpdateY = Integer.MIN_VALUE;
    private int lastRegionUpdateZ = Integer.MIN_VALUE;
    private int lastRegionViewDistance = -1;
    public PlayerChunkManager(Player player) {
        this.player = player;
        this.sentChunks = new LongOpenHashSet();
        this.inRadiusChunks = new LongOpenHashSet();
        this.chunkSendQueue = new LongArrayPriorityQueue(player.getViewDistance() * player.getViewDistance(), chunkBuildOrderComparator);
        this.chunkLoadingQueue = new Long2ObjectOpenHashMap<>(player.getViewDistance() * player.getViewDistance());
        this.trySendChunkCountPerTick = player.getChunkSendCountPerTick();
        this.chunkReadyToSend = new LongArrayPriorityQueue(player.getViewDistance() * player.getViewDistance(), chunkBuildOrderComparator);
        this.inFlightChunks = new LongOpenHashSet();
        this.pendingChunkLoaders = new LongOpenHashSet();
        this.requeueScratch = new LongOpenHashSet();
        this.pruneScratch = new LongOpenHashSet();
        this.generationDemandChunks = new LongOpenHashSet(player.getViewDistance() * player.getViewDistance());
        this.generationDemandScratch = new LongOpenHashSet(player.getViewDistance() * player.getViewDistance());
        this.levelChunkSentAtNanos = new Long2LongOpenHashMap();
        this.levelChunkSentAtNanos.defaultReturnValue(0L);
        this.publisherPosition = Vector3i.from(player.getFloorX(), player.getFloorY(), player.getFloorZ());
        this.publisherRadius = player.getViewDistance() << 4;
    }

    /**
     * Handle chunk loading when the player teleported
     */
    public synchronized void handleTeleport() {
        if (!player.isConnected()) return;
        if (isServerChunkWaitActive()) return;

        refreshComparatorContext();
        int loaderChunkX = player.getChunkX();
        int loaderChunkZ = player.getChunkZ();
        updateInRadiusChunks(1, loaderChunkX, loaderChunkZ);
        removeOutOfRadiusChunks();
        updateInRadiusChunks(player.getViewDistance(), loaderChunkX, loaderChunkZ);
        pruneLoadingQueueOutOfRadius();
        pruneQueueOutOfRadius(chunkReadyToSend, true);
        updateChunkSendingQueue();
        markRegionUpdated();
        sendPublisherUpdate();
        loadQueuedChunks(getChunkSendBudget(), true);
        sendChunk();
    }

    public synchronized void tick() {
        if (!player.isConnected()) return;
        refreshComparatorContext();
        int loaderChunkX = player.getChunkX();
        int loaderChunkZ = player.getChunkZ();
        if (shouldMoveRegion()) {
            updateInRadiusChunks(player.getViewDistance(), loaderChunkX, loaderChunkZ);
            removeOutOfRadiusChunks();
            pruneLoadingQueueOutOfRadius();
            pruneQueueOutOfRadius(chunkReadyToSend, true);
            updateChunkSendingQueue();
            markRegionUpdated();
            sendPublisherUpdate();
        } else {
            invalidateChunkSendingQueuePriority();
        }

        updateGenerationDemand(loaderChunkX, loaderChunkZ);
        final int chunkPreparationBudget = player.locallyInitialized ? trySendChunkCountPerTick : getChunkSendBudget();
        loadQueuedChunks(chunkPreparationBudget, false);
        sendChunk();
    }

    public synchronized void handleViewDistanceChange() {
        if (!player.isConnected()) return;
        if (isServerChunkWaitActive()) return;

        refreshComparatorContext();
        updateInRadiusChunks(player.getViewDistance(), player.getChunkX(), player.getChunkZ());
        removeOutOfRadiusChunks();
        pruneQueueOutOfRadius(chunkSendQueue, false);
        pruneQueueOutOfRadius(chunkReadyToSend, true);
        pruneLoadingQueueOutOfRadius();
        updateChunkSendingQueue();
        markRegionUpdated();
        sendPublisherUpdate();
    }

    @ApiStatus.Internal
    public LongOpenHashSet getUsedChunks() {
        return sentChunks;
    }

    /**
     * Releases all chunk loaders registered for pending chunk requests.
     */
    @ApiStatus.Internal
    public synchronized void releasePendingChunkLoaders() {
        LongIterator iterator = pendingChunkLoaders.longIterator();
        while (iterator.hasNext()) {
            long chunkHash = iterator.nextLong();
            player.level.unregisterChunkLoader(player, Level.getHashX(chunkHash), Level.getHashZ(chunkHash), false);
            iterator.remove();
        }
    }

    @ApiStatus.Internal
    public LongOpenHashSet getInRadiusChunks() {
        return inRadiusChunks;
    }

    @ApiStatus.Internal
    public synchronized void addSendChunk(int x, int z) {
        refreshComparatorContext();
        chunkSendQueue.enqueue(Level.chunkHash(x, z));
    }

    @ApiStatus.Internal
    public synchronized boolean isSentChunk(long hash) {
        return sentChunks.contains(hash);
    }

    /**
     * Returns whether a chunk is currently inside the player's managed radius.
     *
     * @param hash chunk hash
     * @return whether the chunk is in radius
     */
    @ApiStatus.Internal
    public synchronized boolean isInRadiusChunk(long hash) {
        return inRadiusChunks.contains(hash);
    }

    /**
     * Cancels an in-flight chunk request and unloads its player-side state.
     *
     * @param chunkHash chunk hash
     */
    @ApiStatus.Internal
    public synchronized void cancelInFlightChunk(long chunkHash) {
        if (!inFlightChunks.remove(chunkHash)) return;
        unloadChunkForPlayer(chunkHash);
    }

    /**
     * Returns the weighted initial-chunk readiness score around the player.
     *
     * @return initial loaded-chunk score
     */
    @ApiStatus.Internal
    public synchronized int getInitialLoadedChunksScore() {
        int loadedScore = 0;
        final int centerX = player.getChunkX();
        final int centerZ = player.getChunkZ();
        for (int rx = -INITIAL_SPAWN_CHUNK_RADIUS; rx <= INITIAL_SPAWN_CHUNK_RADIUS; rx++) {
            for (int rz = -INITIAL_SPAWN_CHUNK_RADIUS; rz <= INITIAL_SPAWN_CHUNK_RADIUS; rz++) {
                if (Math.abs(rx) + Math.abs(rz) > INITIAL_SPAWN_CHUNK_MANHATTAN_LIMIT) continue;

                final IChunk chunk = player.getLevel().getChunkIfLoaded(centerX + rx, centerZ + rz);
                if (chunk == null) continue;

                loadedScore +=
                        switch (chunk.getFinalizationState()) {
                            case NEEDS_INSTATICKING -> 0;
                            case NEEDS_POPULATION -> 1;
                            case DONE -> {
                                if (chunk instanceof Chunk concreteChunk && !concreteChunk.isLightingReady()) {
                                    yield 1;
                                }

                                yield 2;
                            }
                        };
            }
        }

        return loadedScore;
    }

    private void updateGenerationDemand(int loaderChunkX, int loaderChunkZ) {
        /*
         * Never alter the initial connection synchronization path.
         */
        if (!player.locallyInitialized) return;

        final int viewDistance = player.getViewDistance();
        final long loaderChunkHash = Level.chunkHash(loaderChunkX, loaderChunkZ);
        final int publisherX = this.publisherPosition.getX();
        final int publisherZ = this.publisherPosition.getZ();
        /*
         * When the publisher is free to move, the normal inRadiusChunks path
         * already supplies generation demand. Keep this set synchronized with
         * that normal region and do not speculate.
         */
        if (!isServerChunkWaitActive()) {
            if (loaderChunkHash != this.lastGenerationDemandChunkHash
                    || viewDistance != this.lastGenerationDemandViewDistance
                    || publisherX != this.lastGenerationDemandPublisherX
                    || publisherZ != this.lastGenerationDemandPublisherZ) {
                this.generationDemandChunks.clear();
                this.generationDemandChunks.addAll(this.inRadiusChunks);
                this.lastGenerationDemandChunkHash = loaderChunkHash;
                this.lastGenerationDemandViewDistance = viewDistance;
                this.lastGenerationDemandPublisherX = publisherX;
                this.lastGenerationDemandPublisherZ = publisherZ;
            }

            return;
        }

        /*
         * Nothing relevant changed while the publisher is held.
         */
        if (loaderChunkHash == this.lastGenerationDemandChunkHash
                && viewDistance == this.lastGenerationDemandViewDistance
                && publisherX == this.lastGenerationDemandPublisherX
                && publisherZ == this.lastGenerationDemandPublisherZ) {
            return;
        }

        if (this.lastGenerationDemandChunkHash == Long.MIN_VALUE) {
            this.generationDemandChunks.clear();
            this.generationDemandChunks.addAll(this.inRadiusChunks);
        }

        final int retentionRadius = this.publisherRadius + CHUNK_PUBLISHER_RETENTION_PADDING;
        final long retentionRadiusSquared = (long) retentionRadius * retentionRadius;
        final LongOpenHashSet nextDemand = this.generationDemandScratch;
        nextDemand.clear();
        for (int rx = -viewDistance; rx <= viewDistance; rx++) {
            for (int rz = -viewDistance; rz <= viewDistance; rz++) {
                if (ifChunkNotInRadius(rx, rz, viewDistance)) {
                    continue;
                }

                final int chunkX = loaderChunkX + rx;
                final int chunkZ = loaderChunkZ + rz;
                final long chunkCenterX = ((long) chunkX << 4) + 8L;
                final long chunkCenterZ = ((long) chunkZ << 4) + 8L;
                final long publisherDx = chunkCenterX - publisherX;
                final long publisherDz = chunkCenterZ - publisherZ;
                if (publisherDx * publisherDx + publisherDz * publisherDz >= retentionRadiusSquared) continue;

                final long chunkHash = Level.chunkHash(chunkX, chunkZ);
                nextDemand.add(chunkHash);
                /*
                 * The previous live-demand window already owns this chunk,
                 * or the normal publisher path is already handling it.
                 */
                if (this.generationDemandChunks.contains(chunkHash) || this.inRadiusChunks.contains(chunkHash)) continue;

                final IChunk chunk = player.level.getChunkIfLoaded(chunkX, chunkZ);
                if (chunk != null && chunk.getFinalizationState() == ChunkFinalizationState.DONE) continue;

                /*
                 * Server-side generation demand only.
                 *
                 * Do not modify publisher state, inRadiusChunks, LevelChunk
                 * sending or SubChunk transactions here.
                 */
                player.level.generateChunk(chunkX, chunkZ, false);
            }
        }

        this.generationDemandChunks.clear();
        this.generationDemandChunks.addAll(nextDemand);
        this.lastGenerationDemandChunkHash = loaderChunkHash;
        this.lastGenerationDemandViewDistance = viewDistance;
        this.lastGenerationDemandPublisherX = publisherX;
        this.lastGenerationDemandPublisherZ = publisherZ;
    }

    private void updateChunkSendingQueue() {
        chunkSendQueue.clear();
        LongIterator iter = inRadiusChunks.longIterator();
        while (iter.hasNext()) {
            long v = iter.nextLong();
            if (!sentChunks.contains(v) && !inFlightChunks.contains(v)) {
                chunkSendQueue.enqueue(v);
            }
        }
    }

    private void invalidateChunkSendingQueuePriority() {
        if (!chunkSendQueue.isEmpty()) chunkSendQueue.changed();
    }

    private void updateInRadiusChunks(int viewDistance, int loaderChunkX, int loaderChunkZ) {
        inRadiusChunks.clear();
        for (int rx = -viewDistance; rx <= viewDistance; rx++) {
            for (int rz = -viewDistance; rz <= viewDistance; rz++) {
                if (ifChunkNotInRadius(rx, rz, viewDistance)) continue;
                var chunkX = loaderChunkX + rx;
                var chunkZ = loaderChunkZ + rz;
                var hashXZ = Level.chunkHash(chunkX, chunkZ);
                inRadiusChunks.add(hashXZ);
            }
        }
    }

    private void removeOutOfRadiusChunks() {
        LongArrayList toRemove = null;
        LongIterator iter = sentChunks.longIterator();
        while (iter.hasNext()) {
            long hash = iter.nextLong();
            if (!inRadiusChunks.contains(hash)) {
                if (toRemove == null) {
                    toRemove = new LongArrayList();
                }
                toRemove.add(hash);
            }
        }
        if (toRemove == null) {
            return;
        }
        // Unload blocks that are out of range
        for (int i = 0; i < toRemove.size(); i++) {
            long hash = toRemove.getLong(i);
            unloadChunkForPlayer(hash);
            // The intersection of the remaining sentChunks and inRadiusChunks
            sentChunks.remove(hash);
            levelChunkSentAtNanos.remove(hash);
            recoveryChunks.remove(hash);
        }
    }

    /**
     * Records completion of a LevelChunk send.
     *
     * @param chunkHash chunk hash
     * @param clientNeedsToRequestSubChunks whether SubChunk requests are expected from the client
     */
    @ApiStatus.Internal
    public synchronized void onLevelChunkSent(long chunkHash, boolean clientNeedsToRequestSubChunks) {
        this.levelChunksSentSinceStart++;
        if (clientNeedsToRequestSubChunks && !player.locallyInitialized) {
            this.lastGenerationRequestQueuedNanos = System.nanoTime();
            this.waitingForServerChunks = true;
        }

        this.inFlightChunks.remove(chunkHash);
        this.pendingChunkLoaders.remove(chunkHash);
        this.sentChunks.add(chunkHash);
        this.levelChunkSentAtNanos.put(chunkHash, System.nanoTime());
    }

    /**
     * Returns the number of LevelChunk packets counted since startup.
     *
     * @return sent LevelChunk count, or zero while waiting for initial SubChunk requests
     */
    @ApiStatus.Internal
    public synchronized int getLevelChunksSentSinceStart() {
        if (isServerChunkWaitActive())  return 0;
        return this.levelChunksSentSinceStart;
    }

    private boolean isServerChunkWaitActive() {
        if (!this.waitingForServerChunks) return false;
        return System.nanoTime() - this.lastGenerationRequestQueuedNanos < SERVER_CHUNK_WAIT_NANOS;
    }

    /**
     * Requeues a stale SubChunk transaction when the chunk is still eligible for recovery.
     *
     * @param chunkX chunk X
     * @param chunkZ chunk Z
     * @return whether recovery was scheduled
     */
    @ApiStatus.Internal
    public synchronized boolean tryRecoverStaleSubChunkTransaction(int chunkX, int chunkZ) {
        if (!player.locallyInitialized) return false;

        final long chunkHash = Level.chunkHash(chunkX, chunkZ);
        if (!sentChunks.contains(chunkHash)) return false;
        if (!inRadiusChunks.contains(chunkHash)) return false;
        if (recoveryChunks.contains(chunkHash)) return false;

        final long sentAt = levelChunkSentAtNanos.get(chunkHash);
        if (sentAt == 0L) return false;

        final long age = System.nanoTime() - sentAt;
        if (age < STALE_CHUNK_RECOVERY_MIN_AGE_NANOS) return false;

        final int distanceX = Math.abs(chunkX - player.getChunkX());
        final int distanceZ = Math.abs(chunkZ - player.getChunkZ());
        if (distanceX > STALE_CHUNK_RECOVERY_MAX_DISTANCE || distanceZ > STALE_CHUNK_RECOVERY_MAX_DISTANCE) return false;

        final IChunk chunk = player.getLevel().getChunkIfLoaded(chunkX, chunkZ);
        if (!(chunk instanceof Chunk concreteChunk) || chunk.getFinalizationState() != ChunkFinalizationState.DONE || !concreteChunk.isLightingReady()) {
            return false;
        }

        recoveryChunks.add(chunkHash);
        sentChunks.remove(chunkHash);
        inFlightChunks.remove(chunkHash);
        chunkLoadingQueue.remove(chunkHash);
        chunkSendQueue.enqueue(chunkHash);
        refreshComparatorContext();
        log.debug("Re-arming stale SubChunk transaction for chunk ({}, {}) after {} ms", chunkX, chunkZ, TimeUnit.NANOSECONDS.toMillis(age));
        return true;
    }

    /**
     * Returns whether a recovery send must bypass the normal chunk cache.
     *
     * @param chunkHash chunk hash
     * @return whether the cache should be bypassed
     */
    @ApiStatus.Internal
    public boolean shouldBypassChunkCache(long chunkHash) {
        return recoveryChunks.contains(chunkHash);
    }

    private int getChunkSendBudget() {
        final var metrics = player.getSession().getPeer().getChannel().config().getOption(RakChannelOption.RAK_METRICS);
        if (!(metrics instanceof RakNetNetworkMetrics networkMetrics)) {
            return trySendChunkCountPerTick;
        }

        final int baseBudget =
                switch (networkMetrics.getNetworkLoad()) {
                    case UNRESTRICTED -> 40;
                    case LOW -> 20;
                    case MEDIUM -> 8;
                    case HIGH -> 0;
                };
        if (baseBudget == 0) return 0;

        final int activePlayerCount = player.getLevel().getPlayers().size();
        if (activePlayerCount <= 0) {
            return baseBudget;
        }

        final int dividedBudget = baseBudget >= activePlayerCount ? baseBudget / activePlayerCount : 0;
        return dividedBudget >= 2 ? dividedBudget : 1;
    }

    private void loadQueuedChunks(int trySendChunkCountPerTick, boolean force) {
        if (chunkSendQueue.isEmpty()) return;
        int triedSendChunkCount = 0;
        LongOpenHashSet enqueue = requeueScratch;
        enqueue.clear();
        do {
            triedSendChunkCount++;
            long chunkHash = chunkSendQueue.dequeueLong();
            if (sentChunks.contains(chunkHash) || inFlightChunks.contains(chunkHash)) continue;

            int chunkX = Level.getHashX(chunkHash);
            int chunkZ = Level.getHashZ(chunkHash);
            if (!PlayerPreChunkRequestEvent.getHandlers().isEmpty()) {
                PlayerPreChunkRequestEvent event = new PlayerPreChunkRequestEvent(player, chunkX, chunkZ, force);
                Server.getInstance().getPluginManager().callEvent(event);
                if (event.isCancelled()) continue;
            }

            if (pendingChunkLoaders.add(chunkHash)) {
                player.level.registerChunkLoader(player, chunkX, chunkZ, false);
            }

            var chunkTask = chunkLoadingQueue.get(chunkHash);
            if (chunkTask == null) {
                chunkTask = player.getLevel().getChunkAsync(chunkX, chunkZ);
                chunkLoadingQueue.put(chunkHash, chunkTask);
            }
            if (chunkTask.isDone()) {
                try {
                    IChunk loadedChunk =
                            chunkTask.get(CHUNK_LOAD_TIMEOUT_MICROS, TimeUnit.MICROSECONDS);
                    /*
                     * Resolves the current LevelChunk from its ChunkView
                     * on every queued-send attempt.
                     */
                    IChunk chunk = player.level.getChunkIfLoaded(chunkX, chunkZ);
                    if (loadedChunk != null && chunk != loadedChunk) {
                        chunkLoadingQueue.remove(chunkHash);
                        enqueue.add(chunkHash);
                        continue;
                    }

                    if (chunk == null || chunk.getFinalizationState() != ChunkFinalizationState.DONE) {
                        player.level.generateChunk(chunkX, chunkZ, force);
                        enqueue.add(chunkHash);
                        chunkLoadingQueue.remove(chunkHash);
                        continue;
                    }

                    if (chunk instanceof Chunk concreteChunk && !concreteChunk.isLightingReady()) {
                        player.level.prepareChunkLightingForSend(chunkX, chunkZ, force);
                        enqueue.add(chunkHash);
                        continue;
                    }

                    chunkLoadingQueue.remove(chunkHash);
                    if (inFlightChunks.add(chunkHash)) {
                        chunkReadyToSend.enqueue(chunkHash);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    chunkLoadingQueue.remove(chunkHash);
                    enqueue.add(chunkHash);
                    log.warn("Chunk loading interrupted for chunk ({}, {})", chunkX, chunkZ, e);
                } catch (ExecutionException e) {
                    chunkLoadingQueue.remove(chunkHash);
                    enqueue.add(chunkHash);
                    log.warn(
                            "Chunk loading execution failed for chunk ({}, {})", chunkX, chunkZ, e);
                } catch (TimeoutException e) {
                    chunkLoadingQueue.remove(chunkHash);
                    enqueue.add(chunkHash);
                    log.warn("Timeout while loading chunk ({} {})", chunkX, chunkZ);
                }
            } else {
                enqueue.add(chunkHash);
            }
        } while (!chunkSendQueue.isEmpty() && triedSendChunkCount < trySendChunkCountPerTick);
        enqueue.forEach(chunkSendQueue::enqueue);
    }

    /**
     * Sends the current chunk publisher state while the player is initializing.
     */
    @ApiStatus.Internal
    public void tickPublisher() {
        if (!player.isConnected() || player.locallyInitialized) return;
        sendPublisherUpdate();
    }

    private void sendPublisherUpdate() {
        final NetworkChunkPublisherUpdatePacket packet = new NetworkChunkPublisherUpdatePacket();
        packet.setNewPositionForView(this.publisherPosition);
        packet.setNewRadiusForView(this.publisherRadius);
        player.sendPacket(packet);
    }

    private void sendChunk() {
        if (chunkReadyToSend.isEmpty()) return;

        final int sendBudget = getChunkSendBudget();
        if (sendBudget <= 0) return;

        final LongArrayList chunksToSend = new LongArrayList(sendBudget);
        while (!chunkReadyToSend.isEmpty() && chunksToSend.size() < sendBudget) {
            final long chunkHash = chunkReadyToSend.dequeueLong();
            if (!inRadiusChunks.contains(chunkHash)) {
                sentChunks.remove(chunkHash);
                inFlightChunks.remove(chunkHash);
                unloadChunkForPlayer(chunkHash);
                continue;
            }

            chunksToSend.add(chunkHash);
        }

        if (chunksToSend.isEmpty()) return;

        for (int i = 0; i < chunksToSend.size(); i++) {
            final long chunkHash = chunksToSend.getLong(i);
            final int chunkX = Level.getHashX(chunkHash);
            final int chunkZ = Level.getHashZ(chunkHash);
            if (!PlayerChunkRequestEvent.getHandlers().isEmpty()) {
                PlayerChunkRequestEvent ev = new PlayerChunkRequestEvent(player, chunkX, chunkZ);
                player.getServer().getPluginManager().callEvent(ev);
            }

            player.level.requestChunk(chunkX, chunkZ, player);
        }
    }

    private void pruneQueueOutOfRadius(LongArrayPriorityQueue queue, boolean unloadChunkLoader) {
        if (queue.isEmpty()) return;
        LongOpenHashSet keep = pruneScratch;
        keep.clear();
        while (!queue.isEmpty()) {
            long chunkHash = queue.dequeueLong();
            if (inRadiusChunks.contains(chunkHash)) {
                keep.add(chunkHash);
            } else if (unloadChunkLoader) {
                sentChunks.remove(chunkHash);
                inFlightChunks.remove(chunkHash);
                levelChunkSentAtNanos.remove(chunkHash);
                recoveryChunks.remove(chunkHash);
                unloadChunkForPlayer(chunkHash);
            }
        }
        keep.forEach(queue::enqueue);
    }

    private void pruneLoadingQueueOutOfRadius() {
        LongIterator iterator = chunkLoadingQueue.keySet().iterator();
        while (iterator.hasNext()) {
            long chunkHash = iterator.nextLong();
            if (!inRadiusChunks.contains(chunkHash)) {
                iterator.remove();
            }
        }

        LongIterator pendingIterator = pendingChunkLoaders.longIterator();
        while (pendingIterator.hasNext()) {
            long chunkHash = pendingIterator.nextLong();
            if (inRadiusChunks.contains(chunkHash)) continue;

            pendingIterator.remove();
            player.level.unregisterChunkLoader(player, Level.getHashX(chunkHash), Level.getHashZ(chunkHash));
        }
    }

    private void refreshComparatorContext() {
        this.comparatorLoaderChunkX = player.getChunkX();
        this.comparatorLoaderChunkZ = player.getChunkZ();
        final float yawRadians = (float) Math.toRadians(player.getYaw());
        final float pitchRadians = (float) Math.toRadians(player.getPitch());
        final float horizontal = (float) Math.cos(pitchRadians);
        this.comparatorDirX = -(float) Math.sin(yawRadians) * horizontal;
        this.comparatorDirYAbs = Math.abs((float) Math.sin(pitchRadians));
        this.comparatorDirZ = (float) Math.cos(yawRadians) * horizontal;
    }

    private boolean shouldMoveRegion() {
        if (isServerChunkWaitActive()) return false;
        if (this.lastRegionUpdateX == Integer.MIN_VALUE || this.lastRegionViewDistance != player.getViewDistance()) return true;

        final long dx = (long) player.getFloorX() - this.lastRegionUpdateX;
        final long dy = (long) player.getFloorY() - this.lastRegionUpdateY;
        final long dz = (long) player.getFloorZ() - this.lastRegionUpdateZ;
        return dx * dx + dy * dy + dz * dz >= CHUNK_VIEW_MOVE_MIN_DISTANCE_SQUARED;
    }

    private void markRegionUpdated() {
        this.lastRegionUpdateX = player.getFloorX();
        this.lastRegionUpdateY = player.getFloorY();
        this.lastRegionUpdateZ = player.getFloorZ();
        this.lastRegionViewDistance = player.getViewDistance();
        this.publisherPosition = Vector3i.from(this.lastRegionUpdateX, this.lastRegionUpdateY, this.lastRegionUpdateZ);
        this.publisherRadius = this.lastRegionViewDistance << 4;
        this.waitingForServerChunks = false;
    }

    private void unloadChunkForPlayer(long hash) {
        pendingChunkLoaders.remove(hash);
        int x = Level.getHashX(hash);
        int z = Level.getHashZ(hash);
        if (player.level.unregisterChunkLoader(player, x, z)) {
            for (Entity entity : player.level.getChunkEntities(x, z).values()) {
                if (entity != player) {
                    entity.despawnFrom(player);
                }
            }
        }
    }

    private boolean ifChunkNotInRadius(int chunkX, int chunkZ, int radius) {
        final float effectiveRadius = (radius * 2 + 1) * 0.5f + CHUNK_VIEW_CELL_PADDING;
        final float distanceSquared = (float) chunkX * chunkX + (float) chunkZ * chunkZ;
        return distanceSquared >= effectiveRadius * effectiveRadius;
    }
}
