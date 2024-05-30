package cn.nukkit.level;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.player.PlayerChunkRequestEvent;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.network.protocol.NetworkChunkPublisherUpdatePacket;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayFIFOQueue;
import it.unimi.dsi.fastutil.longs.LongComparator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * Allay Project 2023/7/1
 *
 * @author daoge_cmd | CoolLoong
 */
@Slf4j
public final class PlayerChunkManager {
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
    private final Player player;
    //保存着上tick已经发送的全部区块hash值
    private final @NotNull LongOpenHashSet sentChunks;
    //保存着这tick将要发送的全部区块hash值
    private final @NotNull LongOpenHashSet inRadiusChunks;
    private final int trySendChunkCountPerTick;
    private final LongArrayFIFOQueue chunkSendQueue;
    private final Long2ObjectOpenHashMap<CompletableFuture<IChunk>> chunkLoadingQueue;
    private final Long2ObjectOpenHashMap<IChunk> chunkReadyToSend;
    private final Long2IntOpenHashMap priorityChunkMap = new Long2IntOpenHashMap();
    private long lastLoaderChunkPosHashed = Long.MAX_VALUE;

    public PlayerChunkManager(Player player) {
        this.player = player;
        this.sentChunks = new LongOpenHashSet();
        this.inRadiusChunks = new LongOpenHashSet();
        this.chunkSendQueue = new LongArrayFIFOQueue(player.getViewDistance() * player.getViewDistance());
        this.chunkLoadingQueue = new Long2ObjectOpenHashMap<>(player.getViewDistance() * player.getViewDistance());
        this.trySendChunkCountPerTick = player.getChunkSendCountPerTick();
        this.chunkReadyToSend = new Long2ObjectOpenHashMap<>();
    }

    /**
     * Handle chunk loading when the player teleported
     */
    public synchronized void handleTeleport() {
        if (!player.isConnected()) return;
        BlockVector3 floor = player.asBlockVector3();
        updateInRadiusChunks(1, floor);
        removeOutOfRadiusChunks();
        updateChunkSendingQueue();
        loadQueuedChunks(5, true);
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

    @ApiStatus.Internal
    public LongOpenHashSet getUsedChunks() {
        return sentChunks;
    }

    @ApiStatus.Internal
    public LongOpenHashSet getInRadiusChunks() {
        return inRadiusChunks;
    }

    @ApiStatus.Internal
    public void addSendChunk(int x, int z) {
        chunkSendQueue.enqueue(Level.chunkHash(x, z));
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
        Sets.SetView<Long> difference = Sets.difference(sentChunks, inRadiusChunks);
        //卸载超出范围的区块
        difference.forEach(hash -> {
            int x = Level.getHashX(hash);
            int z = Level.getHashZ(hash);
            if (player.level.unregisterChunkLoader(player, x, z)) {
                for (Entity entity : player.level.getChunkEntities(x, z).values()) {
                    if (entity != player) {
                        entity.despawnFrom(player);
                    }
                }
            }
        });
        //剩下sentChunks和inRadiusChunks的交集
        sentChunks.removeAll(difference);
    }

    private void updateChunkSendingQueue() {
        chunkSendQueue.clear();
        // Blocks that have already been sent will not be resent
        Sets.SetView<Long> difference = Sets.difference(inRadiusChunks, sentChunks);
        for (long l : difference) {
            int i = priorityChunkMap.computeIfAbsent(l, (ll) -> 0);
            priorityChunkMap.put(l, i + 1);
        }
        Set<Long> priorChunk = priorityChunkMap.long2IntEntrySet().stream().filter(e -> e.getIntValue() > 5).map(Long2IntMap.Entry::getLongKey).collect(Collectors.toSet());
        Sets.SetView<Long> lowPriorChunks = Sets.difference(difference, priorChunk);
        priorChunk.forEach(v -> {
            priorityChunkMap.put(v.longValue(), 0);
            chunkSendQueue.enqueue(v.longValue());
        });
        lowPriorChunks.stream().sorted(chunkDistanceComparator).forEachOrdered(v -> chunkSendQueue.enqueue(v.longValue()));
    }

    private void loadQueuedChunks(int trySendChunkCountPerTick, boolean force) {
        if (chunkSendQueue.isEmpty()) return;
        int triedSendChunkCount = 0;
        do {
            triedSendChunkCount++;
            long chunkHash = chunkSendQueue.dequeueLong();
            int chunkX = Level.getHashX(chunkHash);
            int chunkZ = Level.getHashZ(chunkHash);
            var chunkTask = chunkLoadingQueue.computeIfAbsent(chunkHash, (hash) -> player.getLevel().getChunkAsync(chunkX, chunkZ));
            if (chunkTask.isDone()) {
                try {
                    IChunk chunk = chunkTask.get(10, TimeUnit.MICROSECONDS);
                    if (chunk == null || !chunk.getChunkState().canSend()) {
                        player.level.generateChunk(chunkX, chunkZ, force);
                        chunkSendQueue.enqueue(chunkHash);
                        continue;
                    }
                    chunkLoadingQueue.remove(chunkHash);
                    priorityChunkMap.remove(chunkHash);
                    player.level.registerChunkLoader(player, chunkX, chunkZ, false);
                    chunkReadyToSend.put(chunkHash, chunk);
                } catch (InterruptedException | ExecutionException ignore) {
                } catch (TimeoutException e) {
                    log.warn("read chunk timeout {} {}", chunkX, chunkZ);
                }
            } else {
                chunkSendQueue.enqueue(chunkHash);
            }
        } while (!chunkSendQueue.isEmpty() && triedSendChunkCount < trySendChunkCountPerTick);
    }

    private void sendChunk() {
        if (!chunkReadyToSend.isEmpty()) {
            NetworkChunkPublisherUpdatePacket ncp = new NetworkChunkPublisherUpdatePacket();
            ncp.position = player.asBlockVector3();
            ncp.radius = player.getViewDistance() << 4;
            player.dataPacket(ncp);
            for (var e : chunkReadyToSend.long2ObjectEntrySet()) {
                int chunkX = Level.getHashX(e.getLongKey());
                int chunkZ = Level.getHashZ(e.getLongKey());
                PlayerChunkRequestEvent ev = new PlayerChunkRequestEvent(player, chunkX, chunkZ);
                player.getServer().getPluginManager().callEvent(ev);
                player.level.requestChunk(chunkX, chunkZ, player);
            }
            sentChunks.addAll(chunkReadyToSend.keySet());
        }
        chunkReadyToSend.clear();
    }

    private boolean ifChunkNotInRadius(int chunkX, int chunkZ, int radius) {
        return chunkX * chunkX + chunkZ * chunkZ > radius * radius;
    }
}
