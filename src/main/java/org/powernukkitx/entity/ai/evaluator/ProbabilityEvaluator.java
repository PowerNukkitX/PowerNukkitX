package org.powernukkitx.entity.ai.evaluator;

import org.powernukkitx.entity.EntityIntelligent;

import java.util.concurrent.ThreadLocalRandom;


public class ProbabilityEvaluator implements IBehaviorEvaluator {

    protected int probability;
    protected int total;

    public ProbabilityEvaluator(int probability, int total) {
        this.probability = probability;
        this.total = total;
    }

    @Override
    public boolean evaluate(EntityIntelligent entity) {
        return ThreadLocalRandom.current().nextInt(total) < probability;
    }
}
