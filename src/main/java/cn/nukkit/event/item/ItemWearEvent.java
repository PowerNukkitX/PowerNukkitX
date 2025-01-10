package cn.nukkit.event.item;

import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.item.Item;

public class ItemWearEvent extends ItemEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private int newDurability;

    public ItemWearEvent(Item item, int newDurability) {
        super(item);
        this.newDurability = newDurability;
    }

    public int getNewDurability() {
        return newDurability;
    }

    public void setNewDurability(int newDurability) {
        this.newDurability = newDurability;
    }
}
