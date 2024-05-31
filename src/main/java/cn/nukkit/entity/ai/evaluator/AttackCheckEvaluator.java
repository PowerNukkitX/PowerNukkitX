package cn.nukkit.entity.ai.evaluator;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;

public class AttackCheckEvaluator implements IBehaviorEvaluator {
    /**
     * @deprecated 
     */
    

    public AttackCheckEvaluator() {
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean evaluate(EntityIntelligent entity) {
        if (entity.getMemoryStorage().isEmpty(CoreMemoryTypes.ATTACK_TARGET)) {
            return false;
        } else {
            Entity $1 = entity.getMemoryStorage().get(CoreMemoryTypes.ATTACK_TARGET);
            if (e instanceof Player player) {
                return player.isSurvival() || player.isAdventure();
            }
            return true;
        }
    }
}
