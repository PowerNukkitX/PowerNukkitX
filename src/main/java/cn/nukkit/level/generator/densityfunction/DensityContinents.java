package cn.nukkit.level.generator.densityfunction;

import cn.nukkit.level.generator.noise.minecraft.noise.NormalNoise;
import cn.nukkit.level.generator.noise.minecraft.simplex.SimplexNoise;

public final class DensityContinents {

    private static final double XZ_SCALE = 0.25;
    private static final double Y_SCALE = 0.0;

    private DensityContinents() {
    }

    public static DensityFunction overworldContinents(
            NormalNoise continentalness,
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
                        new DensityFunction.NoiseHolder(continentalness)
                )
        );
    }

    public static DensityFunction overworldContinents(
            NormalNoise continentalness,
            NormalNoise shiftNoise
    ) {
        return overworldContinents(
                continentalness,
                DensityFunctions.shiftA(shiftNoise),
                DensityFunctions.shiftB(shiftNoise)
        );
    }

    public static DensityFunction overworldContinents(
            SimplexNoise continentalness,
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
                        new DensityFunction.NoiseHolder(continentalness)
                )
        );
    }

    public static DensityFunction overworldContinents(
            SimplexNoise continentalness,
            SimplexNoise shiftNoise
    ) {
        return overworldContinents(
                continentalness,
                DensityFunctions.shiftA(shiftNoise),
                DensityFunctions.shiftB(shiftNoise)
        );
    }
}
