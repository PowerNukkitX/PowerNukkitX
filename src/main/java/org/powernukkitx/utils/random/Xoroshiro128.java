package org.powernukkitx.utils.random;

import org.jetbrains.annotations.NotNull;

/**
 * Xoroshiro128++ random source with the seeding and sampling behaviour of Vanilla's worldgen random.
 * <p>
 * Not thread-safe: every instance carries mutable state, so share a {@link Xoroshiro128PositionalFactory}
 * and derive one generator per thread instead.
 */
public class Xoroshiro128 implements RandomSourceProvider {

    /**
     * 64-bit golden ratio constant.
     */
    private static final long GOLDEN_RATIO_64 = 0x9E3779B97F4A7C15L; // -7046029254386353131L
    /**
     * 64-bit silver ratio constant.
     */
    private static final long SILVER_RATIO_64 = 0x6A09E667F3BCC909L; // 7640891576956012809L

    private long seed;

    /**
     * State right after initialisation, so that {@link #identical()} also works for {@link #fromState}.
     */
    private long initialLo;
    private long initialHi;

    private long s0;
    private long s1;

    public Xoroshiro128() {
        this(System.nanoTime());
    }

    public Xoroshiro128(long seed) {
        this.setSeed(seed);
    }

    private Xoroshiro128(long seed, long lo, long hi) {
        this.seed = seed;
        this.setState(lo, hi);
    }

    /**
     * Creates a generator directly from its 128-bit state.
     * If {@code lo | hi == 0}, the state {@code (GOLDEN, SILVER)} is used instead.
     * <p>
     * {@link #getSeed()} returns 0 for generators created this way, because there is no 64-bit seed.
     * Code that needs the world seed must not read it from such a generator.
     *
     * @param lo low 64 bits of the state
     * @param hi high 64 bits of the state
     * @return a new generator in the given state
     */
    public static @NotNull Xoroshiro128 fromState(long lo, long hi) {
        return new Xoroshiro128(0L, lo, hi);
    }

    /**
     * Creates a generator from a 64-bit seed using the Vanilla seeding procedure:
     * <pre>
     *   lo = mixStafford13(seed ^ SILVER)
     *   hi = mixStafford13((seed ^ SILVER) + GOLDEN)
     * </pre>
     * This is equivalent to {@link #Xoroshiro128(long)}.
     *
     * @param seed the world seed
     * @return a new generator seeded with {@code seed}
     */
    public static @NotNull Xoroshiro128 ofSeed(long seed) {
        return new Xoroshiro128(seed);
    }

    /**
     * Draws two longs ({@code lo}, then {@code hi}) and builds a positional factory from them.
     * This advances this generator by two steps.
     *
     * @return a new factory; the same generator state always yields the same factory
     */
    public @NotNull Xoroshiro128PositionalFactory forkPositional() {
        long lo = nextLong();
        long hi = nextLong();
        return new Xoroshiro128PositionalFactory(lo, hi);
    }

    /**
     * Returns a random number in {@code [0, bound)} using Lemire's method with rejection, as Vanilla does.
     *
     * @param bound exclusive upper bound, must be positive
     * @return a value in {@code [0, bound)}
     * @throws IllegalArgumentException if {@code bound <= 0}
     */
    @Override
    public int nextExclusiveInt(int bound) {
        if (bound <= 0) {
            throw new IllegalArgumentException("bound must be positive");
        }
        long product = (nextLong() & 0xFFFFFFFFL) * bound;
        long low = product & 0xFFFFFFFFL;
        if (low < bound) {
            long threshold = Integer.remainderUnsigned(-bound, bound);
            while (low < threshold) {
                product = (nextLong() & 0xFFFFFFFFL) * bound;
                low = product & 0xFFFFFFFFL;
            }
        }
        return (int) (product >>> 32);
    }

    private static long mixStafford13(long z) {
        z = (z ^ (z >>> 30)) * 0xBF58476D1CE4E5B9L;
        z = (z ^ (z >>> 27)) * 0x94D049BB133111EBL;
        return z ^ (z >>> 31);
    }

    private void setState(long lo, long hi) {
        if ((lo | hi) == 0L) {
            lo = GOLDEN_RATIO_64;
            hi = SILVER_RATIO_64;
        }
        this.initialLo = lo;
        this.initialHi = hi;
        this.s0 = lo;
        this.s1 = hi;
    }


    @Override
    public Xoroshiro128 fork() {
        return new Xoroshiro128(nextLong());
    }

    /**
     * Returns a new generator in the <b>initial</b> state of this one, not in its current state.
     * Values drawn from this generator before the call are therefore drawn again by the copy.
     * Unlike {@link BedrockRandom#identical()}, this is not a snapshot.
     */
    @Override
    public Xoroshiro128 identical() {
        return new Xoroshiro128(seed, initialLo, initialHi);
    }

    /**
     * @return the lower 32 bits of {@link #nextLong()}, including negative values.
     */
    @Override
    public int nextInt() {
        return (int) nextLong();
    }

    /**
     * @param max exclusive upper bound
     * @return a value in {@code [0, max)}, or {@code 0} if {@code max <= 0}. The lenient result is kept for
     * existing callers (populators); use {@link #nextExclusiveInt(int)} for the strict variant that throws.
     */
    @Override
    public int nextInt(int max) {
        if (max <= 0) {
            return 0;
        }
        return nextExclusiveInt(max);
    }

    /**
     * @return a value in {@code [min, max]}, both bounds inclusive
     * @throws IllegalArgumentException if {@code max < min}
     */
    @Override
    public int nextInt(int min, int max) {
        return min + nextExclusiveInt(max - min + 1);
    }

    /**
     * @param max inclusive upper bound, must not be negative
     * @return a value in {@code [0, max]}, matching {@code NukkitRandom}; use {@link #nextExclusiveInt(int)}
     * for an exclusive bound
     */
    @Override
    public int nextBoundedInt(int max) {
        return nextExclusiveInt(max + 1);
    }

    @Override
    public long nextLong() {
        long i = this.s0;
        long j = this.s1;
        long k = rotateLeft(i + j, 17) + i;
        j ^= i;
        this.s0 = rotateLeft(i, 49) ^ j ^ j << 21;
        this.s1 = rotateLeft(j, 28);
        return k;
    }

    @Override
    public boolean nextBoolean() {
        return (nextLong() & 1L) != 0L;
    }

    @Override
    public float nextFloat() {
        return (float) ((nextLong() >>> 40) * (1.0 / (1L << 24)));
    }

    @Override
    public double nextDouble() {
        long bits = nextLong() >>> 11;
        return bits * (1.0 / (1L << 53));
    }

    @Override
    public double nextGaussian() {
        double u1 = nextDouble();
        double u2 = nextDouble();
        return Math.sqrt(-2.0 * Math.log(u1)) * Math.cos(2 * Math.PI * u2);
    }

    /**
     * Reseeds this generator with the Vanilla seed upgrade, see {@link #ofSeed(long)}.
     */
    @Override
    public Xoroshiro128 setSeed(long seed) {
        this.seed = seed;
        long lo = seed ^ SILVER_RATIO_64;
        long hi = lo + GOLDEN_RATIO_64;
        this.setState(mixStafford13(lo), mixStafford13(hi));
        return this;
    }

    @Override
    public long getSeed() {
        return this.seed;
    }

    private static long rotateLeft(long x, int k) {
        return (x << k) | (x >>> (64 - k));
    }
}
