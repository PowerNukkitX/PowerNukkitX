package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @since 15-10-15
 */
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class InteractPacket extends DataPacket {

    public static final int NETWORK_ID = ProtocolInfo.INTERACT_PACKET;

    public static final int ACTION_VEHICLE_EXIT = 3;
    public static final int ACTION_MOUSEOVER = 4;

    public static final int ACTION_OPEN_INVENTORY = 6;

    public int action;
    public long target;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.action = byteBuf.readByte();
        this.target = byteBuf.readEntityRuntimeId();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        
        byteBuf.writeByte((byte) this.action);
        byteBuf.writeEntityRuntimeId(this.target);
    }

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
