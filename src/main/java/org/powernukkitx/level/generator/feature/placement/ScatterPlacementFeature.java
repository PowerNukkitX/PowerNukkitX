package org.powernukkitx.level.generator.feature.placement;

import com.google.common.base.Preconditions;

/**
 * Repeats a child feature at locally scattered positions.
 *
 * @author Curse
 */
public final class ScatterPlacementFeature implements PlacementFeature {

    private final int iterations;
    private final ScatterEvaluationOrder evaluationOrder;
    private final ScatterCoordinate x;
    private final ScatterCoordinate y;
    private final ScatterCoordinate z;
    private final PlacementFeature child;

    /**
     * Creates a scatter feature.
     */
    public ScatterPlacementFeature(
            int iterations,
            ScatterEvaluationOrder evaluationOrder,
            ScatterCoordinate x,
            ScatterCoordinate y,
            ScatterCoordinate z,
            PlacementFeature child
    ) {
        Preconditions.checkArgument(iterations >= 0, "iterations must be >= 0");
        this.iterations = iterations;
        this.evaluationOrder = Preconditions.checkNotNull(evaluationOrder, "evaluationOrder");
        this.x = Preconditions.checkNotNull(x, "x");
        this.y = Preconditions.checkNotNull(y, "y");
        this.z = Preconditions.checkNotNull(z, "z");
        this.child = Preconditions.checkNotNull(child, "child");
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

        for (int i = 0; i < this.iterations; i++) {
            context.setPosition(inputX, inputY, inputZ);
            this.scatter(context, inputX, inputY, inputZ);

            if (this.child.place(context)) {
                anySuccess = true;
                outputX = context.getX();
                outputY = context.getY();
                outputZ = context.getZ();
            }
        }

        context.setPosition(anySuccess ? outputX : inputX, anySuccess ? outputY : inputY, anySuccess ? outputZ : inputZ);
        return anySuccess;
    }

    private void scatter(FeaturePlacementContext context, int inputX, int inputY, int inputZ) {
        switch (this.evaluationOrder) {
            case XYZ -> {
                this.sampleX(context, inputX);
                this.sampleY(context, inputY);
                this.sampleZ(context, inputZ);
            }
            case XZY -> {
                this.sampleX(context, inputX);
                this.sampleZ(context, inputZ);
                this.sampleY(context, inputY);
            }
            case YXZ -> {
                this.sampleY(context, inputY);
                this.sampleX(context, inputX);
                this.sampleZ(context, inputZ);
            }
            case YZX -> {
                this.sampleY(context, inputY);
                this.sampleZ(context, inputZ);
                this.sampleX(context, inputX);
            }
            case ZXY -> {
                this.sampleZ(context, inputZ);
                this.sampleX(context, inputX);
                this.sampleY(context, inputY);
            }
            case ZYX -> {
                this.sampleZ(context, inputZ);
                this.sampleY(context, inputY);
                this.sampleX(context, inputX);
            }
        }
    }

    private void sampleX(FeaturePlacementContext context, int inputX) {
        int offset = this.x.sample(context);
        context.setPosition(inputX + offset, context.getY(), context.getZ());
    }

    private void sampleY(FeaturePlacementContext context, int inputY) {
        int offset = this.y.sample(context);
        context.setPosition(context.getX(), inputY + offset, context.getZ());
    }

    private void sampleZ(FeaturePlacementContext context, int inputZ) {
        int offset = this.z.sample(context);
        context.setPosition(context.getX(), context.getY(), inputZ + offset);
    }
}
