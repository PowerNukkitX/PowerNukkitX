package cn.nukkit.entity.ai.evaluator;

import cn.nukkit.entity.EntityIntelligent;
import org.jetbrains.annotations.NotNull;

import java.util.Set;


public class AnyMatchEvaluator extends MultiBehaviorEvaluator {
    /**
     * @deprecated 
     */
    

    public AnyMatchEvaluator(@NotNull Set<IBehaviorEvaluator> evaluators) {
        super(evaluators);
    }
    /**
     * @deprecated 
     */
    

    public AnyMatchEvaluator(@NotNull IBehaviorEvaluator... evaluators) {
        super(evaluators);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean evaluate(EntityIntelligent entity) {
        for (IBehaviorEvaluator evaluator : evaluators) {
            if (evaluator.evaluate(entity)) return true;
        }
        return false;
    }
}
