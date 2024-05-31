package cn.nukkit.entity.ai.evaluator;

import cn.nukkit.entity.EntityIntelligent;

import java.util.concurrent.ThreadLocalRandom;


public class ProbabilityEvaluator implements IBehaviorEvaluator {

    protected int probability;
    protected int total;
    /**
     * @deprecated 
     */
    

    public ProbabilityEvaluator(int probability, int total) {
        this.probability = probability;
        this.total = total;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean evaluate(EntityIntelligent entity) {
        return ThreadLocalRandom.current().nextInt(total) < probability;
    }
}
