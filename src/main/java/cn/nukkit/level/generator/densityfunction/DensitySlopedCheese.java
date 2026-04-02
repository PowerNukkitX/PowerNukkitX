package cn.nukkit.level.generator.densityfunction;

import cn.nukkit.level.generator.noise.minecraft.noise.NormalNoise;
import cn.nukkit.level.generator.noise.minecraft.simplex.SimplexNoise;

public final class DensitySlopedCheese {

    private static final double JAGGED_XZ_SCALE = 1500.0;
    private static final double JAGGED_Y_SCALE = 0.0;

    private DensitySlopedCheese() {
    }

    public static DensityFunction overworldSlopedCheese(
            DensityFunction depth,
            DensityFunction jaggedness,
            DensityFunction factor,
            DensityFunction base3dNoise,
            NormalNoise jaggedNoise
    ) {
        DensityFunction jaggedSample = DensityFunctions.noise(jaggedNoise, JAGGED_XZ_SCALE, JAGGED_Y_SCALE).halfNegative();
        DensityFunction combinedDepth = DensityFunctions.add(
                depth,
                DensityFunctions.mul(jaggedness, jaggedSample)
        );

        return DensityFunctions.add(
                DensityFunctions.mul(
                        DensityFunctions.constant(4.0),
                        DensityFunctions.mul(combinedDepth, factor).quarterNegative()
                ),
                base3dNoise
        );
    }

    public static DensityFunction overworldSlopedCheese(
            DensityFunction depth,
            DensityFunction jaggedness,
            DensityFunction factor,
            DensityFunction base3dNoise,
            SimplexNoise jaggedNoise
    ) {
        DensityFunction jaggedSample = DensityFunctions.noise(jaggedNoise, JAGGED_XZ_SCALE, JAGGED_Y_SCALE).halfNegative();
        DensityFunction combinedDepth = DensityFunctions.add(
                depth,
                DensityFunctions.mul(jaggedness, jaggedSample)
        );

        return DensityFunctions.add(
                DensityFunctions.mul(
                        DensityFunctions.constant(4.0),
                        DensityFunctions.mul(combinedDepth, factor).quarterNegative()
                ),
                base3dNoise
        );
    }
}
