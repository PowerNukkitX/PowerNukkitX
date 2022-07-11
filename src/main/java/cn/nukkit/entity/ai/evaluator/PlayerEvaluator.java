package cn.nukkit.entity.ai.evaluator;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.NearestPlayerMemory;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class PlayerEvaluator implements IBehaviorEvaluator{
    @Override
    public boolean evaluate(EntityIntelligent entity) {
        return entity.getBehaviorGroup().getMemory().get(NearestPlayerMemory.class).sensed();
    }
}
