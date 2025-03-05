package cn.nukkit.network.protocol;

import cn.nukkit.Player;
import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AdventureSettingsPacket extends DataPacket {
    public static final int PERMISSION_NORMAL = 0;
    public static final int PERMISSION_OPERATOR = 1;
    public static final int PERMISSION_HOST = 2;
    public static final int PERMISSION_AUTOMATION = 3;
    public static final int PERMISSION_ADMIN = 4;

    /**
     * This constant is used to identify flags that should be set on the second field. In a sensible world, these
     * flags would all be set on the same packet field, but as of MCPE 1.2, the new abilities flags have for some
     * reason been assigned a separate field.
     */
    public static final int BITFLAG_SECOND_SET = 1 << 16;

    public static final int WORLD_IMMUTABLE = 0x01;
    public static final int NO_PVM = 0x02;
    public static final int NO_MVP = 0x04;
    public static final int SHOW_NAME_TAGS = 0x10;
    public static final int AUTO_JUMP = 0x20;
    public static final int ALLOW_FLIGHT = 0x40;
    public static final int NO_CLIP = 0x80;
    public static final int WORLD_BUILDER = 0x100;
    public static final int FLYING = 0x200;
    public static final int MUTED = 0x400;

    public static final int MINE = 0x01 | BITFLAG_SECOND_SET;
    public static final int DOORS_AND_SWITCHES = 0x02 | BITFLAG_SECOND_SET;
    public static final int OPEN_CONTAINERS = 0x04 | BITFLAG_SECOND_SET;
    public static final int ATTACK_PLAYERS = 0x08 | BITFLAG_SECOND_SET;
    public static final int ATTACK_MOBS = 0x10 | BITFLAG_SECOND_SET;
    public static final int OPERATOR = 0x20 | BITFLAG_SECOND_SET;
    public static final int TELEPORT = 0x80 | BITFLAG_SECOND_SET;
    public static final int BUILD = 0x100 | BITFLAG_SECOND_SET;
    public static final int DEFAULT_LEVEL_PERMISSIONS = 0x200 | BITFLAG_SECOND_SET;

    public long flags = 0;
    public long commandPermission = PERMISSION_NORMAL;
    public long flags2 = 0;
    public long playerPermission = Player.PERMISSION_MEMBER;
    public long customFlags; //...
    public long entityUniqueId; //This is a little-endian long, NOT a var-long. (WTF Mojang)

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.flags = byteBuf.readUnsignedVarInt();
        this.commandPermission = byteBuf.readUnsignedVarInt();
        this.flags2 = byteBuf.readUnsignedVarInt();
        this.playerPermission = byteBuf.readUnsignedVarInt();
        this.customFlags = byteBuf.readUnsignedVarInt();
        this.entityUniqueId = byteBuf.readLongLE();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeUnsignedVarInt((int) flags);
        byteBuf.writeUnsignedVarInt((int) commandPermission);
        byteBuf.writeUnsignedVarInt((int) flags2);
        byteBuf.writeUnsignedVarInt((int) playerPermission);
        byteBuf.writeUnsignedVarInt((int) customFlags);
        byteBuf.writeLongLE(entityUniqueId);
    }

    public boolean getFlag(int flag) {
        if ((flag & BITFLAG_SECOND_SET) != 0) {
            return (this.flags2 & flag) != 0;
        }
        return (this.flags & flag) != 0;
    }

    public void setFlag(int flag, boolean value) {
        boolean flags = (flag & BITFLAG_SECOND_SET) != 0;

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
    public int pid() {
        return ProtocolInfo.ADVENTURE_SETTINGS_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
