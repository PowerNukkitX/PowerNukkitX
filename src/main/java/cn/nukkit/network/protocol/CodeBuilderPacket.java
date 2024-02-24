package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.ToString;


@ToString
public class CodeBuilderPacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.CODE_BUILDER_PACKET;
    public boolean isOpening;
    public String url = "";

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.url = byteBuf.readString();
        this.isOpening = byteBuf.readBoolean();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        
        byteBuf.writeString(url);
        byteBuf.writeBoolean(isOpening);
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
