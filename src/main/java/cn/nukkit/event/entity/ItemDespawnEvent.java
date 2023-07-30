package cn.nukkit.event.entity;

import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.event.Cancellable;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemDespawnEvent extends EntityEvent implements Cancellable {

    public ItemDespawnEvent(EntityItem item) {
        this.entity = item;
    }

    @Override
    public EntityItem getEntity() {
        return (EntityItem) this.entity;
    }
}
