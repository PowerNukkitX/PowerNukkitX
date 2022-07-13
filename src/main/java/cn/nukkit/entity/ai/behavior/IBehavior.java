package cn.nukkit.entity.ai.behavior;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.ai.evaluator.IBehaviorEvaluator;
import cn.nukkit.entity.ai.executor.IBehaviorExecutor;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public interface IBehavior extends IBehaviorExecutor, IBehaviorEvaluator {

    /**
     * 返回此行为的优先级，高优先级的行为会覆盖低优先级的行为
     *
     * @return int
     */
    default int getPriority() {
        return 1;
    }

    /**
     * 返回此行为的权重值，高权重的行为有更大几率被选中
     *
     * @return int
     */
    default int getWeight() {
        return 1;
    }

    /**
     * 返回此行为的名称,默认返回类名称
     *
     * @return String
     */
    default String getName() {
        return this.getClass().getSimpleName();
    }

    default boolean equals(IBehavior behavior) {
        return getName().equals(behavior.getName());
    }
}
