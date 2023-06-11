package cn.nukkit.network.process.processor;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.network.protocol.ResourcePackClientResponsePacket;
import cn.nukkit.network.protocol.ResourcePackDataInfoPacket;
import cn.nukkit.network.protocol.ResourcePackStackPacket;
import cn.nukkit.resourcepacks.ResourcePack;
import org.jetbrains.annotations.NotNull;
import org.powernukkit.version.Version;

public class ResourcePackClientResponseProcessor extends DataPacketProcessor<ResourcePackClientResponsePacket> {
    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull ResourcePackClientResponsePacket pk) {
        Player player = playerHandle.player;
        switch (pk.responseStatus) {
            case ResourcePackClientResponsePacket.STATUS_REFUSED -> {
                player.close("", "disconnectionScreen.noReason");
            }
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
                    player.dataResourcePacket(dataInfoPacket);
                }
            }
            case ResourcePackClientResponsePacket.STATUS_HAVE_ALL_PACKS -> {
                ResourcePackStackPacket stackPacket = new ResourcePackStackPacket();
                stackPacket.mustAccept = player.getServer().getForceResources() && !player.getServer().getForceResourcesAllowOwnPacks();
                stackPacket.resourcePackStack = player.getServer().getResourcePackManager().getResourceStack();
                if (player.getServer().isEnableExperimentMode() && !player.getServer().getConfig("settings.waterdogpe", false)) {
                    stackPacket.experiments.add(
                            new ResourcePackStackPacket.ExperimentData("data_driven_items", true)
                    );
                    stackPacket.experiments.add(
                            new ResourcePackStackPacket.ExperimentData("upcoming_creator_features", true)
                    );
                    stackPacket.experiments.add(
                            new ResourcePackStackPacket.ExperimentData("experimental_molang_features", true)
                    );
                    stackPacket.experiments.add(
                            new ResourcePackStackPacket.ExperimentData("cameras", true)
                    );
                }
                player.dataResourcePacket(stackPacket);
            }
            case ResourcePackClientResponsePacket.STATUS_COMPLETED -> {
                playerHandle.setShouldLogin(true);
                if (playerHandle.getPreLoginEventTask().isFinished()) {
                    playerHandle.getPreLoginEventTask().onCompletion(player.getServer());
                }
            }
        }
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.toNewProtocolID(ProtocolInfo.RESOURCE_PACK_CLIENT_RESPONSE_PACKET);
    }
}
