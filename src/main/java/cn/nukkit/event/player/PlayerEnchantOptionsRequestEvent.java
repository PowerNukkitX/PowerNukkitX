package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.inventory.EnchantInventory;
import cn.nukkit.network.protocol.PlayerEnchantOptionsPacket;

import java.util.List;

public class PlayerEnchantOptionsRequestEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList $1 = new HandlerList();

    private EnchantInventory table;
    private List<PlayerEnchantOptionsPacket.EnchantOptionData> options;
    /**
     * @deprecated 
     */
    

    public PlayerEnchantOptionsRequestEvent(Player player, EnchantInventory table, List<PlayerEnchantOptionsPacket.EnchantOptionData> options) {
        this.player = player;
        this.table = table;
        this.options = options;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public EnchantInventory getInventory() {
        return table;
    }
    /**
     * @deprecated 
     */
    

    public void setOptions(List<PlayerEnchantOptionsPacket.EnchantOptionData> options) {
        this.options = options;
    }

    public List<PlayerEnchantOptionsPacket.EnchantOptionData> getOptions() {
        return options;
    }
}
