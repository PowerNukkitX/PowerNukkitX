package cn.nukkit.entity.ai.behavior;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.evaluator.IBehaviorEvaluator;
import cn.nukkit.entity.ai.executor.IBehaviorExecutor;

/**
 * 此接口抽象了一个行为对象，作为行为组{@link IBehaviorGroup}的组成部分
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public interface IBehavior extends IBehaviorExecutor, IBehaviorEvaluator {

    /**
     * 返回此行为的优先级，高优先级的行为会覆盖低优先级的行为
     *
     * @return 优先级
     */
    default int getPriority() {
        return 1;
    }

    /**
     * 返回此行为的权重值，高权重的行为有更大几率被选中
     *
     * @return 权重值
     */
    default int getWeight() {
        return 1;
    }

    /**
     * 返回此行为的刷新周期，小的刷新周期会使得评估器被更频繁的调用
     * 注意此方法只会影响评估器的调用，而不会影响执行器的调用
     *
     * @return 刷新周期
     */
    default int getPeriod() {
        return 1;
    }

    /**
     * @return 此行为当前的状态
     */
    BehaviorState getBehaviorState();

    /**
     * 设置此行为的状态
     *
     * @param state 状态
     */
    void setBehaviorState(BehaviorState state);
}
