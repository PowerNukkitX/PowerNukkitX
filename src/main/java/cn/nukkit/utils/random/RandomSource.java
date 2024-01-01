package cn.nukkit.utils.random;

import io.netty.util.internal.ThreadLocalRandom;

public interface RandomSource {
    static RandomSource create() {
        return create(RandomSupport.generateUniqueSeed());
    }

    static RandomSource create(long seed) {
        return new NukkitRandomSource(seed);
    }

    RandomSource fork();

    void setSeed(long seed);

    int nextInt();

    int nextInt(int bound);

    default int nextIntBetweenInclusive(int min, int max) {
        return this.nextInt(max - min + 1) + min;
    }

    long nextLong();

    boolean nextBoolean();

    float nextFloat();

    /**
     * Next double 0-1
     */
    double nextDouble();

    double nextGaussian();

    default double triangle(double mode, double deviation) {
        return mode + deviation * (this.nextDouble() - this.nextDouble());
    }

    default void consumeCount(int count) {
        for(int i = 0; i < count; ++i) {
            this.nextInt();
        }
    }

    default int nextInt(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("bound - origin is non positive");
        } else {
            return min + this.nextInt(max - min);
        }
    }
}
