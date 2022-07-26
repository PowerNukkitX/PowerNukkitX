package cn.nukkit.entity.ai.behaviorgroup;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.behavior.IBehavior;
import cn.nukkit.entity.ai.controller.IController;
import cn.nukkit.entity.ai.memory.IMemory;
import cn.nukkit.entity.ai.memory.IMemoryStorage;
import cn.nukkit.entity.ai.memory.MemoryStorage;
import cn.nukkit.entity.ai.route.RouteFindingManager;
import cn.nukkit.entity.ai.route.SimpleRouteFinder;
import cn.nukkit.entity.ai.sensor.ISensor;
import cn.nukkit.math.Vector3;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
@Getter
@Setter
public class BehaviorGroup implements IBehaviorGroup {

    /**
     * 决定多少gt更新一次路径
     */
    protected static int ROUTE_UPDATE_CYCLE = 16;//gt


    /**
     * 不会被其他行为覆盖的"核心“行为
     */
    protected final Set<IBehavior> coreBehaviors = new HashSet<>();

    /**
     * 全部行为
     */
    protected final Set<IBehavior> behaviors = new HashSet<>();
    /**
     * 传感器
     */
    protected final Set<ISensor> sensors = new HashSet<>();
    /**
     * 控制器
     */
    protected final Set<IController> controllers = new HashSet<>();
    /**
     * 正在运行的”核心“行为
     */
    protected final Set<IBehavior> runningCoreBehaviors = new HashSet<>();
    /**
     * 正在运行的行为
     */
    protected final Set<IBehavior> runningBehaviors = new HashSet<>();
    /**
     * 用于存储核心行为距离上次评估逝去的gt数
     */
    protected final Map<IBehavior,Integer> coreBehaviorPeriodTimer = new HashMap<>();
    /**
     * 用于存储行为距离上次评估逝去的gt数
     */
    protected final Map<IBehavior,Integer> behaviorPeriodTimer = new HashMap<>();
    /**
     * 用于存储传感器距离上次刷新逝去的gt数
     */
    protected final Map<ISensor,Integer> sensorPeriodTimer = new HashMap<>();
    /**
     * 记忆存储器
     */
    protected final IMemoryStorage memoryStorage = new MemoryStorage();
    /**
     * 寻路器(非异步，因为没必要，生物AI本身就是并行的)
     */
    protected final SimpleRouteFinder routeFinder;
    /**
     * 寻路任务
     */
    protected RouteFindingManager.RouteFindingTask routeFindingTask;

    /**
     * 记录距离上次路径更新过去的gt数
     */
    protected int currentRouteUpdateTick = 0;//gt

    protected boolean forceUpdateRoute = false;


    public BehaviorGroup(Set<IBehavior> coreBehaviors,Set<IBehavior> behaviors, Set<ISensor> sensors, Set<IController> controllers, SimpleRouteFinder routeFinder) {
        this.coreBehaviors.addAll(coreBehaviors);
        this.behaviors.addAll(behaviors);
        this.sensors.addAll(sensors);
        this.controllers.addAll(controllers);
        this.routeFinder = routeFinder;
        this.initPeriodTimer();
    }

    public void addBehavior(IBehavior behavior) {
        this.behaviors.add(behavior);
    }

    public void addSensor(ISensor sensor) {
        this.sensors.add(sensor);
    }

    public void addController(IController controller) {
        this.controllers.add(controller);
    }

    /**
     * 运行并刷新正在运行的行为
     */
    public void tickRunningBehaviors(EntityIntelligent entity) {
        Set<IBehavior> removed = new HashSet<>();
        for (var behavior : runningBehaviors) {
            if (!behavior.execute(entity)) {
                removed.add(behavior);
                behavior.onStop(entity);
            }
        }
        runningBehaviors.removeAll(removed);
    }

    public void tickRunningCoreBehaviors(EntityIntelligent entity) {
        Set<IBehavior> removed = new HashSet<>();
        for (var coreBehavior : runningCoreBehaviors) {
            if (!coreBehavior.execute(entity)) {
                removed.add(coreBehavior);
                coreBehavior.onStop(entity);
            }
        }
        runningCoreBehaviors.removeAll(removed);
    }

