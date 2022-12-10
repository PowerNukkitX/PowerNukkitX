package cn.nukkit.entity.ai.evaluator;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * 提供部分实用方法封装
 * <br/>
 * Provide some utility method encapsulation
 */
@PowerNukkitXOnly
@Since("1.19.50-r3")
public interface LogicalUtils {

    default IBehaviorEvaluator any(@NotNull Set<IBehaviorEvaluator> evaluators) {
        return new AnyMatchEvaluator(evaluators);
    }

    default IBehaviorEvaluator any(@NotNull IBehaviorEvaluator... evaluators) {
        return new AnyMatchEvaluator(evaluators);
    }

    default IBehaviorEvaluator all(@NotNull Set<IBehaviorEvaluator> evaluators) {
        return new AllMatchEvaluator(evaluators);
    }

    default IBehaviorEvaluator all(@NotNull IBehaviorEvaluator... evaluators) {
        return new AllMatchEvaluator(evaluators);
    }
}
