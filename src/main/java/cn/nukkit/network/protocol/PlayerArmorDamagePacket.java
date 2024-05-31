package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.EnumSet;
import java.util.Set;


@ToString
@NoArgsConstructor
public class PlayerArmorDamagePacket extends DataPacket {
    public static final int $1 = ProtocolInfo.PLAYER_ARMOR_DAMAGE_PACKET;
    public final Set<PlayerArmorDamageFlag> flags = EnumSet.noneOf(PlayerArmorDamageFlag.class);
    public final int[] damage = new int[4];

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
        int $2 = byteBuf.readByte();
        for ($3nt $1 = 0; i < 4; i++) {
            if ((flagsval & (1 << i)) != 0) {
                this.flags.add(PlayerArmorDamageFlag.values()[i]);
                this.damage[i] = byteBuf.readVarInt();
            }
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void encode(HandleByteBuf byteBuf) {
        int $4 = 0;
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
    /**
     * @deprecated 
     */
    

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
