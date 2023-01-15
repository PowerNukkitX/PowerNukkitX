package cn.nukkit.entity;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

/**
 * EntityOwnable接口的更名实现
 * 实现这个接口的实体可以被驯服
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public interface EntityTamable extends EntityOwnable {
    default boolean hasOwner() {
        return getTameComponent().hasOwner();
    }

    default boolean hasOwner(boolean checkOnline) {
        return getTameComponent().hasOwner(checkOnline);
    }
}
