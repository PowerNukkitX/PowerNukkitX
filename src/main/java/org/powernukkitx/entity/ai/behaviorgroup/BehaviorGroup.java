package org.powernukkitx.entity.ai.behaviorgroup;

import org.powernukkitx.Server;
import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.ai.EntityAI;
import org.powernukkitx.entity.ai.behavior.Behavior;
import org.powernukkitx.entity.ai.behavior.BehaviorState;
import org.powernukkitx.entity.ai.behavior.IBehavior;
import org.powernukkitx.entity.ai.controller.IController;
import org.powernukkitx.entity.ai.memory.IMemoryStorage;
import org.powernukkitx.entity.ai.memory.MemoryStorage;
import org.powernukkitx.entity.ai.route.RouteFindingManager;
import org.powernukkitx.entity.ai.route.data.Node;
import org.powernukkitx.entity.ai.route.finder.IRouteFinder;
import org.powernukkitx.entity.ai.sensor.ISensor;
import org.powernukkitx.level.DimensionData;
import org.powernukkitx.level.Level;
import org.powernukkitx.math.Vector3;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Standard behavior group implementation
 */

@Getter
@Setter
public class BehaviorGroup implements IBehaviorGroup {

    /**
     * Determines how many gt between each path update
     */
    protected static int ROUTE_UPDATE_CYCLE = 16;//gt


    /**
     * "Core" behaviors that will not be overridden by other behaviors
     */
    protected final Set<IBehavior> coreBehaviors;

    /**
     * All behaviors
     */
    protected final Set<IBehavior> behaviors;
    /**
     * Sensors
     */
    protected final Set<ISensor> sensors;
    /**
     * Controllers
     */
    protected final Set<IController> controllers;
    /**
     * The "core" behaviors currently running
     */
    protected final Set<IBehavior> runningCoreBehaviors = new HashSet<>();
    /**
     * The behaviors currently running
     */
    protected final Set<IBehavior> runningBehaviors = new HashSet<>();
    /**
     * Stores the number of gt elapsed since each core behavior was last evaluated
     */
    protected final Map<IBehavior, Integer> coreBehaviorPeriodTimer = new HashMap<>();
    /**
     * Stores the number of gt elapsed since each behavior was last evaluated
     */
    protected final Map<IBehavior, Integer> behaviorPeriodTimer = new HashMap<>();
    /**
     * Stores the number of gt elapsed since each sensor was last refreshed
     */
    protected final Map<ISensor, Integer> sensorPeriodTimer = new HashMap<>();
    /**
     * Memory storage
     */
    protected final IMemoryStorage memoryStorage;
    /**
     * Pathfinder (not asynchronous, because it isn't necessary - the mob AI is already parallelized)
     */
    protected final IRouteFinder routeFinder;
    /**
     * The entity this behavior group belongs to
     */
    protected final EntityIntelligent entity;
    /**
     * Pathfinding task
     */
    protected RouteFindingManager.RouteFindingTask routeFindingTask;

    protected long blockChangeCache;

    /**
     * Records the number of gt elapsed since the last path update
     */
    protected int currentRouteUpdateTick;//gt

    protected boolean forceUpdateRoute = false;

    public BehaviorGroup(int startRouteUpdateTick,
                         Set<IBehavior> coreBehaviors,
                         Set<IBehavior> behaviors,
                         Set<ISensor> sensors,
                         Set<IController> controllers,
                         IRouteFinder routeFinder,
                         EntityIntelligent entity) {
        //this parameter staggers the path update timing of each entity, to avoid submitting too many path update tasks within a single gt
        this.currentRouteUpdateTick = startRouteUpdateTick;
        this.coreBehaviors = coreBehaviors;
        this.behaviors = behaviors;
        this.sensors = sensors;
        this.controllers = controllers;
        this.routeFinder = routeFinder;
        this.entity = entity;
        this.memoryStorage = new MemoryStorage(entity);
        this.initPeriodTimer();
    }

    /**
     * Creates a new fluent builder bound to the given entity. The route update tick offset
     * defaults to the entity's tickSpread, so entities only need to declare their behaviors,
     * sensors, controllers and route finder.
     */
    public static Builder builder(@NotNull EntityIntelligent entity) {
        return new Builder(entity);
    }

    /**
     * Fluent builder for {@link BehaviorGroup}. Each group is bound to a single entity, since
     * behaviors, sensors and route finders capture the entity instance - so unlike blocks there
     * is no shared definition registry, every entity builds its own group.
     */
    public static class Builder {
        private final EntityIntelligent entity;
        private int startRouteUpdateTick;
        private Set<IBehavior> coreBehaviors = Set.of();
        private Set<IBehavior> behaviors = Set.of();
        private Set<ISensor> sensors = Set.of();
        private Set<IController> controllers = Set.of();
        private IRouteFinder routeFinder;

