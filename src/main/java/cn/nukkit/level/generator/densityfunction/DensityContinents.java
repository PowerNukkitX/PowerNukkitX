package cn.nukkit.level.generator.densityfunction;

import cn.nukkit.level.generator.noise.minecraft.noise.NormalNoise;

/**
 * @author Buddelbubi
 * @since 2026/04/02
 * @implNote <a href="https://github.com/misode/mcmeta/blob/data/data/minecraft/worldgen/density_function/overworld/continents.json">Source</a>
 */
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
        return DensityCommon.flatCache(
                new DensityCommon.ShiftedNoise(
                        shiftX,
                        DensityCommon.zero(),
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
                DensityCommon.shiftA(shiftNoise),
                DensityCommon.shiftB(shiftNoise)
        );
    }

}
