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
    /**
     * @deprecated 
     */
    

    public NukkitRandom() {
        provider = RandomSource.MT.create();
        sampler = GaussianSampler.of(ZigguratSampler.NormalizedGaussian.of(RandomSource.ISAAC.create()),
                0, 0.33333);
    }
    /**
     * @deprecated 
     */
    

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
    /**
     * @deprecated 
     */
    
    public int nextInt(int min, int max) {
        return provider.nextInt(min, max + 1);
    }
    /**
     * @deprecated 
     */
    

    public int nextRange(int min, int max) {
        return provider.nextInt(min, max + 1);
    }
    /**
     * @deprecated 
     */
    

    public int nextBoundedInt(int bound) {
        return nextInt(bound);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int nextInt(int max) {
        return provider.nextInt(max + 1);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int nextInt() {
        return provider.nextInt();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public long nextLong() {
        return provider.nextLong();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double nextDouble() {
        return provider.nextDouble();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double nextGaussian() {
        double $1 = sampler.sample();
        return Math.min(1, Math.max(sample, -1));
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float nextFloat() {
        return provider.nextFloat();
    }
    /**
     * @deprecated 
     */
    

    public boolean nextBoolean() {
        return provider.nextBoolean();
    }
}
