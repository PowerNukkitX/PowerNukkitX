package org.powernukkitx.event.entity;

import org.powernukkitx.entity.EntityLiving;
import org.powernukkitx.event.HandlerList;
import org.powernukkitx.item.Item;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class EntityDeathEvent extends EntityEvent {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private Item[] drops;

    public EntityDeathEvent(EntityLiving entity) {
        this(entity, Item.EMPTY_ARRAY);
    }

    public EntityDeathEvent(EntityLiving entity, Item[] drops) {
        this.entity = entity;
        this.drops = drops;
    }

    public Item[] getDrops() {
        return drops;
    }

    public void setDrops(Item[] drops) {
        if (drops == null) {
            drops = Item.EMPTY_ARRAY;
        }

        this.drops = drops;
    }
}
