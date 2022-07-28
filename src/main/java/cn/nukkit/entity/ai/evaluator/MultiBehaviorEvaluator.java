package cn.nukkit.entity.ai.evaluator;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

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
