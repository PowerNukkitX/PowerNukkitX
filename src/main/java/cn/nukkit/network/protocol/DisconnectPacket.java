package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.DisconnectFailReason;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @since 15-10-12
 */
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DisconnectPacket extends DataPacket {
    public static final int $1 = ProtocolInfo.DISCONNECT_PACKET;

    public DisconnectFailReason $2 = DisconnectFailReason.UNKNOWN;
    public boolean $3 = false;
    public String message;

    @Override
    /**
     * @deprecated 
     */
    
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void decode(HandleByteBuf byteBuf) {
        this.reason = DisconnectFailReason.values()[byteBuf.readVarInt()];
        this.hideDisconnectionScreen = byteBuf.readBoolean();
        this.message = byteBuf.readString();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void encode(HandleByteBuf byteBuf) {
        
        byteBuf.writeVarInt(this.reason.ordinal());
        byteBuf.writeBoolean(this.hideDisconnectionScreen);
        if (!this.hideDisconnectionScreen) {
            byteBuf.writeString(this.message);
        }
    }
    /**
     * @deprecated 
     */
    

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
