package org.powernukkitx.entity.ai.behaviorgroup;

import java.util.function.IntSupplier;

/**
 * Counts the gt elapsed since the wrapped element was last triggered, and reports when its period is reached.
 * Used to schedule behavior evaluations and sensor refreshes without keeping a boxed tick counter per element.
 */
final class PeriodicTrigger<T> {

    private final T value;
    private final IntSupplier period;
    private int elapsedTicks;

    PeriodicTrigger(T value, IntSupplier period) {
        this.value = value;
        this.period = period;
    }

    T value() {
        return value;
    }

    /**
     * Advances the counter by one gt.
     *
     * @return whether the period has been reached, which also resets the counter
     */
    boolean isDue() {
        if (++elapsedTicks < period.getAsInt()) {
            return false;
        }
        elapsedTicks = 0;
        return true;
    }
}
