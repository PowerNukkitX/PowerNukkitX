package cn.nukkit.level.generator.densityfunction;

import cn.nukkit.level.generator.noise.minecraft.noise.NormalNoise;

/**
 * @author Codex
 * @since 2026/04/17
 * @implNote Based on vanilla overworld noise_router vein_* functions
 */
public final class DensityOreVeins {

    private static final DensityFunction Y = new DensityFunction.SimpleFunction() {
        @Override
        public double compute(DensityFunction.FunctionContext context) {
            return context.blockY();
        }

        @Override
        public double minValue() {
            return Double.NEGATIVE_INFINITY;
        }

        @Override
        public double maxValue() {
            return Double.POSITIVE_INFINITY;
        }
    };

    private DensityOreVeins() {
    }

    public static DensityFunction overworldVeinToggle(NormalNoise oreVeininess) {
        return DensityCommon.interpolated(
                DensityCommon.rangeChoice(
                        Y,
                        -60.0,
                        51.0,
                        DensityCommon.noise(oreVeininess, 1.5, 1.5),
                        DensityCommon.zero()
                )
        );
    }

    public static DensityFunction overworldVeinRidged(NormalNoise oreVeinA, NormalNoise oreVeinB) {
        DensityFunction veinA = DensityCommon.interpolated(
                DensityCommon.rangeChoice(
                        Y,
                        -60.0,
                        51.0,
                        DensityCommon.noise(oreVeinA, 4.0, 4.0),
                        DensityCommon.zero()
                )
        );
        DensityFunction veinB = DensityCommon.interpolated(
                DensityCommon.rangeChoice(
                        Y,
                        -60.0,
                        51.0,
                        DensityCommon.noise(oreVeinB, 4.0, 4.0),
                        DensityCommon.zero()
                )
        );
        return DensityCommon.cacheAllInCell(
                DensityCommon.add(
                        DensityCommon.constant(-0.08),
                        DensityCommon.max(veinA.abs(), veinB.abs())
                )
        );
    }

    public static DensityFunction overworldVeinGap(NormalNoise oreGap) {
        return DensityCommon.cacheAllInCell(DensityCommon.noise(oreGap, 1.0, 1.0));
    }
}
