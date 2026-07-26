package org.powernukkitx.entity.ai.evaluator;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityIntelligent;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;


public class ConditionalProbabilityEvaluator extends ProbabilityEvaluator {

    int probability2;
    Function<Entity, Boolean> condition;

    public ConditionalProbabilityEvaluator(int probability1, int probability2, Function<Entity, Boolean> condition, int total) {
        super(probability1, total);
        this.condition = condition;
        this.probability2 = probability2;
    }

    @Override
    public boolean evaluate(EntityIntelligent entity) {
        if (condition.apply(entity)) {
            return ThreadLocalRandom.current().nextInt(total) < probability2;
        } else return ThreadLocalRandom.current().nextInt(total) < probability;
    }
}
