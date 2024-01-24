package cn.nukkit.level;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.player.PlayerChunkRequestEvent;
import cn.nukkit.level.format.ChunkState;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.network.protocol.NetworkChunkPublisherUpdatePacket;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayFIFOQueue;
import it.unimi.dsi.fastutil.longs.LongComparator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import lombok.extern.slf4j.Slf4j;

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
    private final LongOpenHashSet sentChunks = new LongOpenHashSet();
    //保存着这tick将要发送的全部区块hash值
    private final LongOpenHashSet inRadiusChunks = new LongOpenHashSet();
    private final int chunkTrySendCountPerTick;
    private final LongArrayFIFOQueue chunkSendQueue;
    private long lastLoaderChunkPosHashed = -1;


    public PlayerChunkManager(Player player) {
        this.player = player;
        this.chunkSendQueue = new LongArrayFIFOQueue(player.getViewDistance() * player.getViewDistance());
        this.chunkTrySendCountPerTick = player.getChunkSendCountPerTick();
    }

    public void tick() {
        if (!player.isConnected()) return;
        long currentLoaderChunkPosHashed;
        BlockVector3 floor = player.asBlockVector3();
        if ((currentLoaderChunkPosHashed = Level.chunkHash(floor.x >> 4, floor.z >> 4)) != lastLoaderChunkPosHashed) {
            lastLoaderChunkPosHashed = currentLoaderChunkPosHashed;
            updateInRadiusChunks(floor);
            removeOutOfRadiusChunks();
            updateChunkSendingQueue();
        }
        loadAndSendQueuedChunks();
    }

    public LongOpenHashSet getUsedChunks() {
        return sentChunks;
    }

    private void updateInRadiusChunks(BlockVector3 currentPos) {
        inRadiusChunks.clear();
        var loaderChunkX = currentPos.x >> 4;
        var loaderChunkZ = currentPos.z >> 4;
        var chunkLoadingRadius = player.getViewDistance();
        for (int rx = -chunkLoadingRadius; rx <= chunkLoadingRadius; rx++) {
            for (int rz = -chunkLoadingRadius; rz <= chunkLoadingRadius; rz++) {
                if (ifChunkNotInRadius(rx, rz, chunkLoadingRadius)) continue;
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
        //已经发送的区块不再二次发送
        Sets.SetView<Long> difference = Sets.difference(inRadiusChunks, sentChunks);
        difference.stream().sorted(chunkDistanceComparator).forEachOrdered(v -> chunkSendQueue.enqueue(v.longValue()));
    }

    private void loadAndSendQueuedChunks() {
        if (chunkSendQueue.isEmpty()) return;
        var chunkReadyToSend = new Long2ObjectOpenHashMap<IChunk>();
        int triedSendChunkCount = 0;
        do {
            triedSendChunkCount++;
            long chunkHash = chunkSendQueue.dequeueLong();
            int chunkX = Level.getHashX(chunkHash);
            int chunkZ = Level.getHashZ(chunkHash);
            var chunk = player.getLevel().getChunk(chunkX, chunkZ);
            if (chunk == null || !chunk.getChunkState().canSend()) {
                player.level.generateChunk(chunkX, chunkZ);
                chunkSendQueue.enqueue(chunkHash);
                continue;
            }
            player.level.registerChunkLoader(player, chunkX, chunkZ, false);
            chunkReadyToSend.put(chunkHash, chunk);
        } while (!chunkSendQueue.isEmpty() && triedSendChunkCount < chunkTrySendCountPerTick);

        if (!chunkReadyToSend.isEmpty()) {
            NetworkChunkPublisherUpdatePacket packet = new NetworkChunkPublisherUpdatePacket();
            packet.position = player.asBlockVector3();
            packet.radius = player.getViewDistance() << 4;
            player.dataPacket(packet);
            if (true) {
                for (var e : chunkReadyToSend.long2ObjectEntrySet()) {
                    int chunkX = Level.getHashX(e.getLongKey());
                    int chunkZ = Level.getHashZ(e.getLongKey());
                    PlayerChunkRequestEvent ev = new PlayerChunkRequestEvent(player, chunkX, chunkZ);
                    player.getServer().getPluginManager().callEvent(ev);
                    player.level.requestChunk(chunkX, chunkZ, player);
                }
                sentChunks.addAll(chunkReadyToSend.keySet());
            } else {
                /*// 1. Encode all lcps
                List<LevelChunkPacket> lcps;
                Stream<IChunk> lcpStream;
                if (true) {
                    lcpStream = chunkReadyToSend.values().parallelStream();
                } else {
                    lcpStream = chunkReadyToSend.values().stream();
                }
                player.
                lcps = lcpStream.map(chunk.createFullLevelChunkPacketChunk()).toList();
                // 2. Send lcps to client
                for (var lcp : lcps) {
                    chunkLoader.sendLevelChunkPacket(lcp);
                }
                sentChunks.addAll(chunkReadyToSend.keySet());
                // 3. Call onChunkInRangeSent()
                chunkReadyToSend.values().forEach(chunkLoader::onChunkInRangeSent);*/
            }
        }
    }

    private boolean ifChunkNotInRadius(int chunkX, int chunkZ, int radius) {
        return chunkX * chunkX + chunkZ * chunkZ > radius * radius;
    }
}
