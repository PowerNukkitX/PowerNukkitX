package cn.nukkit.level.generator.noise.minecraft.simplex;

import cn.nukkit.level.generator.noise.minecraft.noise.NoiseSampler;
import cn.nukkit.utils.random.RandomSourceProvider;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class OctaveSimplexNoiseSampler implements NoiseSampler {

    public final double lacunarity;
    public final double persistence;
    private final SimplexNoiseSampler[] octaveSamplers;

    public OctaveSimplexNoiseSampler(RandomSourceProvider random, int octaveCount) {
        this.octaveSamplers = new SimplexNoiseSampler[octaveCount];
        for(int i = 0; i < octaveCount; i++) {
            this.octaveSamplers[i] = new SimplexNoiseSampler(random);
        }
        this.lacunarity = 1.0;
        this.persistence = 1.0;
    }

    public OctaveSimplexNoiseSampler(RandomSourceProvider rand, IntStream octaves) {
        this(rand, octaves.boxed().collect(Collectors.toList()));
    }

    public OctaveSimplexNoiseSampler(RandomSourceProvider rand, List<Integer> octaves) {
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

        SimplexNoiseSampler simplex = new SimplexNoiseSampler(rand);

        this.octaveSamplers = new SimplexNoiseSampler[length];

        if(end >= 0 && end < length && octaves.contains(0)) {
            this.octaveSamplers[end] = simplex;
        }

        for(int idx = end + 1; idx < length; ++idx) {
            if(idx >= 0 && octaves.contains(end - idx)) {
                this.octaveSamplers[idx] = new SimplexNoiseSampler(rand);
            } else {
                rand.setSeed(SKIP_262.nextSeed(rand.getSeed()));
            }
        }

        if(end > 0) {
            long noiseSeed = (long)(simplex.sample3D(simplex.originX, simplex.originY, simplex.originZ) * 9.223372036854776E18D);
            rand.setSeed(noiseSeed);
            for(int index = end - 1; index >= 0; --index) {
                if(index < length && octaves.contains(end - index)) {
                    this.octaveSamplers[index] = new SimplexNoiseSampler(rand);
                } else {
                    rand.setSeed(SKIP_262.nextSeed(rand.getSeed()));
                }
            }
        }

        this.persistence = Math.pow(2.0D, end);
        this.lacunarity = 1.0D / (Math.pow(2.0D, length) - 1.0D);
    }

    public double sample(double x, double y) {
        return this.sample(x, y, false);
    }

    public double sample(double x, double y, boolean useRandomOffset) {
        double noise = 0.0D;
        // contribution of each octaves to the final noise, diminished by a factor of 2 (or increased by factor of 0.5)
        double persistence = this.persistence;
        double lacunarity = this.lacunarity;
        for(SimplexNoiseSampler sampler : this.octaveSamplers) {
            if(sampler != null) {
                noise += sampler.sample2D(
                        x * persistence + (useRandomOffset ? sampler.originX : 0.0D),
                        y * persistence + (useRandomOffset ? sampler.originY : 0.0D)
                ) * lacunarity;
            }
            persistence /= 2.0D;
            lacunarity *= 2.0D;

        }
        return noise;
    }

    @Override
    public double sample(double x, double y, double notUsed, double notUsed2) {
        return this.sample(x, y, true) * 0.55D;
    }
}
