package cn.nukkit.entity.ai.behaviorgroup;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.behavior.IBehavior;
import cn.nukkit.entity.ai.controller.IController;
import cn.nukkit.entity.ai.memory.IMemoryStorage;
import cn.nukkit.entity.ai.route.IRouteFinder;
import cn.nukkit.entity.ai.sensor.ISensor;

import java.util.Collections;
import java.util.Set;

/**
 * 用于未实现AI的实体，作为占位符使用
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class EmptyBehaviorGroup implements IBehaviorGroup {
    @Override
    public void evaluateBehaviors(EntityIntelligent entity) {

    }

    @Override
    public void evaluateCoreBehaviors(EntityIntelligent entity) {

    }

    @Override
    public void collectSensorData(EntityIntelligent entity) {

    }

    @Override
    public void tickRunningBehaviors(EntityIntelligent entity) {

    }

    @Override
    public void tickRunningCoreBehaviors(EntityIntelligent entity) {

    }

    @Override
    public void applyController(EntityIntelligent entity) {

    }

    @Override
    public Set<IBehavior> getBehaviors() {
        return null;
    }

    @Override
    public Set<IBehavior> getCoreBehaviors() {
        return null;
    }

    @Override
    public Set<IBehavior> getRunningBehaviors() {
        return null;
    }

    @Override
    public Set<IBehavior> getRunningCoreBehaviors() {
        return null;
    }

    @Override
    public Set<ISensor> getSensors() {
        return Collections.EMPTY_SET;
    }

    @Override
    public Set<IController> getControllers() {
        return Collections.EMPTY_SET;
    }

    @Override
    public IRouteFinder getRouteFinder() {
        return null;
    }

    @Override
    public void updateRoute(EntityIntelligent entity) {

    }

    @Override
    public IMemoryStorage getMemoryStorage() {
        return null;
    }

    @Override
    public void setForceUpdateRoute(boolean forceUpdateRoute) {

    }

    @Override
    public boolean isForceUpdateRoute() {
        return false;
    }
}
