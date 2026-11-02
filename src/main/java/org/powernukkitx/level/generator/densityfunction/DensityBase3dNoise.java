package org.powernukkitx.level.generator.densityfunction;

import org.powernukkitx.level.generator.noise.minecraft.perlin.OctavePerlinNoiseSampler;
import org.powernukkitx.level.generator.noise.minecraft.utils.MathHelper;
import org.powernukkitx.math.NukkitMath;
import org.powernukkitx.utils.random.Xoroshiro128;
import org.powernukkitx.utils.random.Xoroshiro128PositionalFactory;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Buddelbubi
 * @since 2026/04/02
 * @implNote <a href="https://github.com/misode/mcmeta/blob/data/data/minecraft/worldgen/density_function/overworld/base_3d_noise.json">Source</a>
 */
public final class DensityBase3dNoise {

    private static final double MAIN_NOISE_DIVISOR = 10.0;
    private static final double LIMIT_NOISE_DIVISOR = 512.0;
    private static final double RESULT_DIVISOR = 128.0;
    private static final double BASE_SCALE = 684.412;
    private static final List<Integer> LIMIT_OCTAVES = descendingOctaves(-15, 0);
    private static final List<Integer> MAIN_OCTAVES = descendingOctaves(-7, 0);

    /** Name of the stream from which all three Perlin sets are built one after another. */
    private static final String TERRAIN_NOISE_NAME = "minecraft:terrain";

    private DensityBase3dNoise() {
    }

    /**
     * @param random a single stream ({@code noiseFactory.hashOf("minecraft:terrain")}); min-limit (16 octaves),
     *               max-limit (16) and main (8) consume it in exactly this order.
     */
    public static DensityFunction oldBlendedNoise(
            Xoroshiro128 random,
            double xzScale,
            double yScale,
            double xzFactor,
            double yFactor,
            double smearScaleMultiplier
    ) {
        // Order matters: min, max, main. Deliberately no fork(): all three read from the same stream.
        OctavePerlinNoiseSampler minLimit = new OctavePerlinNoiseSampler(random, LIMIT_OCTAVES);
        OctavePerlinNoiseSampler maxLimit = new OctavePerlinNoiseSampler(random, LIMIT_OCTAVES);
        OctavePerlinNoiseSampler main = new OctavePerlinNoiseSampler(random, MAIN_OCTAVES);
        return new OldBlendedNoise(
                minLimit,
                maxLimit,
                main,
                xzScale,
                yScale,
                xzFactor,
                yFactor,
                smearScaleMultiplier
        );
    }

    public static DensityFunction overworld(Xoroshiro128PositionalFactory noiseFactory) {
        return oldBlendedNoise(noiseFactory.hashOf(TERRAIN_NOISE_NAME), 0.25, 0.125, 80.0, 160.0, 8.0);
    }

    public static DensityFunction nether(Xoroshiro128PositionalFactory noiseFactory) {
        return oldBlendedNoise(noiseFactory.hashOf(TERRAIN_NOISE_NAME), 0.25, 0.375, 80.0, 60.0, 8.0);
    }

    private static List<Integer> descendingOctaves(int minInclusive, int maxInclusive) {
        return IntStream.rangeClosed(minInclusive, maxInclusive)
                .boxed()
                .sorted((a, b) -> Integer.compare(b, a))
                .collect(Collectors.toList());
    }

    private record OldBlendedNoise(
            OctavePerlinNoiseSampler minLimitNoise,
            OctavePerlinNoiseSampler maxLimitNoise,
            OctavePerlinNoiseSampler mainNoise,
            double xzScale,
            double yScale,
            double xzFactor,
            double yFactor,
            double smearScaleMultiplier
    ) implements DensityFunction {
        @Override
        public double compute(FunctionContext context) {
            double scaledXZ = BASE_SCALE * xzScale;
            double scaledY = BASE_SCALE * yScale;
            // Smear of the limit noises and (divided by yFactor) of the main noise
            double smear = scaledY * smearScaleMultiplier;
            double mainSmear = smear / yFactor;

            double x = context.blockX() * scaledXZ;
            double y = context.blockY() * scaledY;
            double z = context.blockZ() * scaledXZ;

            double mainX = x / xzFactor;
            double mainY = y / yFactor;
            double mainZ = z / xzFactor;

            double mainValue = 0.0;
            double frequency = 1.0;
            for (int octave = 0; octave < mainNoise.getCount(); octave++) {
                var sampler = mainNoise.getOctave(octave);
                if (sampler != null) {
                    mainValue += sampler.sample(
                            MathHelper.maintainPrecision(mainX * frequency),
                            MathHelper.maintainPrecision(mainY * frequency),
                            MathHelper.maintainPrecision(mainZ * frequency),
                            mainSmear * frequency,
                            mainY * frequency
                    ) / frequency;
                }
                frequency /= 2.0;
            }

            double blend = NukkitMath.clamp(mainValue / MAIN_NOISE_DIVISOR + 1.0, 0.0, 2.0) * 0.5;
            boolean useOnlyMax = blend >= 1.0;
            boolean useOnlyMin = blend <= 0.0;

            double minValue = 0.0;
            double maxValue = 0.0;
            frequency = 1.0;

            int octaveCount = Math.min(minLimitNoise.getCount(), maxLimitNoise.getCount());
            for (int octave = 0; octave < octaveCount; octave++) {
                double sampleX = MathHelper.maintainPrecision(x * frequency);
                double sampleY = MathHelper.maintainPrecision(y * frequency);
                double sampleZ = MathHelper.maintainPrecision(z * frequency);
                double sampleSmear = smear * frequency;
                double yLimit = y * frequency; // not wrapped, as in Vanilla

                if (!useOnlyMax) {
                    var sampler = minLimitNoise.getOctave(octave);
                    if (sampler != null) {
                        minValue += sampler.sample(sampleX, sampleY, sampleZ, sampleSmear, yLimit) / frequency;
                    }
                }

                if (!useOnlyMin) {
                    var sampler = maxLimitNoise.getOctave(octave);
                    if (sampler != null) {
                        maxValue += sampler.sample(sampleX, sampleY, sampleZ, sampleSmear, yLimit) / frequency;
                    }
                }

                frequency /= 2.0;
            }

            double lower = minValue / LIMIT_NOISE_DIVISOR;
            double upper = maxValue / LIMIT_NOISE_DIVISOR;
            return lerp(lower, upper, blend) / RESULT_DIVISOR;
        }

        @Override
        public void fillArray(double[] output, ContextProvider contextProvider) {
            contextProvider.fillAllDirectly(output, this);
        }

        @Override
        public double minValue() {
            return -1.5;
        }

        @Override
        public double maxValue() {
            return 1.5;
        }

        private static double lerp(double first, double second, double alpha) {
            return first + alpha * (second - first);
        }
    }
}
