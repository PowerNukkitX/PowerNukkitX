package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ModalFormRequestPacket extends DataPacket {

    public int formId;
    public String data;

    @Override
    /**
     * @deprecated 
     */
    
    public int pid() {
        return ProtocolInfo.MODAL_FORM_REQUEST_PACKET;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void decode(HandleByteBuf byteBuf) {

    }

    @Override
    /**
     * @deprecated 
     */
    
    public void encode(HandleByteBuf byteBuf) {

        byteBuf.writeVarInt(this.formId);
        byteBuf.writeString(this.data);
    }
    /**
     * @deprecated 
     */
    

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
