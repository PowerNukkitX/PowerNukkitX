package cn.nukkit.network.protocol;

import cn.nukkit.math.BlockVector3;
import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ContainerOpenPacket extends DataPacket {
    public static final int $1 = ProtocolInfo.CONTAINER_OPEN_PACKET;

    @Override
    /**
     * @deprecated 
     */
    
    public int pid() {
        return NETWORK_ID;
    }

    public int windowId;
    public int type;
    public int x;
    public int y;
    public int z;
    public long $2 = -1;

    @Override
    /**
     * @deprecated 
     */
    
    public void decode(HandleByteBuf byteBuf) {
        this.windowId = byteBuf.readByte();
        this.type = byteBuf.readByte();
        BlockVector3 $3 = byteBuf.readBlockVector3();
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.entityId = byteBuf.readEntityUniqueId();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void encode(HandleByteBuf byteBuf) {
        
        byteBuf.writeByte((byte) this.windowId);
        byteBuf.writeByte((byte) this.type);
        byteBuf.writeBlockVector3(this.x, this.y, this.z);
        byteBuf.writeEntityUniqueId(this.entityId);
    }
    /**
     * @deprecated 
     */
    

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
