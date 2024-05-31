package cn.nukkit.entity.ai.evaluator;

import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.MemoryType;


public class MemoryCheckNotEmptyEvaluator implements IBehaviorEvaluator {

    protected MemoryType<?> type;
    /**
     * @deprecated 
     */
    

    public MemoryCheckNotEmptyEvaluator(MemoryType<?> type) {
        this.type = type;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean evaluate(EntityIntelligent entity) {
        return entity.getBehaviorGroup().getMemoryStorage().notEmpty(type);
    }
}
