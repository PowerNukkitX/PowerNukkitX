package cn.nukkit.level.generator.noise.spline;

import java.util.Arrays;

public class JaggednessSpline {

    public static final SplineGenerator.Spline CACHED_SPLINE;

    static {
        SplineGenerator.Spline ridges1 = new SplineGenerator.Spline("minecraft:overworld/ridges", Arrays.asList(
                new SplineGenerator.Point(-0.01, 0.63, 0.0),
                new SplineGenerator.Point(0.01, 0.3, 0.0)
        ));

        SplineGenerator.Spline ridges2 = new SplineGenerator.Spline("minecraft:overworld/ridges", Arrays.asList(
                new SplineGenerator.Point(-0.01, 0.315, 0.0),
                new SplineGenerator.Point(0.01, 0.15, 0.0)
        ));

        SplineGenerator.Spline ridges3 = new SplineGenerator.Spline("minecraft:overworld/ridges", Arrays.asList(
                new SplineGenerator.Point(-0.01, 0.315, 0.0),
                new SplineGenerator.Point(0.01, 0.15, 0.0)
        ));

        SplineGenerator.Spline ridges4 = new SplineGenerator.Spline("minecraft:overworld/ridges", Arrays.asList(
                new SplineGenerator.Point(-0.01, 0.63, 0.0),
                new SplineGenerator.Point(0.01, 0.3, 0.0)
        ));

        SplineGenerator.Spline ridges5 = new SplineGenerator.Spline("minecraft:overworld/ridges", Arrays.asList(
                new SplineGenerator.Point(-0.01, 0.63, 0.0),
                new SplineGenerator.Point(0.01, 0.3, 0.0)
        ));

        SplineGenerator.Spline ridges6 = new SplineGenerator.Spline("minecraft:overworld/ridges", Arrays.asList(
                new SplineGenerator.Point(-0.01, 0.63, 0.0),
                new SplineGenerator.Point(0.01, 0.3, 0.0)
        ));

        SplineGenerator.Spline ridgesFolded1 = new SplineGenerator.Spline("minecraft:overworld/ridges_folded", Arrays.asList(
                new SplineGenerator.Point(0.19999999, 0.0, 0.0),
                new SplineGenerator.Point(0.44999996, 0.0, 0.0),
                new SplineGenerator.Point(1.0, ridges1, 0.0)
        ));

        SplineGenerator.Spline ridgesFolded2 = new SplineGenerator.Spline("minecraft:overworld/ridges_folded", Arrays.asList(
                new SplineGenerator.Point(0.19999999, 0.0, 0.0),
                new SplineGenerator.Point(0.44999996, 0.0, 0.0),
                new SplineGenerator.Point(1.0, ridges2, 0.0)
        ));

        SplineGenerator.Spline ridgesFolded3 = new SplineGenerator.Spline("minecraft:overworld/ridges_folded", Arrays.asList(
                new SplineGenerator.Point(0.19999999, 0.0, 0.0),
                new SplineGenerator.Point(0.44999996, 0.0, 0.0),
                new SplineGenerator.Point(1.0, ridges3, 0.0)
        ));

        SplineGenerator.Spline ridgesFolded4 = new SplineGenerator.Spline("minecraft:overworld/ridges_folded", Arrays.asList(
                new SplineGenerator.Point(0.19999999, 0.0, 0.0),
                new SplineGenerator.Point(0.44999996, ridges4, 0.0),
                new SplineGenerator.Point(1.0, ridges5, 0.0)
        ));

        SplineGenerator.Spline ridgesFolded5 = new SplineGenerator.Spline("minecraft:overworld/ridges_folded", Arrays.asList(
                new SplineGenerator.Point(0.19999999, 0.0, 0.0),
                new SplineGenerator.Point(0.44999996, 0.0, 0.0),
                new SplineGenerator.Point(1.0, ridges6, 0.0)
        ));

        SplineGenerator.Spline erosion1 = new SplineGenerator.Spline("minecraft:overworld/erosion", Arrays.asList(
                new SplineGenerator.Point(-1.0, ridgesFolded1, 0.0),
                new SplineGenerator.Point(-0.78, ridgesFolded2, 0.0),
                new SplineGenerator.Point(-0.5775, ridgesFolded3, 0.0),
                new SplineGenerator.Point(-0.375, 0.0, 0.0)
        ));

        SplineGenerator.Spline erosion2 = new SplineGenerator.Spline("minecraft:overworld/erosion", Arrays.asList(
                new SplineGenerator.Point(-1.0, ridgesFolded4, 0.0),
                new SplineGenerator.Point(-0.78, ridgesFolded5, 0.0),
                new SplineGenerator.Point(-0.5775, ridgesFolded5, 0.0),
                new SplineGenerator.Point(-0.375, 0.0, 0.0)
        ));

        CACHED_SPLINE = new SplineGenerator.Spline("minecraft:overworld/continents", Arrays.asList(
                new SplineGenerator.Point(-0.11, 0.0, 0.0),
                new SplineGenerator.Point(0.03, erosion1, 0.0),
                new SplineGenerator.Point(0.65, erosion2, 0.0)
        ));
    }

}
