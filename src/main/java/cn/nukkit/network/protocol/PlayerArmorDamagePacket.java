package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.EnumSet;
import java.util.Set;


@ToString
@NoArgsConstructor
public class PlayerArmorDamagePacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.PLAYER_ARMOR_DAMAGE_PACKET;
    public final Set<PlayerArmorDamageFlag> flags = EnumSet.noneOf(PlayerArmorDamageFlag.class);
    public final int[] damage = new int[4];

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        int flagsval = byteBuf.readByte();
        for (int i = 0; i < 4; i++) {
            if ((flagsval & (1 << i)) != 0) {
                this.flags.add(PlayerArmorDamageFlag.values()[i]);
                this.damage[i] = byteBuf.readVarInt();
            }
        }
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        int outflags = 0;
        for (PlayerArmorDamageFlag flag : this.flags) {
            outflags |= 1 << flag.ordinal();
        }
        byteBuf.writeByte(outflags);

        for (PlayerArmorDamageFlag flag : this.flags) {
            byteBuf.writeVarInt(this.damage[flag.ordinal()]);
        }
    }

    public enum PlayerArmorDamageFlag {
        HELMET,
        CHESTPLATE,
        LEGGINGS,
        BOOTS
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
