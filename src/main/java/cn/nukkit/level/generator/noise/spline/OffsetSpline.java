package cn.nukkit.level.generator.noise.spline;

import java.util.Arrays;

public class OffsetSpline {

    public static final SplineGenerator.Spline CACHED_SPLINE;

    static {
        SplineGenerator.Spline ridgesFolded1 = new SplineGenerator.Spline("minecraft:overworld/ridges_folded", Arrays.asList(
                new SplineGenerator.Point(-1.0, -0.08880186, 0.38940096),
                new SplineGenerator.Point(1.0, 0.69000006, 0.38940096)
        ));
        SplineGenerator.Spline ridgesFolded2 = new SplineGenerator.Spline("minecraft:overworld/ridges_folded", Arrays.asList(
                new SplineGenerator.Point(-1.0, -0.115760356, 0.37788022),
                new SplineGenerator.Point(1.0, 0.6400001, 0.37788022)
        ));
        SplineGenerator.Spline ridgesFolded3 = new SplineGenerator.Spline("minecraft:overworld/ridges_folded", Arrays.asList(
                new SplineGenerator.Point(-1.0, -0.2222, 0.0),
                new SplineGenerator.Point(-0.75, -0.2222, 0.0),
                new SplineGenerator.Point(-0.65, 0.0, 0.0),
                new SplineGenerator.Point(0.5954547, 2.9802322E-8, 0.0),
                new SplineGenerator.Point(0.6054547, 2.9802322E-8, 0.2534563),
                new SplineGenerator.Point(1.0, 0.100000024, 0.2534563)
        ));
        SplineGenerator.Spline ridgesFolded4 = new SplineGenerator.Spline("minecraft:overworld/ridges_folded", Arrays.asList(
                new SplineGenerator.Point(-1.0, -0.3, 0.5),
                new SplineGenerator.Point(-0.4, 0.05, 0.0),
                new SplineGenerator.Point(0.0, 0.05, 0.0),
                new SplineGenerator.Point(0.4, 0.05, 0.0),
                new SplineGenerator.Point(1.0, 0.060000002, 0.007000001)
        ));
        SplineGenerator.Spline ridgesFolded5 = new SplineGenerator.Spline("minecraft:overworld/ridges_folded", Arrays.asList(
                new SplineGenerator.Point(-1.0, -0.15, 0.5),
                new SplineGenerator.Point(-0.4, 0.0, 0.0),
                new SplineGenerator.Point(0.0, 0.0, 0.0),
                new SplineGenerator.Point(0.4, 0.05, 0.1),
                new SplineGenerator.Point(1.0, 0.060000002, 0.007000001)
        ));
        SplineGenerator.Spline ridgesFolded6 = new SplineGenerator.Spline("minecraft:overworld/ridges_folded", Arrays.asList(
                new SplineGenerator.Point(-1.0, -0.15, 0.5),
                new SplineGenerator.Point(-0.4, 0.0, 0.0),
                new SplineGenerator.Point(0.0, 0.0, 0.0),
                new SplineGenerator.Point(0.4, 0.0, 0.0),
                new SplineGenerator.Point(1.0, 0.0, 0.0)
        ));
        SplineGenerator.Spline ridgesFolded7 = new SplineGenerator.Spline("minecraft:overworld/ridges_folded", Arrays.asList(
                new SplineGenerator.Point(-1.0, -0.02, 0.0),
                new SplineGenerator.Point(-0.4, -0.03, 0.0),
                new SplineGenerator.Point(0.0, -0.03, 0.0),
                new SplineGenerator.Point(0.4, 0.0, 0.06),
                new SplineGenerator.Point(1.0, 0.0, 0.0)
        ));
        SplineGenerator.Spline ridgesFolded8 = new SplineGenerator.Spline("minecraft:overworld/ridges_folded", Arrays.asList(
                new SplineGenerator.Point(-1.0, -0.25, 0.5),
                new SplineGenerator.Point(-0.4, 0.05, 0.0),
                new SplineGenerator.Point(0.0, 0.05, 0.0),
                new SplineGenerator.Point(0.4, 0.05, 0.0),
                new SplineGenerator.Point(1.0, 0.060000002, 0.007000001)
        ));
        SplineGenerator.Spline ridgesFolded9 = new SplineGenerator.Spline("minecraft:overworld/ridges_folded", Arrays.asList(
                new SplineGenerator.Point(-1.0, -0.1, 0.5),
                new SplineGenerator.Point(-0.4, 0.001, 0.01),
                new SplineGenerator.Point(0.0, 0.003, 0.01),
                new SplineGenerator.Point(0.4, 0.05, 0.094000004),
                new SplineGenerator.Point(1.0, 0.060000002, 0.007000001)
        ));
        SplineGenerator.Spline ridgesFolded10 = new SplineGenerator.Spline("minecraft:overworld/ridges_folded", Arrays.asList(
                new SplineGenerator.Point(-1.0, -0.1, 0.5),
                new SplineGenerator.Point(-0.4, 0.01, 0.0),
                new SplineGenerator.Point(0.0, 0.01, 0.0),
                new SplineGenerator.Point(0.4, 0.03, 0.04),
                new SplineGenerator.Point(1.0, 0.1, 0.049)
        ));
        SplineGenerator.Spline ridgesFolded11 = new SplineGenerator.Spline("minecraft:overworld/ridges_folded", Arrays.asList(
                new SplineGenerator.Point(-1.0, -0.02, 0.015),
                new SplineGenerator.Point(-0.4, 0.01, 0.0),
                new SplineGenerator.Point(0.0, 0.01, 0.0),
                new SplineGenerator.Point(0.4, 0.03, 0.04),
                new SplineGenerator.Point(1.0, 0.1, 0.049)
        ));
        SplineGenerator.Spline ridgesFolded12 = new SplineGenerator.Spline("minecraft:overworld/ridges_folded", Arrays.asList(
                new SplineGenerator.Point(-1.0, 0.20235021, 0.0),
                new SplineGenerator.Point(0.0, 0.7161751, 0.5138249),
                new SplineGenerator.Point(1.0, 1.23, 0.5138249)
        ));
        SplineGenerator.Spline ridgesFolded13 = new SplineGenerator.Spline("minecraft:overworld/ridges_folded", Arrays.asList(
                new SplineGenerator.Point(-1.0, 0.2, 0.0),
                new SplineGenerator.Point(0.0, 0.44682026, 0.43317974),
                new SplineGenerator.Point(1.0, 0.88, 0.43317974)
        ));
        SplineGenerator.Spline ridgesFolded14 = new SplineGenerator.Spline("minecraft:overworld/ridges_folded", Arrays.asList(
                new SplineGenerator.Point(-1.0, 0.2, 0.0),
                new SplineGenerator.Point(0.0, 0.30829495, 0.3917051),
                new SplineGenerator.Point(1.0, 0.70000005, 0.3917051)
        ));
        SplineGenerator.Spline ridgesFolded15 = new SplineGenerator.Spline("minecraft:overworld/ridges_folded", Arrays.asList(
                new SplineGenerator.Point(-1.0, -0.25, 0.5),
                new SplineGenerator.Point(-0.4, 0.35, 0.0),
                new SplineGenerator.Point(0.0, 0.35, 0.0),
                new SplineGenerator.Point(0.4, 0.35, 0.0),
                new SplineGenerator.Point(1.0, 0.42000002, 0.049000014)
        ));
        SplineGenerator.Spline ridgesFolded16 = new SplineGenerator.Spline("minecraft:overworld/ridges_folded", Arrays.asList(
                new SplineGenerator.Point(-1.0, -0.1, 0.5),
                new SplineGenerator.Point(-0.4, 0.0069999998, 0.07),
                new SplineGenerator.Point(0.0, 0.021, 0.07),
                new SplineGenerator.Point(0.4, 0.35, 0.658),
                new SplineGenerator.Point(1.0, 0.42000002, 0.049000014)
        ));
        SplineGenerator.Spline ridgesFolded17 = new SplineGenerator.Spline("minecraft:overworld/ridges_folded", Arrays.asList(
                new SplineGenerator.Point(-1.0, -0.1, 0.5),
                new SplineGenerator.Point(-0.4, 0.01, 0.0),
                new SplineGenerator.Point(0.0, 0.01, 0.0),
                new SplineGenerator.Point(0.4, 0.03, 0.04),
                new SplineGenerator.Point(1.0, 0.1, 0.049)
        ));
        SplineGenerator.Spline ridgesFolded18 = new SplineGenerator.Spline("minecraft:overworld/ridges_folded", Arrays.asList(
                new SplineGenerator.Point(-1.0, -0.05, 0.5),
                new SplineGenerator.Point(-0.4, 0.01, 0.0),
                new SplineGenerator.Point(0.0, 0.01, 0.0),
                new SplineGenerator.Point(0.4, 0.03, 0.04),
                new SplineGenerator.Point(1.0, 0.1, 0.049)
        ));
        SplineGenerator.Spline ridgesFolded19 = new SplineGenerator.Spline("minecraft:overworld/ridges_folded", Arrays.asList(
                new SplineGenerator.Point(-1.0, 0.2, 0.0),
                new SplineGenerator.Point(0.0, 0.5391705, 0.4608295),
                new SplineGenerator.Point(1.0, 1.0, 0.4608295)
        ));
        SplineGenerator.Spline ridgesFolded20 = new SplineGenerator.Spline("minecraft:overworld/ridges_folded", Arrays.asList(
                new SplineGenerator.Point(-1.0, -0.2, 0.5),
                new SplineGenerator.Point(-0.4, 0.5, 0.0),
                new SplineGenerator.Point(0.0, 0.5, 0.0),
                new SplineGenerator.Point(0.4, 0.5, 0.0),
                new SplineGenerator.Point(1.0, 0.6, 0.070000015)
        ));
        SplineGenerator.Spline ridgesFolded21 = new SplineGenerator.Spline("minecraft:overworld/ridges_folded", Arrays.asList(
                new SplineGenerator.Point(-1.0, -0.05, 0.5),
                new SplineGenerator.Point(-0.4, 0.01, 0.099999994),
                new SplineGenerator.Point(0.0, 0.03, 0.099999994),
                new SplineGenerator.Point(0.4, 0.5, 0.94),
                new SplineGenerator.Point(1.0, 0.6, 0.070000015)
        ));
        SplineGenerator.Spline ridgesFolded22 = new SplineGenerator.Spline("minecraft:overworld/ridges_folded", Arrays.asList(
                new SplineGenerator.Point(-1.0, -0.05, 0.5),
                new SplineGenerator.Point(-0.4, 0.01, 0.0),
                new SplineGenerator.Point(0.0, 0.01, 0.0),
                new SplineGenerator.Point(0.4, 0.03, 0.04),
                new SplineGenerator.Point(1.0, 0.1, 0.049)
        ));
        SplineGenerator.Spline ridgesFolded23 = new SplineGenerator.Spline("minecraft:overworld/ridges_folded", Arrays.asList(
                new SplineGenerator.Point(-1.0, -0.02, 0.015),
                new SplineGenerator.Point(-0.4, 0.01, 0.0),
                new SplineGenerator.Point(0.0, 0.01, 0.0),
                new SplineGenerator.Point(0.4, 0.03, 0.04),
                new SplineGenerator.Point(1.0, 0.1, 0.049)
        ));
        SplineGenerator.Spline ridgesFolded24 = new SplineGenerator.Spline("minecraft:overworld/ridges_folded", Arrays.asList(
                new SplineGenerator.Point(-1.0, 0.34792626, 0.0),
                new SplineGenerator.Point(0.0, 0.9239631, 0.5760369),
                new SplineGenerator.Point(1.0, 1.5, 0.5760369)
        ));
        SplineGenerator.Spline ridgesFolded25 = new SplineGenerator.Spline("minecraft:overworld/ridges_folded", Arrays.asList(
                new SplineGenerator.Point(-1.0, -0.1, 0.0),
                new SplineGenerator.Point(-0.4, 0.1, 0.0),
                new SplineGenerator.Point(0.0, 0.17, 0.0)
        ));
        SplineGenerator.Spline ridgesFolded26 = new SplineGenerator.Spline("minecraft:overworld/ridges_folded", Arrays.asList(
                new SplineGenerator.Point(-1.0, -0.05, 0.0),
                new SplineGenerator.Point(-0.4, 0.1, 0.0),
                new SplineGenerator.Point(0.0, 0.17, 0.0)
        ));

        SplineGenerator.Spline erosion1 = new SplineGenerator.Spline("minecraft:overworld/erosion", Arrays.asList(
                new SplineGenerator.Point(-0.85, ridgesFolded1, 0.0),
                new SplineGenerator.Point(-0.7, ridgesFolded2, 0.0),
                new SplineGenerator.Point(-0.4, ridgesFolded3, 0.0),
                new SplineGenerator.Point(-0.35, ridgesFolded4, 0.0),
                new SplineGenerator.Point(-0.1, ridgesFolded5, 0.0),
                new SplineGenerator.Point(0.2, ridgesFolded6, 0.0),
                new SplineGenerator.Point(0.7, ridgesFolded7, 0.0)
        ));
        SplineGenerator.Spline erosion2 = new SplineGenerator.Spline("minecraft:overworld/erosion", Arrays.asList(
                new SplineGenerator.Point(-0.85, ridgesFolded1, 0.0),
                new SplineGenerator.Point(-0.7, ridgesFolded2, 0.0),
                new SplineGenerator.Point(-0.4, ridgesFolded3, 0.0),
                new SplineGenerator.Point(-0.35, ridgesFolded8, 0.0),
                new SplineGenerator.Point(-0.1, ridgesFolded9, 0.0),
                new SplineGenerator.Point(0.2, ridgesFolded10, 0.0),
                new SplineGenerator.Point(0.7, ridgesFolded11, 0.0)
        ));
        SplineGenerator.Spline erosion3 = new SplineGenerator.Spline("minecraft:overworld/erosion", Arrays.asList(
                new SplineGenerator.Point(-0.85, ridgesFolded12, 0.0),
                new SplineGenerator.Point(-0.7, ridgesFolded13, 0.0),
                new SplineGenerator.Point(-0.4, ridgesFolded14, 0.0),
                new SplineGenerator.Point(-0.35, ridgesFolded15, 0.0),
                new SplineGenerator.Point(-0.1, ridgesFolded16, 0.0),
                new SplineGenerator.Point(0.2, ridgesFolded17, 0.0),
                new SplineGenerator.Point(0.4, ridgesFolded17, 0.0),
                new SplineGenerator.Point(0.45, ridgesFolded25, 0.0),
                new SplineGenerator.Point(0.55, ridgesFolded25, 0.0),
                new SplineGenerator.Point(0.58, ridgesFolded18, 0.0),
                new SplineGenerator.Point(0.7, ridgesFolded11, 0.0)
        ));
        SplineGenerator.Spline erosion4 = new SplineGenerator.Spline("minecraft:overworld/erosion", Arrays.asList(
                new SplineGenerator.Point(-0.85, ridgesFolded24, 0.0),
                new SplineGenerator.Point(-0.7, ridgesFolded19, 0.0),
                new SplineGenerator.Point(-0.4, ridgesFolded19, 0.0),
                new SplineGenerator.Point(-0.35, ridgesFolded20, 0.0),
                new SplineGenerator.Point(-0.1, ridgesFolded21, 0.0),
                new SplineGenerator.Point(0.2, ridgesFolded22, 0.0),
                new SplineGenerator.Point(0.4, ridgesFolded22, 0.0),
                new SplineGenerator.Point(0.45, ridgesFolded26, 0.0),
                new SplineGenerator.Point(0.55, ridgesFolded26, 0.0),
                new SplineGenerator.Point(0.58, ridgesFolded22, 0.0),
                new SplineGenerator.Point(0.7, ridgesFolded23, 0.0)
        ));


        CACHED_SPLINE = new SplineGenerator.Spline("minecraft:overworld/continents", Arrays.asList(
                new SplineGenerator.Point(-1.1, 0.044, 0.0),
                new SplineGenerator.Point(-1.02, -0.2222, 0.0),
                new SplineGenerator.Point(-0.51, -0.2222, 0.0),
                new SplineGenerator.Point(-0.44, -0.12, 0.0),
                new SplineGenerator.Point(-0.18, -0.12, 0.0),
                new SplineGenerator.Point(-0.16, erosion1, 0.0),
                new SplineGenerator.Point(-0.15, erosion1, 0.0),
                new SplineGenerator.Point(-0.1, erosion2, 0.0),
                new SplineGenerator.Point(0.25, erosion3, 0.0),
                new SplineGenerator.Point(1.0, erosion4, 0.0)
        ));
    }

}
