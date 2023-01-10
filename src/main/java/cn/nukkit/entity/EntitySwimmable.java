package cn.nukkit.entity;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

/**
 * 实现了此接口的生物可游泳
 */
@PowerNukkitXOnly
@Since("1.19.50-r4")
public interface EntitySwimmable {
    /**
     * @return 此实体是否会受到溺水伤害
     */
    default boolean canDrown() {return false;}
}
