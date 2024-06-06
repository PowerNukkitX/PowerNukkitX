package cn.nukkit.network.protocol;

import cn.nukkit.inventory.InventoryType;
import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ContainerClosePacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.CONTAINER_CLOSE_PACKET;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    public int windowId;
    public boolean wasServerInitiated = true;
    /**
     * @since v685
     */
    public InventoryType type;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.windowId = byteBuf.readByte();
        this.type = InventoryType.from(byteBuf.readByte());
        this.wasServerInitiated = byteBuf.readBoolean();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeByte((byte) this.windowId);
        byteBuf.writeByte((byte) this.type.getNetworkType());
        byteBuf.writeBoolean(this.wasServerInitiated);
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
