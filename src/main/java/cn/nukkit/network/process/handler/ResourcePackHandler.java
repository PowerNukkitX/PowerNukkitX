package cn.nukkit.network.process.handler;

import cn.nukkit.network.process.NetworkSession;
import cn.nukkit.network.protocol.ResourcePackChunkDataPacket;
import cn.nukkit.network.protocol.ResourcePackChunkRequestPacket;
import cn.nukkit.network.protocol.ResourcePackClientResponsePacket;
import cn.nukkit.network.protocol.ResourcePackDataInfoPacket;
import cn.nukkit.network.protocol.ResourcePackStackPacket;
import cn.nukkit.network.protocol.ResourcePacksInfoPacket;
import cn.nukkit.resourcepacks.ResourcePack;
import cn.nukkit.utils.version.Version;

public class ResourcePackHandler extends NetworkSessionPacketHandler {

    private final Runnable completionCallback;

    public ResourcePackHandler(NetworkSession session, Runnable completionCallback) {
        super(session);
        ResourcePacksInfoPacket infoPacket = new ResourcePacksInfoPacket();
        infoPacket.resourcePackEntries = session.getServer().getResourcePackManager().getResourceStack();
        infoPacket.mustAccept = session.getServer().getForceResources();
        session.sendDataPacket(infoPacket);
        this.completionCallback = completionCallback;
    }

    @Override
    public void handle(ResourcePackClientResponsePacket pk) {
        switch (pk.responseStatus) {
            case ResourcePackClientResponsePacket.STATUS_REFUSED -> player.close("", "disconnectionScreen.noReason");
            case ResourcePackClientResponsePacket.STATUS_SEND_PACKS -> {
                for (ResourcePackClientResponsePacket.Entry entry : pk.packEntries) {
                    ResourcePack resourcePack = player.getServer().getResourcePackManager().getPackById(entry.uuid);
                    if (resourcePack == null) {
                        player.close("", "disconnectionScreen.resourcePack");
                        return;
                    }

                    ResourcePackDataInfoPacket dataInfoPacket = new ResourcePackDataInfoPacket();
                    dataInfoPacket.packId = resourcePack.getPackId();
                    dataInfoPacket.setPackVersion(new Version(resourcePack.getPackVersion()));
                    dataInfoPacket.maxChunkSize = player.getServer().getResourcePackManager().getMaxChunkSize();
                    dataInfoPacket.chunkCount = (int) Math.ceil(resourcePack.getPackSize() / (double) dataInfoPacket.maxChunkSize);
                    dataInfoPacket.compressedPackSize = resourcePack.getPackSize();
                    dataInfoPacket.sha256 = resourcePack.getSha256();
                    session.sendDataPacket(dataInfoPacket);
                }
            }
            case ResourcePackClientResponsePacket.STATUS_HAVE_ALL_PACKS -> {
                ResourcePackStackPacket stackPacket = new ResourcePackStackPacket();
                stackPacket.mustAccept = player.getServer().getForceResources() && !player.getServer().getForceResourcesAllowOwnPacks();
                stackPacket.resourcePackStack = player.getServer().getResourcePackManager().getResourceStack();
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
                session.sendDataPacket(stackPacket);
            }
            case ResourcePackClientResponsePacket.STATUS_COMPLETED -> this.completionCallback.run();
        }
    }

    @Override
    public void handle(ResourcePackChunkRequestPacket pk) {
        // TODO: Pack version check
        ResourcePack resourcePack = player.getServer().getResourcePackManager().getPackById(pk.getPackId());
        if (resourcePack == null) {
            player.close("", "disconnectionScreen.resourcePack");
            return;
        }
        int maxChunkSize = player.getServer().getResourcePackManager().getMaxChunkSize();
        ResourcePackChunkDataPacket dataPacket = new ResourcePackChunkDataPacket();
        dataPacket.setPackId(resourcePack.getPackId());
        dataPacket.setPackVersion(new Version(resourcePack.getPackVersion()));
        dataPacket.chunkIndex = pk.chunkIndex;
        dataPacket.data = resourcePack.getPackChunk(maxChunkSize * pk.chunkIndex, maxChunkSize);
        dataPacket.progress = maxChunkSize * (long) pk.chunkIndex;
        session.sendDataPacket(dataPacket);
    }
}
