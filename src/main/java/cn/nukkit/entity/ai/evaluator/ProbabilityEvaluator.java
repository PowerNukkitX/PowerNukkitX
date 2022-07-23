package cn.nukkit.entity.ai.evaluator;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;

import java.util.concurrent.ThreadLocalRandom;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class ProbabilityEvaluator implements IBehaviorEvaluator{

    protected int probability;//0 - 1000

    public ProbabilityEvaluator(int probability){
        this.probability = probability;
    }

    @Override
    public boolean evaluate(EntityIntelligent entity) {
        return ThreadLocalRandom.current().nextInt(1000) < probability;
    }
}
