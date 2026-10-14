package org.powernukkitx.entity.ai.behavior;

import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.ai.evaluator.IBehaviorEvaluator;
import org.powernukkitx.entity.ai.executor.IBehaviorExecutor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

/**
 * A single behavior object, containing an executor and an evaluator, with the behavior object delegating their methods
 */


@Getter
public class Behavior extends AbstractBehavior {

    protected final int priority;
    protected final int weight;
    protected final int period;
    protected final IBehaviorExecutor executor;
    protected final IBehaviorEvaluator evaluator;
    protected final boolean reevaluate;

    public Behavior(IBehaviorExecutor executor, IBehaviorEvaluator evaluator) {
        this(executor, evaluator, 1);
    }

    public Behavior(IBehaviorExecutor executor, IBehaviorEvaluator evaluator, int priority) {
        this(executor, evaluator, priority, 1);
    }

    public Behavior(IBehaviorExecutor executor, IBehaviorEvaluator evaluator, int priority, int weight) {
        this(executor, evaluator, priority, weight, 1);
    }

    public Behavior(IBehaviorExecutor executor, IBehaviorEvaluator evaluator, int priority, int weight, int period) {
        this(executor, evaluator, priority, weight, period, true);
    }

    public Behavior(IBehaviorExecutor executor, IBehaviorEvaluator evaluator, int priority, int weight, int period, boolean reevaluate) {
        this.executor = executor;
        this.evaluator = evaluator;
        this.priority = priority;
        this.weight = weight;
        this.period = period;
        this.reevaluate = reevaluate;
    }

    /**
     * Starts building a behavior around the given executor. The behavior runs unconditionally
     * unless an evaluator is supplied through {@link Builder#when(IBehaviorEvaluator)}.
     */
    public static Builder builder(@NotNull IBehaviorExecutor executor) {
        return new Builder(executor);
    }

    @Override
    public boolean shouldReevaluate() {
        return reevaluate;
    }

    @Override
    public boolean evaluate(EntityIntelligent entity) {
        return evaluator.evaluate(entity);
    }

    @Override
    public boolean execute(EntityIntelligent entity) {
        return executor.execute(entity);
    }

    @Override
    public void onStart(EntityIntelligent entity) {
        executor.onStart(entity);
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        executor.onInterrupt(entity);
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        executor.onStop(entity);
    }

    @Override
    public String toString() {
        return "[" + priority + "] " + executor.getClass().getSimpleName() + " | " + evaluator.getClass().getSimpleName();
    }

    /**
     * Fluent builder for {@link Behavior}, an alternative to the telescoping constructors.
     */
    public static class Builder {
        private final IBehaviorExecutor executor;
        private IBehaviorEvaluator evaluator = entity -> true;
        private int priority = 1;
        private int weight = 1;
        private int period = 1;
        private boolean reevaluate = true;

        private Builder(@NotNull IBehaviorExecutor executor) {
            this.executor = executor;
        }

        public Builder when(@NotNull IBehaviorEvaluator evaluator) {
            this.evaluator = evaluator;
            return this;
        }

        public Builder priority(int priority) {
            this.priority = priority;
            return this;
        }

        public Builder weight(int weight) {
            this.weight = weight;
            return this;
        }

        public Builder period(int period) {
            this.period = period;
            return this;
        }

        /**
         * Marks the behavior as evaluated only once, right before it starts. It then keeps running
         * until its executor stops it, even if the evaluator would no longer pass.
         */
        public Builder evaluateOnce() {
            this.reevaluate = false;
            return this;
        }

        public Behavior build() {
            return new Behavior(executor, evaluator, priority, weight, period, reevaluate);
        }
    }
}
