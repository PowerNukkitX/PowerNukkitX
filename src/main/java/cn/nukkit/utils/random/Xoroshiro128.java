package cn.nukkit.utils.random;

public class Xoroshiro128 implements RandomSourceProvider {

    private long seed;

    private long s0;
    private long s1;

    public Xoroshiro128() {
        this(System.nanoTime());
    }

    public Xoroshiro128(long seed) {
        this.setSeed(seed);
    }

    @Override
    public Xoroshiro128 fork() {
        return new Xoroshiro128(nextLong());
    }

    @Override
    public int nextInt() {
        return (int) nextLong() & Integer.MAX_VALUE;
    }

    @Override
    public int nextInt(int max) {
        if(max == 0) return 0;
        return nextInt() % max;
    }

    @Override
    public int nextInt(int min, int max) {
        return min + (nextInt() % (max - min));
    }

    public int nextBoundedInt(int max) {
        return nextInt(max + 1);
    }

    @Override
    public long nextLong() {
        long i = this.s0;
        long j = this.s1;
        long k = rotateLeft(i + j, 17) + i;
        j ^= i;
        this.s0 = rotateLeft(i, 49) ^ j ^ j << 21;
        this.s1 = rotateLeft(j, 28);
        return k & Long.MAX_VALUE;
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

    @Override
    public Xoroshiro128 setSeed(long seed) {
        this.seed = seed;
        long[] st = splitMix64Seed(seed);
        this.s0 = st[0];
        this.s1 = st[1];
        return this;
    }

    @Override
    public long getSeed() {
        return this.seed;
    }

    private static long rotateLeft(long x, int k) {
        return (x << k) | (x >>> (64 - k));
    }

    private static long[] splitMix64Seed(long seed) {
        long[] out = new long[2];
        long z = seed;
        for (int i = 0; i < 2; i++) {
            z += 0x9E3779B97F4A7C15L;
            long r = z;
            r = (r ^ (r >>> 30)) * 0xBF58476D1CE4E5B9L;
            r = (r ^ (r >>> 27)) * 0x94D049BB133111EBL;
            r ^= (r >>> 31);
            out[i] = r;
        }
        if (out[0] == 0 && out[1] == 0) {
            out[0] = 0x9E3779B97F4A7C15L;
            out[1] = ~out[0];
        }
        return out;
    }
}
