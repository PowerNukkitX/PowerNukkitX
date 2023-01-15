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
import cn.nukkit.entity.ai.route.data.Node;
import cn.nukkit.entity.ai.route.finder.SimpleRouteFinder;
import cn.nukkit.entity.ai.sensor.ISensor;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.generic.BaseChunk;
import cn.nukkit.math.Vector3;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 标准行为组实现
 */
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

    protected long blockChangeCache;

    /**
     * 记录距离上次路径更新过去的gt数
     */
    protected int currentRouteUpdateTick;//gt

    protected boolean forceUpdateRoute = false;

    @Builder
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
    @Override
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

    @Override
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

    @Override
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

    @Override
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
    @Override
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
        boolean reachUpdateCycle = currentRouteUpdateTick >= calcActiveDelay(entity, ROUTE_UPDATE_CYCLE + (entity.level.tickRateOptDelay << 1));
        if (reachUpdateCycle) currentRouteUpdateTick = 0;
        Vector3 target = entity.getMoveTarget();
        if (target == null) {
            //没有路径目标，则清除路径信息
            entity.setMoveDirectionStart(null);
            entity.setMoveDirectionEnd(null);
            return;
        }
        //到达更新周期时，开始重新计算新路径
        if (isForceUpdateRoute() || (reachUpdateCycle && shouldUpdateRoute(entity))) {
            //若有路径目标，则计算新路径
            boolean reSubmit = false;
            //         第一次计算                       上一次计算已完成                                         超时，重新提交任务
            if (routeFindingTask == null || routeFindingTask.getFinished() || (reSubmit = (!routeFindingTask.getStarted() && Server.getInstance().getNextTick() - routeFindingTask.getStartTime() > 8))) {
                if (reSubmit) routeFindingTask.cancel(true);
                //clone防止寻路器潜在的修改
                RouteFindingManager.getInstance().submit(routeFindingTask = new RouteFindingManager.RouteFindingTask(routeFinder, task -> {
                    updateMoveDirection(entity);
                    entity.setShouldUpdateMoveDirection(false);
                    setForceUpdateRoute(false);
                    //写入section变更记录
                    cacheSectionBlockChange(entity.level, calPassByChunkSections(this.routeFinder.getRoute().stream().map(Node::getVector3).toList(), entity.level));
                }).setStart(entity.clone()).setTarget(target));
            }
        }
        if (routeFindingTask != null && routeFindingTask.getFinished() && !hasNewUnCalMoveTarget(entity)) {
            //若不能再移动了，且没有正在计算的寻路任务，则清除路径信息
            var reachableTarget = routeFinder.getReachableTarget();
            if (reachableTarget != null && entity.floor().equals(reachableTarget.floor())) {
                entity.setMoveTarget(null);
                entity.setMoveDirectionStart(null);
                entity.setMoveDirectionEnd(null);
                return;
            }
        }
        if (entity.isShouldUpdateMoveDirection()) {
            if (routeFinder.hasNext()) {
                //若有新的移动方向，则更新
                updateMoveDirection(entity);
                entity.setShouldUpdateMoveDirection(false);
            }
        }
    }

    /**
     * 检查路径是否需要更新。此方法检测路径经过的ChunkSection是否发生了变化
     *
     * @return 是否需要更新路径
     */
    @Since("1.19.50-r4")
    protected boolean shouldUpdateRoute(EntityIntelligent entity) {
        //此优化只针对处于非active区块的实体
        if (entity.isActive()) return true;
        //终点发生变化或第一次计算，需要重算
        if (this.routeFinder.getTarget() == null || hasNewUnCalMoveTarget(entity))
            return true;
        Set<ChunkSectionVector> passByChunkSections = calPassByChunkSections(this.routeFinder.getRoute().stream().map(Node::getVector3).toList(), entity.level);
        long total = passByChunkSections.stream().mapToLong(vector3 -> getSectionBlockChange(entity.level, vector3)).sum();
        //Section发生变化，需要重算
        return blockChangeCache != total;
    }

    /**
     * 通过比对寻路器中设置的moveTarget与entity的moveTarget来确认实体是否设置了新的未计算的moveTarget
     *
     * @param entity 实体
     * @return 是否存在新的未计算的寻路目标
     */
    protected boolean hasNewUnCalMoveTarget(EntityIntelligent entity) {
        return !entity.getMoveTarget().equals(this.routeFinder.getTarget());
    }

    /**
     * 缓存section的blockChanges到blockChangeCache
     */
    @Since("1.19.50-r4")
    protected void cacheSectionBlockChange(Level level, Set<ChunkSectionVector> vecs) {
        this.blockChangeCache = vecs.stream().mapToLong(vector3 -> getSectionBlockChange(level, vector3)).sum();
    }

    /**
     * 返回sectionVector对应的section的blockChanges
     */
    @Since("1.19.50-r4")
    protected long getSectionBlockChange(Level level, ChunkSectionVector vector) {
        var chunk = level.getChunk(vector.chunkX, vector.chunkZ);
        //TODO: 此处强转未经检查，可能在未来导致兼容性问题
        return ((BaseChunk) chunk).getSectionBlockChanges(vector.sectionY);
    }

    /**
     * 计算坐标集经过的ChunkSection
     *
     * @return (chunkX | chunkSectionY | chunkZ)
     */
    @Since("1.19.50-r4")
    protected Set<ChunkSectionVector> calPassByChunkSections(Collection<Vector3> nodes, Level level) {
        return nodes.stream().map(vector3 -> new ChunkSectionVector(vector3.getChunkX(), ((int) vector3.y - level.getMinHeight()) >> 4, vector3.getChunkZ())).collect(Collectors.toSet());
    }

    @Override
    public void debugTick(EntityIntelligent entity) {
        var sortedBehaviors = new ArrayList<>(behaviors);
        sortedBehaviors.sort(Comparator.comparing(IBehavior::getPriority, Integer::compareTo));
        Collections.reverse(sortedBehaviors);

        var strBuilder = new StringBuilder();
        for (var behavior : sortedBehaviors) {
            strBuilder.append(behavior.getBehaviorState() == BehaviorState.ACTIVE ? "§b" : "§7");
            strBuilder.append(behavior);
            strBuilder.append("\n");
        }

        entity.setNameTag(strBuilder.toString());
        entity.setNameTagAlwaysVisible();
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

    /**
     * 描述一个ChunkSection的位置
     *
     * @param chunkX
     * @param sectionY
     * @param chunkZ
     */
    protected record ChunkSectionVector(int chunkX, int sectionY, int chunkZ) {
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ChunkSectionVector other)) {
                return false;
            }

            return this.chunkX == other.chunkX && this.sectionY == other.sectionY && this.chunkZ == other.chunkZ;
        }

        @Override
        public int hashCode() {
            return (chunkX ^ (chunkZ << 12)) ^ (sectionY << 24);
        }
    }
}
