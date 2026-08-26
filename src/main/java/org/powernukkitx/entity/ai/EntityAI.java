package org.powernukkitx.entity.ai;

import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.ai.behavior.Behavior;
import org.powernukkitx.entity.ai.behavior.IBehavior;
import org.powernukkitx.entity.ai.behaviorgroup.BehaviorGroup;
import org.powernukkitx.entity.ai.behaviorgroup.IBehaviorGroup;
import org.powernukkitx.entity.ai.controller.IController;
import org.powernukkitx.entity.ai.evaluator.IBehaviorEvaluator;
import org.powernukkitx.entity.ai.executor.IBehaviorExecutor;
import org.powernukkitx.entity.ai.route.finder.IRouteFinder;
import org.powernukkitx.entity.ai.sensor.ISensor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Entry point for declaring the AI of an entity, and holder of the global parameters of the AI framework.
 * <p>
 * Behaviors are declared from the most important to the least important one, and their priority follows
 * that order - there are no priority numbers to keep in sync when a behavior is added or removed. Behaviors
 * that are allowed to run together share a priority through {@link BehaviorSpec#alongsidePrevious()}.
 * <pre>{@code
 * return EntityAI.of(this)
 *         .sensors(new NearestPlayerSensor(16, 0, 20))
 *         .controllers(new WalkController(), new LookController(true, true))
 *         .routeFinder(new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this))
 *         .behavior(new MeleeAttackExecutor(CoreMemoryTypes.ATTACK_TARGET, 0.3f, 40, true, 30))
 *                 .when(new EntityCheckEvaluator(CoreMemoryTypes.ATTACK_TARGET))
 *         .behavior(new FlatRandomRoamExecutor(0.1f, 12, 100))
 *         .build();
 * }</pre>
 */
public final class EntityAI {

    private static final Set<DebugOption> debugOptions = EnumSet.noneOf(DebugOption.class);

    private static long routeParticleSpawnInterval = 500;//ms

    private final EntityIntelligent entity;
    private final List<PrioritizedBehavior> behaviors = new ArrayList<>();
    private final List<Behavior.Builder> coreBehaviors = new ArrayList<>();
    private final Set<ISensor> sensors = new LinkedHashSet<>();
    private final Set<IController> controllers = new LinkedHashSet<>();
    private IRouteFinder routeFinder;

    private EntityAI(@NotNull EntityIntelligent entity) {
        this.entity = entity;
    }

    /**
     * Starts declaring the AI of the given entity.
     */
    public static EntityAI of(@NotNull EntityIntelligent entity) {
        return new EntityAI(entity);
    }

    public static void setDebugOption(DebugOption option, boolean open) {
        if (open) debugOptions.add(option);
        else debugOptions.remove(option);
    }

    public static boolean hasDebugOptions() {
        return !debugOptions.isEmpty();
    }

    public static boolean checkDebugOption(DebugOption option) {
        return debugOptions.contains(option);
    }

    /**
     * Sets route particle spawn interval.(Unit millisecond)
     *
     * @param routeParticleSpawnInterval the route particle spawn interval
     */
    public static void setRouteParticleSpawnInterval(long routeParticleSpawnInterval) {
        EntityAI.routeParticleSpawnInterval = routeParticleSpawnInterval;
    }

    /**
     * Gets route particle spawn interval.
     *
     * @return the route particle spawn interval
     */
    public static long getRouteParticleSpawnInterval() {
        return routeParticleSpawnInterval;
    }

    public EntityAI sensors(@NotNull ISensor... sensors) {
        this.sensors.addAll(List.of(sensors));
        return this;
    }

    public EntityAI sensors(@NotNull Collection<ISensor> sensors) {
        this.sensors.addAll(sensors);
        return this;
    }

    public EntityAI controllers(@NotNull IController... controllers) {
        this.controllers.addAll(List.of(controllers));
        return this;
    }

    public EntityAI controllers(@NotNull Collection<IController> controllers) {
        this.controllers.addAll(controllers);
        return this;
    }

    public EntityAI routeFinder(IRouteFinder routeFinder) {
        this.routeFinder = routeFinder;
        return this;
    }

    /**
     * Declares a behavior. Every behavior declared before this one outranks it, and every behavior declared
     * after it is outranked by it.
     *
     * @param executor the executor to run while the behavior is active
     * @return the declared behavior, which can be refined further and keeps the declaration chain going
     */
    public BehaviorSpec behavior(@NotNull IBehaviorExecutor executor) {
        var prioritized = new PrioritizedBehavior(Behavior.builder(executor));
        behaviors.add(prioritized);
        return new BehaviorSpec(prioritized.builder, prioritized);
    }

