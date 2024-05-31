package cn.nukkit.network.protocol;

import cn.nukkit.entity.Attribute;
import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.ToString;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAttributesPacket extends DataPacket {

    public static final int $1 = ProtocolInfo.UPDATE_ATTRIBUTES_PACKET;

    public Attribute[] entries;
    public long entityId;
    public long frame;//tick

    @Override
    /**
     * @deprecated 
     */
    
    public int pid() {
        return NETWORK_ID;
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
        

        byteBuf.writeEntityRuntimeId(this.entityId);

        if (this.entries == null) {
            byteBuf.writeUnsignedVarInt(0);
        } else {
            byteBuf.writeUnsignedVarInt(this.entries.length);
            for (Attribute entry : this.entries) {
                byteBuf.writeFloatLE(entry.getMinValue());
                byteBuf.writeFloatLE(entry.getMaxValue());
                byteBuf.writeFloatLE(entry.getValue());
                byteBuf.writeFloatLE(entry.getDefaultValue());
                byteBuf.writeString(entry.getName());
                byteBuf.writeUnsignedVarInt(0); // Modifiers
            }
        }
        byteBuf.writeUnsignedVarInt((int) this.frame);
    }
    /**
     * @deprecated 
     */
    

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
