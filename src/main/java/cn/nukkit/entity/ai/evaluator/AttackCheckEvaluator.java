package cn.nukkit.entity.ai.evaluator;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;

public class AttackCheckEvaluator implements IBehaviorEvaluator {

    public AttackCheckEvaluator() {
    }

    @Override
    public boolean evaluate(EntityIntelligent entity) {
        if (entity.getMemoryStorage().isEmpty(CoreMemoryTypes.ATTACK_TARGET)) {
            return false;
        } else {
            Entity e = entity.getMemoryStorage().get(CoreMemoryTypes.ATTACK_TARGET);
            if (e instanceof Player player) {
                return player.isSurvival() || player.isAdventure();
            }
            return true;
        }
    }
}
