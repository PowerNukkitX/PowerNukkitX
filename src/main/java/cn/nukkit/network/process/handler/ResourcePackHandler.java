package cn.nukkit.network.process.handler;

import cn.nukkit.network.connection.BedrockSession;
import cn.nukkit.network.process.SessionState;
import cn.nukkit.network.protocol.ResourcePackChunkDataPacket;
import cn.nukkit.network.protocol.ResourcePackChunkRequestPacket;
import cn.nukkit.network.protocol.ResourcePackClientResponsePacket;
import cn.nukkit.network.protocol.ResourcePackDataInfoPacket;
import cn.nukkit.network.protocol.ResourcePackStackPacket;
import cn.nukkit.network.protocol.ResourcePacksInfoPacket;
import cn.nukkit.network.protocol.types.ExperimentEntry;
import cn.nukkit.resourcepacks.ResourcePack;
import cn.nukkit.utils.version.Version;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayDeque;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.UUID;

@Slf4j
public class ResourcePackHandler extends BedrockSessionPacketHandler {

    private static final class PackMeta {
        final UUID packId;
        final ResourcePack pack;
        final int maxChunkSize;
        final int chunkCount;
        final BitSet want = new BitSet();
        final BitSet sent = new BitSet();
        int nextToSend = 0;

        PackMeta(UUID id, ResourcePack p, int maxChunkSize, int chunkCount) {
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

    public ResourcePackHandler(BedrockSession session) {
        super(session);
        ResourcePacksInfoPacket infoPacket = new ResourcePacksInfoPacket();
        infoPacket.resourcePackEntries = session.getServer().getResourcePackManager().getResourceStack();
        infoPacket.mustAccept = session.getServer().getForceResources();
        infoPacket.disableVibrantVisuals = !session.getServer().allowVibrantVisuals();
        infoPacket.worldTemplateId = UUID.randomUUID();
        infoPacket.worldTemplateVersion = "";
        session.sendPacket(infoPacket);
    }

    @Override
    public void handle(ResourcePackClientResponsePacket pk) {
        var server = session.getServer();
        switch (pk.responseStatus) {
            case ResourcePackClientResponsePacket.STATUS_REFUSED -> {
                log.debug("ResourcePackClientResponsePacket STATUS_REFUSED");
                this.session.close("disconnectionScreen.noReason");
            }
            case ResourcePackClientResponsePacket.STATUS_SEND_PACKS -> {
                log.debug("ResourcePackClientResponsePacket STATUS_SEND_PACKS");
                for (ResourcePackClientResponsePacket.Entry entry : pk.packEntries) {
                    ResourcePack resourcePack = server.getResourcePackManager().getPackById(entry.uuid);
                    if (resourcePack == null) {
                        this.session.close("disconnectionScreen.resourcePack");
                        return;
                    }
                    int maxChunkSize = server.getResourcePackManager().getMaxChunkSize();
                    int chunkCount = (int) Math.ceil(resourcePack.getPackSize() / (double) maxChunkSize);

                    packs.put(resourcePack.getPackId(), new PackMeta(resourcePack.getPackId(), resourcePack, maxChunkSize, chunkCount));

                    ResourcePackDataInfoPacket dataInfoPacket = new ResourcePackDataInfoPacket();
                    dataInfoPacket.packId = resourcePack.getPackId();
                    dataInfoPacket.setPackVersion(new Version(resourcePack.getPackVersion()));
                    dataInfoPacket.maxChunkSize = maxChunkSize;
                    dataInfoPacket.chunkCount = chunkCount;
                    dataInfoPacket.compressedPackSize = resourcePack.getPackSize();
                    dataInfoPacket.sha256 = resourcePack.getSha256();
                    session.sendPacket(dataInfoPacket);
                }
            }
            case ResourcePackClientResponsePacket.STATUS_HAVE_ALL_PACKS -> {
                log.debug("ResourcePackClientResponsePacket STATUS_HAVE_ALL_PACKS");

                ResourcePackStackPacket stackPacket = new ResourcePackStackPacket();
                stackPacket.mustAccept = server.getForceResources() && !server.getForceResourcesAllowOwnPacks();
                stackPacket.resourcePackStack = server.getResourcePackManager().getResourceStack();

                for (ExperimentEntry entry : server.getExperiments()) {
                    stackPacket.experiments.add(new ResourcePackStackPacket.ExperimentData(entry.name(), entry.enabled()));
                }
                session.sendPacket(stackPacket);
            }
            case ResourcePackClientResponsePacket.STATUS_COMPLETED -> {
                log.debug("ResourcePackClientResponsePacket STATUS_COMPLETED");
                this.session.getMachine().fire(SessionState.PRE_SPAWN);
            }
        }
    }

    @Override
    public void handle(ResourcePackChunkRequestPacket pk) {
        chunkRequestQueue.add(pk);

        PackMeta meta = packs.get(pk.getPackId());
        if (meta == null) {
            var mgr = session.getServer().getResourcePackManager();
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

        if (pk.chunkIndex >= 0 && pk.chunkIndex < meta.chunkCount) {
            meta.want.set(pk.chunkIndex);
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
                    ResourcePackChunkDataPacket dataPacket = new ResourcePackChunkDataPacket();
                    dataPacket.setPackId(m.packId);
                    dataPacket.setPackVersion(new Version(m.pack.getPackVersion()));
                    dataPacket.chunkIndex = m.nextToSend;
                    dataPacket.progress = (long) m.maxChunkSize * m.nextToSend;
                    dataPacket.data = m.pack.getPackChunk(m.maxChunkSize * m.nextToSend, m.maxChunkSize);
                    if (dataPacket.data == null) {
                        log.warn("RP chunk out of range or null data: pack={} chunk={}", m.packId, m.nextToSend);
                        this.session.close("disconnectionScreen.resourcePack");
                        return;
                    }

                    // Enqueue to the baseline paced RP FIFO; pacer handles rate limiting
                    session.sendPacket(dataPacket);

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

            session.nudgePacer();
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
