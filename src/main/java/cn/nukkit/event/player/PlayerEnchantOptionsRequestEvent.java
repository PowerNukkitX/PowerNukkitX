package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.inventory.EnchantInventory;
import org.cloudburstmc.protocol.bedrock.data.inventory.ItemEnchantOption;

import java.util.List;

public class PlayerEnchantOptionsRequestEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private EnchantInventory table;
    private List<ItemEnchantOption> options;

    public PlayerEnchantOptionsRequestEvent(Player player, EnchantInventory table, List<ItemEnchantOption> options) {
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

    public void setOptions(List<ItemEnchantOption> options) {
        this.options = options;
    }

    public List<ItemEnchantOption> getOptions() {
        return options;
    }
}
