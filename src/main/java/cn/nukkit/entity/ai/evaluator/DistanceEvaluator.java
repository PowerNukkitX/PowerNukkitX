package cn.nukkit.entity.ai.evaluator;

import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.MemoryType;
import cn.nukkit.math.Vector3;

public class DistanceEvaluator implements IBehaviorEvaluator {

    private final MemoryType<? extends Vector3> type;
    private final double maxDistance;
    private final double minDistance ;


    public DistanceEvaluator(MemoryType<? extends Vector3> type, double maxDistance) {
        this(type, maxDistance, -1);
    }

    public DistanceEvaluator(MemoryType<? extends Vector3> type, double maxDistance, double minDistance) {
        this.type = type;
        this.maxDistance = maxDistance;
        this.minDistance = minDistance;
    }

    @Override
    public boolean evaluate(EntityIntelligent entity) {
        if (entity.getMemoryStorage().isEmpty(type)) {
            return false;
        } else {
            Vector3 location = entity.getMemoryStorage().get(type);
            if(location == null) return false;
            double distance = entity.distance(location);
            return distance <= maxDistance && distance >= minDistance;
        }
    }
}
