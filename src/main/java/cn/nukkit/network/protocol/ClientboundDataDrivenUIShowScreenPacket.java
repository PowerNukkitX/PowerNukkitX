package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.utils.OptionalValue;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ClientboundDataDrivenUIShowScreenPacket extends DataPacket {
    public String screenId;
    public int formId;
    public Integer dataInstanceId;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.screenId = byteBuf.readString();
        this.formId = byteBuf.readIntLE();
        this.dataInstanceId = byteBuf.readOptional(null, byteBuf::readIntLE);
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeString(this.screenId);
        byteBuf.writeIntLE(this.formId);
        byteBuf.writeOptional(OptionalValue.of(this.dataInstanceId), byteBuf::writeIntLE);
    }

    @Override
    public int pid() {
        return ProtocolInfo.CLIENTBOUND_DDUI_SHOW_SCREEN;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
