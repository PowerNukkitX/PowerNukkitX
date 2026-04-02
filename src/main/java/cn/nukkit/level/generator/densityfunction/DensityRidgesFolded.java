package cn.nukkit.level.generator.densityfunction;

public final class DensityRidgesFolded {

    private DensityRidgesFolded() {
    }

    public static DensityFunction overworldRidgesFolded(DensityFunction ridges) {
        return DensityFunctions.mul(
                DensityFunctions.constant(-3.0),
                DensityFunctions.add(
                        DensityFunctions.constant(-0.3333333333333333),
                        DensityFunctions.add(
                                DensityFunctions.constant(-0.6666666666666666),
                                ridges.abs()
                        ).abs()
                )
        );
    }
}
