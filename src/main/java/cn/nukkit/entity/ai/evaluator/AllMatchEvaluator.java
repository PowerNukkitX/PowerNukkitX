package cn.nukkit.entity.ai.evaluator;

import cn.nukkit.entity.EntityIntelligent;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * 全部行为评估通过才执行的一个评估器.
 * <p>
 * An evaluator that is executed only after all behaviors have been evaluated.
 */


public class AllMatchEvaluator extends MultiBehaviorEvaluator {

    public AllMatchEvaluator(@NotNull Set<IBehaviorEvaluator> evaluators) {
        super(evaluators);
    }

    public AllMatchEvaluator(@NotNull IBehaviorEvaluator... evaluators) {
        super(evaluators);
    }

    @Override
    public boolean evaluate(EntityIntelligent entity) {
        for (IBehaviorEvaluator evaluator : evaluators) {
            if (!evaluator.evaluate(entity)) return false;
        }
        return true;
    }
}
