package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.item.Item;
import lombok.Getter;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class PlayerItemHeldEvent extends PlayerEvent implements Cancellable {

    @Getter
    private static final HandlerList handlers = new HandlerList();

    private final Item item;
    private final int hotbarSlot;

    public PlayerItemHeldEvent(Player player, Item item, int hotbarSlot) {
        this.player = player;
        this.item = item;
        this.hotbarSlot = hotbarSlot;
    }

    public int getSlot() {
        return this.hotbarSlot;
    }

    @Deprecated
    public int getInventorySlot() {
        return hotbarSlot;
    }

    public Item getItem() {
        return item;
    }

}
