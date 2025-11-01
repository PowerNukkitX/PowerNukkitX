package cn.nukkit.level.generator.noise.minecraft.perlin;

import cn.nukkit.level.generator.noise.minecraft.noise.NoiseSampler;
import cn.nukkit.level.generator.noise.minecraft.utils.MathHelper;
import cn.nukkit.level.generator.noise.minecraft.utils.Pair;
import cn.nukkit.level.generator.noise.minecraft.utils.Quad;
import cn.nukkit.utils.random.RandomSourceProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class OctavePerlinNoiseSampler implements NoiseSampler {

    public final double lacunarity;
    public final double persistence;
    private final PerlinNoiseSampler[] octaveSamplers;
    private final List<Double> amplitudes;

    public OctavePerlinNoiseSampler(RandomSourceProvider random, int octaveCount) {
        this.amplitudes = null;
        this.octaveSamplers = new PerlinNoiseSampler[octaveCount];
        for(int i = 0; i < octaveCount; i++) {
            this.octaveSamplers[i] = new PerlinNoiseSampler(random);
        }
        this.lacunarity = 1.0;
        this.persistence = 1.0;
    }

    public int getCount() {
        return this.octaveSamplers.length;
    }

    public OctavePerlinNoiseSampler(RandomSourceProvider rand, IntStream octaves) {
        this(rand, octaves.boxed().collect(Collectors.toList()));
    }

    public static Pair<Integer, List<Double>> makeAmplitudes(List<Integer> octaves) {
        Quad<Integer, Integer, Integer, List<Integer>> processedOctaves = processOctaves(octaves);
        int start = processedOctaves.getFirst();
        List<Double> octavePlaces = new ArrayList<>();
        for(int octave : processedOctaves.getFourth()) {
            octavePlaces.set(octave + start, 1.0D);
        }
        return new Pair<>(start, octavePlaces);
    }

    public OctavePerlinNoiseSampler(RandomSourceProvider rand, int firstOctave, List<Double> amplitudes) {
        this(rand, new Pair<>(firstOctave, amplitudes));
    }

    public OctavePerlinNoiseSampler(RandomSourceProvider rand, Pair<Integer, List<Double>> octaveParams) {
        // this is for 1.16.2+ nether
        this.amplitudes = octaveParams.getSecond();
        PerlinNoiseSampler perlin = new PerlinNoiseSampler(rand);
        int length = this.amplitudes.size();
        int start = octaveParams.getFirst();
        this.octaveSamplers = new PerlinNoiseSampler[length];
        if(start >= 0 && start < length) {
            double d0 = this.amplitudes.get(start);
            if(d0 != 0.0D) {
                this.octaveSamplers[start] = perlin;
            }
        }

        for(int idx = start - 1; idx >= 0; --idx) {
            if(idx < length) {
                double d1 = this.amplitudes.get(idx);
                if(d1 != 0.0D) {
                    this.octaveSamplers[idx] = new PerlinNoiseSampler(rand);
                } else {
                    rand.setSeed(SKIP_262.nextSeed(rand.getSeed()));
                }
            } else {
                rand.setSeed(SKIP_262.nextSeed(rand.getSeed()));
            }
        }

        if(start < length - 1) {
            long noiseSeed = (long)(perlin.sample(0.0D, 0.0D, 0.0D, 0.0D, 0.0D) * (double)9.223372E18F);
            rand.setSeed(noiseSeed);

            for(int l = start + 1; l < length; ++l) {
                if(l >= 0) {
                    double d2 = this.amplitudes.get(l);
                    if(d2 != 0.0D) {
                        this.octaveSamplers[l] = new PerlinNoiseSampler(rand);
                    } else {
                        rand.setSeed(SKIP_262.nextSeed(rand.getSeed()));
                    }
                } else {
                    rand.setSeed(SKIP_262.nextSeed(rand.getSeed()));
                }
            }
        }

        this.persistence = Math.pow(2.0D, -start);
        this.lacunarity = Math.pow(2.0D, length - 1) / (Math.pow(2.0D, length) - 1.0D);
    }

    private static Quad<Integer, Integer, Integer, List<Integer>> processOctaves(List<Integer> octaves) {
        octaves = octaves.stream().sorted(Integer::compareTo).collect(Collectors.toList());

        if(octaves.isEmpty()) {
            throw new IllegalArgumentException("Need some octaves!");
        }

        int start = -octaves.get(0);
        int end = octaves.get(octaves.size() - 1);
        int length = start + end + 1;
        if(length < 1) {
            throw new IllegalArgumentException("Total number of octaves needs to be >= 1");
        }
        return new Quad<>(start, end, length, octaves);
    }

    public OctavePerlinNoiseSampler(RandomSourceProvider rand, List<Integer> octaves) {
        // this is the old method
        this.amplitudes = null;

        Quad<Integer, Integer, Integer, List<Integer>> processedOctaves = processOctaves(octaves);
        int end = processedOctaves.getSecond();
        int length = processedOctaves.getThird();

        PerlinNoiseSampler perlin = new PerlinNoiseSampler(rand);

        this.octaveSamplers = new PerlinNoiseSampler[length];

        if(end >= 0 && end < length && octaves.contains(0)) {
            this.octaveSamplers[end] = perlin;
        }

        for(int idx = end + 1; idx < length; ++idx) {
            if(idx >= 0 && octaves.contains(end - idx)) {
                this.octaveSamplers[idx] = new PerlinNoiseSampler(rand);
            } else {
                rand.setSeed(SKIP_262.nextSeed(rand.getSeed()));
            }
        }

        if(end > 0) {
            long noiseSeed = (long)(perlin.sample(0.0D, 0.0D, 0.0D, 0.0D, 0.0D) * 9.223372036854776E18D);
            rand.setSeed(noiseSeed);
            for(int index = end - 1; index >= 0; --index) {
                if(index < length && octaves.contains(end - index)) {
                    this.octaveSamplers[index] = new PerlinNoiseSampler(rand);
                } else {
                    rand.setSeed(SKIP_262.nextSeed(rand.getSeed()));
                }
            }
        }

        this.persistence = Math.pow(2.0D, end);
        this.lacunarity = 1.0D / (Math.pow(2.0D, length) - 1.0D);
    }

    public double sample(double x, double y, double z) {
        return this.sample(x, y, z, 0.0D, 0.0D, false);
    }

    public double sample(double x, double y, double z, double yAmplification, double minY, boolean useDefaultY) {
        double noise = 0.0D;
        // contribution of each octaves to the final noise, diminished by a factor of 2 (or increased by factor of 0.5)
        double persistence = this.persistence;
        // distance between octaves, increased for each by a factor of 2
        double lacunarity = this.lacunarity;

        for(int idx = 0; idx < this.octaveSamplers.length; idx++) {
            PerlinNoiseSampler sampler = this.octaveSamplers[idx];
            if(sampler != null) {
                double sample = sampler.sample(MathHelper.maintainPrecision(x * persistence),
                        useDefaultY ? -sampler.originY : MathHelper.maintainPrecision(y * persistence),
                        MathHelper.maintainPrecision(z * persistence),
                        yAmplification * persistence,
                        minY * persistence);
                noise += this.amplitudes.get(idx) * sample * lacunarity;
            }
            persistence /= 2.0D;
            lacunarity *= 2.0D;
        }
        return noise;
    }

    public PerlinNoiseSampler getOctave(int octave) {
        return this.octaveSamplers[octave];
    }

    @Override
    public double sample(double x, double y, double yAmplification, double minY) {
        return this.sample(x, y, 0.0D, yAmplification, minY, false);
    }
}