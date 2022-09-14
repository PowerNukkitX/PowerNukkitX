package cn.nukkit.entity.ai.evaluator;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * 一个抽象类代表着这个评估器会评估多个行为;
 * <p>
 * An abstract class represents multiple behaviors that this evaluator will evaluate.
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public abstract class MultiBehaviorEvaluator implements IBehaviorEvaluator {
    protected Set<IBehaviorEvaluator> evaluators;

    public MultiBehaviorEvaluator(@NotNull Set<IBehaviorEvaluator> evaluators) {
        this.evaluators = evaluators;
    }

    public MultiBehaviorEvaluator(@NotNull IBehaviorEvaluator... evaluators) {
        this.evaluators = Set.of(evaluators);
    }
}
