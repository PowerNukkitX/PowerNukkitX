package cn.nukkit.level.generator.densityfunction;

public final class DensityDepth {

    private static final int FROM_Y = -64;
    private static final int TO_Y = 320;
    private static final double FROM_VALUE = 1.5;
    private static final double TO_VALUE = -1.5;

    private DensityDepth() {
    }

    public static DensityFunction overworldDepth(
            DensityFunction continents,
            DensityFunction erosion,
            DensityFunction ridgesFolded
    ) {
        return overworldDepth(DensityOffset.overworldOffset(continents, erosion, ridgesFolded));
    }

    public static DensityFunction overworldDepth(DensityFunction offset) {
        return DensityFunctions.add(
                DensityFunctions.yClampedGradient(FROM_Y, TO_Y, FROM_VALUE, TO_VALUE),
                offset
        );
    }
}
