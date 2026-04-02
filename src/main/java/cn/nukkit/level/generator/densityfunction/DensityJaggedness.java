package cn.nukkit.level.generator.densityfunction;

import static cn.nukkit.level.generator.densityfunction.DensityFunctions.p;
import static cn.nukkit.level.generator.densityfunction.DensityFunctions.spline;

public final class DensityJaggedness {

    private DensityJaggedness() {
    }

    public static DensityFunction overworldJaggedness(
            DensityFunction continents,
            DensityFunction erosion,
            DensityFunction ridges,
            DensityFunction ridgesFolded
    ) {
        return DensityFunctions.flatCache(
                DensityFunctions.cache2d(
                        DensityFunctions.add(
                                DensityFunctions.constant(0.0),
                                DensityFunctions.mul(
                                        DensityFunctions.blendAlpha(),
                                        DensityFunctions.add(
                                                DensityFunctions.constant(-0.0),
                                                jaggednessSpline(continents, erosion, ridges, ridgesFolded)
                                        )
                                )
                        )
                )
        );
    }

    private static DensityFunction jaggednessSpline(
            DensityFunction continents,
            DensityFunction erosion,
            DensityFunction ridges,
            DensityFunction ridgesFolded
    ) {
        DensityFunction ridges1 = spline(ridges,
                p(-0.01, 0.63, 0.0),
                p(0.01, 0.3, 0.0)
        );
        DensityFunction ridges2 = spline(ridges,
                p(-0.01, 0.315, 0.0),
                p(0.01, 0.15, 0.0)
        );
        DensityFunction ridges3 = spline(ridges,
                p(-0.01, 0.315, 0.0),
                p(0.01, 0.15, 0.0)
        );
        DensityFunction ridges4 = spline(ridges,
                p(-0.01, 0.63, 0.0),
                p(0.01, 0.3, 0.0)
        );
        DensityFunction ridges5 = spline(ridges,
                p(-0.01, 0.63, 0.0),
                p(0.01, 0.3, 0.0)
        );
        DensityFunction ridges6 = spline(ridges,
                p(-0.01, 0.63, 0.0),
                p(0.01, 0.3, 0.0)
        );

        DensityFunction ridgesFolded1 = spline(ridgesFolded,
                p(0.19999999, 0.0, 0.0),
                p(0.44999996, 0.0, 0.0),
                p(1.0, ridges1, 0.0)
        );
        DensityFunction ridgesFolded2 = spline(ridgesFolded,
                p(0.19999999, 0.0, 0.0),
                p(0.44999996, 0.0, 0.0),
                p(1.0, ridges2, 0.0)
        );
        DensityFunction ridgesFolded3 = spline(ridgesFolded,
                p(0.19999999, 0.0, 0.0),
                p(0.44999996, 0.0, 0.0),
                p(1.0, ridges3, 0.0)
        );
        DensityFunction ridgesFolded4 = spline(ridgesFolded,
                p(0.19999999, 0.0, 0.0),
                p(0.44999996, ridges4, 0.0),
                p(1.0, ridges5, 0.0)
        );
        DensityFunction ridgesFolded5 = spline(ridgesFolded,
                p(0.19999999, 0.0, 0.0),
                p(0.44999996, 0.0, 0.0),
                p(1.0, ridges6, 0.0)
        );

        DensityFunction erosion1 = spline(erosion,
                p(-1.0, ridgesFolded1, 0.0),
                p(-0.78, ridgesFolded2, 0.0),
                p(-0.5775, ridgesFolded3, 0.0),
                p(-0.375, 0.0, 0.0)
        );
        DensityFunction erosion2 = spline(erosion,
                p(-1.0, ridgesFolded4, 0.0),
                p(-0.78, ridgesFolded5, 0.0),
                p(-0.5775, ridgesFolded5, 0.0),
                p(-0.375, 0.0, 0.0)
        );

        return spline(continents,
                p(-0.11, 0.0, 0.0),
                p(0.03, erosion1, 0.0),
                p(0.65, erosion2, 0.0)
        );
    }

}
