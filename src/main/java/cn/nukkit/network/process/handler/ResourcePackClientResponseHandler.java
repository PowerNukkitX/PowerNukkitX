package cn.nukkit.network.process.handler;

import cn.nukkit.Server;
import cn.nukkit.network.process.PacketHandler;
import cn.nukkit.network.process.PlayerSessionHolder;
import cn.nukkit.network.process.SessionState;
import cn.nukkit.resourcepacks.ResourcePack;
import org.cloudburstmc.protocol.bedrock.data.DisconnectFailReason;
import org.cloudburstmc.protocol.bedrock.data.PackType;
import org.cloudburstmc.protocol.bedrock.packet.ResourcePackClientResponsePacket;
import org.cloudburstmc.protocol.bedrock.packet.ResourcePackDataInfoPacket;
import org.cloudburstmc.protocol.bedrock.packet.ResourcePackStackPacket;

import java.util.Arrays;
import java.util.UUID;

/**
 * @author Kaooot
 */
public class ResourcePackClientResponseHandler implements PacketHandler<ResourcePackClientResponsePacket> {

    @Override
    public void handle(ResourcePackClientResponsePacket packet, PlayerSessionHolder holder, Server server) {
        if (!holder.getState().equals(SessionState.RESOURCE_PACK)) {
            holder.disconnect(DisconnectFailReason.UNEXPECTED_PACKET);
            return;
        }
        switch (packet.getResponse()) {
            case CANCEL -> holder.disconnect(DisconnectFailReason.RESOURCE_PACK_PROBLEM);
            case DOWNLOADING -> {
                for (String downloadingPack : packet.getDownloadingPacks()) {
                    final UUID uuid;
                    if (downloadingPack.contains("_")) {
                        uuid = UUID.fromString(downloadingPack.split("_")[0]);
                    } else {
                        uuid = UUID.fromString(downloadingPack);
                    }
                    final ResourcePack resourcePack = server.getResourcePackManager().getPackById(uuid);
                    if (resourcePack == null) {
                        holder.disconnect(DisconnectFailReason.RESOURCE_PACK_LOADING_FAILED);
                        return;
                    }
                    final int maxChunkSize = server.getResourcePackManager().getMaxChunkSize();
                    final int chunkCount = (int) Math.ceil(resourcePack.getPackSize() / (double) maxChunkSize);

                    holder.getPlayer().getInternalPackManager().registerPack(uuid, resourcePack, maxChunkSize, chunkCount);

                    final ResourcePackDataInfoPacket resourcePackDataInfoPacket = new ResourcePackDataInfoPacket();
                    resourcePackDataInfoPacket.setPackId(resourcePack.getPackId());
                    resourcePackDataInfoPacket.setPackVersion(resourcePack.getPackVersion());
                    resourcePackDataInfoPacket.setChunkSize(maxChunkSize);
                    resourcePackDataInfoPacket.setNumberOfChunks(chunkCount);
                    resourcePackDataInfoPacket.setFileSize(resourcePack.getPackSize());
                    resourcePackDataInfoPacket.setFileHash(resourcePack.getSha256());
                    resourcePackDataInfoPacket.setPackType(PackType.RESOURCES);

                    holder.getSession().sendPacketImmediately(resourcePackDataInfoPacket);
                }
            }
            case DOWNLOADING_FINISHED -> {
                final ResourcePackStackPacket resourcePackStack = new ResourcePackStackPacket();
                resourcePackStack.setTexturePackRequired(server.getForceResources() && !server.getForceResourcesAllowOwnPacks());
                resourcePackStack.getTexturePackList().addAll(
                        Arrays.stream(server.getResourcePackManager().getResourceStack())
                                .map(resourcePack ->
                                        new ResourcePackStackPacket.Entry(
                                                resourcePack.getPackId() + "_" + resourcePack.getPackVersion(),
                                                resourcePack.getPackVersion(),
                                                resourcePack.getSubPackName()
                                        )
                                ).toList()
                );
                resourcePackStack.setBaseGameVersion("*");
                resourcePackStack.getExperiments().addAll(server.getExperiments());
                resourcePackStack.setWereAnyExperimentsEverToggled(!server.getExperiments().isEmpty());

                holder.getSession().sendPacketImmediately(resourcePackStack);
            }
            case RESOURCE_PACK_STACK_FINISHED -> {
                holder.sendBeforeSpawn(server);
                holder.setState(SessionState.CHUNKS);
            }
        }
    }
}