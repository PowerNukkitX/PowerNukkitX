package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.ddui.DataDrivenScreenClosedReason;
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
public class ServerboundDataDrivenClosedPacket extends DataPacket {
    public int formId;
    public DataDrivenScreenClosedReason closeReason;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        setFormId(byteBuf.readIntLE());
        setCloseReason(DataDrivenScreenClosedReason.from(byteBuf.readString()));
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeIntLE(formId);
        byteBuf.writeString(closeReason.getId());
    }

    @Override
    public int pid() {
        return ProtocolInfo.SERVERBOUND_DATA_DRIVEN_SCREEN_CLOSED;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
