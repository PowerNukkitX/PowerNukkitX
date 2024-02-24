package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.ToString;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@ToString
public class ContainerClosePacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.CONTAINER_CLOSE_PACKET;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    public int windowId;
    public boolean wasServerInitiated = true;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.windowId = byteBuf.readByte();
        this.wasServerInitiated = byteBuf.readBoolean();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        
        byteBuf.writeByte((byte) this.windowId);
        byteBuf.writeBoolean(this.wasServerInitiated);
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