    /**
     * Declares a core behavior. Core behaviors ignore priorities entirely - each of them runs as soon as its
     * own evaluator passes, regardless of what the entity is doing.
     *
     * @param executor the executor to run while the behavior is active
     * @return the declared behavior, which can be refined further and keeps the declaration chain going
     */
    public BehaviorSpec coreBehavior(@NotNull IBehaviorExecutor executor) {
        var builder = Behavior.builder(executor);
        coreBehaviors.add(builder);
        return new BehaviorSpec(builder, null);
    }

    public IBehaviorGroup build() {
        return BehaviorGroup.builder(entity)
                .behaviors(buildBehaviors())
                .coreBehaviors(buildCoreBehaviors())
                .sensors(sensors)
                .controllers(controllers)
                .routeFinder(routeFinder)
                .build();
    }

    /**
     * Turns the declaration order into priorities - the first declared behavior gets the highest one, and
     * behaviors marked as running alongside their predecessor share its priority.
     */
    private Collection<IBehavior> buildBehaviors() {
        int priority = countPriorityLevels();
        var built = new ArrayList<IBehavior>(behaviors.size());
        for (int i = 0; i < behaviors.size(); i++) {
            PrioritizedBehavior behavior = behaviors.get(i);
            if (i > 0 && !behavior.alongsidePrevious) priority--;
            built.add(behavior.builder.priority(priority).build());
        }
        return built;
    }

    private int countPriorityLevels() {
        int levels = 0;
        for (int i = 0; i < behaviors.size(); i++) {
            if (i == 0 || !behaviors.get(i).alongsidePrevious) levels++;
        }
        return levels;
    }

    private Collection<IBehavior> buildCoreBehaviors() {
        var built = new ArrayList<IBehavior>(coreBehaviors.size());
        coreBehaviors.forEach(builder -> built.add(builder.build()));
        return built;
    }

    public enum DebugOption {
        /**
         * Show route waypoints.
         */
        ROUTE,
        /**
         * Show the behavior state in the mob's name tag.
         */
        BEHAVIOR,
        /**
         * Allow right-clicking a mob with a stick to query its memory state.
         */
        MEMORY
    }

    private static final class PrioritizedBehavior {
        private final Behavior.Builder builder;
        private boolean alongsidePrevious;

        private PrioritizedBehavior(Behavior.Builder builder) {
            this.builder = builder;
        }
    }

    /**
     * A behavior being declared. Refines the behavior that was just declared, and delegates back to the
     * owning {@link EntityAI} so that the whole AI can be written as a single chain.
     */
    public final class BehaviorSpec {

        private final Behavior.Builder builder;
        private final PrioritizedBehavior prioritized;

        private BehaviorSpec(Behavior.Builder builder, PrioritizedBehavior prioritized) {
            this.builder = builder;
            this.prioritized = prioritized;
        }

        /**
         * Restricts the behavior to the entities and situations matched by the given evaluator. A behavior
         * without an evaluator always applies.
         */
        public BehaviorSpec when(@NotNull IBehaviorEvaluator evaluator) {
            builder.when(evaluator);
            return this;
        }

        /**
         * Sets how likely this behavior is to be picked among the behaviors sharing its priority.
         */
        public BehaviorSpec weight(int weight) {
            builder.weight(weight);
            return this;
        }

        /**
         * Sets how many gt pass between two evaluations of this behavior. A larger period means the behavior
         * reacts more slowly, but costs less.
         */
        public BehaviorSpec period(int period) {
            builder.period(period);
            return this;
        }

        /**
         * Evaluates the behavior only once, right before it starts. It then keeps running until its executor
         * stops it, even if the evaluator would no longer pass.
         */
        public BehaviorSpec evaluateOnce() {
            builder.evaluateOnce();
            return this;
        }

        /**
         * Lets this behavior share the priority of the behavior declared before it, so that both may run at
         * the same time.
         */
        public BehaviorSpec alongsidePrevious() {
            if (prioritized == null) {
                throw new IllegalStateException("Core behaviors do not have priorities");
            }
            if (behaviors.indexOf(prioritized) == 0) {
                throw new IllegalStateException("The first declared behavior has no previous behavior");
            }
            prioritized.alongsidePrevious = true;
            return this;
        }

        public BehaviorSpec behavior(@NotNull IBehaviorExecutor executor) {
            return EntityAI.this.behavior(executor);
        }

        public BehaviorSpec coreBehavior(@NotNull IBehaviorExecutor executor) {
            return EntityAI.this.coreBehavior(executor);
        }

        public EntityAI sensors(@NotNull ISensor... sensors) {
            return EntityAI.this.sensors(sensors);
        }

        public EntityAI controllers(@NotNull IController... controllers) {
            return EntityAI.this.controllers(controllers);
        }

        public EntityAI routeFinder(IRouteFinder routeFinder) {
            return EntityAI.this.routeFinder(routeFinder);
        }

        public IBehaviorGroup build() {
            return EntityAI.this.build();
        }
    }
}
