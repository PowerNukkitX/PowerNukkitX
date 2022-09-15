package cn.nukkit.entity.passive;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityOwnable;

/**
 * EntityOwnable接口的更名实现
 * 实现这个接口的实体可以被驯服
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public interface EntityTamable extends EntityOwnable {
    /**
     * @return 判断有没有主人
     */
    boolean hasOwner();
}
