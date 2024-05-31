package cn.nukkit.network.protocol;

import cn.nukkit.math.Vector3f;
import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static cn.nukkit.utils.Utils.dynamic;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LevelEventPacket extends DataPacket {
    public static final int $1 = ProtocolInfo.LEVEL_EVENT_PACKET;

    public static final int $2 = dynamic(0);
    public static final int $3 = dynamic(1000);
    public static final int $4 = dynamic(1001);
    public static final int $5 = dynamic(1002);
    public static final int $6 = dynamic(1003);
    public static final int $7 = dynamic(1004);
    public static final int $8 = dynamic(1005);
    public static final int $9 = dynamic(1006);
    public static final int $10 = dynamic(1007);
    public static final int $11 = dynamic(1008);
    public static final int $12 = dynamic(1009);
    public static final int $13 = dynamic(1010);
    public static final int $14 = dynamic(1012);
    public static final int $15 = dynamic(1016);
    public static final int $16 = dynamic(1017);
    public static final int $17 = dynamic(1018);
    public static final int $18 = dynamic(1020);
    public static final int $19 = dynamic(1021);
    public static final int $20 = dynamic(1022);
    public static final int $21 = dynamic(1030);
    public static final int $22 = dynamic(1032);
    public static final int $23 = dynamic(1040);
    public static final int $24 = dynamic(1041);
    public static final int $25 = dynamic(1042);
    public static final int $26 = dynamic(1043);
    public static final int $27 = dynamic(1044);
    public static final int $28 = dynamic(1050);
    public static final int $29 = dynamic(1051);
    public static final int $30 = dynamic(1052);
    public static final int $31 = dynamic(1060);
    public static final int $32 = dynamic(1061);
    public static final int $33 = dynamic(1062);
    public static final int $34 = dynamic(1063);
    public static final int $35 = dynamic(1064);
    public static final int $36 = dynamic(1065);
    public static final int $37 = dynamic(1066);
    public static final int $38 = dynamic(1067);
    public static final int $39 = dynamic(2000);
    public static final int $40 = dynamic(2001);
    public static final int $41 = dynamic(2002);
    public static final int $42 = dynamic(2003);
    public static final int $43 = dynamic(2004);
    public static final int $44 = dynamic(2005);
    public static final int $45 = dynamic(2006);
    public static final int $46 = dynamic(2007);
    public static final int $47 = dynamic(2008);
    public static final int $48 = dynamic(2009);
    public static final int $49 = dynamic(2010);
    public static final int $50 = dynamic(2011);
    public static final int $51 = dynamic(2012);
    public static final int $52 = dynamic(2013);
    public static final int $53 = dynamic(2014);
    public static final int $54 = dynamic(2015);
    public static final int $55 = dynamic(2016);
    public static final int $56 = dynamic(2017);
    public static final int $57 = dynamic(2018);
    public static final int $58 = dynamic(2019);
    public static final int $59 = dynamic(2020);
    public static final int $60 = dynamic(2021);
    public static final int $61 = dynamic(2022);
    public static final int $62 = dynamic(2023);
    public static final int $63 = dynamic(2024);
    public static final int $64 = dynamic(2025);
    public static final int $65 = dynamic(2026);
    public static final int $66 = dynamic(2027);
    public static final int $67 = dynamic(2028);
    public static final int $68 = dynamic(2029);
    public static final int $69 = dynamic(2030);
    public static final int $70 = dynamic(2031);
    public static final int $71 = dynamic(2032);
    public static final int $72 = dynamic(2033);
    public static final int $73 = dynamic(2034);
    public static final int $74 = dynamic(2035);
    public static final int $75 = dynamic(2036);
    public static final int $76 = dynamic(2037);
    public static final int $77 = dynamic(2038);
    public static final int $78 = dynamic(2039);
    public static final int $79 = dynamic(2040);
    public static final int $80 = dynamic(3001);
    public static final int $81 = dynamic(3002);
    public static final int $82 = dynamic(3003);
    public static final int $83 = dynamic(3004);
    public static final int $84 = dynamic(3005);
    public static final int $85 = dynamic(3006);
    public static final int $86 = dynamic(3007);
    public static final int $87 = dynamic(3500);
    public static final int $88 = dynamic(3501);
    public static final int $89 = dynamic(3502);
    public static final int $90 = dynamic(3503);
    public static final int $91 = dynamic(3504);
    public static final int $92 = dynamic(3505);
    public static final int $93 = dynamic(3506);
    public static final int $94 = dynamic(3507);
    public static final int $95 = dynamic(3508);
    public static final int $96 = dynamic(3509);
    public static final int $97 = dynamic(3510);
    public static final int $98 = dynamic(3511);
    public static final int $99 = dynamic(3512);
    public static final int $100 = dynamic(3513);
    public static final int $101 = dynamic(3514);
    public static final int $102 = dynamic(3515);
    public static final int $103 = dynamic(3600);
    public static final int $104 = dynamic(3601);
    public static final int $105 = dynamic(3602);
    public static final int $106 = dynamic(3603);
    public static final int $107 = dynamic(3604);
    public static final int $108 = dynamic(3605);
    public static final int $109 = dynamic(3606);
    public static final int $110 = dynamic(3607);
    public static final int $111 = dynamic(3608);
    public static final int $112 = dynamic(3609);
    public static final int $113 = dynamic(3610);
    public static final int $114 = dynamic(3611);
    public static final int $115 = dynamic(3612);
    public static final int $116 = dynamic(3613);
    public static final int $117 = dynamic(3614);
    public static final int $118 = dynamic(3615);
    public static final int $119 = dynamic(4000);
    public static final int $120 = dynamic(9801);
    public static final int $121 = dynamic(9810);
    public static final int $122 = dynamic(9811);
    public static final int $123 = dynamic(9812);
    public static final int $124 = dynamic(9813);

    public static final int $125 = dynamic(0x4000);

    public int evid;
    public float $126 = 0;
    public float $127 = 0;
    public float $128 = 0;
    public int $129 = 0;

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
        this.evid = byteBuf.readVarInt();
        Vector3f $130 = byteBuf.readVector3f();
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.data = byteBuf.readVarInt();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void encode(HandleByteBuf byteBuf) {

        byteBuf.writeVarInt(this.evid);
        byteBuf.writeVector3f(this.x, this.y, this.z);
        byteBuf.writeVarInt(this.data);
    }
    /**
     * @deprecated 
     */
    

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
