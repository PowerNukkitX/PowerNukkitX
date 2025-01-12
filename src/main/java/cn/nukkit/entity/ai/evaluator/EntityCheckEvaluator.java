package cn.nukkit.entity.ai.evaluator;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.MemoryType;

public class EntityCheckEvaluator implements IBehaviorEvaluator {

    private MemoryType<? extends Entity> memoryType;


    public EntityCheckEvaluator(MemoryType<? extends Entity> type) {
        this.memoryType = type;
    }
    @Override
    public boolean evaluate(EntityIntelligent entity) {
        if (entity.getMemoryStorage().isEmpty(memoryType)) {
            return false;
        } else {
            Entity e = entity.getMemoryStorage().get(memoryType);
            if (e instanceof Player player) {
                return (player.locallyInitialized && (player.isSurvival() || player.isAdventure()));
            }
            return !e.isClosed();
        }
    }
}