        private Builder(@NotNull EntityIntelligent entity) {
            this.entity = entity;
            this.startRouteUpdateTick = entity.tickSpread;
        }

        public Builder startRouteUpdateTick(int startRouteUpdateTick) {
            this.startRouteUpdateTick = startRouteUpdateTick;
            return this;
        }

        public Builder coreBehaviors(IBehavior... coreBehaviors) {
            this.coreBehaviors = Set.of(coreBehaviors);
            return this;
        }

        public Builder coreBehaviors(Collection<IBehavior> coreBehaviors) {
            this.coreBehaviors = Set.copyOf(coreBehaviors);
            return this;
        }

        public Builder behaviors(IBehavior... behaviors) {
            this.behaviors = Set.of(behaviors);
            return this;
        }

        public Builder behaviors(Collection<IBehavior> behaviors) {
            this.behaviors = Set.copyOf(behaviors);
            return this;
        }

        public Builder sensors(ISensor... sensors) {
            this.sensors = Set.of(sensors);
            return this;
        }

        public Builder sensors(Collection<ISensor> sensors) {
            this.sensors = Set.copyOf(sensors);
            return this;
        }

        public Builder controllers(IController... controllers) {
            this.controllers = Set.of(controllers);
            return this;
        }

        public Builder controllers(Collection<IController> controllers) {
            this.controllers = Set.copyOf(controllers);
            return this;
        }

        public Builder routeFinder(IRouteFinder routeFinder) {
            this.routeFinder = routeFinder;
            return this;
        }

        public BehaviorGroup build() {
            return new BehaviorGroup(startRouteUpdateTick, coreBehaviors, behaviors, sensors, controllers, routeFinder, entity);
        }
    }

    /**
     * Runs and refreshes the currently running behaviors
     */
    @Override
    public void tickRunningBehaviors(EntityIntelligent entity) {
        boolean stoppedBehavior = false;
        var iterator = runningBehaviors.iterator();
        while (iterator.hasNext()) {
            IBehavior behavior = iterator.next();
            if (behavior instanceof Behavior normalBehavior) {
                if (normalBehavior.isReevaluate() && !normalBehavior.evaluate(entity)) {
                    behavior.onInterrupt(entity);
                    behavior.setBehaviorState(BehaviorState.STOP);
                    iterator.remove();
                    stoppedBehavior = true;
                    continue;
                }
            }
            if (!behavior.execute(entity)) {
                behavior.onStop(entity);
                behavior.setBehaviorState(BehaviorState.STOP);
                iterator.remove();
                stoppedBehavior = true;
            }
        }
        if (stoppedBehavior) {
            evaluateBehaviors(entity);
        }
    }

