package cn.nukkit.entity;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

/**
 * 可坐下实体接口
 */
@PowerNukkitXOnly
@Since("1.19.50-r3")
public interface EntityCanSit {
    default boolean isSitting() {
        return ((EntityCreature) this).sitting;
    }

    default void setSitting(boolean sitting) {
        var entity = (EntityCreature) this;
        entity.sitting = sitting;
        entity.setDataFlag(Entity.DATA_FLAGS, Entity.DATA_FLAG_SITTING, sitting);
    }
}
