package org.powernukkitx.entity.ai.behaviorgroup;

import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.ai.behavior.BehaviorState;
import org.powernukkitx.entity.ai.behavior.IBehavior;
import org.powernukkitx.entity.ai.controller.IController;
import org.powernukkitx.entity.ai.memory.IMemoryStorage;
import org.powernukkitx.entity.ai.memory.MemoryStorage;
import org.powernukkitx.entity.ai.route.RouteUpdater;
import org.powernukkitx.entity.ai.route.finder.IRouteFinder;
import org.powernukkitx.entity.ai.sensor.ISensor;
import lombok.AccessLevel;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.ToIntFunction;

/**
 * Standard behavior group implementation.
 * <p>
 * The group owns the scheduling of its behaviors and sensors; pathfinding is delegated to a
 * {@link RouteUpdater} and debug rendering to a {@link BehaviorDebugRenderer}.
 */

@Getter
public class BehaviorGroup implements IBehaviorGroup {

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
     * Memory storage
     */
    protected final IMemoryStorage memoryStorage;
    /**
     * The entity this behavior group belongs to
     */
    protected final EntityIntelligent entity;

    @Getter(AccessLevel.NONE)
    private final List<PeriodicTrigger<IBehavior>> coreBehaviorTriggers;
    @Getter(AccessLevel.NONE)
    private final List<PeriodicTrigger<IBehavior>> behaviorTriggers;
    @Getter(AccessLevel.NONE)
    private final List<PeriodicTrigger<ISensor>> sensorTriggers;
    @Getter(AccessLevel.NONE)
    private final RouteUpdater routeUpdater;
    @Getter(AccessLevel.NONE)
    private final BehaviorDebugRenderer debugRenderer = new BehaviorDebugRenderer(this);
    /**
     * Scratch space for {@link #evaluateBehaviors(EntityIntelligent)}, reused because that method runs
     * for every mob on every tick. A group is only ever evaluated by one thread at a time.
     */
    @Getter(AccessLevel.NONE)
    private final List<IBehavior> evaluationResult = new ArrayList<>();

    public BehaviorGroup(int startRouteUpdateTick,
                         Set<IBehavior> coreBehaviors,
                         Set<IBehavior> behaviors,
                         Set<ISensor> sensors,
                         Set<IController> controllers,
                         IRouteFinder routeFinder,
                         EntityIntelligent entity) {
        this.coreBehaviors = coreBehaviors;
        this.behaviors = behaviors;
        this.sensors = sensors;
        this.controllers = controllers;
        this.entity = entity;
        this.memoryStorage = new MemoryStorage(entity);
        this.routeUpdater = new RouteUpdater(routeFinder, startRouteUpdateTick);
        this.coreBehaviorTriggers = createTriggers(coreBehaviors, IBehavior::getPeriod);
        this.behaviorTriggers = createTriggers(behaviors, IBehavior::getPeriod);
        this.sensorTriggers = createTriggers(sensors, ISensor::getPeriod);
    }

    /**
     * Creates a new fluent builder bound to the given entity. The route update tick offset
     * defaults to the entity's tickSpread, so entities only need to declare their behaviors,
     * sensors, controllers and route finder.
     */
    public static Builder builder(@NotNull EntityIntelligent entity) {
        return new Builder(entity);
    }

    @Override
    public IRouteFinder getRouteFinder() {
        return routeUpdater.getRouteFinder();
    }

    /**
     * Runs and refreshes the currently running behaviors
     */
    @Override
    public void tickRunningBehaviors(EntityIntelligent entity) {
        if (tickRunning(entity, runningBehaviors)) {
            evaluateBehaviors(entity);
        }
    }

    @Override
    public void tickRunningCoreBehaviors(EntityIntelligent entity) {
        tickRunning(entity, runningCoreBehaviors);
    }

    @Override
    public void collectSensorData(EntityIntelligent entity) {
        for (int i = 0; i < sensorTriggers.size(); i++) {
            PeriodicTrigger<ISensor> trigger = sensorTriggers.get(i);
            if (trigger.isDue()) trigger.value().sense(entity);
        }
    }

