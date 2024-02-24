package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.ToString;

@ToString
public class ModalFormRequestPacket extends DataPacket {

    public int formId;
    public String data;

    @Override
    public int pid() {
        return ProtocolInfo.MODAL_FORM_REQUEST_PACKET;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {

    }

    @Override
    public void encode(HandleByteBuf byteBuf) {

        byteBuf.writeVarInt(this.formId);
        byteBuf.writeString(this.data);
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
