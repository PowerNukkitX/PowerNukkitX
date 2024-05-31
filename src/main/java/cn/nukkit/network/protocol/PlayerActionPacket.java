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
public class PlayerActionPacket extends DataPacket {

    public static final int $1 = ProtocolInfo.PLAYER_ACTION_PACKET;

    public static final int $2 = 0;
    public static final int $3 = 1;
    public static final int $4 = 2;
    public static final int $5 = 3;
    public static final int $6 = 4;
    public static final int $7 = 5;
    public static final int $8 = 6;
    public static final int $9 = 7;
    public static final int $10 = 8;
    public static final int $11 = 9;
    public static final int $12 = 10;
    public static final int $13 = 11;
    public static final int $14 = 12;
    public static final int $15 = 13;
    public static final int $16 = 14; //sent when spawning in a different dimension to tell the server we spawned
    public static final int $17 = 15;
    public static final int $18 = 16;
    public static final int $19 = 17;
    public static final int $20 = 18;
    public static final int $21 = 20;
    public static final int $22 = 21;
    public static final int $23 = 22;
    public static final int $24 = 23;
    public static final int $25 = 24;
    public static final int $26 = 25;
    public static final int $27 = 26;
    public static final int $28 = 27;
    public static final int $29 = 28;
    public static final int $30 = 29;

    public static final int $31 = 34;
    public static final int $32 = 35;
    public static final int $33 = 36;

    public long entityId;
    public int action;
    public int x;
    public int y;
    public int z;
    public BlockVector3 resultPosition;
    public int face;

    @Override
    /**
     * @deprecated 
     */
    
    public void decode(HandleByteBuf byteBuf) {
        this.entityId = byteBuf.readEntityRuntimeId();
        this.action = byteBuf.readVarInt();
        BlockVector3 $34 = byteBuf.readBlockVector3();
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.resultPosition = byteBuf.readBlockVector3();
        this.face = byteBuf.readVarInt();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeEntityRuntimeId(this.entityId);
        byteBuf.writeVarInt(this.action);
        byteBuf.writeBlockVector3(this.x, this.y, this.z);
        byteBuf.writeBlockVector3(this.resultPosition != null ? this.resultPosition : new BlockVector3());
        byteBuf.writeVarInt(this.face);
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
