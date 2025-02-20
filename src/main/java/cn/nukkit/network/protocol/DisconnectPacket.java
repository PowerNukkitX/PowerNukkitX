package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.DisconnectFailReason;
import lombok.*;

/**
 * @since 15-10-12
 */

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DisconnectPacket extends DataPacket {
    public DisconnectFailReason reason = DisconnectFailReason.UNKNOWN;
    public boolean hideDisconnectionScreen = false;
    public String message;
    private String filteredMessage = "";

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.reason = DisconnectFailReason.values()[byteBuf.readVarInt()];
        this.hideDisconnectionScreen = byteBuf.readBoolean();
        this.message = byteBuf.readString();
        this.filteredMessage=byteBuf.readString();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeVarInt(this.reason.ordinal());
        byteBuf.writeBoolean(this.hideDisconnectionScreen);
        if (!this.hideDisconnectionScreen) {
            byteBuf.writeString(this.message);
            byteBuf.writeString(this.filteredMessage);
        }
    }

    @Override
    public int pid() {
        return ProtocolInfo.DISCONNECT_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
