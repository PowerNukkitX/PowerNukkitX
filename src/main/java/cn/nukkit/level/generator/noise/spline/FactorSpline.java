package cn.nukkit.level.generator.noise.spline;

import java.util.Arrays;
import java.util.List;

public class FactorSpline {

    public static final SplineGenerator.Spline CACHED_SPLINE;

    static {
        // Ridges Splines
        SplineGenerator.Spline ridges1 = new SplineGenerator.Spline("minecraft:overworld/ridges", Arrays.asList(
                new SplineGenerator.Point(-0.2, 6.3, 0.0),
                new SplineGenerator.Point(0.2, 6.25, 0.0)
        ));

        SplineGenerator.Spline ridges2 = new SplineGenerator.Spline("minecraft:overworld/ridges", Arrays.asList(
                new SplineGenerator.Point(-0.05, 6.3, 0.0),
                new SplineGenerator.Point(0.05, 2.67, 0.0)
        ));

        SplineGenerator.Spline ridges3 = new SplineGenerator.Spline("minecraft:overworld/ridges", Arrays.asList(
                new SplineGenerator.Point(-0.2, 6.3, 0.0),
                new SplineGenerator.Point(0.2, 6.25, 0.0)
        ));

        SplineGenerator.Spline ridges4 = new SplineGenerator.Spline("minecraft:overworld/ridges", Arrays.asList(
                new SplineGenerator.Point(-0.2, 6.3, 0.0),
                new SplineGenerator.Point(0.2, 6.25, 0.0)
        ));

        SplineGenerator.Spline ridges5 = new SplineGenerator.Spline("minecraft:overworld/ridges", Arrays.asList(
                new SplineGenerator.Point(-0.05, 2.67, 0.0),
                new SplineGenerator.Point(0.05, 6.3, 0.0)
        ));

        SplineGenerator.Spline ridges6 = new SplineGenerator.Spline("minecraft:overworld/ridges", Arrays.asList(
                new SplineGenerator.Point(-0.2, 6.3, 0.0),
                new SplineGenerator.Point(0.2, 6.25, 0.0)
        ));

        SplineGenerator.Spline ridges7 = new SplineGenerator.Spline("minecraft:overworld/ridges", Arrays.asList(
                new SplineGenerator.Point(0.0, 6.25, 0.0),
                new SplineGenerator.Point(0.1, 0.625, 0.0)
        ));

        SplineGenerator.Spline ridges8 = new SplineGenerator.Spline("minecraft:overworld/ridges", Arrays.asList(
                new SplineGenerator.Point(-0.2, 6.3, 0.0),
                new SplineGenerator.Point(0.2, 5.47, 0.0)
        ));

        SplineGenerator.Spline ridges9 = new SplineGenerator.Spline("minecraft:overworld/ridges", Arrays.asList(
                new SplineGenerator.Point(-0.2, 6.3, 0.0),
                new SplineGenerator.Point(0.2, 5.08, 0.0)
        ));

        SplineGenerator.Spline ridges10 = new SplineGenerator.Spline("minecraft:overworld/ridges", Arrays.asList(
                new SplineGenerator.Point(-0.2, 6.3, 0.0),
                new SplineGenerator.Point(0.2, 4.69, 0.0)
        ));

        SplineGenerator.Spline ridges11 = new SplineGenerator.Spline("minecraft:overworld/ridges", Arrays.asList(
                new SplineGenerator.Point(0.0, 5.47, 0.0),
                new SplineGenerator.Point(0.1, 0.625, 0.0)
        ));

        SplineGenerator.Spline ridges12 = new SplineGenerator.Spline("minecraft:overworld/ridges", Arrays.asList(
                new SplineGenerator.Point(0.0, 5.08, 0.0),
                new SplineGenerator.Point(0.1, 0.625, 0.0)
        ));

        SplineGenerator.Spline ridges13 = new SplineGenerator.Spline("minecraft:overworld/ridges", Arrays.asList(
                new SplineGenerator.Point(0.0, 4.69, 0.0),
                new SplineGenerator.Point(0.1, 0.625, 0.0)
        ));

        // Ridges Folded Splines
        SplineGenerator.Spline ridgesFolded1 = new SplineGenerator.Spline("minecraft:overworld/ridges_folded", Arrays.asList(
                new SplineGenerator.Point(-0.9, 6.25, 0.0),
                new SplineGenerator.Point(-0.69, ridges7, 0.0)
        ));

        SplineGenerator.Spline ridgesFolded2 = new SplineGenerator.Spline("minecraft:overworld/ridges_folded", Arrays.asList(
                new SplineGenerator.Point(-0.9, 5.47, 0.0),
                new SplineGenerator.Point(-0.69, ridges11, 0.0)
        ));

        SplineGenerator.Spline ridgesFolded3 = new SplineGenerator.Spline("minecraft:overworld/ridges_folded", Arrays.asList(
                new SplineGenerator.Point(-0.9, 5.08, 0.0),
                new SplineGenerator.Point(-0.69, ridges12, 0.0)
        ));

        SplineGenerator.Spline ridgesFolded4 = new SplineGenerator.Spline("minecraft:overworld/ridges_folded", Arrays.asList(
                new SplineGenerator.Point(0.45, ridges10, 0.0),
                new SplineGenerator.Point(0.7, 1.56, 0.0)
        ));

        SplineGenerator.Spline ridgesFolded5 = new SplineGenerator.Spline("minecraft:overworld/ridges_folded", Arrays.asList(
                new SplineGenerator.Point(-0.7, ridges10, 0.0),
                new SplineGenerator.Point(-0.15, 1.37, 0.0)
        ));

        // Erosion Splines
        SplineGenerator.Spline erosion1 = new SplineGenerator.Spline("minecraft:overworld/erosion", Arrays.asList(
                new SplineGenerator.Point(-0.6, ridges1, 0.0),
                new SplineGenerator.Point(-0.5, ridges2, 0.0),
                new SplineGenerator.Point(-0.35, ridges3, 0.0),
                new SplineGenerator.Point(-0.25, ridges4, 0.0),
                new SplineGenerator.Point(-0.1, ridges5, 0.0),
                new SplineGenerator.Point(0.03, ridges6, 0.0),
                new SplineGenerator.Point(0.35, 6.25, 0.0),
                new SplineGenerator.Point(0.45, ridgesFolded1, 0.0),
                new SplineGenerator.Point(0.55, ridgesFolded1, 0.0),
                new SplineGenerator.Point(0.62, 6.25, 0.0)
        ));

        SplineGenerator.Spline erosion2 = new SplineGenerator.Spline("minecraft:overworld/erosion", Arrays.asList(
                new SplineGenerator.Point(-0.6, ridges8, 0.0),
                new SplineGenerator.Point(-0.5, ridges2, 0.0),
                new SplineGenerator.Point(-0.35, ridges8, 0.0),
                new SplineGenerator.Point(-0.25, ridges8, 0.0),
                new SplineGenerator.Point(-0.1, ridges5, 0.0),
                new SplineGenerator.Point(0.03, ridges8, 0.0),
                new SplineGenerator.Point(0.35, 5.47, 0.0),
                new SplineGenerator.Point(0.45, ridgesFolded2, 0.0),
                new SplineGenerator.Point(0.55, ridgesFolded2, 0.0),
                new SplineGenerator.Point(0.62, 5.47, 0.0)
        ));

        SplineGenerator.Spline erosion3 = new SplineGenerator.Spline("minecraft:overworld/erosion", Arrays.asList(
                new SplineGenerator.Point(-0.6, ridges9, 0.0),
                new SplineGenerator.Point(-0.5, ridges2, 0.0),
                new SplineGenerator.Point(-0.35, ridges9, 0.0),
                new SplineGenerator.Point(-0.25, ridges9, 0.0),
                new SplineGenerator.Point(-0.1, ridges5, 0.0),
                new SplineGenerator.Point(0.03, ridges9, 0.0),
                new SplineGenerator.Point(0.35, 5.08, 0.0),
                new SplineGenerator.Point(0.45, ridgesFolded3, 0.0),
                new SplineGenerator.Point(0.55, ridgesFolded3, 0.0),
                new SplineGenerator.Point(0.62, 5.08, 0.0)
        ));

        SplineGenerator.Spline erosion4 = new SplineGenerator.Spline("minecraft:overworld/erosion", Arrays.asList(
                new SplineGenerator.Point(-0.6, ridges10, 0.0),
                new SplineGenerator.Point(-0.5, ridges2, 0.0),
                new SplineGenerator.Point(-0.35, ridges10, 0.0),
                new SplineGenerator.Point(-0.25, ridges10, 0.0),
                new SplineGenerator.Point(-0.1, ridges5, 0.0),
                new SplineGenerator.Point(0.03, ridges10, 0.0),
                new SplineGenerator.Point(0.05, ridgesFolded4, 0.0),
                new SplineGenerator.Point(0.4, ridgesFolded4, 0.0),
                new SplineGenerator.Point(0.45, ridgesFolded5, 0.0),
                new SplineGenerator.Point(0.55, ridgesFolded5, 0.0),
                new SplineGenerator.Point(0.58, 4.69, 0.0)
        ));


        CACHED_SPLINE = new SplineGenerator.Spline("minecraft:overworld/continents", Arrays.asList(
                new SplineGenerator.Point(-0.19, 3.95, 0.0),
                new SplineGenerator.Point(-0.15, erosion1, 0.0),
                new SplineGenerator.Point(-0.1, erosion2, 0.0),
                new SplineGenerator.Point(0.03, erosion3, 0.0),
                new SplineGenerator.Point(0.06, erosion4, 0.0)
        ));
    }
}