package cn.nukkit.network.protocol;


import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ScriptMessagePacket extends DataPacket {
    private String channel;
    private String message;

    @Override
    public int pid() {
        return ProtocolInfo.SCRIPT_MESSAGE_PACKET;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.channel = byteBuf.readString();
        this.message = byteBuf.readString();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeString(channel);
        byteBuf.writeString(message);
    }

    public String _getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
