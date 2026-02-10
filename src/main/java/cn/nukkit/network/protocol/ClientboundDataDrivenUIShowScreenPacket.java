package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ClientboundDataDrivenUIShowScreenPacket extends DataPacket {
    public String screenId;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.screenId = byteBuf.readString();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeString(this.screenId);
    }

    @Override
    public int pid() {
        return ProtocolInfo.CLIENTBOUND_DDUI_SHOW_SCREEN;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
