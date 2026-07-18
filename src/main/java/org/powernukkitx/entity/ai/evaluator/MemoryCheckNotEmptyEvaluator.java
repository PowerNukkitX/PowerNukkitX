package org.powernukkitx.entity.ai.evaluator;

import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.ai.memory.MemoryType;


public class MemoryCheckNotEmptyEvaluator implements IBehaviorEvaluator {

    protected MemoryType<?> type;

    public MemoryCheckNotEmptyEvaluator(MemoryType<?> type) {
        this.type = type;
    }

    @Override
    public boolean evaluate(EntityIntelligent entity) {
        return entity.getBehaviorGroup().getMemoryStorage().notEmpty(type);
    }
}
