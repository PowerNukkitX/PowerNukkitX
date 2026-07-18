package org.powernukkitx.entity.ai.evaluator;

import org.powernukkitx.Player;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.ai.memory.MemoryType;

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
                return (player.spawned && player.isOnline() && (player.isSurvival() || player.isAdventure()) && player.isAlive());
            }
            return !e.isClosed();
        }
    }
}