    public void collectSensorData(EntityIntelligent entity) {
        //刷新gt数
        sensorPeriodTimer.forEach((k,v) -> sensorPeriodTimer.put(k,++v));
        for (ISensor sensor : sensors) {
            //没到周期就不评估
            if (sensorPeriodTimer.get(sensor) < sensor.getPeriod()) continue;
            sensorPeriodTimer.put(sensor,0);
            sensor.sense(entity);
        }
    }

    public void evaluateCoreBehaviors(EntityIntelligent entity) {
        //刷新gt数
        coreBehaviorPeriodTimer.forEach((k,v) -> coreBehaviorPeriodTimer.put(k,++v));
        for (IBehavior coreBehavior : coreBehaviors) {
            //没到周期就不评估
            if (coreBehaviorPeriodTimer.get(coreBehavior) < coreBehavior.getPeriod()) continue;
            //若已经在运行了，就不需要评估了
            if (runningCoreBehaviors.contains(coreBehavior)) continue;
            coreBehaviorPeriodTimer.put(coreBehavior,0);
            if (coreBehavior.evaluate(entity)) {
                coreBehavior.onStart(entity);
                runningCoreBehaviors.add(coreBehavior);
            }
        }
    }

    /**
     * 评估所有行为
     *
     * @param entity 评估的实体对象
     */
    public void evaluateBehaviors(EntityIntelligent entity) {
        //刷新gt数
        behaviorPeriodTimer.forEach((k,v) -> behaviorPeriodTimer.put(k,++v));
        //存储评估成功的行为（未过滤优先级）
        var evalSucceed = new HashSet<IBehavior>();
        int highestPriority = Integer.MIN_VALUE;
        for (IBehavior behavior : behaviors) {
            //没到周期就不评估
            if (behaviorPeriodTimer.get(behavior) < behavior.getPeriod()) continue;
            //若已经在运行了，就不需要评估了
            if (runningBehaviors.contains(behavior)) continue;
            behaviorPeriodTimer.put(behavior,0);
            if (behavior.evaluate(entity)) {
                evalSucceed.add(behavior);
                if (behavior.getPriority() > highestPriority) {
                    highestPriority = behavior.getPriority();
                }
            }
        }
        //如果没有评估结果，则返回空
        if (evalSucceed.isEmpty()) return;
        //过滤掉低优先级的行为
        final var finalHighestPriority = highestPriority;
        evalSucceed.removeIf(entry -> entry.getPriority() != finalHighestPriority);
        if (evalSucceed.isEmpty()) return;
        //当前运行的行为的优先级（优先级必定都是一样的，所以说不需要比较得出）
        var currentHighestPriority = runningBehaviors.isEmpty() ? Integer.MIN_VALUE : runningBehaviors.iterator().next().getPriority();
        //如果result的优先级低于当前运行的行为，则不执行
        if (highestPriority < currentHighestPriority){
            //do nothing
        } else if (highestPriority > currentHighestPriority) {
            //如果result的优先级比当前运行的行为的优先级高，则替换当前运行的所有行为
            interruptAllRunningBehaviors(entity);
            runningBehaviors.addAll(evalSucceed);
        }
        //如果result的优先级和当前运行的行为的优先级一样，则添加result的行为
        else addToRunningBehaviors(entity, evalSucceed);
    }

    @Override
    public void applyController(EntityIntelligent entity) {
        for (IController controller : controllers) {
            controller.control(entity);
        }
    }

    /**
     * 计算活跃实体延迟
     * @param entity 实体
     * @param originalDelay 原始延迟
     * @return 如果实体是非活跃的，则延迟*2
     */
    protected int calcActiveDelay(@NotNull EntityIntelligent entity, int originalDelay) {
        if (!entity.isActive()) {
            return originalDelay << 1;
        }
        return originalDelay;
    }

