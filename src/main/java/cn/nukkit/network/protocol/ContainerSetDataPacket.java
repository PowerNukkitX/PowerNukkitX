package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ContainerSetDataPacket extends DataPacket {
    public static final int $1 = ProtocolInfo.CONTAINER_SET_DATA_PACKET;

    public static final int $2 = 0;
    public static final int $3 = 1;
    public static final int $4 = 2;
    //TODO: check property 3
    public static final int $5 = 4;

    public static final int $6 = 0;
    public static final int $7 = 1;
    public static final int $8 = 2;

    @Override
    /**
     * @deprecated 
     */
    
    public int pid() {
        return NETWORK_ID;
    }

    public int windowId;
    public int property;
    public int value;

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

        byteBuf.writeByte((byte) this.windowId);
        byteBuf.writeVarInt(this.property);
        byteBuf.writeVarInt(this.value);
    }
    /**
     * @deprecated 
     */
    

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
