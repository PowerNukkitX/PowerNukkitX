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
public class ClientboundControlSchemeSetPacket extends DataPacket {

    public byte controlScheme;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        controlScheme = byteBuf.readByte();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeByte(controlScheme);
    }

    @Override
    public int pid() {
        return ProtocolInfo.CLIENTBOUND_CONTROL_SCHEME_SET_PACKET;
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
