package org.powernukkitx.utils.random;

import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Positional factory for {@link Xoroshiro128} (Vanilla:
 * {@code XoroshiroPositionalRandomFactory}).
 * <p>
 * A factory is an immutable 128-bit pair {@code (lo, hi)}. Any number of independent random streams can be
 * derived from it, and the order of the calls does not matter:
 * <ul>
 *     <li>{@link #hashOf(String)} - named stream (e.g. {@code "minecraft:aquifer_barrier"}, {@code "octave_-6"})</li>
 *     <li>{@link #at(int, int, int)} - stream per block position</li>
 * </ul>
 * Create one with {@link Xoroshiro128#forkPositional()}. Instances are immutable and safe to share between
 * threads.
 */
public final class Xoroshiro128PositionalFactory {

    private final long lo;
    private final long hi;

    /**
     * @param lo low 64 bits of the factory pair
     * @param hi high 64 bits of the factory pair
     */
    public Xoroshiro128PositionalFactory(long lo, long hi) {
        this.lo = lo;
        this.hi = hi;
    }

    /**
     * @return the low 64 bits of the factory pair
     */
    public long getLo() {
        return lo;
    }

    /**
     * @return the high 64 bits of the factory pair
     */
    public long getHi() {
        return hi;
    }

    /**
     * Returns the named stream: the first 8 bytes (big endian) of {@code MD5(name)} become {@code lo}, the next
     * 8 bytes become {@code hi}, each XORed with the factory pair. The name is hashed as UTF-8.
     * <p>
     * A different name gives a different stream without any error, so names must match Vanilla exactly.
     * Allocates a {@link MessageDigest} per call; call it during setup, not per block.
     *
     * @param name the stream name, e.g. {@code "minecraft:terrain"}
     * @return a new generator; the same factory and name always give the same stream
     */
    public @NotNull Xoroshiro128 hashOf(@NotNull String name) {
        byte[] md5 = md5(name.getBytes(StandardCharsets.UTF_8));
        ByteBuffer buffer = ByteBuffer.wrap(md5); // big endian is the default
        long nameLo = buffer.getLong(0);
        long nameHi = buffer.getLong(8);
        return Xoroshiro128.fromState(nameLo ^ this.lo, nameHi ^ this.hi);
    }

    /**
     * Returns the stream for a block position: {@code (blockSeed(x, y, z) ^ lo, hi)}.
     * Allocates a new generator per call.
     *
     * @return a new generator; the same factory and position always give the same stream
     */
    public @NotNull Xoroshiro128 at(int x, int y, int z) {
        return Xoroshiro128.fromState(blockSeed(x, y, z) ^ this.lo, this.hi);
    }

    /**
     * Position hash (Vanilla: {@code Mth.getSeed}).
     * {@code x * 3129871} is deliberately a 32-bit int multiplication with overflow; only the result is widened
     * to long.
     */
    public static long blockSeed(int x, int y, int z) {
        long seed = (long) (x * 3129871) ^ ((long) z * 116129781L) ^ (long) y;
        seed = seed * seed * 42317861L + seed * 11L;
        return seed >> 16;
    }

    private static byte[] md5(byte[] input) {
        try {
            // MessageDigest is not thread-safe, so every call uses its own instance.
            return MessageDigest.getInstance("MD5").digest(input);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("MD5 is required by every Java platform", e);
        }
    }
}
