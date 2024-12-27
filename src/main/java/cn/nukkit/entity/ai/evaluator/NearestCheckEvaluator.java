package cn.nukkit.entity.ai.evaluator;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;

public class NearestCheckEvaluator implements IBehaviorEvaluator {

    public NearestCheckEvaluator() {
    }

    @Override
    public boolean evaluate(EntityIntelligent entity) {
        if (entity.getMemoryStorage().isEmpty(CoreMemoryTypes.NEAREST_PLAYER)) {
            return false;
        } else {
            Entity e = entity.getMemoryStorage().get(CoreMemoryTypes.NEAREST_PLAYER);
            if (e instanceof Player player) {
                return player.isSurvival() || player.isAdventure();
            }
            return true;
        }
    }
}
