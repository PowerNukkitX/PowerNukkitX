package cn.nukkit.level.generator.noise.minecraft.simplex;

import cn.nukkit.utils.random.NukkitRandom;
import cn.nukkit.utils.random.RandomSourceProvider;

public class SimplexNoise {

    private final int firstOctave;
    private final float[] amplitudes;
    private final SimplexNoiseSampler[] noiseLevels;
    private final int levels;

    private final float lowestFreqValueFactor;
    private final float lowestFreqInputFactor;
    private final float maxValue;

    public SimplexNoise(RandomSourceProvider random, int firstOctave, float[] amplitudes) {
        this.firstOctave = firstOctave;
        this.amplitudes = amplitudes;
        this.levels = amplitudes.length;
        this.noiseLevels = new SimplexNoiseSampler[this.levels];
        this.lowestFreqInputFactor = (float) Math.pow(2, firstOctave);
        this.lowestFreqValueFactor = (float) (Math.pow(2, this.levels - 1) / (Math.pow(2, this.levels) - 1));
        for (int i = 0; i < this.levels; ++i) {
            if(this.amplitudes[i] != 0.0f) {
                this.noiseLevels[i] = new SimplexNoiseSampler(new NukkitRandom(random.nextLong() + ("octave_" + (this.firstOctave + i)).hashCode()));
            }
        }
        this.maxValue = this.edgeValue(2d);
    }

    public float getValue(double x, double y, double z) {

        float d0 = 0.0f;
        float d1 = this.lowestFreqInputFactor;
        float d2 = this.lowestFreqValueFactor;

        for (int i = 0; i < this.noiseLevels.length; i++) {
            SimplexNoiseSampler noise = this.noiseLevels[i];
            if (noise != null) {
                float d3 = (float) noise.sample3D(
                        wrap(x * d1),
                        wrap(y * d1),
                        wrap(z * d1)
                );
                d0 += this.amplitudes[i] * d3 * d2;
            }

            d1 *= 2.0;
            d2 /= 2.0;
        }

        return d0;
    }


    public static float wrap(double p_75407_) {
        return (float) (p_75407_ - Math.floor(p_75407_ / 3.3554432E7 + 0.5) * 3.3554432E7);
    }

    private float edgeValue(double input) {
        float cumulativeSum = 0f;
        float octaveContributionFactor = this.lowestFreqValueFactor;

        for (int i = 0; i < this.noiseLevels.length; i++) {
            SimplexNoiseSampler octave = this.noiseLevels[i];
            if (octave != null) {
                cumulativeSum += this.amplitudes[i] * input * octaveContributionFactor;
            }
            octaveContributionFactor /= 2f;
        }
        return cumulativeSum;
    }

    public float getMax() {
        return this.maxValue;
    }
}
