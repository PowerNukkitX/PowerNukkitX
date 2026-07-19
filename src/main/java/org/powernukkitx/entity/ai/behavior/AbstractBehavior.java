package org.powernukkitx.entity.ai.behavior;

import lombok.Getter;
import lombok.Setter;

/**
 * AbstractBehavior contains a {@link BehaviorState} property and its Getter/Setter
 */


public abstract class AbstractBehavior implements IBehavior {
    @Getter
    @Setter
    protected BehaviorState behaviorState = BehaviorState.STOP;
}
