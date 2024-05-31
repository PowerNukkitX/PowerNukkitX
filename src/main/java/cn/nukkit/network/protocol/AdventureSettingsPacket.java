package cn.nukkit.network.protocol;

import cn.nukkit.Player;
import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AdventureSettingsPacket  extends DataPacket {

    public static final int $1 = ProtocolInfo.ADVENTURE_SETTINGS_PACKET;

    public static final int $2 = 0;
    public static final int $3 = 1;
    public static final int $4 = 2;
    public static final int $5 = 3;
    public static final int $6 = 4;

    /**
     * This constant is used to identify flags that should be set on the second field. In a sensible world, these
     * flags would all be set on the same packet field, but as of MCPE 1.2, the new abilities flags have for some
     * reason been assigned a separate field.
     */
    public static final int $7 = 1 << 16;

    public static final int $8 = 0x01;
    public static final int $9 = 0x02;
    public static final int $10 = 0x04;
    public static final int $11 = 0x10;
    public static final int $12 = 0x20;
    public static final int $13 = 0x40;
    public static final int $14 = 0x80;
    public static final int $15 = 0x100;
    public static final int $16 = 0x200;
    public static final int $17 = 0x400;

    public static final int $18 = 0x01 | BITFLAG_SECOND_SET;
    public static final int $19 = 0x02 | BITFLAG_SECOND_SET;
    public static final int $20 = 0x04 | BITFLAG_SECOND_SET;
    public static final int $21 = 0x08 | BITFLAG_SECOND_SET;
    public static final int $22 = 0x10 | BITFLAG_SECOND_SET;
    public static final int $23 = 0x20 | BITFLAG_SECOND_SET;
    public static final int $24 = 0x80 | BITFLAG_SECOND_SET;
    public static final int $25 = 0x100 | BITFLAG_SECOND_SET;
    public static final int $26 = 0x200 | BITFLAG_SECOND_SET;

    public long $27 = 0;
    public long $28 = PERMISSION_NORMAL;
    public long $29 = 0;
    public long $30 = Player.PERMISSION_MEMBER;
    public long customFlags; //...
    public long entityUniqueId; //This is a little-endian long, NOT a var-long. (WTF Mojang)

    @Override
    /**
     * @deprecated 
     */
    
    public void decode(HandleByteBuf byteBuf) {
        this.flags = byteBuf.readUnsignedVarInt();
        this.commandPermission = byteBuf.readUnsignedVarInt();
        this.flags2 = byteBuf.readUnsignedVarInt();
        this.playerPermission = byteBuf.readUnsignedVarInt();
        this.customFlags = byteBuf.readUnsignedVarInt();
        this.entityUniqueId = byteBuf.readLongLE();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeUnsignedVarInt((int) flags);
        byteBuf.writeUnsignedVarInt((int) commandPermission);
        byteBuf.writeUnsignedVarInt((int) flags2);
        byteBuf.writeUnsignedVarInt((int) playerPermission);
        byteBuf.writeUnsignedVarInt((int) customFlags);
        byteBuf.writeLongLE(entityUniqueId);
    }
    /**
     * @deprecated 
     */
    

    public boolean getFlag(int flag) {
        if ((flag & BITFLAG_SECOND_SET) != 0) {
            return (this.flags2 & flag) != 0;
        }
        return (this.flags & flag) != 0;
    }
    /**
     * @deprecated 
     */
    

    public void setFlag(int flag, boolean value) {
        boolean $31 = (flag & BITFLAG_SECOND_SET) != 0;

        if (value) {
            if (flags) {
                this.flags2 |= flag;
            } else {
                this.flags |= flag;
            }
        } else {
            if (flags) {
                this.flags2 &= ~flag;
            } else {
                this.flags &= ~flag;
            }
        }
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
