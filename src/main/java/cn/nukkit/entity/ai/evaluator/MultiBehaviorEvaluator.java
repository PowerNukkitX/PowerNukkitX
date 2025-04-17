package cn.nukkit.entity.ai.evaluator;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * 一个抽象类代表着这个评估器会评估多个行为;
 * <p>
 * An abstract class represents multiple behaviors that this evaluator will evaluate.
 */


public abstract class MultiBehaviorEvaluator implements IBehaviorEvaluator {
    protected ObjectArrayList<IBehaviorEvaluator> evaluators;

    public MultiBehaviorEvaluator(@NotNull ObjectArrayList<IBehaviorEvaluator> evaluators) {
        this.evaluators = evaluators;
    }

    public MultiBehaviorEvaluator(@NotNull IBehaviorEvaluator... evaluators) {
        this.evaluators = ObjectArrayList.of(evaluators);
    }
}
