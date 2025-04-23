package cn.nukkit.level;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.player.PlayerChunkRequestEvent;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.network.protocol.NetworkChunkPublisherUpdatePacket;
import it.unimi.dsi.fastutil.longs.*;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

@Slf4j
public final class PlayerChunkManager {
    private final Player player;
    private final LongOpenHashSet sentChunks = new LongOpenHashSet();
    private final LongOpenHashSet inRadiusChunks = new LongOpenHashSet();
    private final LongArrayPriorityQueue chunkSendQueue;
    private final Long2ObjectOpenHashMap<CompletableFuture<IChunk>> chunkLoadingQueue = new Long2ObjectOpenHashMap<>();
    private final Long2ObjectOpenHashMap<IChunk> chunkReadyToSend = new Long2ObjectOpenHashMap<>();
    private long lastLoaderChunkPosHashed = Long.MAX_VALUE;
    private final int trySendChunkCountPerTick;

    private final LongComparator chunkDistanceComparator = new LongComparator() {
        @Override
        public int compare(long chunkHash1, long chunkHash2) {
            BlockVector3 floor = player.getPosition().asBlockVector3();
            var loaderChunkX = floor.x >> 4;
            var loaderChunkZ = floor.z >> 4;
            var chunkDX1 = loaderChunkX - Level.getHashX(chunkHash1);
            var chunkDZ1 = loaderChunkZ - Level.getHashZ(chunkHash1);
            var chunkDX2 = loaderChunkX - Level.getHashX(chunkHash2);
            var chunkDZ2 = loaderChunkZ - Level.getHashZ(chunkHash2);
            //Compare distance to loader
            return Integer.compare(
                    chunkDX1 * chunkDX1 + chunkDZ1 * chunkDZ1,
                    chunkDX2 * chunkDX2 + chunkDZ2 * chunkDZ2
            );
        }
    };

    public PlayerChunkManager(Player player) {
        this.player = player;
        this.trySendChunkCountPerTick = player.getChunkSendCountPerTick();
        this.chunkSendQueue = new LongArrayPriorityQueue(player.getViewDistance() * player.getViewDistance(), this::compareChunkDistance);
    }

    public synchronized void tick() {
        if (!player.isConnected()) return;

        BlockVector3 pos = player.asBlockVector3();
        long currentPosHash = Level.chunkHash(pos.x >> 4, pos.z >> 4);

        if (currentPosHash != lastLoaderChunkPosHashed) {
            lastLoaderChunkPosHashed = currentPosHash;
            updateInRadiusChunks();
            removeOutOfRadiusChunks();
            updateChunkSendQueue();
        }

        loadChunks(trySendChunkCountPerTick, false);
        sendChunks();
    }

    public synchronized void handleTeleport() {
        if (!player.isConnected()) return;

        updateInRadiusChunks();
        removeOutOfRadiusChunks();
        updateChunkSendQueue();
        loadChunks(5, true);
        sendChunks();
    }

    private void updateInRadiusChunks() {
        inRadiusChunks.clear();
        int viewDistance = player.getViewDistance();
        BlockVector3 pos = player.asBlockVector3();
        int cx = pos.x >> 4;
        int cz = pos.z >> 4;

        for (int dx = -viewDistance; dx <= viewDistance; dx++) {
            for (int dz = -viewDistance; dz <= viewDistance; dz++) {
                if ((dx * dx + dz * dz) > (viewDistance * viewDistance)) continue;
                inRadiusChunks.add(Level.chunkHash(cx + dx, cz + dz));
            }
        }
    }

    private void removeOutOfRadiusChunks() {
        LongOpenHashSet toRemove = new LongOpenHashSet(sentChunks);
        toRemove.removeAll(inRadiusChunks);

        for (long hash : toRemove) {
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

        sentChunks.removeAll(toRemove);
    }

    private void updateChunkSendQueue() {
        chunkSendQueue.clear();
        for (long hash : inRadiusChunks) {
            if (!sentChunks.contains(hash)) {
                chunkSendQueue.enqueue(hash);
            }
        }
    }

    private void loadChunks(int limit, boolean force) {
        int sent = 0;
        while (!chunkSendQueue.isEmpty() && sent < limit) {
            long hash = chunkSendQueue.dequeueLong();
            int x = Level.getHashX(hash);
            int z = Level.getHashZ(hash);

            CompletableFuture<IChunk> future = chunkLoadingQueue.computeIfAbsent(hash, h -> player.level.getChunkAsync(x, z));

            if (future.isDone()) {
                try {
                    IChunk chunk = future.get(5, TimeUnit.MILLISECONDS);
                    if (chunk == null || !chunk.getChunkState().canSend()) {
                        player.level.generateChunk(x, z, force);
                        chunkSendQueue.enqueue(hash);
                        continue;
                    }

                    chunkLoadingQueue.remove(hash);
                    player.level.registerChunkLoader(player, x, z, false);
                    chunkReadyToSend.put(hash, chunk);
                } catch (InterruptedException | ExecutionException ignore) {
                } catch (TimeoutException e) {
                    log.warn("Chunk load timeout at {} {}", x, z);
                }
            } else {
                chunkSendQueue.enqueue(hash);
            }

            sent++;
        }
    }

    public void addSendChunk(int x, int z) {
        chunkSendQueue.enqueue(Level.chunkHash(x, z));
    }

    private void sendChunks() {
        if (chunkReadyToSend.isEmpty()) return;

        NetworkChunkPublisherUpdatePacket ncp = new NetworkChunkPublisherUpdatePacket();
        ncp.position = player.asBlockVector3();
        ncp.radius = player.getViewDistance() << 4;
        player.dataPacket(ncp);

        for (Long2ObjectMap.Entry<IChunk> entry : chunkReadyToSend.long2ObjectEntrySet()) {
            int x = Level.getHashX(entry.getLongKey());
            int z = Level.getHashZ(entry.getLongKey());

            PlayerChunkRequestEvent event = new PlayerChunkRequestEvent(player, x, z);
            player.getServer().getPluginManager().callEvent(event);
            player.level.requestChunk(x, z, player);
        }

        sentChunks.addAll(chunkReadyToSend.keySet());
        chunkReadyToSend.clear();
    }

    private int compareChunkDistance(long a, long b) {
        BlockVector3 pos = player.asBlockVector3();
        int cx = pos.x >> 4;
        int cz = pos.z >> 4;

        int ax = Level.getHashX(a);
        int az = Level.getHashZ(a);
        int bx = Level.getHashX(b);
        int bz = Level.getHashZ(b);

        int distA = (cx - ax) * (cx - ax) + (cz - az) * (cz - az);
        int distB = (cx - bx) * (cx - bx) + (cz - bz) * (cz - bz);

        return Integer.compare(distA, distB);
    }

    public LongOpenHashSet getUsedChunks() {
        return sentChunks;
    }

    public LongOpenHashSet getInRadiusChunks() {
        return inRadiusChunks;
    }
}
