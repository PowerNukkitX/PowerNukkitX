package cn.nukkit.level.generator.densityfunction;

import cn.nukkit.level.generator.noise.minecraft.noise.NormalNoise;

/**
 * @author Buddelbubi
 * @since 2026/04/02
 * @implNote <a href="https://github.com/misode/mcmeta/blob/data/data/minecraft/worldgen/density_function/overworld/sloped_cheese.json">Source</a>
 */
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
        DensityFunction jaggedSample = DensityCommon.noise(jaggedNoise, JAGGED_XZ_SCALE, JAGGED_Y_SCALE).halfNegative();
        DensityFunction combinedDepth = DensityCommon.add(
                depth,
                DensityCommon.mul(jaggedness, jaggedSample)
        );

        return DensityCommon.add(
                DensityCommon.mul(
                        DensityCommon.constant(4.0),
                        DensityCommon.mul(combinedDepth, factor).quarterNegative()
                ),
                base3dNoise
        );
    }

}
