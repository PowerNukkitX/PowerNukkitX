package cn.nukkit.entity.ai.evaluator;

import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.MemoryType;
import cn.nukkit.math.Vector3;

public class DistanceEvaluator implements IBehaviorEvaluator {

    private final MemoryType<? extends Vector3> type;
    private final double minDistance ;
    private final double maxDistance;

    public DistanceEvaluator(MemoryType<? extends Vector3> type, double maxDistance) {
        this(type, 0, maxDistance);
    }

    public DistanceEvaluator(MemoryType<? extends Vector3> type, double minDistance, double maxDistance) {
        this.type = type;
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
    }

    @Override
    public boolean evaluate(EntityIntelligent entity) {
        if (entity.getMemoryStorage().isEmpty(type)) {
            return false;
        } else {
            Vector3 location = entity.getMemoryStorage().get(type);
            double distance = entity.distance(location);
            return distance <= maxDistance && distance >= minDistance;
        }
    }
}
