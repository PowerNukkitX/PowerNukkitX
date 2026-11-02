package org.powernukkitx.level.generator.noise.minecraft.noise;

import org.jetbrains.annotations.NotNull;
import org.powernukkitx.level.generator.noise.minecraft.perlin.PerlinNoiseSampler;
import org.powernukkitx.level.generator.noise.minecraft.utils.MathHelper;
import org.powernukkitx.utils.random.RandomSourceProvider;
import org.powernukkitx.utils.random.Xoroshiro128;
import org.powernukkitx.utils.random.Xoroshiro128PositionalFactory;

public class NormalNoise {

    private static final double INPUT_FACTOR = 1.0181268882175227D;
    private final PerlinNoise first;
    private final PerlinNoise second;
    private final double valueFactor;
    private final double maxValue;
    /** {@code true} for {@link #create}: summands in the order octave0.first, octave0.second, octave1.first, ... */
    private final boolean interleaved;

    /**
     * Legacy seeding that draws both Perlin sets from one sequential random. The result does not match Vanilla;
     * use {@link #create(Xoroshiro128PositionalFactory, String, int, float[])} for Vanilla parity.
     */
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
        this.interleaved = false;
    }

    private NormalNoise(PerlinNoise first, PerlinNoise second, double valueFactor) {
        this.first = first;
        this.second = second;
        this.valueFactor = valueFactor;
        this.maxValue = (first.maxValue() + second.maxValue()) * valueFactor;
        this.interleaved = true;
    }

    /**
     * Creates a named noise like Vanilla: the stream is {@code positional.hashOf(name)}, e.g.
     * {@code create(positional, "minecraft:aquifer_barrier", -3, new float[]{1f})}.
     * The result does not depend on the order in which noises are created.
     *
     * @param positional  world factory: {@code Xoroshiro128.ofSeed(worldSeed).forkPositional()}
     * @param name        the Vanilla noise name; a wrong name silently gives a different noise
     * @param firstOctave index of the first octave (negative, e.g. -6)
     * @param amplitudes  octave amplitudes, at least one non-zero
     * @return the noise
     */
    public static @NotNull NormalNoise create(@NotNull Xoroshiro128PositionalFactory positional, @NotNull String name, int firstOctave, @NotNull float[] amplitudes) {
        return create(positional.hashOf(name), firstOctave, amplitudes);
    }

    /**
     * Creates the noise from an already derived stream. The stream yields two positional factories in a row:
     * the first for {@code first}, the second for {@code second}. Each octave is taken from its factory with
     * {@code hashOf("octave_" + (firstOctave + i))}. Advances {@code random} by four steps.
     *
     * @param random      the derived stream, e.g. {@code positional.hashOf(name)}
     * @param firstOctave index of the first octave (negative, e.g. -6)
     * @param amplitudes  octave amplitudes, at least one non-zero
     * @return the noise
     */
    public static @NotNull NormalNoise create(@NotNull Xoroshiro128 random, int firstOctave, @NotNull float[] amplitudes) {
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

        // Order matters: "first" is drawn before "second" from the same stream.
        Xoroshiro128PositionalFactory firstFactory = random.forkPositional();
        Xoroshiro128PositionalFactory secondFactory = random.forkPositional();

        PerlinNoise first = new PerlinNoise(firstFactory, firstOctave, octaveAmplitudes);
        PerlinNoise second = new PerlinNoise(secondFactory, firstOctave, octaveAmplitudes);
        double valueFactor = 1.0D / 6.0D / expectedDeviation(max - min);
        return new NormalNoise(first, second, valueFactor);
    }

    public float getValue(double x, double y, double z) {
        double x2 = x * INPUT_FACTOR;
        double y2 = y * INPUT_FACTOR;
        double z2 = z * INPUT_FACTOR;
        if (this.interleaved) {
            return getValueInterleaved(x, y, z, x2, y2, z2);
        }
        return (float) ((this.first.getValue(x, y, z) + this.second.getValue(x2, y2, z2)) * this.valueFactor);
    }

    /**
     * Sums {@code first}, then {@code second} per octave into one accumulator this matters
     * as soon as Perlin is switched to float.
     */
    private float getValueInterleaved(double x, double y, double z, double x2, double y2, double z2) {
        double value = 0.0D;
        double factor = this.first.lowestFreqInputFactor();
        double valueFactor = this.first.lowestFreqValueFactor();

        for (int i = 0; i < this.first.octaveCount(); i++) {
            PerlinNoiseSampler a = this.first.octave(i);
            if (a != null) {
                double noiseValue = a.sample(
                    MathHelper.maintainPrecision(x * factor),
                    MathHelper.maintainPrecision(y * factor),
                    MathHelper.maintainPrecision(z * factor),
                    0.0D,
                    0.0D
                );
                value += this.first.amplitude(i) * noiseValue * valueFactor;
            }

            PerlinNoiseSampler b = this.second.octave(i);
            if (b != null) {
                double noiseValue = b.sample(
                    MathHelper.maintainPrecision(x2 * factor),
                    MathHelper.maintainPrecision(y2 * factor),
                    MathHelper.maintainPrecision(z2 * factor),
                    0.0D,
                    0.0D
                );
                value += this.second.amplitude(i) * noiseValue * valueFactor;
            }

            factor *= 2.0D;
            valueFactor /= 2.0D;
        }

        return (float) (value * this.valueFactor);
    }

    public double getMax() {
        return this.maxValue;
    }

    private static double expectedDeviation(int octaveSpan) {
        return 0.1D * (1.0D + 1.0D / (octaveSpan + 1.0D));
    }
}
