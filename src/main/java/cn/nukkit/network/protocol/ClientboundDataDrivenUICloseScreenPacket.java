package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.utils.OptionalValue;
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
public class ClientboundDataDrivenUICloseScreenPacket extends DataPacket {
    private Integer formId;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        setFormId(byteBuf.readOptional(null, byteBuf::readIntLE));
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeOptional(OptionalValue.of(formId), byteBuf::writeIntLE);
    }

    @Override
    public int pid() {
        return ProtocolInfo.CLIENTBOUND_DDUI_CLOSE_SCREEN;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
