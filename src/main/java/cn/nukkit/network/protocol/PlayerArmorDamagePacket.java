package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

import java.util.EnumSet;
import java.util.Set;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class PlayerArmorDamagePacket extends DataPacket {
    public final Set<PlayerArmorDamageFlag> flags = EnumSet.noneOf(PlayerArmorDamageFlag.class);
    public final int[] damage = new int[5];

    @Override
    public void decode(HandleByteBuf byteBuf) {
        int flagsval = byteBuf.readByte();
        for (int i = 0; i < 5; i++) {
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
        BOOTS,
        BODY;
    }

    @Override
    public int pid() {
        return ProtocolInfo.PLAYER_ARMOR_DAMAGE_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
