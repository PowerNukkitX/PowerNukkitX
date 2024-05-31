package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SetSpawnPositionPacket extends DataPacket {

    public static final int $1 = ProtocolInfo.SET_SPAWN_POSITION_PACKET;

    public static final int $2 = 0;
    public static final int $3 = 1;

    public int spawnType;
    public int y;
    public int z;
    public int x;


    public int $4 = 0;
    
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
        
        byteBuf.writeVarInt(this.spawnType);
        byteBuf.writeBlockVector3(this.x, this.y, this.z);
        byteBuf.writeVarInt(dimension);
        byteBuf.writeBlockVector3(this.x, this.y, this.z);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int pid() {
        return NETWORK_ID;
    }
    /**
     * @deprecated 
     */
    

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
