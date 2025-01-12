package cn.nukkit.event.item;

import cn.nukkit.event.Event;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class ItemEvent extends Event {

    private final Item item;

    public ItemEvent(Item item) {
        this.item = item;
    }

    public Item getItem() {
        return item;
    }
}
