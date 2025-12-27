package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ClientboundDataDrivenUICloseAllScreensPacket extends DataPacket {

    public byte controlScheme;

    @Override
    public void decode(HandleByteBuf byteBuf) {

    }

    @Override
    public void encode(HandleByteBuf byteBuf) {

    }

    @Override
    public int pid() {
        return ProtocolInfo.CLIENTBOUND_DATA_DRIVEN_UI_CLOSE_ALL_SCREENS_PACKET;
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
