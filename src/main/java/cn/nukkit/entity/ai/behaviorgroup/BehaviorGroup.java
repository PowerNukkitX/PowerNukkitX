package cn.nukkit.entity.ai.behaviorgroup;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.behavior.BehaviorState;
import cn.nukkit.entity.ai.behavior.IBehavior;
import cn.nukkit.entity.ai.controller.IController;
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
    protected final Set<IBehavior> coreBehaviors;

    /**
     * 全部行为
     */
    protected final Set<IBehavior> behaviors;
    /**
     * 传感器
     */
    protected final Set<ISensor> sensors;
    /**
     * 控制器
     */
    protected final Set<IController> controllers;
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
    protected final Map<IBehavior, Integer> coreBehaviorPeriodTimer = new HashMap<>();
    /**
     * 用于存储行为距离上次评估逝去的gt数
     */
    protected final Map<IBehavior, Integer> behaviorPeriodTimer = new HashMap<>();
    /**
     * 用于存储传感器距离上次刷新逝去的gt数
     */
    protected final Map<ISensor, Integer> sensorPeriodTimer = new HashMap<>();
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
    protected int currentRouteUpdateTick;//gt

    protected boolean forceUpdateRoute = false;


    public BehaviorGroup(int startRouteUpdateTick, Set<IBehavior> coreBehaviors, Set<IBehavior> behaviors, Set<ISensor> sensors, Set<IController> controllers, SimpleRouteFinder routeFinder) {
        //此参数用于错开各个实体路径更新的时间，避免在1gt内提交过多路径更新任务
        this.currentRouteUpdateTick = startRouteUpdateTick;
        this.coreBehaviors = coreBehaviors;
        this.behaviors = behaviors;
        this.sensors = sensors;
        this.controllers = controllers;
        this.routeFinder = routeFinder;
        this.initPeriodTimer();
    }

    /**
     * 运行并刷新正在运行的行为
     */
    public void tickRunningBehaviors(EntityIntelligent entity) {
        var iterator = runningBehaviors.iterator();
        while (iterator.hasNext()) {
            IBehavior behavior = iterator.next();
            if (!behavior.execute(entity)) {
                behavior.onStop(entity);
                behavior.setBehaviorState(BehaviorState.STOP);
                iterator.remove();
            }
        }
    }

    public void tickRunningCoreBehaviors(EntityIntelligent entity) {
        var iterator = runningCoreBehaviors.iterator();
        while (iterator.hasNext()) {
            IBehavior coreBehavior = iterator.next();
            if (!coreBehavior.execute(entity)) {
                coreBehavior.onStop(entity);
                coreBehavior.setBehaviorState(BehaviorState.STOP);
                iterator.remove();
            }
        }
    }

    public void collectSensorData(EntityIntelligent entity) {
        sensorPeriodTimer.forEach((sensor, tick) -> {
            //刷新gt数
            sensorPeriodTimer.put(sensor, ++tick);
            //没到周期就不评估
            if (sensorPeriodTimer.get(sensor) < sensor.getPeriod()) return;
            sensorPeriodTimer.put(sensor, 0);
            sensor.sense(entity);
        });
    }

    public void evaluateCoreBehaviors(EntityIntelligent entity) {
        coreBehaviorPeriodTimer.forEach((coreBehavior, tick) -> {
            //若已经在运行了，就不需要评估了
            if (runningCoreBehaviors.contains(coreBehavior)) return;
            int nextTick = ++tick;
            //刷新gt数
            coreBehaviorPeriodTimer.put(coreBehavior, nextTick);
            //没到周期就不评估
            if (nextTick < coreBehavior.getPeriod()) return;
            coreBehaviorPeriodTimer.put(coreBehavior, 0);
            if (coreBehavior.evaluate(entity)) {
                coreBehavior.onStart(entity);
                coreBehavior.setBehaviorState(BehaviorState.ACTIVE);
                runningCoreBehaviors.add(coreBehavior);
            }
        });
    }

    /**
     * 评估所有行为
     *
     * @param entity 评估的实体对象
     */
    public void evaluateBehaviors(EntityIntelligent entity) {
        //存储评估成功的行为（未过滤优先级）
        var evalSucceed = new HashSet<IBehavior>(behaviors.size());
        int highestPriority = Integer.MIN_VALUE;
        for (Map.Entry<IBehavior, Integer> entry : behaviorPeriodTimer.entrySet()) {
            IBehavior behavior = entry.getKey();
            //若已经在运行了，就不需要评估了
            if (runningBehaviors.contains(behavior)) continue;
            int tick = entry.getValue();
            int nextTick = ++tick;
            //刷新gt数
            behaviorPeriodTimer.put(behavior, nextTick);
            //没到周期就不评估
            if (nextTick < behavior.getPeriod()) continue;
            behaviorPeriodTimer.put(behavior, 0);
            if (behavior.evaluate(entity)) {
                if (behavior.getPriority() > highestPriority) {
                    evalSucceed.clear();
                    highestPriority = behavior.getPriority();
                } else if (behavior.getPriority() < highestPriority) {
                    continue;
                }
                evalSucceed.add(behavior);
            }
        }
        //如果没有评估结果，则返回空
        if (evalSucceed.isEmpty()) return;
        var first = runningBehaviors.isEmpty() ? null : runningBehaviors.iterator().next();
        var runningBehaviorPriority = first != null ? first.getPriority() : Integer.MIN_VALUE;
        //如果result的优先级低于当前运行的行为，则不执行
        if (highestPriority < runningBehaviorPriority) {
            //do nothing
        } else if (highestPriority > runningBehaviorPriority) {
            //如果result的优先级比当前运行的行为的优先级高，则替换当前运行的所有行为
            interruptAllRunningBehaviors(entity);
            addToRunningBehaviors(entity, evalSucceed);
        } else {
            //如果result的优先级和当前运行的行为的优先级一样，则添加result的行为
            addToRunningBehaviors(entity, evalSucceed);
        }
    }

    @Override
    public void applyController(EntityIntelligent entity) {
        for (IController controller : controllers) {
            controller.control(entity);
        }
    }

    @Override
    public void updateRoute(EntityIntelligent entity) {
        currentRouteUpdateTick++;
        Vector3 target = entity.getMoveTarget();
        if (target == null) {
            //没有路径目标，则清除路径信息
            entity.setMoveDirectionStart(null);
            entity.setMoveDirectionEnd(null);
            return;
        }
        //到达更新周期时，开始重新计算新路径
        if (currentRouteUpdateTick >= calcActiveDelay(entity, ROUTE_UPDATE_CYCLE + (entity.level.tickRateOptDelay << 1)) || isForceUpdateRoute()) {
            //若有路径目标，则计算新路径
            if ((routeFindingTask == null || routeFindingTask.getFinished() || Server.getInstance().getNextTick() - routeFindingTask.getStartTime() > 8)) {
                //clone防止寻路器潜在的修改
                RouteFindingManager.getInstance().submit(routeFindingTask = new RouteFindingManager.RouteFindingTask(routeFinder, task -> {
                    updateMoveDirection(entity);
                    entity.setNeedUpdateMoveDirection(false);
                    currentRouteUpdateTick = 0;
                    setForceUpdateRoute(false);
                })
                        .setStart(entity.clone())
                        .setTarget(target));
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

    /**
     * 计算活跃实体延迟
     *
     * @param entity        实体
     * @param originalDelay 原始延迟
     * @return 如果实体是非活跃的，则延迟*4，否则返回原始延迟
     */
    protected int calcActiveDelay(@NotNull EntityIntelligent entity, int originalDelay) {
        if (!entity.isActive()) {
            return originalDelay << 2;
        }
        return originalDelay;
    }

    protected void initPeriodTimer() {
        coreBehaviors.forEach(coreBehavior -> coreBehaviorPeriodTimer.put(coreBehavior, 0));
        behaviors.forEach(behavior -> behaviorPeriodTimer.put(behavior, 0));
        sensors.forEach(sensor -> sensorPeriodTimer.put(sensor, 0));
    }

//    protected void clearMemory(Class<? extends IMemory<?>> clazz) {
//        memoryStorage.clear(clazz);
//    }
//
//    protected <T> void setMemoryData(Class<? extends IMemory<T>> clazz, T data) {
//        memoryStorage.get(clazz).setData(data);
//    }

    protected void updateMoveDirection(EntityIntelligent entity) {
        Vector3 end = entity.getMoveDirectionEnd();
        if (end == null) {
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
            behavior.setBehaviorState(BehaviorState.ACTIVE);
            runningBehaviors.add(behavior);
        });
    }

    /**
     * 中断所有正在运行的行为
     */
    protected void interruptAllRunningBehaviors(EntityIntelligent entity) {
        for (IBehavior behavior : runningBehaviors) {
            behavior.onInterrupt(entity);
            behavior.setBehaviorState(BehaviorState.STOP);
        }
        runningBehaviors.clear();
    }

//    /**
//     * 获取指定Set<IBehavior>内的最高优先级
//     *
//     * @param behaviors 行为组
//     * @return int 最高优先级
//     */
//    protected int getHighestPriority(@NotNull Set<IBehavior> behaviors) {
//        int highestPriority = Integer.MIN_VALUE;
//        for (IBehavior behavior : behaviors) {
//            if (behavior.getPriority() > highestPriority) {
//                highestPriority = behavior.getPriority();
//            }
//        }
//        return highestPriority;
//    }
}
