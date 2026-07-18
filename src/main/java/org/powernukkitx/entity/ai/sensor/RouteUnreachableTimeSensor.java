package org.powernukkitx.entity.ai.sensor;

import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.ai.memory.MemoryType;


public class RouteUnreachableTimeSensor implements ISensor {

    protected MemoryType<Integer> type;

    public RouteUnreachableTimeSensor(MemoryType<Integer> type) {
        this.type = type;
    }

    @Override
    public void sense(EntityIntelligent entity) {
        var old = entity.getMemoryStorage().get(type);
        if (!entity.getBehaviorGroup().getRouteFinder().isReachable()) {
            entity.getMemoryStorage().put(type, old + 1);
        } else {
            entity.getMemoryStorage().put(type, 0);
        }
    }
}
