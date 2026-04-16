package cn.nukkit.level.generator.densityfunction;

import cn.nukkit.level.generator.noise.minecraft.perlin.OctavePerlinNoiseSampler;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.utils.random.RandomSourceProvider;

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

    private DensityBase3dNoise() {
    }

    public static DensityFunction oldBlendedNoise(
            RandomSourceProvider random,
            double xzScale,
            double yScale,
            double xzFactor,
            double yFactor,
            double smearScaleMultiplier
    ) {
        return new OldBlendedNoise(
                new OctavePerlinNoiseSampler(random.fork(), LIMIT_OCTAVES),
                new OctavePerlinNoiseSampler(random.fork(), LIMIT_OCTAVES),
                new OctavePerlinNoiseSampler(random.fork(), MAIN_OCTAVES),
                xzScale,
                yScale,
                xzFactor,
                yFactor,
                smearScaleMultiplier
        );
    }

    public static DensityFunction overworld(RandomSourceProvider random) {
        return oldBlendedNoise(random, 0.25, 0.125, 80.0, 160.0, 8.0);
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
            double mainScaledXZ = scaledXZ / xzFactor;
            double mainScaledY = scaledY / yFactor;
            double smearScale = scaledY * smearScaleMultiplier;

            double x = context.blockX();
            double y = context.blockY();
            double z = context.blockZ();

            double mainValue = 0.0;
            double frequency = 1.0;
            for (int octave = 0; octave < mainNoise.getCount(); octave++) {
                var sampler = mainNoise.getOctave(octave);
                if (sampler != null) {
                    mainValue += sampler.sample(
                            x * mainScaledXZ * frequency,
                            y * mainScaledY * frequency,
                            z * mainScaledXZ * frequency,
                            smearScale * frequency,
                            y * mainScaledY * frequency
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
                double sampleX = x * scaledXZ * frequency;
                double sampleY = y * scaledY * frequency;
                double sampleZ = z * scaledXZ * frequency;
                double sampleSmear = smearScale * frequency;

                if (!useOnlyMin) {
                    var sampler = minLimitNoise.getOctave(octave);
                    if (sampler != null) {
                        minValue += sampler.sample(sampleX, sampleY, sampleZ, sampleSmear, sampleY) / frequency;
                    }
                }

                if (!useOnlyMax) {
                    var sampler = maxLimitNoise.getOctave(octave);
                    if (sampler != null) {
                        maxValue += sampler.sample(sampleX, sampleY, sampleZ, sampleSmear, sampleY) / frequency;
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
