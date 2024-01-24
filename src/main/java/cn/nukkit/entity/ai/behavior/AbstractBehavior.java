package cn.nukkit.entity.ai.behavior;

import lombok.Getter;
import lombok.Setter;

/**
 * AbstractBehavior包含一个{@link BehaviorState}属性以及它的Getter/Setter
 * <p>
 * AbstractBehavior contains a {@link BehaviorState} property and its Getter/Setter
 */


public abstract class AbstractBehavior implements IBehavior {
    @Getter
    @Setter
    protected BehaviorState behaviorState = BehaviorState.STOP;
}
