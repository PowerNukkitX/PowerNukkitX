package org.powernukkitx.level;

import org.powernukkitx.Player;
import org.powernukkitx.Server;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.event.player.PlayerChunkRequestEvent;
import org.powernukkitx.event.player.PlayerPreChunkRequestEvent;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.math.BlockVector3;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongArrayPriorityQueue;
import it.unimi.dsi.fastutil.longs.LongComparator;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.protocol.bedrock.packet.NetworkChunkPublisherUpdatePacket;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
public final class PlayerChunkManager {


    /**
     * Chunks closer than this distance to the player are always considered to be in the field of view.
     */
    private static final double MIN_FOV_CHECK_DISTANCE = 4.0;

    /**
     * Timeout for asynchronously loading a chunk before retrying or generating it, in microseconds.
     */
    private static final long CHUNK_LOAD_TIMEOUT_MICROS = 10L;

    private static final double MIN_FOV_CHECK_DISTANCE_SQUARED = MIN_FOV_CHECK_DISTANCE * MIN_FOV_CHECK_DISTANCE;

    private int comparatorLoaderChunkX;
    private int comparatorLoaderChunkZ;
    private double comparatorDirX;
    private double comparatorDirZ;
    private double comparatorCosFov;

    private final LongComparator chunkDistanceAndFovComparator = new LongComparator() {
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

            long squaredDist1 = (long) dx1 * dx1 + (long) dz1 * dz1;
            long squaredDist2 = (long) dx2 * dx2 + (long) dz2 * dz2;

            boolean inFov1 = isInPlayerFov(dx1, dz1, squaredDist1);
            boolean inFov2 = isInPlayerFov(dx2, dz2, squaredDist2);

            if (inFov1 && !inFov2) return -1;
            if (!inFov1 && inFov2) return 1;

            return Long.compare(squaredDist1, squaredDist2);
        }

