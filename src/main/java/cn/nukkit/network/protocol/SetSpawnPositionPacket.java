package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SetSpawnPositionPacket extends DataPacket {
    public static final int TYPE_PLAYER_SPAWN = 0;
    public static final int TYPE_WORLD_SPAWN = 1;

    public int spawnType;
    public int y;
    public int z;
    public int x;

    public int dimension = 0;
    
    @Override
    public void decode(HandleByteBuf byteBuf) {

    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeVarInt(this.spawnType);
        byteBuf.writeBlockVector3(this.x, this.y, this.z);
        byteBuf.writeVarInt(dimension);
        byteBuf.writeBlockVector3(this.x, this.y, this.z);
    }

    @Override
    public int pid() {
        return ProtocolInfo.SET_SPAWN_POSITION_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
