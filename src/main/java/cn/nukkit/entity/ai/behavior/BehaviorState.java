package cn.nukkit.entity.ai.behavior;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

/**
 * 表示一个行为的状态
 * <p>
 * Indicates the state of a behavior
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public enum BehaviorState {
    ACTIVE,
    STOP
}
