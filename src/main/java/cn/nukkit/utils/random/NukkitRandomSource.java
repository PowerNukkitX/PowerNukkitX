package cn.nukkit.utils.random;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.zip.CRC32;

/**
 * @author Angelic47 (Nukkit Project)
 */
public class NukkitRandomSource implements BitRandomSource {
    protected long seed;

    public NukkitRandomSource() {
        this(-1);
    }

    public NukkitRandomSource(long seeds) {
        if (seeds == -1) {
            seeds = System.currentTimeMillis() / 1000L;
        }
        this.setSeed(seeds);
    }

    @Override
    public RandomSource fork() {
        return new NukkitRandomSource(nextLong());
    }

    public void setSeed(long seeds) {
        CRC32 crc32 = new CRC32();
        ByteBuffer buffer = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN);
        buffer.putInt((int) seeds);
        crc32.update(buffer.array());
        this.seed = crc32.getValue();
    }

    public int nextSignedInt() {
        int t = (((int) ((this.seed * 65535) + 31337) >> 8) + 1337);
        this.seed ^= t;
        return t;
    }

    public long getSeed() {
        return seed;
    }

    @Override
    public int next(int bits) {
        long l = this.seed;
        long m = l * 25214903917L + 11L & 281474976710655L;
        return (int) (m >> 48 - bits);
    }

    @Override
    public int nextInt() {
        return this.nextSignedInt() & 0x7fffffff;
    }

    @Override
    public double nextDouble() {
        return (double) this.nextInt() / 0x7fffffff;
    }

    @Override
    public double nextGaussian() {
        return 0;//todo implement it
    }

    @Override
    public float nextFloat() {
        return (float) this.nextInt() / 0x7fffffff;
    }

    public float nextSignedFloat() {
        return (float) this.nextInt() / 0x7fffffff;
    }

    public double nextSignedDouble() {
        return (double) this.nextSignedInt() / 0x7fffffff;
    }

    public boolean nextBoolean() {
        return (this.nextSignedInt() & 0x01) == 0;
    }

    public int nextRange() {
        return nextRange(0, 0x7fffffff);
    }

    public int nextRange(int start) {
        return nextRange(start, 0x7fffffff);
    }

    public int nextRange(int start, int end) {
        return start + (this.nextInt() % (end + 1 - start));
    }

    public int nextBoundedInt(int bound) {
        return bound == 0 ? 0 : this.nextInt() % bound;
    }
}
