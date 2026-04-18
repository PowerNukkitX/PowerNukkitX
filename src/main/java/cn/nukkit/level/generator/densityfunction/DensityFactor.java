package cn.nukkit.level.generator.densityfunction;

import static cn.nukkit.level.generator.densityfunction.DensityCommon.p;
import static cn.nukkit.level.generator.densityfunction.DensityCommon.spline;

/**
 * @author Buddelbubi
 * @since 2026/04/02
 * @implNote <a href="https://github.com/misode/mcmeta/blob/data/data/minecraft/worldgen/density_function/overworld/factor.json">Source</a>
 */
public final class DensityFactor {

    private DensityFactor() {
    }

    public static DensityFunction overworldFactor(
            DensityFunction continents,
            DensityFunction erosion,
            DensityFunction ridges,
            DensityFunction ridgesFolded
    ) {
        return DensityCommon.flatCache(
                DensityCommon.cache2d(
                        DensityCommon.add(
                                DensityCommon.constant(10.0),
                                DensityCommon.mul(
                                        DensityCommon.blendAlpha(),
                                        DensityCommon.add(
                                                DensityCommon.constant(-10.0),
                                                factorSpline(continents, erosion, ridges, ridgesFolded)
                                        )
                                )
                        )
                )
        );
    }

    private static DensityFunction factorSpline(
            DensityFunction continents,
            DensityFunction erosion,
            DensityFunction ridges,
            DensityFunction ridgesFolded
    ) {
        DensityFunction ridgesA = spline(ridges,
                p(-0.2, 6.3, 0.0),
                p(0.2, 6.25, 0.0)
        );
        DensityFunction ridgesB = spline(ridges,
                p(-0.05, 6.3, 0.0),
                p(0.05, 2.67, 0.0)
        );
        DensityFunction ridgesC = spline(ridges,
                p(-0.05, 2.67, 0.0),
                p(0.05, 6.3, 0.0)
        );
        DensityFunction ridgesD = spline(ridges,
                p(-0.2, 6.3, 0.0),
                p(0.2, 5.47, 0.0)
        );
        DensityFunction ridgesE = spline(ridges,
                p(-0.2, 6.3, 0.0),
                p(0.2, 5.08, 0.0)
        );
        DensityFunction ridgesF = spline(ridges,
                p(-0.2, 6.3, 0.0),
                p(0.2, 4.69, 0.0)
        );
        DensityFunction ridgesG = spline(ridges,
                p(0.0, 6.25, 0.0),
                p(0.1, 0.625, 0.0)
        );
        DensityFunction ridgesH = spline(ridges,
                p(0.0, 5.47, 0.0),
                p(0.1, 0.625, 0.0)
        );
        DensityFunction ridgesI = spline(ridges,
                p(0.0, 5.08, 0.0),
                p(0.1, 0.625, 0.0)
        );

        DensityFunction ridgesFoldedA = spline(ridgesFolded,
                p(-0.9, 6.25, 0.0),
                p(-0.69, ridgesG, 0.0)
        );
        DensityFunction ridgesFoldedB = spline(ridgesFolded,
                p(-0.9, 5.47, 0.0),
                p(-0.69, ridgesH, 0.0)
        );
        DensityFunction ridgesFoldedC = spline(ridgesFolded,
                p(-0.9, 5.08, 0.0),
                p(-0.69, ridgesI, 0.0)
        );
        DensityFunction ridgesFoldedD = spline(ridgesFolded,
                p(0.45, ridgesF, 0.0),
                p(0.7, 1.56, 0.0)
        );
        DensityFunction ridgesFoldedE = spline(ridgesFolded,
                p(-0.7, ridgesF, 0.0),
                p(-0.15, 1.37, 0.0)
        );

        DensityFunction erosion1 = spline(erosion,
                p(-0.6, ridgesA, 0.0),
                p(-0.5, ridgesB, 0.0),
                p(-0.35, ridgesA, 0.0),
                p(-0.25, ridgesA, 0.0),
                p(-0.1, ridgesC, 0.0),
                p(0.03, ridgesA, 0.0),
                p(0.35, 6.25, 0.0),
                p(0.45, ridgesFoldedA, 0.0),
                p(0.55, ridgesFoldedA, 0.0),
                p(0.62, 6.25, 0.0)
        );
        DensityFunction erosion2 = spline(erosion,
                p(-0.6, ridgesD, 0.0),
                p(-0.5, ridgesB, 0.0),
                p(-0.35, ridgesD, 0.0),
                p(-0.25, ridgesD, 0.0),
                p(-0.1, ridgesC, 0.0),
                p(0.03, ridgesD, 0.0),
                p(0.35, 5.47, 0.0),
                p(0.45, ridgesFoldedB, 0.0),
                p(0.55, ridgesFoldedB, 0.0),
                p(0.62, 5.47, 0.0)
        );
        DensityFunction erosion3 = spline(erosion,
                p(-0.6, ridgesE, 0.0),
                p(-0.5, ridgesB, 0.0),
                p(-0.35, ridgesE, 0.0),
                p(-0.25, ridgesE, 0.0),
                p(-0.1, ridgesC, 0.0),
                p(0.03, ridgesE, 0.0),
                p(0.35, 5.08, 0.0),
                p(0.45, ridgesFoldedC, 0.0),
                p(0.55, ridgesFoldedC, 0.0),
                p(0.62, 5.08, 0.0)
        );
        DensityFunction erosion4 = spline(erosion,
                p(-0.6, ridgesF, 0.0),
                p(-0.5, ridgesB, 0.0),
                p(-0.35, ridgesF, 0.0),
                p(-0.25, ridgesF, 0.0),
                p(-0.1, ridgesC, 0.0),
                p(0.03, ridgesF, 0.0),
                p(0.05, ridgesFoldedD, 0.0),
                p(0.4, ridgesFoldedD, 0.0),
                p(0.45, ridgesFoldedE, 0.0),
                p(0.55, ridgesFoldedE, 0.0),
                p(0.58, 4.69, 0.0)
        );

        return spline(continents,
                p(-0.19, 3.95, 0.0),
                p(-0.15, erosion1, 0.0),
                p(-0.1, erosion2, 0.0),
                p(0.03, erosion3, 0.0),
                p(0.06, erosion4, 0.0)
        );
    }

}