    @Override
    public void evaluateCoreBehaviors(EntityIntelligent entity) {
        for (int i = 0; i < coreBehaviorTriggers.size(); i++) {
            PeriodicTrigger<IBehavior> trigger = coreBehaviorTriggers.get(i);
            IBehavior coreBehavior = trigger.value();
            //if it's already running, there's no need to evaluate it
            if (runningCoreBehaviors.contains(coreBehavior)) continue;
            if (!trigger.isDue() || !coreBehavior.evaluate(entity)) continue;
            start(entity, coreBehavior, runningCoreBehaviors);
        }
    }

    /**
     * Evaluates all behaviors, and hands the entity over to the highest-priority ones that pass
     *
     * @param entity the entity object being evaluated
     */
    @Override
    public void evaluateBehaviors(EntityIntelligent entity) {
        //stores the highest-priority behaviors that evaluated successfully
        var evalSucceed = evaluationResult;
        evalSucceed.clear();
        int highestPriority = Integer.MIN_VALUE;
        for (int i = 0; i < behaviorTriggers.size(); i++) {
            PeriodicTrigger<IBehavior> trigger = behaviorTriggers.get(i);
            IBehavior behavior = trigger.value();
            //if it's already running, there's no need to evaluate it
            if (runningBehaviors.contains(behavior)) continue;
            if (!trigger.isDue() || !behavior.evaluate(entity)) continue;
            if (behavior.getPriority() < highestPriority) continue;
            if (behavior.getPriority() > highestPriority) {
                evalSucceed.clear();
                highestPriority = behavior.getPriority();
            }
            evalSucceed.add(behavior);
        }
        //return if there are no evaluation results
        if (evalSucceed.isEmpty()) return;
        IBehavior running = runningBehaviors.isEmpty() ? null : runningBehaviors.iterator().next();
        int runningPriority = running != null ? running.getPriority() : Integer.MIN_VALUE;
        boolean runningStillValid = running == null || !running.shouldReevaluate() || running.evaluate(entity);
        //if the result's priority is lower than the still valid running behavior, keep running it
        if (highestPriority < runningPriority && runningStillValid) return;
        //if the result wins on priority, or the running behavior is no longer valid, replace all running behaviors
        if (highestPriority > runningPriority || !runningStillValid) {
            interruptAllRunningBehaviors(entity);
        }
        //otherwise the priorities are equal, so the results simply join the running behaviors
        for (int i = 0; i < evalSucceed.size(); i++) {
            start(entity, evalSucceed.get(i), runningBehaviors);
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
        routeUpdater.update(entity);
    }

    @Override
    public boolean isForceUpdateRoute() {
        return routeUpdater.isForceUpdate();
    }

    @Override
    public void setForceUpdateRoute(boolean forceUpdateRoute) {
        routeUpdater.setForceUpdate(forceUpdateRoute);
    }

    @Override
    public void debugTick(EntityIntelligent entity) {
        debugRenderer.render(entity);
    }

    /**
     * Executes the given running behaviors, and drops the ones that were interrupted or that finished
     *
     * @return whether at least one behavior stopped running
     */
    protected boolean tickRunning(EntityIntelligent entity, Set<IBehavior> running) {
        boolean stoppedBehavior = false;
        Iterator<IBehavior> iterator = running.iterator();
        while (iterator.hasNext()) {
            IBehavior behavior = iterator.next();
            if (behavior.shouldReevaluate() && !behavior.evaluate(entity)) {
                behavior.onInterrupt(entity);
            } else if (!behavior.execute(entity)) {
                behavior.onStop(entity);
            } else continue;
            behavior.setBehaviorState(BehaviorState.STOP);
            iterator.remove();
            stoppedBehavior = true;
        }
        return stoppedBehavior;
    }

    /**
     * Starts a behavior and adds it to the given set of running behaviors
     */
    protected void start(EntityIntelligent entity, IBehavior behavior, Set<IBehavior> running) {
        behavior.onStart(entity);
        behavior.setBehaviorState(BehaviorState.ACTIVE);
        running.add(behavior);
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

    private <T> List<PeriodicTrigger<T>> createTriggers(Collection<T> elements, ToIntFunction<T> period) {
        var triggers = new ArrayList<PeriodicTrigger<T>>(elements.size());
        for (T element : elements) {
            triggers.add(new PeriodicTrigger<>(element, () -> period.applyAsInt(element)));
        }
        return triggers;
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
}
