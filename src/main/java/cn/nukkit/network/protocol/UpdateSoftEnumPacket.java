package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSoftEnumPacket extends DataPacket {

    public List<String> values = List.of();
    public String $1 = "";
    public Type $2 = Type.SET;

    @Override
    /**
     * @deprecated 
     */
    
    public int pid() {
        return ProtocolInfo.UPDATE_SOFT_ENUM_PACKET;
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
        
        byteBuf.writeString(name);
        byteBuf.writeUnsignedVarInt(values.size());

        for (String value : values) {
            byteBuf.writeString(value);
        }
        byteBuf.writeByte((byte) type.ordinal());
    }

    public enum Type {
        ADD,
        REMOVE,
        SET
    }
    /**
     * @deprecated 
     */
    

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
