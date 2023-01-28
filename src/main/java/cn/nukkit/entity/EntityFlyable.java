package cn.nukkit.entity;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

/**
 * 实现了此接口的实体可飞行
 */
@PowerNukkitXOnly
@Since("1.19.60-r1")
public interface EntityFlyable {
    /**
     * @return 是否具有摔落伤害
     */
    default boolean hasFallingDamage() {
        return false;
    }
}
