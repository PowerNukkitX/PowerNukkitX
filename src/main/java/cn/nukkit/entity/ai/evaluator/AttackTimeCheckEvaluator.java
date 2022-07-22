package cn.nukkit.entity.ai.evaluator;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.AttackMemory;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class AttackTimeCheckEvaluator implements IBehaviorEvaluator{

    protected int time;

    public AttackTimeCheckEvaluator(int time){
        this.time = time;
    }

    @Override
    public boolean evaluate(EntityIntelligent entity) {
        var attackMemory = entity.getMemoryStorage().get(AttackMemory.class);
        return attackMemory != null && (Server.getInstance().getTick() - attackMemory.getAttackTime()) <= time;
    }
}
