package org.powernukkitx.utils.random;

import com.google.common.base.Preconditions;

public final class BedrockRandom implements RandomSourceProvider {

    private static final int STATE_SIZE = 624;
    private static final int PERIOD = 397;
    private static final int MATRIX_A = 0x9908b0df;
    private static final int UPPER_MASK = 0x80000000;
    private static final int LOWER_MASK = 0x7fffffff;

    private final int[] state = new int[STATE_SIZE];

    private long seed;
    private int index = STATE_SIZE;
    private boolean hasGaussian;
    private double gaussian;

    public BedrockRandom(long seed) {
        this.setSeed(seed);
    }

    @Override
    public BedrockRandom fork() {
        return new BedrockRandom(this.nextLong());
    }

    @Override
    public BedrockRandom identical() {
        BedrockRandom copy = new BedrockRandom(this.seed);
        System.arraycopy(this.state, 0, copy.state, 0, STATE_SIZE);
        copy.index = this.index;
        copy.hasGaussian = this.hasGaussian;
        copy.gaussian = this.gaussian;
        return copy;
    }

    @Override
    public int nextInt() {
        return this.nextRaw() >>> 1;
    }

    @Override
    public int nextInt(int max) {
        if (max <= 0) return 0;
        return Integer.remainderUnsigned(this.nextRaw(), max);
    }

    @Override
    public int nextInt(int min, int max) {
        Preconditions.checkArgument(max >= min, "max must be greater than or equal to min");
        if (max == min) return min;

        long range = (long) max - min + 1L;
        return (int) (min + Integer.toUnsignedLong(this.nextRaw()) % range);
    }

    @Override
    public int nextBoundedInt(int max) {
        return this.nextInt(max);
    }

    @Override
    public int nextExclusiveInt(int bound) {
        return this.nextInt(bound);
    }

    @Override
    public long nextLong() {
        long high = Integer.toUnsignedLong(this.nextRaw()) << 32;
        return high + Integer.toUnsignedLong(this.nextRaw());
    }

    @Override
    public boolean nextBoolean() {
        return ((this.nextRaw() >>> 27) & 1) != 0;
    }

    @Override
    public float nextFloat() {
        return (float) (Integer.toUnsignedLong(this.nextRaw()) * 0x1.0p-32);
    }

    @Override
    public double nextDouble() {
        return Integer.toUnsignedLong(this.nextRaw()) * 0x1.0p-32;
    }

    @Override
    public double nextGaussian() {
        if (this.hasGaussian) {
            this.hasGaussian = false;
            return this.gaussian;
        }

        double x, y, squared;
        do {
            x = 2.0 * this.nextDouble() - 1.0;
            y = 2.0 * this.nextDouble() - 1.0;
            squared = x * x + y * y;
        } while (squared >= 1.0 || squared == 0.0);

        double multiplier = Math.sqrt(-2.0 * Math.log(squared) / squared);
        this.gaussian = y * multiplier;
        this.hasGaussian = true;
        return x * multiplier;
    }

    @Override
    public BedrockRandom setSeed(long seed) {
        int value = (int) seed;
        this.seed = value;
        this.state[0] = value;

        int[] s = this.state;
        for (int i = 1; i < STATE_SIZE; i++) {
            int previous = s[i - 1];
            s[i] = 1812433253 * (previous ^ (previous >>> 30)) + i;
        }

        this.index = STATE_SIZE;
        this.hasGaussian = false;
        this.gaussian = 0;
        return this;
    }

    @Override
    public long getSeed() {
        return this.seed;
    }

    private int nextRaw() {
        int idx = this.index;
        if (idx >= STATE_SIZE) {
            this.twist();
            idx = this.index;
        }
        this.index = idx + 1;

        int value = this.state[idx];
        value ^= value >>> 11;
        value ^= (value << 7) & 0x9d2c5680;
        value ^= (value << 15) & 0xefc60000;
        value ^= value >>> 18;
        return value;
    }

    private void twist() {
        int[] s = this.state;
        int i = 0;

        while (i < STATE_SIZE - PERIOD) {
            int y = (s[i] & UPPER_MASK) | (s[i + 1] & LOWER_MASK);
            s[i] = s[i + PERIOD] ^ (y >>> 1) ^ (-(y & 1) & MATRIX_A);
            i++;
        }

        while (i < STATE_SIZE - 1) {
            int y = (s[i] & UPPER_MASK) | (s[i + 1] & LOWER_MASK);
            s[i] = s[i + PERIOD - STATE_SIZE] ^ (y >>> 1) ^ (-(y & 1) & MATRIX_A);
            i++;
        }

        int y = (s[STATE_SIZE - 1] & UPPER_MASK) | (s[0] & LOWER_MASK);
        s[STATE_SIZE - 1] = s[PERIOD - 1] ^ (y >>> 1) ^ (-(y & 1) & MATRIX_A);

        this.index = 0;
    }
}
