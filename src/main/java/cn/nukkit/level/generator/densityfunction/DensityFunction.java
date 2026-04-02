package cn.nukkit.level.generator.densityfunction;

import cn.nukkit.level.generator.noise.minecraft.noise.NormalNoise;

/**
 * DensityFunction implementation for PowerNukkitX
 * @author Buddelbubi
 * @since 2026/04/02
 */
public interface DensityFunction {

    double compute(FunctionContext context);

    void fillArray(double[] output, ContextProvider contextProvider);

    double minValue();

    double maxValue();

    default DensityFunction clamp(double min, double max) {
        return new DensityCommon.Clamp(this, min, max);
    }

    default DensityFunction abs() {
        return DensityCommon.map(this, DensityCommon.Mapped.Type.ABS);
    }

    default DensityFunction square() {
        return DensityCommon.map(this, DensityCommon.Mapped.Type.SQUARE);
    }

    default DensityFunction cube() {
        return DensityCommon.map(this, DensityCommon.Mapped.Type.CUBE);
    }

    default DensityFunction halfNegative() {
        return DensityCommon.map(this, DensityCommon.Mapped.Type.HALF_NEGATIVE);
    }

    default DensityFunction quarterNegative() {
        return DensityCommon.map(this, DensityCommon.Mapped.Type.QUARTER_NEGATIVE);
    }

    default DensityFunction invert() {
        return DensityCommon.map(this, DensityCommon.Mapped.Type.INVERT);
    }

    default DensityFunction squeeze() {
        return DensityCommon.map(this, DensityCommon.Mapped.Type.SQUEEZE);
    }

    interface ContextProvider {
        FunctionContext forIndex(int index);

        default void fillAllDirectly(double[] output, DensityFunction function) {
            for (int i = 0; i < output.length; i++) {
                output[i] = function.compute(forIndex(i));
            }
        }
    }

    interface FunctionContext {
        int blockX();

        int blockY();

        int blockZ();
    }

    interface NoiseSampler {
        double getValue(double x, double y, double z);

        double maxValue();
    }

    final class NoiseHolder implements NoiseSampler {
        private final NoiseSampler noise;

        public NoiseHolder(NormalNoise noise) {
            this(noise == null ? null : new NormalNoiseAdapter(noise));
        }

        public NoiseHolder(NoiseSampler noise) {
            this.noise = noise;
        }

        @Override
        public double getValue(double x, double y, double z) {
            return noise == null ? 0.0 : noise.getValue(x, y, z);
        }

        @Override
        public double maxValue() {
            return noise == null ? 2.0 : noise.maxValue();
        }

        private record NormalNoiseAdapter(NormalNoise noise) implements NoiseSampler {
            @Override
            public double getValue(double x, double y, double z) {
                return noise.getValue(x, y, z);
            }

            @Override
            public double maxValue() {
                return noise.getMax();
            }
        }

    }

    interface SimpleFunction extends DensityFunction {
        @Override
        default void fillArray(double[] output, ContextProvider contextProvider) {
            contextProvider.fillAllDirectly(output, this);
        }
    }

    record SinglePointContext(int blockX, int blockY, int blockZ) implements FunctionContext {
    }

    final class MutableFunctionContext implements FunctionContext {
        private int blockX;
        private int blockY;
        private int blockZ;

        public MutableFunctionContext set(int blockX, int blockY, int blockZ) {
            this.blockX = blockX;
            this.blockY = blockY;
            this.blockZ = blockZ;
            return this;
        }

        @Override
        public int blockX() {
            return blockX;
        }

        @Override
        public int blockY() {
            return blockY;
        }

        @Override
        public int blockZ() {
            return blockZ;
        }
    }

}
