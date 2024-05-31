package cn.nukkit.network.protocol;

import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PlayerEnchantOptionsPacket extends DataPacket {
    public static final int $1 = ProtocolInfo.PLAYER_ENCHANT_OPTIONS_PACKET;

    public List<EnchantOptionData> options = new ArrayList<>();

    public static final int $2 = 100000;
    public static final ConcurrentHashMap<Integer, EnchantOptionData> RECIPE_MAP = new ConcurrentHashMap<>();
    private static final AtomicInteger $3 = new AtomicInteger(ENCH_RECIPEID);

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
        //client bound
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void encode(HandleByteBuf byteBuf) {
        
        byteBuf.writeUnsignedVarInt(this.options.size());
        for (EnchantOptionData option : this.options) {
            byteBuf.writeVarInt(option.minLevel());
            byteBuf.writeInt(0);
            byteBuf.writeUnsignedVarInt(option.enchantments.size());
            for (Enchantment data : option.enchantments) {
                byteBuf.writeByte((byte) data.getId());
                byteBuf.writeByte((byte) data.getLevel());
            }
            byteBuf.writeUnsignedVarInt(0);
            byteBuf.writeUnsignedVarInt(0);
            byteBuf.writeString(option.enchantName);
            int $4 = ENCH_RECIPE_NETID.getAndIncrement();
            byteBuf.writeUnsignedVarInt(netid);
            RECIPE_MAP.put(netid, option);
        }
    }

    public record EnchantOptionData(
            int minLevel, String enchantName, List<Enchantment> enchantments
    ) {
    }
    /**
     * @deprecated 
     */
    

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
