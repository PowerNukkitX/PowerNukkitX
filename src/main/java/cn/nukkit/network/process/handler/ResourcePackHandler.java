package cn.nukkit.network.process.handler;

import cn.nukkit.network.connection.BedrockSession;
import cn.nukkit.network.process.SessionState;
import cn.nukkit.network.protocol.ResourcePackChunkDataPacket;
import cn.nukkit.network.protocol.ResourcePackChunkRequestPacket;
import cn.nukkit.network.protocol.ResourcePackClientResponsePacket;
import cn.nukkit.network.protocol.ResourcePackDataInfoPacket;
import cn.nukkit.network.protocol.ResourcePackStackPacket;
import cn.nukkit.network.protocol.ResourcePacksInfoPacket;
import cn.nukkit.resourcepacks.ResourcePack;
import cn.nukkit.utils.version.Version;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
public class ResourcePackHandler extends BedrockSessionPacketHandler {

    private static final int PACKET_SEND_DELAY = 4; // DELAY THE SEND OF PACKETS TO AVOID BURSTING SLOWER AND/OR HIGHER PING CLIENTS
    private final Queue<ResourcePackChunkRequestPacket> chunkRequestQueue = new ConcurrentLinkedQueue<>();
    private boolean sendingChunks = false;

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

                    ResourcePackDataInfoPacket dataInfoPacket = new ResourcePackDataInfoPacket();
                    dataInfoPacket.packId = resourcePack.getPackId();
                    dataInfoPacket.setPackVersion(new Version(resourcePack.getPackVersion()));
                    dataInfoPacket.maxChunkSize = server.getResourcePackManager().getMaxChunkSize();
                    dataInfoPacket.chunkCount = (int) Math.ceil(resourcePack.getPackSize() / (double) dataInfoPacket.maxChunkSize);
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
                stackPacket.experiments.add(
                        new ResourcePackStackPacket.ExperimentData("data_driven_items", true)
                );
                stackPacket.experiments.add(
                        new ResourcePackStackPacket.ExperimentData("data_driven_biomes", true)
                );
                stackPacket.experiments.add(
                        new ResourcePackStackPacket.ExperimentData("upcoming_creator_features", true)
                );
                stackPacket.experiments.add(
                        new ResourcePackStackPacket.ExperimentData("gametest", true)
                );
                stackPacket.experiments.add(
                        new ResourcePackStackPacket.ExperimentData("experimental_molang_features", true)
                );
                stackPacket.experiments.add(
                        new ResourcePackStackPacket.ExperimentData("cameras", true)
                );
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
        if (!sendingChunks) {
            sendingChunks = true;
            processNextChunk();
        }
    }

    private void processNextChunk() {
        ResourcePackChunkRequestPacket pk = chunkRequestQueue.poll();
        if (pk == null) {
            sendingChunks = false;
            return;
        }

        var mgr = session.getServer().getResourcePackManager();
        ResourcePack resourcePack = mgr.getPackById(pk.getPackId());
        if (resourcePack == null) {
            this.session.close("disconnectionScreen.resourcePack");
            sendingChunks = false;
            return;
        }

        int maxChunkSize = mgr.getMaxChunkSize();
        ResourcePackChunkDataPacket dataPacket = new ResourcePackChunkDataPacket();
        dataPacket.setPackId(resourcePack.getPackId());
        dataPacket.setPackVersion(new Version(resourcePack.getPackVersion()));
        dataPacket.chunkIndex = pk.chunkIndex;
        dataPacket.data = resourcePack.getPackChunk(maxChunkSize * pk.chunkIndex, maxChunkSize);
        dataPacket.progress = maxChunkSize * (long) pk.chunkIndex;

        session.sendPacket(dataPacket);
        session.flushSendBuffer();

        // DELAY THE SEND OF PACKETS TO AVOID BURSTING SLOWER AND/OR HIGHER PIGNS CLIENTS
        session.getServer().getScheduler().scheduleDelayedTask(() -> {
            processNextChunk();
        }, PACKET_SEND_DELAY);
    }
}
