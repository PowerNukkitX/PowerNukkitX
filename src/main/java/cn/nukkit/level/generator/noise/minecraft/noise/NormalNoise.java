package cn.nukkit.level.generator.noise.minecraft.noise;

import cn.nukkit.utils.random.RandomSourceProvider;

public class NormalNoise {

    private static final double INPUT_FACTOR = 1.0181268882175227D;
    private final PerlinNoise first;
    private final PerlinNoise second;
    private final double valueFactor;
    private final double maxValue;

    public NormalNoise(RandomSourceProvider random, int firstOctave, float[] amplitudes) {
        double[] octaveAmplitudes = new double[amplitudes.length];
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;

        for (int i = 0; i < amplitudes.length; i++) {
            double amplitude = amplitudes[i];
            octaveAmplitudes[i] = amplitude;
            if (amplitude != 0.0D) {
                min = Math.min(min, i);
                max = Math.max(max, i);
            }
        }

        this.first = new PerlinNoise(random, firstOctave, octaveAmplitudes);
        this.second = new PerlinNoise(random, firstOctave, octaveAmplitudes);
        this.valueFactor = 1.0D / 6.0D / expectedDeviation(max - min);
        this.maxValue = (this.first.maxValue() + this.second.maxValue()) * this.valueFactor;
    }

    public float getValue(double x, double y, double z) {
        double x2 = x * INPUT_FACTOR;
        double y2 = y * INPUT_FACTOR;
        double z2 = z * INPUT_FACTOR;
        return (float) ((this.first.getValue(x, y, z) + this.second.getValue(x2, y2, z2)) * this.valueFactor);
    }

    public double getMax() {
        return this.maxValue;
    }

    private static double expectedDeviation(int octaveSpan) {
        return 0.1D * (1.0D + 1.0D / (octaveSpan + 1.0D));
    }
}
