package cn.nukkit.entity.ai.evaluator;

import cn.nukkit.entity.EntityIntelligent;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.NotNull;

import java.util.Set;


public class AnyMatchEvaluator extends MultiBehaviorEvaluator {

    public AnyMatchEvaluator(@NotNull ObjectArrayList<IBehaviorEvaluator> evaluators) {
        super(evaluators);
    }

    public AnyMatchEvaluator(@NotNull IBehaviorEvaluator... evaluators) {
        super(evaluators);
    }

    @Override
    public boolean evaluate(EntityIntelligent entity) {
        for (IBehaviorEvaluator evaluator : evaluators) {
            if (evaluator.evaluate(entity)) return true;
        }
        return false;
    }
}
