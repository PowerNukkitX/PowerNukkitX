package cn.nukkit.utils.random;

import org.apache.commons.rng.RestorableUniformRandomProvider;
import org.apache.commons.rng.sampling.distribution.ContinuousSampler;
import org.apache.commons.rng.sampling.distribution.GaussianSampler;
import org.apache.commons.rng.sampling.distribution.ZigguratSampler;
import org.apache.commons.rng.simple.RandomSource;

/**
 * @author Angelic47 (Nukkit Project)
 */
public class NukkitRandom implements RandomSourceProvider {
    final RestorableUniformRandomProvider provider;
    final ContinuousSampler sampler;

    public NukkitRandom() {
        provider = RandomSource.MT.create();
        sampler = GaussianSampler.of(ZigguratSampler.NormalizedGaussian.of(RandomSource.ISAAC.create()),
                0, 0.33333);
    }

    public NukkitRandom(long seeds) {
        provider = RandomSource.MT.create(seeds);
        sampler = GaussianSampler.of(ZigguratSampler.NormalizedGaussian.of(RandomSource.ISAAC.create()),
                0, 0.33333);
    }

    @Override
    public RandomSourceProvider fork() {
        return new NukkitRandom(nextLong());
    }

    @Override
    public int nextInt(int min, int max) {
        return provider.nextInt(min, max + 1);
    }

    public int nextRange(int min, int max) {
        return provider.nextInt(min, max + 1);
    }

    public int nextBoundedInt(int bound) {
        return nextInt(bound);
    }

    @Override
    public int nextInt(int max) {
        return provider.nextInt(max + 1);
    }

    @Override
    public int nextInt() {
        return provider.nextInt();
    }

    @Override
    public long nextLong() {
        return provider.nextLong();
    }

    @Override
    public double nextDouble() {
        return provider.nextDouble();
    }

    @Override
    public double nextGaussian() {
        double sample = sampler.sample();
        return Math.min(1, Math.max(sample, -1));
    }

    @Override
    public float nextFloat() {
        return provider.nextFloat();
    }

    public boolean nextBoolean() {
        return provider.nextBoolean();
    }
}
