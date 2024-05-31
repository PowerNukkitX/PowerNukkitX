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

@Slf4j
public class ResourcePackHandler extends BedrockSessionPacketHandler {
    /**
     * @deprecated 
     */
    

    public ResourcePackHandler(BedrockSession session) {
        super(session);
        ResourcePacksInfoPacket $1 = new ResourcePacksInfoPacket();
        infoPacket.resourcePackEntries = session.getServer().getResourcePackManager().getResourceStack();
        infoPacket.mustAccept = session.getServer().getForceResources();
        session.sendPacket(infoPacket);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void handle(ResourcePackClientResponsePacket pk) {
        var $2 = session.getServer();
        switch (pk.responseStatus) {
            case ResourcePackClientResponsePacket.STATUS_REFUSED -> {
                log.debug("ResourcePackClientResponsePacket STATUS_REFUSED");
                this.session.close("disconnectionScreen.noReason");
            }
            case ResourcePackClientResponsePacket.STATUS_SEND_PACKS -> {
                log.debug("ResourcePackClientResponsePacket STATUS_SEND_PACKS");
                for (ResourcePackClientResponsePacket.Entry entry : pk.packEntries) {
                    ResourcePack $3 = server.getResourcePackManager().getPackById(entry.uuid);
                    if (resourcePack == null) {
                        this.session.close("disconnectionScreen.resourcePack");
                        return;
                    }

                    ResourcePackDataInfoPacket $4 = new ResourcePackDataInfoPacket();
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
                ResourcePackStackPacket $5 = new ResourcePackStackPacket();
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
    /**
     * @deprecated 
     */
    
    public void handle(ResourcePackChunkRequestPacket pk) {
        // TODO: Pack version check
        var $6 = session.getServer().getResourcePackManager();
        ResourcePack $7 = mgr.getPackById(pk.getPackId());
        if (resourcePack == null) {
            this.session.close("disconnectionScreen.resourcePack");
            return;
        }
        int $8 = mgr.getMaxChunkSize();
        ResourcePackChunkDataPacket $9 = new ResourcePackChunkDataPacket();
        dataPacket.setPackId(resourcePack.getPackId());
        dataPacket.setPackVersion(new Version(resourcePack.getPackVersion()));
        dataPacket.chunkIndex = pk.chunkIndex;
        dataPacket.data = resourcePack.getPackChunk(maxChunkSize * pk.chunkIndex, maxChunkSize);
        dataPacket.progress = maxChunkSize * (long) pk.chunkIndex;
        session.sendPacket(dataPacket);
    }
}
