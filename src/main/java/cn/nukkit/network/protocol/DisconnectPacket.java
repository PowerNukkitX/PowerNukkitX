package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.DisconnectFailReason;
import lombok.ToString;

/**
 * @since 15-10-12
 */
@ToString
public class DisconnectPacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.DISCONNECT_PACKET;

    public DisconnectFailReason reason = DisconnectFailReason.UNKNOWN;
    public boolean hideDisconnectionScreen = false;
    public String message;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.reason = DisconnectFailReason.values()[byteBuf.readVarInt()];
        this.hideDisconnectionScreen = byteBuf.readBoolean();
        this.message = byteBuf.readString();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        
        byteBuf.writeVarInt(this.reason.ordinal());
        byteBuf.writeBoolean(this.hideDisconnectionScreen);
        if (!this.hideDisconnectionScreen) {
            byteBuf.writeString(this.message);
        }
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
