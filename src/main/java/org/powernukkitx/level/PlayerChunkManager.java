package org.powernukkitx.level;

import org.powernukkitx.Player;
import org.powernukkitx.Server;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.event.player.PlayerChunkRequestEvent;
import org.powernukkitx.event.player.PlayerPreChunkRequestEvent;
import org.powernukkitx.level.format.Chunk;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.format.leveldb.LevelDBProvider;
import org.powernukkitx.network.process.cache.ClientBlobCacheManager;

import org.cloudburstmc.math.vector.Vector2i;
import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.protocol.bedrock.packet.NetworkChunkPublisherUpdatePacket;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongArrayPriorityQueue;
import it.unimi.dsi.fastutil.longs.LongComparator;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;

import java.lang.ref.WeakReference;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class PlayerChunkManager {
    private static final int CHUNK_VIEW_MOVE_MIN_DISTANCE = 16;
    private static final long CHUNK_VIEW_MOVE_MIN_DISTANCE_SQUARED = (long) CHUNK_VIEW_MOVE_MIN_DISTANCE * CHUNK_VIEW_MOVE_MIN_DISTANCE;
    private static final float CHUNK_VIEW_CELL_PADDING = 1.7320508f;
    private static final long CHUNK_LOAD_TIMEOUT_MICROS = 10L;
    private static final int INITIAL_SPAWN_CHUNK_RADIUS = 4;
    private static final int INITIAL_SPAWN_CHUNK_MANHATTAN_LIMIT = 5;
    private static final int SERVER_VIEW_PADDING = 6;
    private static final int MIN_SERVER_TICK_DISTANCE = 4;
    private static final int MAX_SERVER_TICK_DISTANCE = 12;
    private int levelChunksSentSinceStart;
    private volatile Vector3i publisherPosition;
    private volatile int publisherRadius;
    private final LongComparator chunkDistanceComparator = (chunkHash1, chunkHash2) -> {
        final int centerX = publisherPosition.getX() >> 4;
        final int centerZ = publisherPosition.getZ() >> 4;
        final int dx1 = Level.getHashX(chunkHash1) - centerX;
        final int dz1 = Level.getHashZ(chunkHash1) - centerZ;
        final int dx2 = Level.getHashX(chunkHash2) - centerX;
        final int dz2 = Level.getHashZ(chunkHash2) - centerZ;
        final long squaredDistance1 = (long) dx1 * dx1 + (long) dz1 * dz1;
        final long squaredDistance2 = (long) dx2 * dx2 + (long) dz2 * dz2;
        return Long.compare(squaredDistance1, squaredDistance2);
    };
    private final Player player;
    // holds all chunk hash values already sent in the previous tick
    private final @NotNull LongOpenHashSet sentChunks;
    // holds all chunk hash values to be sent this tick
    private final @NotNull LongOpenHashSet inRadiusChunks;
    private final LongOpenHashSet serverBuiltChunks;
    private final LongArrayPriorityQueue chunkSendQueue;
    private final Long2ObjectOpenHashMap<CompletableFuture<IChunk>> chunkLoadingQueue;
    private final Long2ObjectOpenHashMap<WeakReference<Chunk>> pendingChunks;
    private final LongArrayPriorityQueue chunkReadyToSend;
    private final LongOpenHashSet inFlightChunks;
    private final LongOpenHashSet serverViewChunks;
    private final LongOpenHashSet serverViewScratch;
    private final LongOpenHashSet requeueScratch;
    private final LongOpenHashSet queueScratch;
    private int lastServerViewChunkX = Integer.MIN_VALUE;
    private int lastServerViewChunkZ = Integer.MIN_VALUE;
    private int lastServerViewDistance = -1;
    private int lastRegionUpdateX = Integer.MIN_VALUE;
    private int lastRegionUpdateY = Integer.MIN_VALUE;
    private int lastRegionUpdateZ = Integer.MIN_VALUE;
    private int lastRegionViewDistance = -1;

    public PlayerChunkManager(Player player) {
        this.player = player;
        final int side = player.getViewDistance() * 2 + 1;
        final int initialCapacity = side * side;
        this.sentChunks = new LongOpenHashSet(initialCapacity);
        this.inRadiusChunks = new LongOpenHashSet(initialCapacity);
        this.serverBuiltChunks = new LongOpenHashSet(initialCapacity);
        this.chunkSendQueue = new LongArrayPriorityQueue(initialCapacity, chunkDistanceComparator);
        this.chunkLoadingQueue = new Long2ObjectOpenHashMap<>(initialCapacity);
        this.pendingChunks = new Long2ObjectOpenHashMap<>(initialCapacity);
        this.chunkReadyToSend = new LongArrayPriorityQueue(initialCapacity, chunkDistanceComparator);
        this.inFlightChunks = new LongOpenHashSet();
        this.serverViewChunks = new LongOpenHashSet(initialCapacity);
        this.serverViewScratch = new LongOpenHashSet(initialCapacity);
        this.requeueScratch = new LongOpenHashSet();
        this.queueScratch = new LongOpenHashSet();
        this.publisherPosition = Vector3i.from(player.getFloorX(), player.getFloorY(), player.getFloorZ());
        this.publisherRadius = player.getViewDistance() << 4;
    }

    /**
     * Handle chunk loading when the player teleported
     */
    public synchronized void handleTeleport() {
        if (!player.isConnected()) return;

        int loaderChunkX = player.getChunkX();
        int loaderChunkZ = player.getChunkZ();
        updateServerViewChunks(loaderChunkX, loaderChunkZ, true);

        updateInRadiusChunks(player.getViewDistance(), loaderChunkX, loaderChunkZ);
        removeOutOfRadiusChunks();
        markRegionUpdated();
        reprioritizeQueue(chunkReadyToSend);
        updateChunkSendingQueue();
        sendPublisherUpdate();
        loadQueuedChunks(true);
        sendChunk();
    }

    public synchronized void tick() {
        if (!player.isConnected()) return;

        int loaderChunkX = player.getChunkX();
        int loaderChunkZ = player.getChunkZ();
        updateServerViewChunks(loaderChunkX, loaderChunkZ, false);

        if (shouldMoveRegion()) {
            updateInRadiusChunks(player.getViewDistance(), loaderChunkX, loaderChunkZ);
            removeOutOfRadiusChunks();
            markRegionUpdated();
            reprioritizeQueue(chunkReadyToSend);
            updateChunkSendingQueue();
            sendPublisherUpdate();
        }

        loadQueuedChunks(false);
        sendChunk();
    }

    public synchronized void handleViewDistanceChange() {
        if (!player.isConnected()) return;

        updateServerViewChunks(player.getChunkX(), player.getChunkZ(), false);

        updateInRadiusChunks(player.getViewDistance(), player.getChunkX(), player.getChunkZ());
        removeOutOfRadiusChunks();
        markRegionUpdated();
        reprioritizeQueue(chunkReadyToSend);
        updateChunkSendingQueue();
        sendPublisherUpdate();
    }

    @ApiStatus.Internal
    public LongOpenHashSet getUsedChunks() {
        return sentChunks;
    }

    /**
     * Releases pending publisher state and retained server chunk views.
     */
    @ApiStatus.Internal
    public synchronized void releasePendingChunkLoaders() {
        chunkSendQueue.clear();
        chunkLoadingQueue.clear();
        pendingChunks.clear();
        chunkReadyToSend.clear();
        inFlightChunks.clear();
        serverBuiltChunks.clear();

        LongIterator iterator = serverViewChunks.longIterator();
        while (iterator.hasNext()) {
            player.level.releaseChunkView(iterator.nextLong());
            iterator.remove();
        }

        player.level.completeChunkViewAcquisition();
        serverViewScratch.clear();
        lastServerViewChunkX = Integer.MIN_VALUE;
        lastServerViewChunkZ = Integer.MIN_VALUE;
        lastServerViewDistance = -1;
    }

    @ApiStatus.Internal
    public LongOpenHashSet getInRadiusChunks() {
        return inRadiusChunks;
    }

    @ApiStatus.Internal
    public synchronized void addSendChunk(int x, int z) {
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
     * Returns whether a chunk belongs to the player's current physical server view.
     *
     * @param hash chunk hash
     * @return whether the physical server view contains the chunk
     */
    @ApiStatus.Internal
    public synchronized boolean isInServerViewChunk(long hash) {
        return serverViewChunks.contains(hash);
    }

    /**
     * Cancels an in-flight chunk request and unloads its player-side state.
     *
     * @param chunkHash chunk hash
     */
    @ApiStatus.Internal
    public synchronized void cancelInFlightChunk(long chunkHash) {
        if (!inFlightChunks.remove(chunkHash)) return;
        pendingChunks.remove(chunkHash);
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

                final IChunk chunk = player.getLevel().getPhysicalChunkIfLoaded(centerX + rx, centerZ + rz);
                if (chunk == null || chunk instanceof Chunk concreteChunk && concreteChunk.isDiscarded()) continue;

                loadedScore +=
                        switch (chunk.getGenerationState()) {
                            case NEEDS_GENERATION, GENERATING -> 0;
                            case COMPLETE -> 2;
                            default -> 1;
                        };
            }
        }

        return loadedScore;
    }

    private void updateChunkSendingQueue() {
        chunkSendQueue.clear();

        LongIterator iter = chunkLoadingQueue.keySet().iterator();
        while (iter.hasNext()) {
            long v = iter.nextLong();

            if (!serverViewChunks.contains(v)) {
                iter.remove();
                continue;
            }

            if (!sentChunks.contains(v) && !inFlightChunks.contains(v)) {
                chunkSendQueue.enqueue(v);
            }
        }

        iter = inRadiusChunks.longIterator();
        while (iter.hasNext()) {
            long v = iter.nextLong();
            if (!sentChunks.contains(v) && !inFlightChunks.contains(v) && !chunkLoadingQueue.containsKey(v)) {
                chunkSendQueue.enqueue(v);
            }
        }
    }

    private void updateInRadiusChunks(int viewDistance, int loaderChunkX, int loaderChunkZ) {
        inRadiusChunks.clear();
        for (int rx = -viewDistance; rx <= viewDistance; rx++) {
            for (int rz = -viewDistance; rz <= viewDistance; rz++) {
                if (!isInChunkViewRadius(rx, rz, viewDistance)) continue;
                inRadiusChunks.add(Level.chunkHash(loaderChunkX + rx, loaderChunkZ + rz));
            }
        }
    }

    private static boolean isInChunkViewRadius(int offsetX, int offsetZ, int viewDistance) {
        final float effectiveRadius = (viewDistance * 2 + 1) * 0.5f + CHUNK_VIEW_CELL_PADDING;
        final float distanceSquared = (float) offsetX * offsetX + (float) offsetZ * offsetZ;
        return distanceSquared < effectiveRadius * effectiveRadius;
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
        }
    }

    /**
     * Records completion of a LevelChunk send.
     *
     * @param chunkHash chunk hash
     */
    @ApiStatus.Internal
    public synchronized void onLevelChunkSent(long chunkHash) {
        this.levelChunksSentSinceStart++;
        this.inFlightChunks.remove(chunkHash);
        this.pendingChunks.remove(chunkHash);
        player.level.registerChunkLoader(player, Level.getHashX(chunkHash), Level.getHashZ(chunkHash), false);
        this.sentChunks.add(chunkHash);
    }

    synchronized void recordServerBuiltChunk(long chunkHash) {
        this.serverBuiltChunks.add(chunkHash);
    }

    /**
     * Returns the number of LevelChunk packets counted since startup.
     *
     * @return sent LevelChunk count
     */
    @ApiStatus.Internal
    public synchronized int getLevelChunksSentSinceStart() {
        return this.levelChunksSentSinceStart;
    }

    private int getChunkSendBudget() {
        return player.getServer().getChunkPublisherBudgetController().acquireChunkSendBudget(
                player,
                chunkReadyToSend.size(),
                player.getServer().getNetwork().getNetworkPressure(player)
        );
    }

    private void loadQueuedChunks(boolean force) {
        if (chunkSendQueue.isEmpty()) return;

        LongOpenHashSet enqueue = requeueScratch;
        enqueue.clear();

        while (!chunkSendQueue.isEmpty()) {
            long chunkHash = chunkSendQueue.dequeueLong();
            if (sentChunks.contains(chunkHash) || inFlightChunks.contains(chunkHash)) continue;

            if (!serverViewChunks.contains(chunkHash)) {
                chunkLoadingQueue.remove(chunkHash);
                pendingChunks.remove(chunkHash);
                cancelInFlightChunk(chunkHash);
                continue;
            }

            int chunkX = Level.getHashX(chunkHash);
            int chunkZ = Level.getHashZ(chunkHash);
            var chunkTask = chunkLoadingQueue.get(chunkHash);

            if (chunkTask == null) {
                if (!inRadiusChunks.contains(chunkHash)) continue;

                if (!PlayerPreChunkRequestEvent.getHandlers().isEmpty()) {
                    PlayerPreChunkRequestEvent event = new PlayerPreChunkRequestEvent(player, chunkX, chunkZ, force);
                    Server.getInstance().getPluginManager().callEvent(event);
                    if (event.isCancelled()) continue;
                }

                chunkTask = player.getLevel().getChunkAsync(chunkX, chunkZ);
                chunkLoadingQueue.put(chunkHash, chunkTask);
            }

            if (!chunkTask.isDone()) {
                enqueue.add(chunkHash);
                continue;
            }

            try {
                IChunk loadedChunk = chunkTask.get(CHUNK_LOAD_TIMEOUT_MICROS, TimeUnit.MICROSECONDS);
                chunkLoadingQueue.remove(chunkHash);
                IChunk currentChunk = player.level.getChunkIfLoaded(chunkX, chunkZ);

                if (!(loadedChunk instanceof Chunk concreteChunk)
                        || currentChunk != concreteChunk
                        || concreteChunk.isDiscarded()) {
                    if (inRadiusChunks.contains(chunkHash)) {
                        enqueue.add(chunkHash);
                    }
                    continue;
                }

                if (!concreteChunk.isGenerationComplete() || !concreteChunk.isLightingReady()) {
                    player.level.prepareChunkLightingForSend(chunkX, chunkZ, force);
                }

                pendingChunks.put(chunkHash, new WeakReference<>(concreteChunk));
                if (inFlightChunks.add(chunkHash)) {
                    chunkReadyToSend.enqueue(chunkHash);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                chunkLoadingQueue.remove(chunkHash);
                if (inRadiusChunks.contains(chunkHash)) {
                    enqueue.add(chunkHash);
                }
                log.warn("Chunk loading interrupted for chunk ({}, {})", chunkX, chunkZ, e);
            } catch (ExecutionException e) {
                chunkLoadingQueue.remove(chunkHash);
                if (inRadiusChunks.contains(chunkHash)) {
                    enqueue.add(chunkHash);
                }
                log.warn("Chunk loading execution failed for chunk ({}, {})", chunkX, chunkZ, e);
            } catch (TimeoutException e) {
                chunkLoadingQueue.remove(chunkHash);
                if (inRadiusChunks.contains(chunkHash)) {
                    enqueue.add(chunkHash);
                }
                log.warn("Timeout while loading chunk ({} {})", chunkX, chunkZ);
            }
        }

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

    private synchronized void sendPublisherUpdate() {
        final NetworkChunkPublisherUpdatePacket packet = new NetworkChunkPublisherUpdatePacket();
        packet.setNewPositionForView(this.publisherPosition);
        packet.setNewRadiusForView(this.publisherRadius);

        final LongIterator iterator = this.serverBuiltChunks.longIterator();
        while (iterator.hasNext()) {
            final long chunkHash = iterator.nextLong();
            packet.getServerBuiltChunksList().add(Vector2i.from(Level.getHashX(chunkHash), Level.getHashZ(chunkHash)));
        }
        this.serverBuiltChunks.clear();

        player.sendPacket(packet);
    }

    private void sendChunk() {
        if (chunkReadyToSend.isEmpty() || isCacheTransferBlocked()) return;

        final int sendBudget = getChunkSendBudget();
        if (sendBudget <= 0) return;

        final LongArrayList deferred = new LongArrayList();
        int sent = 0;

        try {
            while (!chunkReadyToSend.isEmpty() && sent < sendBudget) {
                final long chunkHash = chunkReadyToSend.dequeueLong();
                final Chunk chunk = resolvePendingChunk(chunkHash);

                if (chunk == null) {
                    if (inRadiusChunks.contains(chunkHash) && !sentChunks.contains(chunkHash)) {
                        chunkSendQueue.enqueue(chunkHash);
                    }
                    continue;
                }

                if (!inRadiusChunks.contains(chunkHash)) {
                    deferred.add(chunkHash);
                    continue;
                }

                final int chunkX = Level.getHashX(chunkHash);
                final int chunkZ = Level.getHashZ(chunkHash);

                if (!PlayerChunkRequestEvent.getHandlers().isEmpty()) {
                    PlayerChunkRequestEvent event = new PlayerChunkRequestEvent(player, chunkX, chunkZ);
                    player.getServer().getPluginManager().callEvent(event);
                }

                if (player.level.trySendChunk(chunk, player)) {
                    sent++;
                } else if (resolvePendingChunk(chunkHash) == chunk) {
                    deferred.add(chunkHash);
                } else if (inRadiusChunks.contains(chunkHash) && !sentChunks.contains(chunkHash)) {
                    chunkSendQueue.enqueue(chunkHash);
                }
            }
        } finally {
            for (int i = 0; i < deferred.size(); i++) {
                chunkReadyToSend.enqueue(deferred.getLong(i));
            }

            player.getServer().getChunkPublisherBudgetController()
                    .releaseUnusedChunkBudget(player, sendBudget - sent);
        }
    }

    private boolean isCacheTransferBlocked() {
        if (!(player.getLevel().getProvider() instanceof LevelDBProvider)) return false;
        if (player.getClientChainData() == null
                || !player.getClientChainData().isCompatibleWithClientSideChunkGen()) return false;
        if (!ClientBlobCacheManager.isEnabled(player.getSession())) return false;

        return ClientBlobCacheManager.getActiveTransferCount(player.getSession())
                > ClientBlobCacheManager.getMaximumConcurrentTransfers(player.getSession());
    }

    private void reprioritizeQueue(LongArrayPriorityQueue queue) {
        if (queue.isEmpty()) return;

        LongOpenHashSet queued = queueScratch;
        queued.clear();

        while (!queue.isEmpty()) {
            queued.add(queue.dequeueLong());
        }

        queued.forEach(queue::enqueue);
    }

    private Chunk resolvePendingChunk(long chunkHash) {
        if (!serverViewChunks.contains(chunkHash)) {
            pendingChunks.remove(chunkHash);
            inFlightChunks.remove(chunkHash);
            return null;
        }

        WeakReference<Chunk> reference = pendingChunks.get(chunkHash);
        if (reference == null) {
            inFlightChunks.remove(chunkHash);
            return null;
        }

        Chunk chunk = reference.get();
        if (chunk == null
                || chunk.isDiscarded()
                || player.level.getChunkIfLoaded(chunk.getX(), chunk.getZ()) != chunk) {
            pendingChunks.remove(chunkHash);
            inFlightChunks.remove(chunkHash);
            return null;
        }

        return chunk;
    }

    private int getServerViewDistance() {
        int tickDistance = Math.max(
                MIN_SERVER_TICK_DISTANCE,
                Math.min(MAX_SERVER_TICK_DISTANCE, player.getServer().getSettings().chunkSettings().tickRadius()));

        return Math.max(player.getViewDistance(), tickDistance) + SERVER_VIEW_PADDING;
    }

    private void updateServerViewChunks(int centerX, int centerZ, boolean force) {
        final int viewDistance = getServerViewDistance();

        if (!force
                && centerX == lastServerViewChunkX
                && centerZ == lastServerViewChunkZ
                && viewDistance == lastServerViewDistance) {
            return;
        }

        serverViewScratch.clear();

        for (int dx = -viewDistance; dx <= viewDistance; dx++) {
            for (int dz = -viewDistance; dz <= viewDistance; dz++) {
                if (!isInChunkViewRadius(dx, dz, viewDistance)) continue;
                serverViewScratch.add(Level.chunkHash(centerX + dx, centerZ + dz));
            }
        }

        LongIterator iterator = serverViewScratch.longIterator();
        while (iterator.hasNext()) {
            long hash = iterator.nextLong();
            if (serverViewChunks.contains(hash)) {
                player.level.reacquireChunkView(hash);
            } else {
                player.level.retainChunkView(hash);
            }
        }

        iterator = serverViewChunks.longIterator();
        while (iterator.hasNext()) {
            long hash = iterator.nextLong();
            if (!serverViewScratch.contains(hash)) {
                player.level.releaseChunkView(hash);
            }
        }

        serverViewChunks.clear();
        serverViewChunks.addAll(serverViewScratch);
        player.level.completeChunkViewAcquisition();

        lastServerViewChunkX = centerX;
        lastServerViewChunkZ = centerZ;
        lastServerViewDistance = viewDistance;
    }

    private boolean shouldMoveRegion() {
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
    }

    private void unloadChunkForPlayer(long hash) {
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
}
