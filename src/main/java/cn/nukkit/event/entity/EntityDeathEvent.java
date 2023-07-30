package cn.nukkit.event.entity;

import cn.nukkit.entity.EntityLiving;
import cn.nukkit.item.Item;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class EntityDeathEvent extends EntityEvent {

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