    @Override
    public void updateRoute(EntityIntelligent entity) {
        currentRouteUpdateTick++;
        //到达更新周期时，开始重新计算新路径
        if (currentRouteUpdateTick >= calcActiveDelay(entity, ROUTE_UPDATE_CYCLE + (entity.level.tickRateOptDelay << 1)) || isForceUpdateRoute()) {
            Vector3 target = entity.getMoveTarget();
            //若有路径目标，则计算新路径
            if (target != null && (routeFindingTask == null || routeFindingTask.getFinished() || Server.getInstance().getNextTick() - routeFindingTask.getStartTime() > 8)) {
                    //clone防止寻路器潜在的修改
                    RouteFindingManager.getInstance().submit(routeFindingTask = new RouteFindingManager.RouteFindingTask(routeFinder, task -> {
                        updateMoveDirection(entity);
                        entity.setNeedUpdateMoveDirection(false);
                        currentRouteUpdateTick = 0;
                        setForceUpdateRoute(false);
                    })
                    .setStart(entity.clone())
                    .setTarget(target));
            } else {
                //没有路径目标，则清除路径信息
                entity.setMoveDirectionStart(null);
                entity.setMoveDirectionEnd(null);
            }
        }
        //若不能再移动了，则清除路径信息
        var reachableTarget = routeFinder.getReachableTarget();
        if (reachableTarget != null && entity.floor().equals(reachableTarget.floor())) {
            entity.setMoveTarget(null);
            entity.setMoveDirectionStart(null);
            entity.setMoveDirectionEnd(null);
        }
        if (entity.isNeedUpdateMoveDirection()) {
            if (routeFinder.hasNext()) {
                //若有新的移动方向，则更新
                updateMoveDirection(entity);
                entity.setNeedUpdateMoveDirection(false);
            }
        }
    }

    protected void initPeriodTimer(){
        coreBehaviors.forEach(coreBehavior -> coreBehaviorPeriodTimer.put(coreBehavior, 0));
        behaviors.forEach(behavior -> behaviorPeriodTimer.put(behavior, 0));
        sensors.forEach(sensor -> sensorPeriodTimer.put(sensor, 0));
    }

    protected void clearMemory(Class<? extends IMemory<?>> clazz) {
        memoryStorage.clear(clazz);
    }

    protected <T> void setMemoryData(Class<? extends IMemory<T>> clazz, T data) {
        memoryStorage.get(clazz).setData(data);
    }

    protected void updateMoveDirection(EntityIntelligent entity) {
        Vector3 end = entity.getMoveDirectionEnd();
        if (end == null){
            end = entity.clone();
        }
        var next = routeFinder.next();
        if (next != null) {
            entity.setMoveDirectionStart(end);
            entity.setMoveDirectionEnd(next.getVector3());
        }
    }

    /**
     * 添加评估成功后的行为到{@link BehaviorGroup#runningBehaviors}
     *
     * @param entity    评估的实体
     * @param behaviors 要添加的行为
     */
    protected void addToRunningBehaviors(EntityIntelligent entity, @NotNull Set<IBehavior> behaviors) {
        behaviors.forEach((behavior) -> {
            behavior.onStart(entity);
            runningBehaviors.add(behavior);
        });
    }

    /**
     * 中断所有正在运行的行为
     */
    protected void interruptAllRunningBehaviors(EntityIntelligent entity) {
        for (IBehavior behavior : runningBehaviors) {
            behavior.onInterrupt(entity);
        }
        runningBehaviors.clear();
    }

    /**
     * 获取指定Set<IBehavior>内的最高优先级
     *
     * @param behaviors 行为组
     * @return int 最高优先级
     */
    protected int getHighestPriority(@NotNull Set<IBehavior> behaviors) {
        int highestPriority = Integer.MIN_VALUE;
        for (IBehavior behavior : behaviors) {
            if (behavior.getPriority() > highestPriority) {
                highestPriority = behavior.getPriority();
            }
        }
        return highestPriority;
    }
}
