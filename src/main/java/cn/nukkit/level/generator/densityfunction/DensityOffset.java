package cn.nukkit.level.generator.densityfunction;

import static cn.nukkit.level.generator.densityfunction.DensityCommon.p;
import static cn.nukkit.level.generator.densityfunction.DensityCommon.spline;

/**
 * @author Buddelbubi
 * @since 2026/04/02
 * @implNote <a href="https://github.com/misode/mcmeta/blob/data/data/minecraft/worldgen/density_function/overworld/offset.json">Source</a>
 */
public final class DensityOffset {

    private static final double BASE_OFFSET = -0.5037500262260437;

    private DensityOffset() {
    }

    public static DensityFunction overworldOffset(
            DensityFunction continents,
            DensityFunction erosion,
            DensityFunction ridgesFolded
    ) {
        DensityFunction blendAlpha = DensityCommon.cacheOnce(DensityCommon.blendAlpha());
        DensityFunction blendTerm = DensityCommon.mul(
                DensityCommon.blendOffset(),
                DensityCommon.add(
                        DensityCommon.constant(1.0),
                        DensityCommon.mul(DensityCommon.constant(-1.0), blendAlpha)
                )
        );

        DensityFunction offsetTerm = DensityCommon.mul(
                DensityCommon.add(DensityCommon.constant(BASE_OFFSET), offsetSpline(continents, erosion, ridgesFolded)),
                blendAlpha
        );

        return DensityCommon.flatCache(
                DensityCommon.cache2d(
                        DensityCommon.add(blendTerm, offsetTerm)
                )
        );
    }

