package cn.nukkit.entity;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public interface EntityAgeable {
    default boolean isBaby() {
        return ((Entity) this).getDataFlag(Entity.DATA_FLAGS, Entity.DATA_FLAG_BABY);
    }

    @PowerNukkitXOnly
    @Since("1.19.50-r3")
    default void setBaby(boolean flag) {
        var entity = (Entity) this;
        entity.setDataFlag(Entity.DATA_FLAGS, Entity.DATA_FLAG_BABY, flag);
        entity.setScale(flag ? 0.5f : 1f);
    }
}
