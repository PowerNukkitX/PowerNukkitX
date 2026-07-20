package org.powernukkitx.entity.ai.evaluator;

import org.powernukkitx.entity.EntityIntelligent;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.NotNull;


/**
 * An evaluator that is executed only after all behaviors have been evaluated.
 */
public class AllMatchEvaluator extends MultiBehaviorEvaluator {

    public AllMatchEvaluator(@NotNull ObjectArrayList<IBehaviorEvaluator> evaluators) {
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
