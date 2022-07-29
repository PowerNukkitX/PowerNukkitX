package cn.nukkit.entity.ai.evaluator;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
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
