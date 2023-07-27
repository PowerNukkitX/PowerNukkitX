package cn.nukkit.network.process.processor;

import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.network.protocol.ResourcePackChunkDataPacket;
import cn.nukkit.network.protocol.ResourcePackChunkRequestPacket;
import cn.nukkit.player.Player;
import cn.nukkit.player.PlayerHandle;
import cn.nukkit.resourcepacks.ResourcePack;
import org.jetbrains.annotations.NotNull;
import org.powernukkit.version.Version;

public class ResourcePackChunkRequestProcessor extends DataPacketProcessor<ResourcePackChunkRequestPacket> {
    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull ResourcePackChunkRequestPacket pk) {
        Player player = playerHandle.player;
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
        player.dataResourcePacket(dataPacket);
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.toNewProtocolID(ProtocolInfo.RESOURCE_PACK_CHUNK_REQUEST_PACKET);
    }
}
