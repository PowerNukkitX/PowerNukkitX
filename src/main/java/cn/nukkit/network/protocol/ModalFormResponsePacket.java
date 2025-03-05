package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ModalFormResponsePacket extends DataPacket {
    public int formId;
    public String data = "null";
    public int cancelReason;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.formId = byteBuf.readVarInt();
        if (byteBuf.readBoolean()) {
            this.data = byteBuf.readString();
        }
        if (byteBuf.readBoolean()) {
            this.cancelReason = byteBuf.readByte();
        }
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {

    }

    @Override
    public int pid() {
        return ProtocolInfo.MODAL_FORM_RESPONSE_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
