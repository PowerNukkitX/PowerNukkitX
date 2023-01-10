package cn.nukkit.entity.ai.evaluator;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.MemoryType;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
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
