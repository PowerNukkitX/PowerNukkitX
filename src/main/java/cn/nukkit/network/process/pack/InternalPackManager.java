package cn.nukkit.network.process.pack;

import cn.nukkit.Server;
import cn.nukkit.resourcepacks.ResourcePack;
import io.netty.buffer.Unpooled;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.protocol.bedrock.BedrockServerSession;
import org.cloudburstmc.protocol.bedrock.packet.ResourcePackChunkDataPacket;
import org.cloudburstmc.protocol.bedrock.packet.ResourcePackChunkRequestPacket;

import java.util.ArrayDeque;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.UUID;

/**
 * @author Kaooot
 */
@Slf4j
@RequiredArgsConstructor
public class InternalPackManager {

    private final BedrockServerSession session;
    private final Server server;

    private static final class PackMeta {
        final UUID packId;
        final ResourcePack pack;
        final int maxChunkSize;
        final int chunkCount;
        final BitSet want = new BitSet();
        final BitSet sent = new BitSet();
        int nextToSend = 0;

        public PackMeta(UUID id, ResourcePack p, int maxChunkSize, int chunkCount) {
            this.packId = id;
            this.pack = p;
            this.maxChunkSize = maxChunkSize;
            this.chunkCount = chunkCount;
        }

        boolean finished() {
            return nextToSend >= chunkCount;
        }
    }

    private final Map<UUID, PackMeta> packs = new HashMap<>();
    private final ArrayDeque<UUID> packOrder = new ArrayDeque<>();
    private UUID activePack = null;
    private final Queue<ResourcePackChunkRequestPacket> chunkRequestQueue = new ArrayDeque<>();
    private volatile boolean draining = false;

    public void registerPack(UUID uuid, ResourcePack resourcePack, int maxChunkSize, int chunkCount) {
        this.packs.put(resourcePack.getPackId(), new InternalPackManager.PackMeta(resourcePack.getPackId(), resourcePack, maxChunkSize, chunkCount));
    }

    public void handleChunkRequest(ResourcePackChunkRequestPacket pk) {
        chunkRequestQueue.add(pk);

        PackMeta meta = packs.get(pk.getPackId());
        if (meta == null) {
            var mgr = server.getResourcePackManager();
            ResourcePack p = mgr.getPackById(pk.getPackId());
            if (p == null) {
                this.session.close("disconnectionScreen.resourcePack");
                return;
            }
            int maxChunkSize = mgr.getMaxChunkSize();
            int chunkCount = (int) Math.ceil(p.getPackSize() / (double) maxChunkSize);
            meta = new PackMeta(p.getPackId(), p, maxChunkSize, chunkCount);
            packs.put(meta.packId, meta);
        }

        if (activePack == null) {
            activePack = meta.packId;
            packOrder.addLast(meta.packId);
        } else if (!Objects.equals(activePack, meta.packId) && !packOrder.contains(meta.packId)) {
            packOrder.addLast(meta.packId);
        }

        if (pk.getChunk() >= 0 && pk.getChunk() < meta.chunkCount) {
            meta.want.set(pk.getChunk());
        }

        drainActiveStrict();
    }

    private void drainActiveStrict() {
        if (draining) return;
        draining = true;
        try {
            while (activePack != null) {
                PackMeta m = packs.get(activePack);
                if (m == null) {
                    nextPack();
                    continue;
                }

                // Send strictly in order, but ONLY when the client has requested that chunk
                boolean progressed = false;
                while (m.nextToSend < m.chunkCount && m.want.get(m.nextToSend) && !m.sent.get(m.nextToSend)) {
                    final ResourcePackChunkDataPacket dataPacket = new ResourcePackChunkDataPacket();
                    dataPacket.setPackId(m.packId);
                    dataPacket.setPackVersion(m.pack.getPackVersion());
                    dataPacket.setChunkID(m.nextToSend);
                    dataPacket.setByteOffset((long) m.maxChunkSize * m.nextToSend);
                    dataPacket.setChunkData(Unpooled.wrappedBuffer(m.pack.getPackChunk(m.maxChunkSize * m.nextToSend, m.maxChunkSize)));
                    if (dataPacket.getChunkData() == null) {
                        log.warn("RP chunk out of range or null data: pack={} chunk={}", m.packId, m.nextToSend);
                        this.session.close("disconnectionScreen.resourcePack");
                        return;
                    }

                    // Enqueue to the baseline paced RP FIFO; pacer handles rate limiting
                    session.sendPacketImmediately(dataPacket);

                    m.sent.set(m.nextToSend);
                    m.nextToSend++;
                    progressed = true;
                }

                if (m.finished()) {
                    nextPack();
                    // Loop back and start draining the next pack (if any)
                    continue;
                }

                if (!progressed) {
                    break;
                }
            }
        } finally {
            draining = false;
        }
    }

    private void nextPack() {
        if (activePack == null) return;
        UUID current = packOrder.peekFirst();
        if (Objects.equals(current, activePack)) {
            packOrder.pollFirst();
        } else {
            packOrder.remove(activePack);
        }
        activePack = packOrder.peekFirst();
    }
}