package org.powernukkitx.network.process.handler;

import org.powernukkitx.Server;
import org.powernukkitx.network.process.PacketHandler;
import org.powernukkitx.network.process.PlayerSessionHolder;
import org.powernukkitx.network.process.SessionState;
import org.powernukkitx.resourcepacks.ResourcePack;
import org.cloudburstmc.protocol.bedrock.data.DisconnectFailReason;
import org.cloudburstmc.protocol.bedrock.data.PackType;
import org.cloudburstmc.protocol.bedrock.data.payload.experiment.ExperimentToggle;
import org.cloudburstmc.protocol.bedrock.data.payload.experiment.Experiments;
import org.cloudburstmc.protocol.bedrock.data.payload.pack.PackInstanceId;
import org.cloudburstmc.protocol.bedrock.packet.ResourcePackClientResponsePacket;
import org.cloudburstmc.protocol.bedrock.packet.ResourcePackDataInfoPacket;
import org.cloudburstmc.protocol.bedrock.packet.ResourcePackStackPacket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

/**
 * @author Kaooot
 */
public class ResourcePackClientResponseHandler implements PacketHandler<ResourcePackClientResponsePacket> {
    private ArrayList<String> usedUuids = new ArrayList<>();

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

                    if (usedUuids.contains(uuid.toString())) {
                        holder.disconnect(DisconnectFailReason.RESOURCE_PACK_LOADING_FAILED);
                        return;
                    }
                    usedUuids.add(uuid.toString());

                    final ResourcePack resourcePack = server.getResourcePackManager().getPackById(uuid);
                    if (resourcePack == null) {
                        holder.disconnect(DisconnectFailReason.RESOURCE_PACK_LOADING_FAILED);
                        return;
                    }
                    final int maxChunkSize = server.getResourcePackManager().getMaxChunkSize();
                    final int chunkCount = (int) Math.ceil(resourcePack.getPackSize() / (double) maxChunkSize);

                    holder.getInternalPackManager().registerPack(resourcePack, maxChunkSize, chunkCount);

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
                                        new PackInstanceId(
                                                resourcePack.getPackId().toString(),
                                                resourcePack.getPackVersion(),
                                                resourcePack.getSubPackName()
                                        )
                                ).toList()
                );
                resourcePackStack.setBaseGameVersion("*");
                final Experiments experiments = new Experiments();
                experiments.getToggles().addAll(server.getExperiments().stream().map(experiment -> new ExperimentToggle(experiment.getName(), experiment.isEnabled())).toList());
                experiments.setExperimentsEverToggled(!server.getExperiments().isEmpty());
                resourcePackStack.setExperiments(experiments);

                holder.getSession().sendPacketImmediately(resourcePackStack);
            }
            case RESOURCE_PACK_STACK_FINISHED -> {
                holder.sendBeforeSpawn(server);
                holder.setState(SessionState.CHUNKS);
            }
        }
    }
}
