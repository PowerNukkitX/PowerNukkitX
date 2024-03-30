package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ToastRequestPacket extends DataPacket{

    public String title = "";
    public String content = "";

    @Override
    public int pid() {
        return ProtocolInfo.TOAST_REQUEST_PACKET;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.title = byteBuf.readString();
        this.content = byteBuf.readString();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {

        byteBuf.writeString(this.title);
        byteBuf.writeString(this.content);
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
