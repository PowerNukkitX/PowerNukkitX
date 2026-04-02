package cn.nukkit.level.generator.densityfunction;

import cn.nukkit.level.generator.noise.minecraft.noise.NormalNoise;
import cn.nukkit.level.generator.noise.minecraft.simplex.SimplexNoise;

public final class DensityErosion {

    private static final double XZ_SCALE = 0.25;
    private static final double Y_SCALE = 0.0;

    private DensityErosion() {
    }

    public static DensityFunction overworldErosion(
            NormalNoise erosion,
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
                        new DensityFunction.NoiseHolder(erosion)
                )
        );
    }

    public static DensityFunction overworldErosion(
            NormalNoise erosion,
            NormalNoise shiftNoise
    ) {
        return overworldErosion(
                erosion,
                DensityFunctions.shiftA(shiftNoise),
                DensityFunctions.shiftB(shiftNoise)
        );
    }

    public static DensityFunction overworldErosion(
            SimplexNoise erosion,
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
                        new DensityFunction.NoiseHolder(erosion)
                )
        );
    }

    public static DensityFunction overworldErosion(
            SimplexNoise erosion,
            SimplexNoise shiftNoise
    ) {
        return overworldErosion(
                erosion,
                DensityFunctions.shiftA(shiftNoise),
                DensityFunctions.shiftB(shiftNoise)
        );
    }
}
