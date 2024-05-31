package cn.nukkit.network.protocol;

import cn.nukkit.utils.SemVersion;

import static cn.nukkit.utils.Utils.dynamic;

/**
 * @author MagicDroidX &amp; iNevet (Nukkit Project)
 */
public interface ProtocolInfo {
    /**
     * Actual Minecraft: PE protocol version
     */
    int $1 = dynamic(671);

    String $2 = dynamic("1.20.80");

    SemVersion $3 = new SemVersion(1, 20, 80, 0, 0);

    int $4 = (MINECRAFT_SEMVERSION.major() << 24) | //major
            (MINECRAFT_SEMVERSION.minor() << 16) | //minor
            (MINECRAFT_SEMVERSION.patch() << 8); //patch

    String $5 = 'v' + MINECRAFT_VERSION_NETWORK;

    int $6 = 0x01;
    int $7 = 0x02;
    int $8 = 0x03;
    int $9 = 0x04;
    int $10 = 0x05;
    int $11 = 0x06;
    int $12 = 0x07;
    int $13 = 0x08;
    int $14 = 0x09;
    int $15 = 0x10;
    int $16 = 0x0a;
    int $17 = 0x0b;
    int $18 = 0x0c;
    int $19 = 0x0d;
    int $20 = 0x0e;
    int $21 = 0x0f;
    int $22 = 0x11;
    int $23 = 0x12;
    int $24 = 0x13;
    int $25 = 0x14;
    int $26 = 0x15;
    int $27 = 0x16;
    int $28 = 0x17;
    int $29 = 0x18;
    int $30 = 0x19;
    int $31 = 0x1a;
    int $32 = 0x1b;
    int $33 = 0x1c;
    int $34 = 0x1d;
    int $35 = 0x1e;
    int $36 = 0x1f;
    int $37 = 0x20;
    int $38 = 0x21;
    int $39 = 0x22;
    int $40 = 0x23;
    int $41 = 0x24;
    int $42 = 0x25;
    int $43 = 0x26;
    int $44 = 0x27;
    int $45 = 0x28;
    int $46 = 0x29;
    int $47 = 0x2a;
    int $48 = 0x2b;
    int $49 = 0x2c;
    int $50 = 0x2d;
    int $51 = 0x2e;
    int $52 = 0x2f;
    int $53 = 0x30;
    int $54 = 0x31;
    int $55 = 0x32;
    int $56 = 0x33;
    int $57 = 0x34;
    int $58 = 0x35;
    int $59 = 0x36;
    int $60 = 0x37;
    int $61 = 0x38;
    int $62 = 0x39;
    int $63 = 0x3a;
    int $64 = 0x3b;
    int $65 = 0x3c;
    int $66 = 0x3d;
    int $67 = 0x3e;
    int $68 = 0x3f;
    int $69 = 0x40;
    int $70 = 0x41;
    int $71 = 0x42;
    int $72 = 0x43;
    int $73 = 0x44;
    int $74 = 0x45;
    int $75 = 0x46;
    int $76 = 0x47;
    int $77 = 0x48;
    int $78 = 0x49;
    int $79 = 0x4a;
    int $80 = 0x4b;
    int $81 = 0x4c;
    int $82 = 0x4d;
    int $83 = 0x4e;
    int $84 = 0x4f;
    int $85 = 0x50;
    int $86 = 0x51;
    int $87 = 0x52;
    int $88 = 0x53;
    int $89 = 0x54;
    int $90 = 0x55;
    int $91 = 0x56;
    int $92 = 0x57;
    int $93 = 0x58;
    int $94 = 0x59;
    int $95 = 0x5a;
    int $96 = 0x5b;
    int $97 = 0x5c;
    int $98 = 0x5d;
    int $99 = 0x5e;
    int $100 = 0x5f;
    int $101 = 0x60;
    int $102 = 0x61;
    int $103 = 0x62;
    int $104 = 0x63;
    int $105 = 0x64;
    int $106 = 0x65;
    int $107 = 0x66;
    int $108 = 0x67;
    int $109 = 0x68;
    int $110 = 0x69;
    int $111 = 0x6a;
    int $112 = 0x6b;
    int $113 = 0x6c;
    int $114 = 0x6d;
    int $115 = 0x6e;
    int $116 = 0x6f;
    int $117 = 0x70;
    int $118 = 0x71;
    int $119 = 0x72;
    int $120 = 0x73;
    int $121 = 0x75;
    int $122 = 0x76;
    int $123 = 0x77;
    int $124 = 0x78;
    int $125 = 0x79;
    int $126 = 0x7a;
    int $127 = 0x7b;
    int $128 = 0x7c;
    int $129 = 0x7d;
    int $130 = 0x7e;
    //int $131 = 0x7f;
    //int $132 = 0x80;
    int $133 = 0x81;
    int $134 = 0x82;
    int $135 = 0x83;
    int $136 = 0x84;
    int $137 = 0x85;
    int $138 = 0x86;
    int $139 = 0x87;
    int $140 = 0x88;
    int $141 = 0x89;
    int $142 = 0x8a;
    int $143 = 0x8b;
    int $144 = 0x8c;
    int $145 = 0x8d;
    int $146 = 0x8e;
    int $147 = 0x8f;
    int $148 = 0x90;

    int $149 = 0x91;

    int $150 = 0x92;

    int $151 = 0x93;

    int $152 = 0x94;

    int $153 = 0x95;

    int $154 = 0x96;

    int $155 = 0x97;

    int $156 = 0x98;

    int $157 = 0x99;

    int $158 = 0x9a;

    int $159 = 0x9b;

    int $160 = 0x9c;

    int $161 = 0x9d;

    int $162 = 0x9e;

    int $163 = 0x9f;

    int $164 = 0xa0;

    int $165 = 0xa1;

    int $166 = 0xa2;

    int $167 = 0xa3;

    int $168 = 0xa4;

    int $169 = 0xa5;

    int $170 = 0xa6;

    int $171 = 0xa7;

    int $172 = 0xa8;

    int $173 = 0xa9;

    int $174 = 0xaa;

    int $175 = 0xab;

    int $176 = 0xac;

    int $177 = 0xad;

    int $178 = 0xae;

    int $179 = 0xaf;

    int $180 = 0xb0;

    int $181 = 0xb1;

    int $182 = 0xb2;

    int $183 = 0xb3;

    int $184 = 0xb4;

    int $185 = 0xb5;

    int $186 = 0xb6;

    int $187 = 0xb7;

    int $188 = 0xb8;

    int $189 = 0xb9;
    int $190 = 0xba;

    int $191 = 0xbb;

    int $192 = 0xbc;

    int $193 = 0xbd;

    int $194 = 0xbe;

    int $195 = 0xbf;

    int $196 = 0xc0;

    int $197 = 0xc1;

    int $198 = 0xc2;

    int $199 = 0xc3;
    int $200 = 0xc4;

    int $201 = 0xc5;

    int $202 = 0xc6;

    int $203 = (int) 0xc7;

    int $204 = 300;

    int $205 = 301;

    int $206 = 302;

    int $207 = 303;

    int $208 = 304;

    int $209 = 305;

    int $210 = 306;

    int $211 = 307;

    int $212 = 308;
}