    @Override
    public void tickRunningCoreBehaviors(EntityIntelligent entity) {
        var iterator = runningCoreBehaviors.iterator();
        while (iterator.hasNext()) {
            IBehavior coreBehavior = iterator.next();
            if(coreBehavior instanceof Behavior behavior) {
                if(behavior.isReevaluate() && !behavior.evaluate(entity)) {
                    coreBehavior.onInterrupt(entity);
                    coreBehavior.setBehaviorState(BehaviorState.STOP);
                    iterator.remove();
                    continue;
                }
            }
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
            //refresh the gt count
            sensorPeriodTimer.put(sensor, ++tick);
            //don't evaluate until the period is reached
            if (sensorPeriodTimer.get(sensor) < sensor.getPeriod()) return;
            sensorPeriodTimer.put(sensor, 0);
            sensor.sense(entity);
        });
    }

    @Override
    public void evaluateCoreBehaviors(EntityIntelligent entity) {
        coreBehaviorPeriodTimer.forEach((coreBehavior, tick) -> {
            //if it's already running, there's no need to evaluate it
            if (runningCoreBehaviors.contains(coreBehavior)) return;
            int nextTick = ++tick;
            //refresh the gt count
            coreBehaviorPeriodTimer.put(coreBehavior, nextTick);
            //don't evaluate until the period is reached
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
     * Evaluates all behaviors
     *
     * @param entity the entity object being evaluated
     */
    @Override
    public void evaluateBehaviors(EntityIntelligent entity) {
        //stores the behaviors that evaluated successfully (priority not yet filtered)
        var evalSucceed = new HashSet<IBehavior>(behaviors.size());
        int highestPriority = Integer.MIN_VALUE;
        for (Map.Entry<IBehavior, Integer> entry : behaviorPeriodTimer.entrySet()) {
            IBehavior behavior = entry.getKey();
            //if it's already running, there's no need to evaluate it
            if (runningBehaviors.contains(behavior)) continue;
            int tick = entry.getValue();
            int nextTick = ++tick;
            //refresh the gt count
            behaviorPeriodTimer.put(behavior, nextTick);
            //don't evaluate until the period is reached
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
        //return if there are no evaluation results
        if (evalSucceed.isEmpty()) return;
        IBehavior first = runningBehaviors.isEmpty() ? null : runningBehaviors.iterator().next();
        int runningBehaviorPriority = first != null ? first.getPriority() : Integer.MIN_VALUE;
        boolean firstEval = true;
        if(first != null) {
            if(first instanceof Behavior behavior) {
                if(behavior.isReevaluate()) {
                    firstEval = first.evaluate(entity);
                }
            }
        }
        //if the result's priority is lower than the currently running behavior, do nothing
        if (highestPriority < runningBehaviorPriority && firstEval) {
            //do nothing
        } else if (highestPriority > runningBehaviorPriority || !firstEval) {
            //if the result's priority is higher than the currently running behavior, replace all currently running behaviors
            interruptAllRunningBehaviors(entity);
            addToRunningBehaviors(entity, evalSucceed);
        } else {
            //if the result's priority equals the currently running behavior, add the result's behaviors
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
            //no path target, so clear the path information
            entity.setMoveDirectionStart(null);
            entity.setMoveDirectionEnd(null);
            return;
        }
        //when the update cycle is reached, start recalculating the new path
        if (isForceUpdateRoute() || (reachUpdateCycle && shouldUpdateRoute(entity))) {
            //if there is a path target, calculate the new path
            boolean reSubmit = false;
            //         first calculation                 previous calculation finished                          timed out, resubmit the task
            if (routeFindingTask == null || routeFindingTask.getFinished() || (reSubmit = (!routeFindingTask.getStarted() && Server.getInstance().getNextTick() - routeFindingTask.getStartTime() > 8))) {
                if (reSubmit) routeFindingTask.cancel(true);
                //clone to prevent potential modification by the pathfinder
                RouteFindingManager.getInstance().submit(routeFindingTask = new RouteFindingManager.RouteFindingTask(routeFinder, task -> {
                    updateMoveDirection(entity);
                    entity.setShouldUpdateMoveDirection(false);
                    setForceUpdateRoute(false);
                    //write the section change record
                    cacheSectionBlockChange(entity.level, calPassByChunkSections(this.routeFinder.getRoute().stream().map(Node::getVector3).toList(), entity.level));
                }).setStart(entity.clone()).setTarget(target));
            }
        }
        if (routeFindingTask != null && routeFindingTask.getFinished() && !hasNewUnCalMoveTarget(entity)) {
            //if it can no longer move and there is no pathfinding task in progress, clear the path information
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
                //if there is a new movement direction, update it
                updateMoveDirection(entity);
                entity.setShouldUpdateMoveDirection(false);
            }
        }
    }

    /**
     * Checks whether the path needs to be updated. This method detects whether the ChunkSections the path passes through have changed
     *
     * @return whether the path needs to be updated
     */
    protected boolean shouldUpdateRoute(EntityIntelligent entity) {
        //this optimization only applies to entities in non-active chunks
        if (entity.isActive()) return true;
        //the endpoint changed or it's the first calculation, so recalculation is needed
        if (this.routeFinder.getTarget() == null || hasNewUnCalMoveTarget(entity))
            return true;
        Set<ChunkSectionVector> passByChunkSections = calPassByChunkSections(this.routeFinder.getRoute().stream().map(Node::getVector3).toList(), entity.level);
        long total = passByChunkSections.stream().mapToLong(vector3 -> getSectionBlockChange(entity.level, vector3)).sum();
        //a Section changed, so recalculation is needed
        return blockChangeCache != total;
    }

    /**
     * Confirms whether the entity has set a new, uncalculated moveTarget by comparing the moveTarget set in the pathfinder with the entity's moveTarget
     *
     * @param entity the entity
     * @return whether a new, uncalculated pathfinding target exists
     */
    protected boolean hasNewUnCalMoveTarget(EntityIntelligent entity) {
        return !entity.getMoveTarget().equals(this.routeFinder.getTarget());
    }

    /**
     * Caches the section's blockChanges into blockChangeCache
     */

    protected void cacheSectionBlockChange(Level level, Set<ChunkSectionVector> vecs) {
        this.blockChangeCache = vecs.stream().mapToLong(vector3 -> getSectionBlockChange(level, vector3)).sum();
    }

    /**
     * Returns the blockChanges of the section corresponding to the sectionVector
     */
    protected long getSectionBlockChange(Level level, ChunkSectionVector vector) {
        var chunk = level.getChunk(vector.chunkX, vector.chunkZ);
        return chunk.getSectionBlockChanges(vector.sectionY);
    }

    /**
     * Calculates the ChunkSections that the set of coordinates passes through
     *
     * @return (chunkX | chunkSectionY | chunkZ)
     */
    protected Set<ChunkSectionVector> calPassByChunkSections(Collection<Vector3> nodes, Level level) {
        return nodes.stream()
                .map(vector3 -> {
                    final DimensionData dimensionData = level.getDimensionData();
                    final int chunkX = vector3.getChunkX();
                    final int y = Math.min(dimensionData.getMaxHeight(), Math.max(dimensionData.getMinHeight(), vector3.getFloorY() - dimensionData.getMinHeight()));
                    final int chunkZ = vector3.getChunkZ();
                    return new ChunkSectionVector(chunkX, y >> 4, chunkZ);
                })
                .collect(Collectors.toSet());
    }

    @Override
    public void debugTick(EntityIntelligent entity) {

        var strBuilder = new StringBuilder();

        if(EntityAI.checkDebugOption(EntityAI.DebugOption.MEMORY)) {
            var sortedMemory = new ArrayList<>(getMemoryStorage().getAll().entrySet());
            sortedMemory.sort(Comparator.comparing(s -> s.getKey().getIdentifier().getPath(), String::compareTo));
            Collections.reverse(sortedMemory);

            for (var memory : sortedMemory) {
                strBuilder.append("§e" + memory.getKey().getIdentifier().getPath());
                strBuilder.append("=");
                strBuilder.append("§7" + memory.getValue().toString());
                strBuilder.append("\n");
            }
            strBuilder.append("\n\n");
        }

        if(EntityAI.checkDebugOption(EntityAI.DebugOption.BEHAVIOR)) {
            if(!coreBehaviors.isEmpty()) {
                var sortedCoreBehaviors = new ArrayList<>(coreBehaviors);
                sortedCoreBehaviors.sort(Comparator.comparing(IBehavior::getPriority, Integer::compareTo));
                Collections.reverse(sortedCoreBehaviors);

                for (var behavior : sortedCoreBehaviors) {
                    strBuilder.append(behavior.getBehaviorState() == BehaviorState.ACTIVE ? "§b" : "§7");
                    strBuilder.append(behavior);
                    strBuilder.append("\n");
                }
                strBuilder.append("\n\n");
            }

            var sortedBehaviors = new ArrayList<>(behaviors);
            sortedBehaviors.sort(Comparator.comparing(IBehavior::getPriority, Integer::compareTo));
            Collections.reverse(sortedBehaviors);

            for (var behavior : sortedBehaviors) {
                strBuilder.append(behavior.getBehaviorState() == BehaviorState.ACTIVE ? "§b" : "§7");
                strBuilder.append(behavior);
                strBuilder.append("\n");
            }
        }

        entity.setNameTag(strBuilder.toString());
        entity.setNameTagAlwaysVisible(true);
    }

    /**
     * Calculates the active entity delay
     *
     * @param entity        the entity
     * @param originalDelay the original delay
     * @return if the entity is inactive, the delay is multiplied by 4, otherwise the original delay is returned
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
     * Adds the successfully evaluated behaviors to {@link BehaviorGroup#runningBehaviors}
     *
     * @param entity    the entity being evaluated
     * @param behaviors the behaviors to add
     */
    protected void addToRunningBehaviors(EntityIntelligent entity, @NotNull Set<IBehavior> behaviors) {
        behaviors.forEach((behavior) -> {
            behavior.onStart(entity);
            behavior.setBehaviorState(BehaviorState.ACTIVE);
            runningBehaviors.add(behavior);
        });
    }

    /**
     * Interrupts all currently running behaviors
     */
    protected void interruptAllRunningBehaviors(EntityIntelligent entity) {
        for (IBehavior behavior : runningBehaviors) {
            behavior.onInterrupt(entity);
            behavior.setBehaviorState(BehaviorState.STOP);
        }
        runningBehaviors.clear();
    }

    /**
     * Describes the position of a ChunkSection
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
