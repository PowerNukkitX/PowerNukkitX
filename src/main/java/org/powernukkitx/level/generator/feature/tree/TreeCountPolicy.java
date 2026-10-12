package org.powernukkitx.level.generator.feature.tree;

import com.google.common.base.Preconditions;
import org.powernukkitx.utils.random.RandomSourceProvider;

/**
 * Selects the number of tree placement attempts for one tree rule.
 *
 * @author Curse
 */
@FunctionalInterface
public interface TreeCountPolicy {

    /**
     * Samples the number of tree attempts.
     */
    int sample(RandomSourceProvider random);

    /**
     * Returns a fixed attempt count.
     */
    static TreeCountPolicy fixed(int count) {
        Preconditions.checkArgument(count >= 0, "count must be >= 0");
        return random -> count;
    }

    /**
     * Creates the vanilla legacy tree-count policy for an amount scalar.
     */
    static TreeCountPolicy legacy(float amount) {
        Preconditions.checkArgument(Float.isFinite(amount) && amount >= 0, "amount must be finite and >= 0");

        return random -> {
            if (random.nextExclusiveInt(10) == 0) {
                return (int) (amount * random.nextFloat());
            }

            if (amount < 1.0f) {
                return amount > random.nextFloat() ? 1 : 0;
            }

            int base = (int) amount;
            return base + Math.max(random.nextExclusiveInt(10), 7) - 7;
        };
    }
}
