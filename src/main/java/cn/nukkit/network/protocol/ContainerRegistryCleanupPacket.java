package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.inventory.FullContainerName;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;

import java.util.List;

@Getter
public class ContainerRegistryCleanupPacket extends DataPacket {
    private final List<FullContainerName> removedContainers = new ObjectArrayList<>();

    @Override
    public int pid() {
        return ProtocolInfo.CONTAINER_REGISTRY_CLEANUP_PACKET;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeArray(this.getRemovedContainers(), byteBuf::writeFullContainerName);
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