    private static DensityFunction offsetSpline(DensityFunction continents, DensityFunction erosion, DensityFunction ridgesFolded) {
        DensityFunction ridgesFolded1 = DensityCommon.spline(ridgesFolded,
                DensityCommon.p(-1.0, -0.08880186, 0.38940096),
                DensityCommon.p(1.0, 0.69000006, 0.38940096)
        );
        DensityFunction ridgesFolded2 = DensityCommon.spline(ridgesFolded,
                DensityCommon.p(-1.0, -0.115760356, 0.37788022),
                DensityCommon.p(1.0, 0.6400001, 0.37788022)
        );
        DensityFunction ridgesFolded3 = DensityCommon.spline(ridgesFolded,
                DensityCommon.p(-1.0, -0.2222, 0.0),
                DensityCommon.p(-0.75, -0.2222, 0.0),
                DensityCommon.p(-0.65, 0.0, 0.0),
                DensityCommon.p(0.5954547, 2.9802322E-8, 0.0),
                DensityCommon.p(0.6054547, 2.9802322E-8, 0.2534563),
                DensityCommon.p(1.0, 0.100000024, 0.2534563)
        );
        DensityFunction ridgesFolded4 = DensityCommon.spline(ridgesFolded,
                DensityCommon.p(-1.0, -0.3, 0.5),
                DensityCommon.p(-0.4, 0.05, 0.0),
                DensityCommon.p(0.0, 0.05, 0.0),
                DensityCommon.p(0.4, 0.05, 0.0),
                DensityCommon.p(1.0, 0.060000002, 0.007000001)
        );
        DensityFunction ridgesFolded5 = DensityCommon.spline(ridgesFolded,
                DensityCommon.p(-1.0, -0.15, 0.5),
                DensityCommon.p(-0.4, 0.0, 0.0),
                DensityCommon.p(0.0, 0.0, 0.0),
                DensityCommon.p(0.4, 0.05, 0.1),
                DensityCommon.p(1.0, 0.060000002, 0.007000001)
        );
        DensityFunction ridgesFolded6 = DensityCommon.spline(ridgesFolded,
                DensityCommon.p(-1.0, -0.15, 0.5),
                DensityCommon.p(-0.4, 0.0, 0.0),
                DensityCommon.p(0.0, 0.0, 0.0),
                DensityCommon.p(0.4, 0.0, 0.0),
                DensityCommon.p(1.0, 0.0, 0.0)
        );
        DensityFunction ridgesFolded7 = DensityCommon.spline(ridgesFolded,
                DensityCommon.p(-1.0, -0.02, 0.0),
                DensityCommon.p(-0.4, -0.03, 0.0),
                DensityCommon.p(0.0, -0.03, 0.0),
                DensityCommon.p(0.4, 0.0, 0.06),
                DensityCommon.p(1.0, 0.0, 0.0)
        );
        DensityFunction ridgesFolded8 = spline(ridgesFolded,
                p(-1.0, -0.25, 0.5),
                p(-0.4, 0.05, 0.0),
                p(0.0, 0.05, 0.0),
                p(0.4, 0.05, 0.0),
                p(1.0, 0.060000002, 0.007000001)
        );
        DensityFunction ridgesFolded9 = spline(ridgesFolded,
                p(-1.0, -0.1, 0.5),
                p(-0.4, 0.001, 0.01),
                p(0.0, 0.003, 0.01),
                p(0.4, 0.05, 0.094000004),
                p(1.0, 0.060000002, 0.007000001)
        );
        DensityFunction ridgesFolded10 = spline(ridgesFolded,
                p(-1.0, -0.1, 0.5),
                p(-0.4, 0.01, 0.0),
                p(0.0, 0.01, 0.0),
                p(0.4, 0.03, 0.04),
                p(1.0, 0.1, 0.049)
        );
        DensityFunction ridgesFolded11 = spline(ridgesFolded,
                p(-1.0, -0.02, 0.015),
                p(-0.4, 0.01, 0.0),
                p(0.0, 0.01, 0.0),
                p(0.4, 0.03, 0.04),
                p(1.0, 0.1, 0.049)
        );
        DensityFunction ridgesFolded12 = spline(ridgesFolded,
                p(-1.0, 0.20235021, 0.0),
                p(0.0, 0.7161751, 0.5138249),
                p(1.0, 1.23, 0.5138249)
        );
        DensityFunction ridgesFolded13 = spline(ridgesFolded,
                p(-1.0, 0.2, 0.0),
                p(0.0, 0.44682026, 0.43317974),
                p(1.0, 0.88, 0.43317974)
        );
        DensityFunction ridgesFolded14 = spline(ridgesFolded,
                p(-1.0, 0.2, 0.0),
                p(0.0, 0.30829495, 0.3917051),
                p(1.0, 0.70000005, 0.3917051)
        );
        DensityFunction ridgesFolded15 = spline(ridgesFolded,
                p(-1.0, -0.25, 0.5),
                p(-0.4, 0.35, 0.0),
                p(0.0, 0.35, 0.0),
                p(0.4, 0.35, 0.0),
                p(1.0, 0.42000002, 0.049000014)
        );
        DensityFunction ridgesFolded16 = spline(ridgesFolded,
                p(-1.0, -0.1, 0.5),
                p(-0.4, 0.0069999998, 0.07),
                p(0.0, 0.021, 0.07),
                p(0.4, 0.35, 0.658),
                p(1.0, 0.42000002, 0.049000014)
        );
        DensityFunction ridgesFolded17 = spline(ridgesFolded,
                p(-1.0, -0.1, 0.5),
                p(-0.4, 0.01, 0.0),
                p(0.0, 0.01, 0.0),
                p(0.4, 0.03, 0.04),
                p(1.0, 0.1, 0.049)
        );
        DensityFunction ridgesFolded18 = spline(ridgesFolded,
                p(-1.0, -0.05, 0.5),
                p(-0.4, 0.01, 0.0),
                p(0.0, 0.01, 0.0),
                p(0.4, 0.03, 0.04),
                p(1.0, 0.1, 0.049)
        );
        DensityFunction ridgesFolded19 = spline(ridgesFolded,
                p(-1.0, 0.2, 0.0),
                p(0.0, 0.5391705, 0.4608295),
                p(1.0, 1.0, 0.4608295)
        );
        DensityFunction ridgesFolded20 = spline(ridgesFolded,
                p(-1.0, -0.2, 0.5),
                p(-0.4, 0.5, 0.0),
                p(0.0, 0.5, 0.0),
                p(0.4, 0.5, 0.0),
                p(1.0, 0.6, 0.070000015)
        );
        DensityFunction ridgesFolded21 = spline(ridgesFolded,
                p(-1.0, -0.05, 0.5),
                p(-0.4, 0.01, 0.099999994),
                p(0.0, 0.03, 0.099999994),
                p(0.4, 0.5, 0.94),
                p(1.0, 0.6, 0.070000015)
        );
        DensityFunction ridgesFolded22 = spline(ridgesFolded,
                p(-1.0, -0.05, 0.5),
                p(-0.4, 0.01, 0.0),
                p(0.0, 0.01, 0.0),
                p(0.4, 0.03, 0.04),
                p(1.0, 0.1, 0.049)
        );
        DensityFunction ridgesFolded23 = spline(ridgesFolded,
                p(-1.0, -0.02, 0.015),
                p(-0.4, 0.01, 0.0),
                p(0.0, 0.01, 0.0),
                p(0.4, 0.03, 0.04),
                p(1.0, 0.1, 0.049)
        );
        DensityFunction ridgesFolded24 = spline(ridgesFolded,
                p(-1.0, 0.34792626, 0.0),
                p(0.0, 0.9239631, 0.5760369),
                p(1.0, 1.5, 0.5760369)
        );
        DensityFunction ridgesFolded25 = spline(ridgesFolded,
                p(-1.0, -0.1, 0.0),
                p(-0.4, 0.1, 0.0),
                p(0.0, 0.17, 0.0)
        );
        DensityFunction ridgesFolded26 = spline(ridgesFolded,
                p(-1.0, -0.05, 0.0),
                p(-0.4, 0.1, 0.0),
                p(0.0, 0.17, 0.0)
        );

        DensityFunction erosion1 = DensityCommon.spline(erosion,
                p(-0.85, ridgesFolded1, 0.0),
                p(-0.7, ridgesFolded2, 0.0),
                p(-0.4, ridgesFolded3, 0.0),
                p(-0.35, ridgesFolded4, 0.0),
                p(-0.1, ridgesFolded5, 0.0),
                p(0.2, ridgesFolded6, 0.0),
                p(0.7, ridgesFolded7, 0.0)
        );
        DensityFunction erosion2 = DensityCommon.spline(erosion,
                p(-0.85, ridgesFolded1, 0.0),
                p(-0.7, ridgesFolded2, 0.0),
                p(-0.4, ridgesFolded3, 0.0),
                p(-0.35, ridgesFolded8, 0.0),
                p(-0.1, ridgesFolded9, 0.0),
                p(0.2, ridgesFolded10, 0.0),
                p(0.7, ridgesFolded11, 0.0)
        );
        DensityFunction erosion3 = DensityCommon.spline(erosion,
                p(-0.85, ridgesFolded12, 0.0),
                p(-0.7, ridgesFolded13, 0.0),
                p(-0.4, ridgesFolded14, 0.0),
                p(-0.35, ridgesFolded15, 0.0),
                p(-0.1, ridgesFolded16, 0.0),
                p(0.2, ridgesFolded17, 0.0),
                p(0.4, ridgesFolded17, 0.0),
                p(0.45, ridgesFolded25, 0.0),
                p(0.55, ridgesFolded25, 0.0),
                p(0.58, ridgesFolded18, 0.0),
                p(0.7, ridgesFolded11, 0.0)
        );
        DensityFunction erosion4 = DensityCommon.spline(erosion,
                p(-0.85, ridgesFolded24, 0.0),
                p(-0.7, ridgesFolded19, 0.0),
                p(-0.4, ridgesFolded19, 0.0),
                p(-0.35, ridgesFolded20, 0.0),
                p(-0.1, ridgesFolded21, 0.0),
                p(0.2, ridgesFolded22, 0.0),
                p(0.4, ridgesFolded22, 0.0),
                p(0.45, ridgesFolded26, 0.0),
                p(0.55, ridgesFolded26, 0.0),
                p(0.58, ridgesFolded22, 0.0),
                p(0.7, ridgesFolded23, 0.0)
        );

        return DensityCommon.spline(continents,
                p(-1.1, 0.044, 0.0),
                p(-1.02, -0.2222, 0.0),
                p(-0.51, -0.2222, 0.0),
                p(-0.44, -0.12, 0.0),
                p(-0.18, -0.12, 0.0),
                p(-0.16, erosion1, 0.0),
                p(-0.15, erosion1, 0.0),
                p(-0.1, erosion2, 0.0),
                p(0.25, erosion3, 0.0),
                p(1.0, erosion4, 0.0)
        );
    }

}
