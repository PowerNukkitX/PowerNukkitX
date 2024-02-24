package cn.nukkit.entity;

import cn.nukkit.entity.data.EntityFlag;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public interface EntityAgeable {
    default boolean isBaby() {
        return ((Entity) this).getDataFlag(EntityFlag.BABY);
    }

    default void setBaby(boolean flag) {
        var entity = (Entity) this;
        entity.setDataFlag(EntityFlag.BABY, flag);
        entity.setScale(flag ? 0.5f : 1f);
    }
}
