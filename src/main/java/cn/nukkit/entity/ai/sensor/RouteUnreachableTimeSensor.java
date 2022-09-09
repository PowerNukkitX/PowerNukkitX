package cn.nukkit.entity.ai.sensor;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.IntegerMemory;

@PowerNukkitXOnly
@Since("1.19.21-r4")
public class RouteUnreachableTimeSensor implements ISensor{

    protected Class<? extends IntegerMemory> clazz;
    protected int period;

    public RouteUnreachableTimeSensor(Class<? extends IntegerMemory> clazz, int period) {
        this.clazz = clazz;
        this.period = period;
    }

    @Override
    public void sense(EntityIntelligent entity) {
        var memory = entity.getMemoryStorage().get(clazz);
        if (!entity.getBehaviorGroup().getRouteFinder().isReachable()) {
            memory.setData(memory.getData() + 1);
        } else {
            memory.setData(memory.getData() - 1);
        }
    }

    @Override
    public int getPeriod() {
        return this.period;
    }
}
