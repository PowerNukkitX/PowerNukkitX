package cn.nukkit.level.generator.noise.f.vanilla;

import cn.nukkit.utils.random.NukkitRandom;
import cn.nukkit.utils.random.RandomSourceProvider;

public class NoiseGeneratorPerlinF {

    private final int firstOctave;
    private final float[] amplitudes;
    private final NoiseGeneratorSimplexF[] noiseLevels;
    private final int levels;

    private final float lowestFreqValueFactor;
    private final float lowestFreqInputFactor;
    private final float maxValue;

    public NoiseGeneratorPerlinF(RandomSourceProvider random, int firstOctave, float[] amplitudes) {
        this.firstOctave = firstOctave;
        this.amplitudes = amplitudes;
        this.levels = amplitudes.length;
        this.noiseLevels = new NoiseGeneratorSimplexF[this.levels];
        this.lowestFreqInputFactor = (float) Math.pow(2, firstOctave);
        this.lowestFreqValueFactor = (float) (Math.pow(2, this.levels - 1) / (Math.pow(2, this.levels) - 1));
        for (int i = 0; i < this.levels; ++i) {
            if(this.amplitudes[i] != 0.0f) {
                this.noiseLevels[i] = new NoiseGeneratorSimplexF(new NukkitRandom(random.nextLong() + ("octave_" + (this.firstOctave + i)).hashCode()));
            }
        }
        this.maxValue = this.edgeValue(2d);
    }

    public float getValue(double x, double y, double z) {

        float d0 = 0.0f;
        float d1 = this.lowestFreqInputFactor;
        float d2 = this.lowestFreqValueFactor;

        for (int i = 0; i < this.noiseLevels.length; i++) {
            NoiseGeneratorSimplexF noise = this.noiseLevels[i];
            if (noise != null) {
                double d3 = noise.getValue(
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
    public float[] getRegion(float[] p_151599_1_, float p_151599_2_, float p_151599_4_, int p_151599_6_, int p_151599_7_, float p_151599_8_, float p_151599_10_, float p_151599_12_) {
        return this.getRegion(p_151599_1_, p_151599_2_, p_151599_4_, p_151599_6_, p_151599_7_, p_151599_8_, p_151599_10_, p_151599_12_, 0.5f);
    }

    public float[] getRegion(float[] p_151600_1_, float p_151600_2_, float p_151600_4_, int p_151600_6_, int p_151600_7_, float p_151600_8_, float p_151600_10_, float p_151600_12_, float p_151600_14_) {
        if (p_151600_1_ != null && p_151600_1_.length >= p_151600_6_ * p_151600_7_) {
            for (int i = 0; i < p_151600_1_.length; ++i) {
                p_151600_1_[i] = 0.0f;
            }
        } else {
            p_151600_1_ = new float[p_151600_6_ * p_151600_7_];
        }

        float d1 = 1.0f;
        float d0 = 1.0f;

        for (int j = 0; j < this.levels; ++j) {
            this.noiseLevels[j].add(
                    p_151600_1_,                 // buffer
                    p_151600_2_,                 // x
                    0.0f,                        // z fest auf 0
                    p_151600_4_,                 // y
                    p_151600_6_,                 // sizeX
                    p_151600_7_,                 // sizeY
                    1,                           // sizeZ = 1 → nur eine Schicht
                    p_151600_8_ * d0 * d1,       // scaleX
                    p_151600_10_ * d0 * d1,      // scaleY
                    1.0f,                        // scaleZ = 1.0f → neutral
                    0.55f / d1                   // amplitude
            );
            d0 *= p_151600_12_;
            d1 *= p_151600_14_;
        }

        return p_151600_1_;
    }

    private float edgeValue(double input) {
        float cumulativeSum = 0f;
        float octaveContributionFactor = this.lowestFreqValueFactor;

        for (int i = 0; i < this.noiseLevels.length; i++) {
            NoiseGeneratorSimplexF octave = this.noiseLevels[i];
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
