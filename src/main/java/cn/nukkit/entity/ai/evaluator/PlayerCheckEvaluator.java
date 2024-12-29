package cn.nukkit.entity.ai.evaluator;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.ai.memory.MemoryType;

import java.util.concurrent.ThreadLocalRandom;

public class PlayerCheckEvaluator implements IBehaviorEvaluator {

    private MemoryType<? extends Entity> memoryType;
    private double minRange;
    private double ignoreMinDistanceProbability;

    public PlayerCheckEvaluator(MemoryType<? extends Entity> type) {
        this(type, 0, 0);
    }

    public PlayerCheckEvaluator(MemoryType<? extends Entity> type, double minRange, double ignoreMinDistanceProbability) {
        this.memoryType = type;
        this.minRange = minRange;
        this.ignoreMinDistanceProbability = ignoreMinDistanceProbability;
    }
    @Override
    public boolean evaluate(EntityIntelligent entity) {
        if (entity.getMemoryStorage().isEmpty(memoryType)) {
            return false;
        } else {
            Entity e = entity.getMemoryStorage().get(memoryType);
            if(e.distance(entity) >= minRange) {
                if (e instanceof Player player) {
                    return (player.isSurvival() || player.isAdventure());
                }
                return true;
            }
            if(ignoreMinDistanceProbability != 0 && ThreadLocalRandom.current().nextDouble() < ignoreMinDistanceProbability) {
                return true;
            }
            return false;
        }
    }
}
