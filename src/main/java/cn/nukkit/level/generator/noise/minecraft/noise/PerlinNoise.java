package cn.nukkit.level.generator.noise.minecraft.noise;

import cn.nukkit.level.generator.noise.minecraft.perlin.PerlinNoiseSampler;
import cn.nukkit.level.generator.noise.minecraft.utils.MathHelper;
import cn.nukkit.utils.random.RandomSourceProvider;

public class PerlinNoise {
    private final PerlinNoiseSampler[] noiseLevels;
    private final double[] amplitudes;
    private final double lowestFreqValueFactor;
    private final double lowestFreqInputFactor;
    private final double maxValue;

    public PerlinNoise(RandomSourceProvider random, int firstOctave, double[] amplitudes) {
        this.amplitudes = amplitudes;
        int octaves = amplitudes.length;
        int zeroOctaveIndex = -firstOctave;
        this.noiseLevels = new PerlinNoiseSampler[octaves];

        PerlinNoiseSampler zeroOctave = new PerlinNoiseSampler(random);
        if (zeroOctaveIndex >= 0 && zeroOctaveIndex < octaves) {
            double amplitude = amplitudes[zeroOctaveIndex];
            if (amplitude != 0.0D) {
                this.noiseLevels[zeroOctaveIndex] = zeroOctave;
            }
        }

        for (int ix = zeroOctaveIndex - 1; ix >= 0; --ix) {
            if (ix < octaves) {
                if (amplitudes[ix] != 0.0D) {
                    this.noiseLevels[ix] = new PerlinNoiseSampler(random);
                } else {
                    skipOctave(random);
                }
            } else {
                skipOctave(random);
            }
        }

        this.lowestFreqInputFactor = Math.pow(2.0D, -zeroOctaveIndex);
        this.lowestFreqValueFactor = Math.pow(2.0D, octaves - 1) / (Math.pow(2.0D, octaves) - 1.0D);
        this.maxValue = this.edgeValue(2.0D);
    }

    private static void skipOctave(RandomSourceProvider random) {
        random.setSeed(NoiseSampler.SKIP_262.nextSeed(random.getSeed()));
    }

    public double getValue(double x, double y, double z) {
        double value = 0.0D;
        double factor = this.lowestFreqInputFactor;
        double valueFactor = this.lowestFreqValueFactor;

        for (int i = 0; i < this.noiseLevels.length; i++) {
            PerlinNoiseSampler noise = this.noiseLevels[i];
            if (noise != null) {
                double noiseValue = noise.sample(
                        MathHelper.maintainPrecision(x * factor),
                        MathHelper.maintainPrecision(y * factor),
                        MathHelper.maintainPrecision(z * factor),
                        0.0D,
                        0.0D
                );
                value += this.amplitudes[i] * noiseValue * valueFactor;
            }

            factor *= 2.0D;
            valueFactor /= 2.0D;
        }

        return value;
    }

    private double edgeValue(double noiseValue) {
        double value = 0.0D;
        double valueFactor = this.lowestFreqValueFactor;

        for (int i = 0; i < this.noiseLevels.length; i++) {
            PerlinNoiseSampler noise = this.noiseLevels[i];
            if (noise != null) {
                value += this.amplitudes[i] * noiseValue * valueFactor;
            }
            valueFactor /= 2.0D;
        }

        return value;
    }

    public double maxValue() {
        return this.maxValue;
    }
}
