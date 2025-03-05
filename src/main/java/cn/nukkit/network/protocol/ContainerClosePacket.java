package cn.nukkit.network.protocol;

import cn.nukkit.inventory.InventoryType;
import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ContainerClosePacket extends DataPacket {
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

    @Override
    public int pid() {
        return ProtocolInfo.CONTAINER_CLOSE_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
