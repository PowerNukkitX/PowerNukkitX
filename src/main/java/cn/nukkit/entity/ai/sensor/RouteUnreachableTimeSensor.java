package cn.nukkit.entity.ai.sensor;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.IntegerMemory;

@PowerNukkitXOnly
@Since("1.19.21-r4")
public class RouteUnreachableTimeSensor implements ISensor {

    protected Class<? extends IntegerMemory> clazz;

    public RouteUnreachableTimeSensor(Class<? extends IntegerMemory> clazz) {
        this.clazz = clazz;
    }

    @Override
    public void sense(EntityIntelligent entity) {
        var memory = entity.getMemoryStorage().get(clazz);
        var old = memory.hasData() ? memory.getData() : 0;
        if (!entity.getBehaviorGroup().getRouteFinder().isReachable()) {
            memory.setData(old + 1);
        } else {
            memory.setData(0);
        }
    }
}
