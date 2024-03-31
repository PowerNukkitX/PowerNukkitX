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
    public static final int NETWORK_ID = ProtocolInfo.PLAYER_ENCHANT_OPTIONS_PACKET;

    public List<EnchantOptionData> options = new ArrayList<>();

    public static final int ENCH_RECIPEID = 100000;
    public static final ConcurrentHashMap<Integer, EnchantOptionData> RECIPE_MAP = new ConcurrentHashMap<>();
    private static final AtomicInteger ENCH_RECIPE_NETID = new AtomicInteger(ENCH_RECIPEID);

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        //client bound
    }

    @Override
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
            int netid = ENCH_RECIPE_NETID.getAndIncrement();
            byteBuf.writeUnsignedVarInt(netid);
            RECIPE_MAP.put(netid, option);
        }
    }

    public record EnchantOptionData(
            int minLevel, String enchantName, List<Enchantment> enchantments
    ) {
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