        private boolean isInPlayerFov(int dx, int dz, long squaredDistance) {
            if (squaredDistance < MIN_FOV_CHECK_DISTANCE_SQUARED) return true;

            double len = Math.sqrt(squaredDistance);

            double toChunkX = dx / len;
            double toChunkZ = dz / len;

            double dot = comparatorDirX * toChunkX + comparatorDirZ * toChunkZ;

            return dot >= comparatorCosFov;
        }
    };

    private final Player player;
    //holds all chunk hash values already sent in the previous tick
    private final @NotNull LongOpenHashSet sentChunks;
    //holds all chunk hash values to be sent this tick
    private final @NotNull LongOpenHashSet inRadiusChunks;
    private final int trySendChunkCountPerTick;
    private final LongArrayPriorityQueue chunkSendQueue;
    private final Long2ObjectOpenHashMap<CompletableFuture<IChunk>> chunkLoadingQueue;
    private final LongArrayPriorityQueue chunkReadyToSend;
    private long lastLoaderChunkPosHashed = Long.MAX_VALUE;

    public PlayerChunkManager(Player player) {
        this.player = player;
        this.sentChunks = new LongOpenHashSet();
        this.inRadiusChunks = new LongOpenHashSet();
        this.chunkSendQueue = new LongArrayPriorityQueue(player.getViewDistance() * player.getViewDistance(), chunkDistanceAndFovComparator);
        this.chunkLoadingQueue = new Long2ObjectOpenHashMap<>(player.getViewDistance() * player.getViewDistance());
        this.trySendChunkCountPerTick = player.getChunkSendCountPerTick();
        this.chunkReadyToSend = new LongArrayPriorityQueue(player.getViewDistance() * player.getViewDistance(), chunkDistanceAndFovComparator);
    }

    /**
     * Handle chunk loading when the player teleported
     */
    public synchronized void handleTeleport() {
        if (!player.isConnected()) return;
        refreshComparatorContext();
        BlockVector3 floor = player.asBlockVector3();
        updateInRadiusChunks(1, floor);
        removeOutOfRadiusChunks();
        updateInRadiusChunks(8, floor);
        pruneLoadingQueueOutOfRadius();
        pruneQueueOutOfRadius(chunkReadyToSend, true);
        updateChunkSendingQueue();
        loadQueuedChunks(8, true);
        sendChunk();
    }

    public synchronized void tick() {
        if (!player.isConnected()) return;
        refreshComparatorContext();
        long currentLoaderChunkPosHashed;
        BlockVector3 floor = player.asBlockVector3();
        if ((currentLoaderChunkPosHashed = Level.chunkHash(floor.x >> 4, floor.z >> 4)) != lastLoaderChunkPosHashed) {
            lastLoaderChunkPosHashed = currentLoaderChunkPosHashed;
            updateInRadiusChunks(player.getViewDistance(), floor);
            removeOutOfRadiusChunks();
            updateChunkSendingQueue();
        }
        loadQueuedChunks(trySendChunkCountPerTick, false);
        sendChunk();
    }

    public synchronized void handleViewDistanceChange() {
        if (!player.isConnected()) return;
        refreshComparatorContext();
        BlockVector3 floor = player.asBlockVector3();
        updateInRadiusChunks(player.getViewDistance(), floor);
        removeOutOfRadiusChunks();
        pruneQueueOutOfRadius(chunkSendQueue, false);
        pruneQueueOutOfRadius(chunkReadyToSend, true);
        pruneLoadingQueueOutOfRadius();
        updateChunkSendingQueue();
    }

    @ApiStatus.Internal
    public LongOpenHashSet getUsedChunks() {
        return sentChunks;
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

    private void updateChunkSendingQueue() {
        chunkSendQueue.clear();
        // Blocks that have already been sent will not be sent again
        LongIterator iter = inRadiusChunks.longIterator();
        while (iter.hasNext()) {
            long v = iter.nextLong();
            if (!sentChunks.contains(v)) {
                chunkSendQueue.enqueue(v);
            }
        }
    }

    private void updateInRadiusChunks(int viewDistance, BlockVector3 currentPos) {
        inRadiusChunks.clear();
        var loaderChunkX = currentPos.x >> 4;
        var loaderChunkZ = currentPos.z >> 4;
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
        }
    }

    private void loadQueuedChunks(int trySendChunkCountPerTick, boolean force) {
        if (chunkSendQueue.isEmpty()) return;
        int triedSendChunkCount = 0;
        LongOpenHashSet enqueue = new LongOpenHashSet();
        do {
            triedSendChunkCount++;
            long chunkHash = chunkSendQueue.dequeueLong();
            int chunkX = Level.getHashX(chunkHash);
            int chunkZ = Level.getHashZ(chunkHash);
            PlayerPreChunkRequestEvent event = new PlayerPreChunkRequestEvent(player, chunkX, chunkZ, force);
            Server.getInstance().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                continue;
            }
            var chunkTask = chunkLoadingQueue.computeIfAbsent(chunkHash, (hash) -> player.getLevel().getChunkAsync(chunkX, chunkZ));
            if (chunkTask.isDone()) {
                try {
                    IChunk chunk = chunkTask.get(CHUNK_LOAD_TIMEOUT_MICROS, TimeUnit.MICROSECONDS);
                    // Cached future may be stale - re-check in-memory map
                    if (chunk == null || !chunk.getChunkState().canSend()) {
                        chunk = player.level.getChunkIfLoaded(chunkX, chunkZ);
                    }
                    if (chunk == null || !chunk.getChunkState().canSend()) {
                        player.level.generateChunk(chunkX, chunkZ, force);
                        enqueue.add(chunkHash);
                        chunkLoadingQueue.remove(chunkHash);
                        continue;
                    }
                    chunkLoadingQueue.remove(chunkHash);
                    player.level.registerChunkLoader(player, chunkX, chunkZ, false);
                    chunkReadyToSend.enqueue(chunkHash);
                } catch (InterruptedException e) {
                    log.warn("Chunk loading interrupted for chunk ({}, {})", chunkX, chunkZ, e);
                } catch (ExecutionException e) {
                    log.warn("Chunk loading execution failed for chunk ({}, {})", chunkX, chunkZ, e);
                } catch (TimeoutException e) {
                    log.warn("Timeout while loading chunk ({} {})", chunkX, chunkZ);
                }
            } else {
                enqueue.add(chunkHash);
            }
        } while (!chunkSendQueue.isEmpty() && triedSendChunkCount < trySendChunkCountPerTick);
        enqueue.forEach(chunkSendQueue::enqueue);
    }

    private void sendChunk() {
        if (!chunkReadyToSend.isEmpty()) {
            final NetworkChunkPublisherUpdatePacket packet = new NetworkChunkPublisherUpdatePacket();
            packet.setNewPositionForView(player.asBlockVector3().toNetwork());
            packet.setNewRadiusForView(player.getViewDistance() << 4);
            player.sendPacket(packet);
            while (!chunkReadyToSend.isEmpty()) {
                long chunkHash = chunkReadyToSend.dequeueLong();
                if (!inRadiusChunks.contains(chunkHash)) {
                    sentChunks.remove(chunkHash);
                    unloadChunkForPlayer(chunkHash);
                    continue;
                }
                int chunkX = Level.getHashX(chunkHash);
                int chunkZ = Level.getHashZ(chunkHash);
                PlayerChunkRequestEvent ev = new PlayerChunkRequestEvent(player, chunkX, chunkZ);
                player.getServer().getPluginManager().callEvent(ev);
                player.level.requestChunk(chunkX, chunkZ, player);
                sentChunks.add(chunkHash);
            }
        }
        chunkReadyToSend.clear();
    }

    private void pruneQueueOutOfRadius(LongArrayPriorityQueue queue, boolean unloadChunkLoader) {
        if (queue.isEmpty()) return;
        LongOpenHashSet keep = new LongOpenHashSet();
        while (!queue.isEmpty()) {
            long chunkHash = queue.dequeueLong();
            if (inRadiusChunks.contains(chunkHash)) {
                keep.add(chunkHash);
            } else if (unloadChunkLoader) {
                sentChunks.remove(chunkHash);
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
    }

    private void refreshComparatorContext() {
        BlockVector3 floor = player.getPosition().asBlockVector3();
        this.comparatorLoaderChunkX = floor.x >> 4;
        this.comparatorLoaderChunkZ = floor.z >> 4;

        double yawRadians = Math.toRadians(player.getYaw());
        this.comparatorDirX = -Math.sin(yawRadians);
        this.comparatorDirZ = Math.cos(yawRadians);
        this.comparatorCosFov = Math.cos(Math.toRadians(player.getServer().getSettings().levelSettings().fieldOfView()));
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

    private boolean ifChunkNotInRadius(int chunkX, int chunkZ, int radius) {
        return chunkX * chunkX + chunkZ * chunkZ > radius * radius;
    }
}
