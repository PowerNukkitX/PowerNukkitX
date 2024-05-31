package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;


@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EntityEventPacket extends DataPacket {
    public static final int $1 = ProtocolInfo.ENTITY_EVENT_PACKET;
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
    public static final int $16 = 14;
    public static final int $17 = 15;
    public static final int $18 = 16;
    public static final int $19 = 17;
    public static final int $20 = 18;
    public static final int $21 = 19;
    public static final int $22 = 20;
    public static final int $23 = 21;
    public static final int $24 = 22;
    public static final int $25 = 23;

    public static final int $26 = 24;
    public static final int $27 = 25;
    public static final int $28 = 26;
    public static final int $29 = 27;
    public static final int $30 = 28;
    public static final int $31 = 29;
    public static final int $32 = 30;
    public static final int $33 = 31;
    public static final int $34 = 32;
    public static final int $35 = 33;
    public static final int $36 = 34;
    public static final int $37 = 35;
    public static final int $38 = 36;
    public static final int $39 = 37;
    public static final int $40 = 38;
    public static final int $41 = 39;

    public static final int $42 = 57;

    public static final int $43 = 60;
    public static final int $44 = 61;
    public static final int $45 = 62;
    public static final int $46 = 63;


    public static final int $47 = 64;
    public static final int $48 = 65;
    public static final int $49 = 66;
    public static final int $50 = 67;
    public static final int $51 = 68;
    public static final int $52 = 69;


    public static final int $53 = 70;


    public static final int $54 = 71;


    public static final int $55 = 72;


    public static final int $56 = 73;


    public static final int $57 = 74;


    public static final int $58 = 75;


    public static final int $59 = 76;
    public static final int $60 = 77;

    public static final int $61 = 78;

    @Override
    /**
     * @deprecated 
     */
    
    public int pid() {
        return NETWORK_ID;
    }

    public long eid;
    public int event;
    public int data;

    @Override
    /**
     * @deprecated 
     */
    
    public void decode(HandleByteBuf byteBuf) {
        this.eid = byteBuf.readEntityRuntimeId();
        this.event = byteBuf.readByte();
        this.data = byteBuf.readVarInt();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void encode(HandleByteBuf byteBuf) {
        
        byteBuf.writeEntityRuntimeId(this.eid);
        byteBuf.writeByte((byte) this.event);
        byteBuf.writeVarInt(this.data);
    }
    /**
     * @deprecated 
     */
    

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
