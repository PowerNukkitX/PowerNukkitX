package cn.nukkit.level;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.player.PlayerChunkRequestEvent;
import cn.nukkit.event.player.PlayerPreChunkRequestEvent;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.network.protocol.NetworkChunkPublisherUpdatePacket;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayPriorityQueue;
import it.unimi.dsi.fastutil.longs.LongComparator;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
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
    private double cachedDirX, cachedDirZ, cachedCosFov;
    private double cachedYaw = Double.NaN;

    /**
     * Timeout for asynchronously loading a chunk before retrying or generating it, in microseconds.
     */
    private static final long CHUNK_LOAD_TIMEOUT_MICROS = 10L;

    private final LongComparator chunkDistanceAndFovComparator = new LongComparator() {
        @Override
        public int compare(long chunkHash1, long chunkHash2) {
            refreshFovCache();
            BlockVector3 floor = player.getPosition().asBlockVector3();
            int loaderChunkX = floor.x >> 4;
            int loaderChunkZ = floor.z >> 4;

            int dx1 = Level.getHashX(chunkHash1) - loaderChunkX;
            int dz1 = Level.getHashZ(chunkHash1) - loaderChunkZ;
            int dx2 = Level.getHashX(chunkHash2) - loaderChunkX;
            int dz2 = Level.getHashZ(chunkHash2) - loaderChunkZ;

            boolean inFov1 = isInPlayerFov(dx1, dz1);
            boolean inFov2 = isInPlayerFov(dx2, dz2);

            if (inFov1 && !inFov2) return -1;
            if (!inFov1 && inFov2) return 1;

            return Double.compare(Math.hypot(dx1, dz1), Math.hypot(dx2, dz2));
        }

        private boolean isInPlayerFov(int dx, int dz) {
            double len = Math.sqrt(dx * dx + dz * dz);
            if (len < MIN_FOV_CHECK_DISTANCE) return true;
            double dot = cachedDirX * (dx / len) + cachedDirZ * (dz / len);
            return dot >= cachedCosFov;
        }
    };

    private final Player player;
    //保存着上tick已经发送的全部区块hash值
    private final @NotNull LongOpenHashSet sentChunks;
    //保存着这tick将要发送的全部区块hash值
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
        BlockVector3 floor = player.asBlockVector3();
        updateInRadiusChunks(1, floor);
        removeOutOfRadiusChunks();
        updateInRadiusChunks(8, floor);
        updateChunkSendingQueue();
        loadQueuedChunks(8, true);
        sendChunk();
    }

    public synchronized void tick() {
        if (!player.isConnected()) return;
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
        chunkSendQueue.enqueue(Level.chunkHash(x, z));
    }

    @ApiStatus.Internal
    public synchronized boolean isSentChunk(long hash) {
        return sentChunks.contains(hash);
    }

    private void updateChunkSendingQueue() {
        chunkSendQueue.clear();
        LongIterator iter = inRadiusChunks.iterator();
        while (iter.hasNext()) {
            long hash = iter.nextLong();
            if (!sentChunks.contains(hash)) {
                chunkSendQueue.enqueue(hash);
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
        LongOpenHashSet toRemove = new LongOpenHashSet();
        LongIterator iter = sentChunks.iterator();
        while (iter.hasNext()) {
            long hash = iter.nextLong();
            if (!inRadiusChunks.contains(hash)) {
                toRemove.add(hash);
            }
        }
        LongIterator removeIter = toRemove.iterator();
        while (removeIter.hasNext()) {
            long hash = removeIter.nextLong();
            unloadChunkForPlayer(hash);
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
            NetworkChunkPublisherUpdatePacket ncp = new NetworkChunkPublisherUpdatePacket();
            ncp.position = player.asBlockVector3();
            ncp.radius = player.getViewDistance() << 4;
            player.dataPacket(ncp);
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

    private void refreshFovCache() {
        double yaw = player.getYaw();
        if (yaw == cachedYaw) return;
        cachedYaw = yaw;
        double yawRad = Math.toRadians(yaw);
        cachedDirX = -Math.sin(yawRad);
        cachedDirZ = Math.cos(yawRad);
        cachedCosFov = Math.cos(Math.toRadians(
                player.getServer().getSettings().levelSettings().fieldOfView()));
    }

    private boolean ifChunkNotInRadius(int chunkX, int chunkZ, int radius) {
        return chunkX * chunkX + chunkZ * chunkZ > radius * radius;
    }
}
