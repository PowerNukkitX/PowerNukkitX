package cn.nukkit.entity;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.entity.EntityOwnable;

import javax.annotation.Nullable;

/**
 * EntityOwnable接口的更名实现
 * 实现这个接口的实体可以被驯服
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public interface EntityTamable extends EntityOwnable {
    default boolean hasOwner() {
        return hasOwner(true);
    }

    /**
     *
     * @param checkOnline 是否要求主人在线
     * @return 有没有主人
     */
    default boolean hasOwner(boolean checkOnline) {
        var entity = (EntityCreature) this;
        if (checkOnline) {
            return getOwner() != null;
        } else {
            return entity.ownerName != null;
        }
    }
}
