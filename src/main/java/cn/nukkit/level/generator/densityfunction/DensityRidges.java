package cn.nukkit.level.generator.densityfunction;

import cn.nukkit.level.generator.noise.minecraft.noise.NormalNoise;
import cn.nukkit.level.generator.noise.minecraft.simplex.SimplexNoise;

public final class DensityRidges {

    private static final double XZ_SCALE = 0.25;
    private static final double Y_SCALE = 0.0;

    private DensityRidges() {
    }

    public static DensityFunction overworldRidges(
            NormalNoise ridge,
            DensityFunction shiftX,
            DensityFunction shiftZ
    ) {
        return DensityFunctions.flatCache(
                new DensityFunctions.ShiftedNoise(
                        shiftX,
                        DensityFunctions.zero(),
                        shiftZ,
                        XZ_SCALE,
                        Y_SCALE,
                        new DensityFunction.NoiseHolder(ridge)
                )
        );
    }

    public static DensityFunction overworldRidges(
            NormalNoise ridge,
            NormalNoise shiftNoise
    ) {
        return overworldRidges(
                ridge,
                DensityFunctions.shiftA(shiftNoise),
                DensityFunctions.shiftB(shiftNoise)
        );
    }

    public static DensityFunction overworldRidges(
            SimplexNoise ridge,
            DensityFunction shiftX,
            DensityFunction shiftZ
    ) {
        return DensityFunctions.flatCache(
                new DensityFunctions.ShiftedNoise(
                        shiftX,
                        DensityFunctions.zero(),
                        shiftZ,
                        XZ_SCALE,
                        Y_SCALE,
                        new DensityFunction.NoiseHolder(ridge)
                )
        );
    }

    public static DensityFunction overworldRidges(
            SimplexNoise ridge,
            SimplexNoise shiftNoise
    ) {
        return overworldRidges(
                ridge,
                DensityFunctions.shiftA(shiftNoise),
                DensityFunctions.shiftB(shiftNoise)
        );
    }
}
