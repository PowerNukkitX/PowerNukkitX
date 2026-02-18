package cn.nukkit.network.process.handler;

import cn.nukkit.network.connection.BedrockSession;
import cn.nukkit.network.process.SessionState;
import cn.nukkit.network.protocol.ProtocolInfo;
import org.cloudburstmc.protocol.bedrock.data.ExperimentData;
import cn.nukkit.resourcepacks.ResourcePack;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.protocol.bedrock.data.ResourcePackType;
import org.cloudburstmc.protocol.bedrock.packet.ResourcePackChunkDataPacket;
import org.cloudburstmc.protocol.bedrock.packet.ResourcePackChunkRequestPacket;
import org.cloudburstmc.protocol.bedrock.packet.ResourcePackClientResponsePacket;
import org.cloudburstmc.protocol.bedrock.packet.ResourcePackDataInfoPacket;
import org.cloudburstmc.protocol.bedrock.packet.ResourcePackStackPacket;
import org.cloudburstmc.protocol.bedrock.packet.ResourcePacksInfoPacket;
import org.cloudburstmc.protocol.common.PacketSignal;

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
        var packs = session.getServer().getResourcePackManager().getResourceStack();
        for (var entry : packs) {
            infoPacket.getResourcePackInfos().add(new ResourcePacksInfoPacket.Entry(
                    entry.getPackId(),
                    entry.getPackVersion(),
                    entry.getPackSize(),
                    entry.getEncryptionKey(),
                    entry.getSubPackName(),
                    !entry.getEncryptionKey().isEmpty() ? entry.getPackId().toString() : "",
                    entry.usesScript(),
                    entry.isRaytracingCapable(),
                    entry.isAddonPack(),
                    entry.cdnUrl()
            ));
        }
        infoPacket.setForcedToAccept(session.getServer().getForceResources());
        infoPacket.setVibrantVisualsForceDisabled(!session.getServer().allowVibrantVisuals());
        infoPacket.setWorldTemplateId(UUID.randomUUID());
        infoPacket.setWorldTemplateVersion("");
        session.sendPacket(infoPacket);
    }

    @Override
    public PacketSignal handle(ResourcePackClientResponsePacket pk) {
        var server = session.getServer();
        switch (pk.getStatus()) {
            case REFUSED -> {
                log.debug("ResourcePackClientResponsePacket REFUSED");
                this.session.close("disconnectionScreen.noReason");
                return PacketSignal.HANDLED;
            }
            case SEND_PACKS -> {
                log.debug("ResourcePackClientResponsePacket SEND_PACKS");
                for (String packEntry : pk.getPackIds()) {
                    UUID packId = parsePackId(packEntry);
                    ResourcePack resourcePack = packId == null ? null : server.getResourcePackManager().getPackById(packId);
                    if (resourcePack == null) {
                        this.session.close("disconnectionScreen.resourcePack");
                        return PacketSignal.HANDLED;
                    }
                    int maxChunkSize = server.getResourcePackManager().getMaxChunkSize();
                    int chunkCount = (int) Math.ceil(resourcePack.getPackSize() / (double) maxChunkSize);

                    packs.put(resourcePack.getPackId(), new PackMeta(resourcePack.getPackId(), resourcePack, maxChunkSize, chunkCount));

                    ResourcePackDataInfoPacket dataInfoPacket = new ResourcePackDataInfoPacket();
                    dataInfoPacket.setPackId(resourcePack.getPackId());
                    dataInfoPacket.setPackVersion(resourcePack.getPackVersion());
                    dataInfoPacket.setMaxChunkSize(maxChunkSize);
                    dataInfoPacket.setChunkCount(chunkCount);
                    dataInfoPacket.setCompressedPackSize(resourcePack.getPackSize());
                    dataInfoPacket.setHash(resourcePack.getSha256());
                    dataInfoPacket.setType(ResourcePackType.RESOURCES);
                    session.sendPacket(dataInfoPacket);
                }
            }
            case HAVE_ALL_PACKS -> {
                log.debug("ResourcePackClientResponsePacket HAVE_ALL_PACKS");

                ResourcePackStackPacket stackPacket = new ResourcePackStackPacket();
                stackPacket.setForcedToAccept(server.getForceResources() && !server.getForceResourcesAllowOwnPacks());
                stackPacket.setGameVersion(ProtocolInfo.MINECRAFT_VERSION_NETWORK);
                for (ResourcePack entry : server.getResourcePackManager().getResourceStack()) {
                    stackPacket.getResourcePacks().add(new ResourcePackStackPacket.Entry(
                            entry.getPackId().toString(),
                            entry.getPackVersion(),
                            entry.getSubPackName()
                    ));
                }

                stackPacket.getExperiments().addAll(server.getExperiments());
                session.sendPacket(stackPacket);
            }
            case COMPLETED -> {
                log.debug("ResourcePackClientResponsePacket COMPLETED");
                this.session.getMachine().fire(SessionState.PRE_SPAWN);
            }
            default -> {
            }
        }
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ResourcePackChunkRequestPacket pk) {
        chunkRequestQueue.add(pk);

        PackMeta meta = packs.get(pk.getPackId());
        if (meta == null) {
            var mgr = session.getServer().getResourcePackManager();
            ResourcePack p = mgr.getPackById(pk.getPackId());
            if (p == null) {
                this.session.close("disconnectionScreen.resourcePack");
                return PacketSignal.HANDLED;
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

        int chunkIndex = pk.getChunkIndex();
        if (chunkIndex >= 0 && chunkIndex < meta.chunkCount) {
            meta.want.set(chunkIndex);
        }

        drainActiveStrict();
        return PacketSignal.HANDLED;
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
                    dataPacket.setPackVersion(m.pack.getPackVersion());
                    dataPacket.setChunkIndex(m.nextToSend);
                    dataPacket.setProgress((long) m.maxChunkSize * m.nextToSend);
                    byte[] chunk = m.pack.getPackChunk(m.maxChunkSize * m.nextToSend, m.maxChunkSize);
                    if (chunk == null) {
                        log.warn("RP chunk out of range or null data: pack={} chunk={}", m.packId, m.nextToSend);
                        this.session.close("disconnectionScreen.resourcePack");
                        return;
                    }
                    dataPacket.setData(Unpooled.wrappedBuffer(chunk));

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

    private static UUID parsePackId(String entry) {
        if (entry == null || entry.isBlank()) {
            return null;
        }
        String uuidStr = entry;
        int separator = entry.indexOf('_');
        if (separator > 0) {
            uuidStr = entry.substring(0, separator);
        }
        try {
            return UUID.fromString(uuidStr);
        } catch (IllegalArgumentException ignore) {
            return null;
        }
    }
}
