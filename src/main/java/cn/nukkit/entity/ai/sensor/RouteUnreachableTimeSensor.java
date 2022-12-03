package cn.nukkit.entity.ai.sensor;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.MemoryType;

@PowerNukkitXOnly
@Since("1.19.21-r4")
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
