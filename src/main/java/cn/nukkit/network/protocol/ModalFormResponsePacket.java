package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ModalFormResponsePacket extends DataPacket {

    public int formId;
    public String $1 = "null";
    public int cancelReason;

    @Override
    /**
     * @deprecated 
     */
    
    public int pid() {
        return ProtocolInfo.MODAL_FORM_RESPONSE_PACKET;
    }

    @Override
    /**
     * @deprecated 
     */
    
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
    /**
     * @deprecated 
     */
    
    public void encode(HandleByteBuf byteBuf) {

    }
    /**
     * @deprecated 
     */
    

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
