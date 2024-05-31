package cn.nukkit.network.protocol;

import cn.nukkit.math.Vector3f;
import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ToggleCrafterSlotRequestPacket extends DataPacket {

    public static final int $1 = ProtocolInfo.TOGGLE_CRAFTER_SLOT_REQUEST;

    public Vector3f blockPosition;
    public byte slot;
    public boolean disabled;

    @Override
    /**
     * @deprecated 
     */
    
    public int pid() {
        return (byte) NETWORK_ID;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void decode(HandleByteBuf byteBuf) {
        this.blockPosition = byteBuf.readVector3f();
        this.slot = (byte) byteBuf.readByte();
        this.disabled = byteBuf.readBoolean();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void encode(HandleByteBuf byteBuf) {
        
        byteBuf.writeVector3f(this.blockPosition);
        byteBuf.writeByte(this.slot);
        byteBuf.writeBoolean(this.disabled);
    }
    /**
     * @deprecated 
     */
    

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
