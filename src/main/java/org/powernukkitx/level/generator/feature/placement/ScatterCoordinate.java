package org.powernukkitx.level.generator.feature.placement;

import com.google.common.base.Preconditions;

/**
 * Evaluates one relative feature-scatter coordinate.
 *
 * @author Curse
 */
@FunctionalInterface
public interface ScatterCoordinate {

    /**
     * Samples one relative coordinate offset.
     */
    int sample(FeaturePlacementContext context);

    /**
     * Returns a constant coordinate offset.
     */
    static ScatterCoordinate fixed(int offset) {
        return context -> offset;
    }

    /**
     * Creates the Bedrock Gaussian distribution for the supplied inclusive extents.
     */
    static ScatterCoordinate gaussian(int min, int max) {
        Preconditions.checkArgument(min <= max, "min must be <= max");

        int width = max - min;
        int half = width / 2;
        int center = min + half;
        if (width + 1 < 3) {
            return fixed(center);
        }

        return context -> center
                + context.getRandom().nextExclusiveInt(half)
                - context.getRandom().nextExclusiveInt(half);
    }

    /**
     * Creates the Bedrock Triangle distribution for the supplied inclusive extents.
     */
    static ScatterCoordinate triangle(int min, int max) {
        Preconditions.checkArgument(min <= max, "min must be <= max");

        int width = max - min;
        int left = width / 2;
        int right = width - left;

        return context -> min
                + context.getRandom().nextExclusiveInt(left + 1)
                + context.getRandom().nextExclusiveInt(right + 1);
    }
}
