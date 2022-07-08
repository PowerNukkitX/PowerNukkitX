package cn.nukkit.entity.ai.evaluator;

import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.PlayerMemory;

public class PlayerEvaluator implements IBehaviorEvaluator{
    @Override
    public boolean evaluate(EntityIntelligent entity) {
        return entity.getBehaviorGroup().getMemory().get(PlayerMemory.class).sensed();
    }
}
