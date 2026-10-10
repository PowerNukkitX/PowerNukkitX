package org.powernukkitx.level.generator.feature.placement;

import com.google.common.base.Preconditions;

import java.util.List;

/**
 * Places children from the same input position with aggregate early-out behavior.
 *
 * @author Curse
 */
public final class AggregatePlacementFeature implements PlacementFeature {

    /**
     * Aggregate child evaluation policy.
     */
    public enum EarlyOut {
        NONE,
        FIRST_FAILURE,
        FIRST_SUCCESS
    }

    private final List<PlacementFeature> features;
    private final EarlyOut earlyOut;

    /**
     * Creates an aggregate feature.
     */
    public AggregatePlacementFeature(List<PlacementFeature> features, EarlyOut earlyOut) {
        Preconditions.checkNotNull(features, "features");
        Preconditions.checkArgument(features.size() > 0, "features cannot be empty");

        this.features = List.copyOf(features);
        this.earlyOut = Preconditions.checkNotNull(earlyOut, "earlyOut");
    }

    @Override
    public boolean place(FeaturePlacementContext context) {
        int inputX = context.getX();
        int inputY = context.getY();
        int inputZ = context.getZ();

        boolean anySuccess = false;
        int outputX = inputX;
        int outputY = inputY;
        int outputZ = inputZ;

        for (PlacementFeature feature : this.features) {
            context.setPosition(inputX, inputY, inputZ);

            boolean success = feature.place(context);
            if (success) {
                anySuccess = true;
                outputX = context.getX();
                outputY = context.getY();
                outputZ = context.getZ();

                if (this.earlyOut == EarlyOut.FIRST_SUCCESS) {
                    return true;
                }
            } else if (this.earlyOut == EarlyOut.FIRST_FAILURE) {
                if (anySuccess) {
                    context.setPosition(outputX, outputY, outputZ);
                } else {
                    context.setPosition(inputX, inputY, inputZ);
                }
                return anySuccess;
            }
        }

        if (anySuccess) {
            context.setPosition(outputX, outputY, outputZ);
        } else {
            context.setPosition(inputX, inputY, inputZ);
        }
        return anySuccess;
    }
}
