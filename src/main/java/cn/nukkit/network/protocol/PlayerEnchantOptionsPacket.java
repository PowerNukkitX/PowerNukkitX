package cn.nukkit.network.protocol;

import cn.nukkit.item.enchantment.Enchantment;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


@ToString
public class PlayerEnchantOptionsPacket extends DataPacket {
    public static final ConcurrentHashMap<Integer, EnchantOptionData> RECIPE_MAP = new ConcurrentHashMap<>();
    public static final int NETWORK_ID = ProtocolInfo.PLAYER_ENCHANT_OPTIONS_PACKET;
    public static final int ENCH_RECIPEID = 100000;
    public List<EnchantOptionData> options = new ArrayList<>();
    private static final AtomicInteger ENCH_RECIPE_NETID = new AtomicInteger(ENCH_RECIPEID);

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        //client bound
    }

    @Override
    public void encode() {
        this.reset();
        this.putUnsignedVarInt(this.options.size());
        for (EnchantOptionData option : this.options) {
            this.putVarInt(option.minLevel());
            this.putInt(0);
            this.putUnsignedVarInt(option.enchantments.size());
            for (Enchantment data : option.enchantments) {
                this.putByte((byte) data.getId());
                this.putByte((byte) data.getLevel());
            }
            this.putUnsignedVarInt(0);
            this.putUnsignedVarInt(0);
            this.putString(option.enchantName);
            int netid = ENCH_RECIPE_NETID.getAndIncrement();
            this.putUnsignedVarInt(netid);
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
