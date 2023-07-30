package cn.nukkit.event.entity;

import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.event.Cancellable;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Is cancellable only in PowerNukkit")
public class ItemSpawnEvent extends EntityEvent implements Cancellable {

    public ItemSpawnEvent(EntityItem item) {
        this.entity = item;
    }

    @Override
    public EntityItem getEntity() {
        return (EntityItem) this.entity;
    }
}
