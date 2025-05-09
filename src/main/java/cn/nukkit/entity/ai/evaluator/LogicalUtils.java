package cn.nukkit.entity.ai.evaluator;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * 提供部分实用方法封装
 * <br/>
 * Provide some utility method encapsulation
 */


public interface LogicalUtils {

    default IBehaviorEvaluator any(@NotNull ObjectArrayList<IBehaviorEvaluator> evaluators) {
        return new AnyMatchEvaluator(evaluators);
    }

    default IBehaviorEvaluator any(@NotNull IBehaviorEvaluator... evaluators) {
        return new AnyMatchEvaluator(evaluators);
    }

    default IBehaviorEvaluator all(@NotNull ObjectArrayList<IBehaviorEvaluator> evaluators) {
        return new AllMatchEvaluator(evaluators);
    }

    default IBehaviorEvaluator all(@NotNull IBehaviorEvaluator... evaluators) {
        return new AllMatchEvaluator(evaluators);
    }

    default IBehaviorEvaluator not(@NotNull IBehaviorEvaluator evaluator) {
        return new NotMatchEvaluator(evaluator);
    }

    default IBehaviorEvaluator none() {
        return entity -> true;
    }



}
