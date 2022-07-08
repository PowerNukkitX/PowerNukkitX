package cn.nukkit.entity.ai.executor;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.route.RouteFinder;
import cn.nukkit.entity.ai.route.RouteFinderSearchTask;
import cn.nukkit.entity.ai.route.RouteFinderThreadPool;
import cn.nukkit.entity.ai.route.WalkerRouteFinder;
import cn.nukkit.math.Vector3;
import lombok.Getter;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
@Getter
public class WalkToTargetExecutor extends BaseMoveExecutor{

    //指示执行器应该从哪个Memory获取目标位置
    protected Class<?> memoryClazz;
    protected double maxDistance;
    protected RouteFinder route;

    public WalkToTargetExecutor(Class<?> memoryClazz,double maxDistance){
        this.memoryClazz = memoryClazz;
        this.maxDistance = maxDistance;
    }

    @Override
    public boolean execute(EntityIntelligent entity) {
        if (route == null)
            route = new WalkerRouteFinder(entity,entity,memoryClazz);
        Vector3 target = (Vector3) entity.getBehaviorGroup().getMemory().get(memoryClazz).getData();
        if (target == null)
            return false;
        //若实体超出最大距离，则return false
        if (entity.distanceSquared(target) > maxDistance * maxDistance)
            return false;
        Vector3 next = null;
        if (!this.route.isSearching()) {
            RouteFinderThreadPool.executeRouteFinderThread(new RouteFinderSearchTask(this.route));
        }
        if (this.route.hasNext()) {
            next = this.route.next();
        }
        if (next != null){
            double diff = Math.abs(next.x - entity.x) + Math.abs(next.z - entity.z);
            move(entity, new Vector3((next.x - entity.x)/diff*0.1,(next.y - entity.y)/diff*0.1,(next.z - entity.z)/diff*0.1));
        }
        return true;
    }
}
