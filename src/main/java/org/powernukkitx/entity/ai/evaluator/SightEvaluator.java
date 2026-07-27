package org.powernukkitx.entity.ai.evaluator;

import org.powernukkitx.block.Block;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.ai.memory.MemoryType;
import org.powernukkitx.math.Vector3;

public class SightEvaluator implements IBehaviorEvaluator {

    private final MemoryType<? extends Entity> type;

    public SightEvaluator(MemoryType<? extends Entity> type) {
        this.type = type;
    }

    @Override
    public boolean evaluate(EntityIntelligent entity) {
        if (entity.getMemoryStorage().isEmpty(type)) {
            return false;
        }
        return entity.hasLineOfSight(entity.getMemoryStorage().get(type));
    }
}
