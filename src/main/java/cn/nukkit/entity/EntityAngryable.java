package cn.nukkit.entity;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

/**
 * 实体可生气
 */
@PowerNukkitXOnly
@Since("1.19.50-r3")
public interface EntityAngryable {
    default boolean isAngry() {
        return ((EntityCreature) this).angry;
    }

    default void setAngry(boolean angry) {
        var entity = (EntityCreature) this;
        entity.angry = angry;
        entity.setDataFlag(Entity.DATA_FLAGS, Entity.DATA_FLAG_ANGRY, angry);
    }
}
