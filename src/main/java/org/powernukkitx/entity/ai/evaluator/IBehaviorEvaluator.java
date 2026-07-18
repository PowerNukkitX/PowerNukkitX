package org.powernukkitx.entity.ai.evaluator;

import org.powernukkitx.entity.EntityIntelligent;

/**
 * This interface abstracts a Behavior Evaluator that <br> decides whether to activate the Actuator bound to it
 */


public interface IBehaviorEvaluator {

    /**
     * Whether the executor bound to it needs to be activated
     * <p>
     * This method evaluates a behavior only once, and if the evaluation passes, the executor execution behavior will start running until the behavior is interrupted or completed, and the next evaluation will not begin
     *
     * @param entity the target entity to assess
     * @return whether it needs to be activated
     */
    boolean evaluate(EntityIntelligent entity);
}
