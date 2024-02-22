package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.item.Item;

public class PlayerChangeArmorStandEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Entity armorStand;
    private final int slot;
    private Item item;

    public PlayerChangeArmorStandEvent(Player player, Entity armorStand, Item item, int slot) {
        this.player = player;
        this.armorStand = armorStand;
        this.item = item;
        this.slot = slot;
    }

    public Entity getArmorStand() {
        return armorStand;
    }

    public int getSlot() {
        return slot;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }
}
