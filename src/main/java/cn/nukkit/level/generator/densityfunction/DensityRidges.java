package cn.nukkit.level.generator.densityfunction;

import cn.nukkit.level.generator.noise.minecraft.noise.NormalNoise;

/**
 * @author Buddelbubi
 * @since 2026/04/02
 * @implNote <a href="https://github.com/misode/mcmeta/blob/data/data/minecraft/worldgen/density_function/overworld/ridges.json">Source</a>
 */
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
        return DensityCommon.flatCache(
                new DensityCommon.ShiftedNoise(
                        shiftX,
                        DensityCommon.zero(),
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
                DensityCommon.shiftA(shiftNoise),
                DensityCommon.shiftB(shiftNoise)
        );
    }

}
