package cn.nukkit.entity.ai.behaviorgroup;

import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.behavior.IBehavior;
import cn.nukkit.entity.ai.controller.IController;
import cn.nukkit.entity.ai.memory.IMemoryStorage;
import cn.nukkit.entity.ai.memory.MemoryStorage;
import cn.nukkit.entity.ai.route.finder.IRouteFinder;
import cn.nukkit.entity.ai.sensor.ISensor;

import java.util.Collections;
import java.util.Set;

/**
 * 用于未实现AI的实体，作为占位符使用
 */


public class EmptyBehaviorGroup implements IBehaviorGroup {

    protected EntityIntelligent entity;
    protected IMemoryStorage memoryStorage;

    public EmptyBehaviorGroup(EntityIntelligent entity) {
        this.entity = entity;
        this.memoryStorage = new MemoryStorage(entity);
    }
    
    
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
        return Collections.emptySet();
    }

    @Override
    public Set<IController> getControllers() {
        return Collections.emptySet();
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
        return this.memoryStorage;
    }

    @Override
    public boolean isForceUpdateRoute() {
        return false;
    }

    @Override
    public void setForceUpdateRoute(boolean forceUpdateRoute) {

    }
}
