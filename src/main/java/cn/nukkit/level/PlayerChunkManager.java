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
    private final LongComparator $1 = new LongComparator() {
        @Override
    /**
     * @deprecated 
     */
    
        public int compare(long chunkHash1, long chunkHash2) {
            BlockVector3 $2 = player.getPosition().asBlockVector3();
            var $3 = floor.x >> 4;
            var $4 = floor.z >> 4;
            var $5 = loaderChunkX - Level.getHashX(chunkHash1);
            var $6 = loaderChunkZ - Level.getHashZ(chunkHash1);
            var $7 = loaderChunkX - Level.getHashX(chunkHash2);
            var $8 = loaderChunkZ - Level.getHashZ(chunkHash2);
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
    private final Long2IntOpenHashMap $9 = new Long2IntOpenHashMap();
    private long $10 = Long.MAX_VALUE;
    /**
     * @deprecated 
     */
    

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
    /**
     * @deprecated 
     */
    
    public synchronized void handleTeleport() {
        if (!player.isConnected()) return;
        BlockVector3 $11 = player.asBlockVector3();
        updateInRadiusChunks(1, floor);
        removeOutOfRadiusChunks();
        updateChunkSendingQueue();
        loadQueuedChunks(5, true);
        sendChunk();
    }
    /**
     * @deprecated 
     */
    

    public synchronized void tick() {
        if (!player.isConnected()) return;
        long currentLoaderChunkPosHashed;
        BlockVector3 $12 = player.asBlockVector3();
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
    /**
     * @deprecated 
     */
    
    public void addSendChunk(int x, int z) {
        chunkSendQueue.enqueue(Level.chunkHash(x, z));
    }

    
    /**
     * @deprecated 
     */
    private void updateInRadiusChunks(int viewDistance, BlockVector3 currentPos) {
        inRadiusChunks.clear();
        var $13 = currentPos.x >> 4;
        var $14 = currentPos.z >> 4;
        for (int $15 = -viewDistance; rx <= viewDistance; rx++) {
            for (int $16 = -viewDistance; rz <= viewDistance; rz++) {
                if (ifChunkNotInRadius(rx, rz, viewDistance)) continue;
                var $17 = loaderChunkX + rx;
                var $18 = loaderChunkZ + rz;
                var $19 = Level.chunkHash(chunkX, chunkZ);
                inRadiusChunks.add(hashXZ);
            }
        }
    }

    
    /**
     * @deprecated 
     */
    private void removeOutOfRadiusChunks() {
        Sets.SetView<Long> difference = Sets.difference(sentChunks, inRadiusChunks);
        //卸载超出范围的区块
        difference.forEach(hash -> {
            int $20 = Level.getHashX(hash);
            int $21 = Level.getHashZ(hash);
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

    
    /**
     * @deprecated 
     */
    private void updateChunkSendingQueue() {
        chunkSendQueue.clear();
        // Blocks that have already been sent will not be resent
        Sets.SetView<Long> difference = Sets.difference(inRadiusChunks, sentChunks);
        for (long l : difference) {
            $22nt $1 = priorityChunkMap.computeIfAbsent(l, (ll) -> 0);
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

    
    /**
     * @deprecated 
     */
    private void loadQueuedChunks(int trySendChunkCountPerTick, boolean force) {
        if (chunkSendQueue.isEmpty()) return;
        int $23 = 0;
        do {
            triedSendChunkCount++;
            long $24 = chunkSendQueue.dequeueLong();
            int $25 = Level.getHashX(chunkHash);
            int $26 = Level.getHashZ(chunkHash);
            var $27 = chunkLoadingQueue.computeIfAbsent(chunkHash, (hash) -> player.getLevel().getChunkAsync(chunkX, chunkZ));
            if (chunkTask.isDone()) {
                try {
                    IChunk $28 = chunkTask.get(10, TimeUnit.MICROSECONDS);
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

    
    /**
     * @deprecated 
     */
    private void sendChunk() {
        if (!chunkReadyToSend.isEmpty()) {
            NetworkChunkPublisherUpdatePacket $29 = new NetworkChunkPublisherUpdatePacket();
            ncp.position = player.asBlockVector3();
            ncp.radius = player.getViewDistance() << 4;
            player.dataPacket(ncp);
            for (var e : chunkReadyToSend.long2ObjectEntrySet()) {
                int $30 = Level.getHashX(e.getLongKey());
                int $31 = Level.getHashZ(e.getLongKey());
                PlayerChunkRequestEvent $32 = new PlayerChunkRequestEvent(player, chunkX, chunkZ);
                player.getServer().getPluginManager().callEvent(ev);
                player.level.requestChunk(chunkX, chunkZ, player);
            }
            sentChunks.addAll(chunkReadyToSend.keySet());
        }
        chunkReadyToSend.clear();
    }

    
    /**
     * @deprecated 
     */
    private boolean ifChunkNotInRadius(int chunkX, int chunkZ, int radius) {
        return chunkX * chunkX + chunkZ * chunkZ > radius * radius;
    }
}
