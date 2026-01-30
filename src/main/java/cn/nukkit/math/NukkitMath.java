package cn.nukkit.math;

import cn.nukkit.utils.random.RandomSourceProvider;
import java.math.BigInteger;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class NukkitMath {
    private static final Byte ZERO_BYTE = 0;
    private static final Integer ZERO_INTEGER = 0;
    private static final Short ZERO_SHORT = 0;
    private static final Long ZERO_LONG = 0L;

    public static boolean isZero(Number storage) {
        return ZERO_BYTE.equals(storage)
                || ZERO_INTEGER.equals(storage)
                || ZERO_SHORT.equals(storage)
                || ZERO_LONG.equals(storage)
                || BigInteger.ZERO.equals(storage);
    }

    public static int floorDouble(double n) {
        int i = (int) n;
        return n >= i ? i : i - 1;
    }

    public static int ceilDouble(double n) {
        int i = (int) n;
        return n > i ? i + 1 : i;
    }

    public static int floorFloat(float n) {
        int i = (int) n;
        return n >= i ? i : i - 1;
    }

    public static int ceilFloat(float n) {
        int i = (int) n;
        return n > i ? i + 1 : i;
    }

    public static int randomRange(RandomSourceProvider random) {
        return randomRange(random, 0);
    }

    public static int randomRange(RandomSourceProvider random, int start) {
        return randomRange(random, 0, 0x7fffffff);
    }

    public static int randomRange(RandomSourceProvider random, int start, int end) {
        return start + (random.nextInt() % (end + 1 - start));
    }

    public static int randomRangeTriangle(RandomSourceProvider random, int start, int end) {
        int heightDiff = Math.abs(end - start);
        int heightDiffHalf = heightDiff / 2;
        int heightDiffHalf2 = heightDiff - heightDiffHalf;
        return Math.min(start, end) + NukkitMath.randomRange(random, 0, heightDiffHalf2) + NukkitMath.randomRange(random, 0, heightDiffHalf);
    }

    public static double round(double d) {
        return round(d, 0);
    }

    public static double round(double d, int precision) {
        double pow = Math.pow(10, precision);
        return ((double) Math.round(d * pow)) / pow;
    }

    public static double clamp(double value, double min, double max) {
        return value < min ? min : (Math.min(value, max));
    }

    public static int clamp(int value, int min, int max) {
        return value < min ? min : (Math.min(value, max));
    }

    public static float clamp(float value, float min, float max) {
        return value < min ? min : (Math.min(value, max));
    }

    public static float remap(float input, float inMin, float inMax, float outMin, float outMax) {
        return outMin + ((input - inMin) / (inMax - inMin) * (outMax - outMin));
    }

    public static float remapNormalized(float input, float inMin, float inMax) {
        return remap(input, inMin, inMax, -1, 1);
    }

    public static double getDirection(double diffX, double diffZ) {
        diffX = Math.abs(diffX);
        diffZ = Math.abs(diffZ);

        return Math.max(diffX, diffZ);
    }

    public static int bitLength(byte data) {
        if (data < 0) {
            return 32;
        }

        if (data == 0) {
            return 1;
        }

        int bits = 0;
        while (data != 0) {
            data >>>= 1;
            bits++;
        }

        return bits;
    }

    public static int bitLength(int data) {
        if (data < 0) {
            return 32;
        }

        if (data == 0) {
            return 1;
        }

        int bits = 0;
        while (data != 0) {
            data >>>= 1;
            bits++;
        }

        return bits;
    }

    public static int bitLength(long data) {
        if (data < 0) {
            return 64;
        }

        if (data == 0) {
            return 1;
        }

        int bits = 0;
        while (data != 0) {
            data >>>= 1;
            bits++;
        }

        return bits;
    }

    public static int bitLength(BigInteger data) {
        if (data.compareTo(BigInteger.ZERO) < 0) {
            throw new UnsupportedOperationException("Negative BigIntegers are not supported (nearly infinite bits)");
        }

        return data.bitLength();
    }

    public static boolean isPowerOf2(long value) {
        return (value & -value) == value;
    }

    public static boolean isPowerOf2(BigInteger value) {
        return value.and(value.subtract(BigInteger.ONE)).equals(BigInteger.ZERO);
    }

    public static long getPow2(int bits) {
        return 1L << bits;
    }

    public static BigInteger getBigPow2(int bits) {
        return BigInteger.ONE.shiftLeft(bits);
    }

    public static long getMask(int bits) {
        return bits >= 64 ? ~0 : getPow2(bits) - 1;
    }

    public static BigInteger getBigMask(int bits) {
        return getBigPow2(bits).subtract(BigInteger.ONE);
    }

    public static long mask(long value, int bits) {
        return value & getMask(bits);
    }

    public static BigInteger bigMask(BigInteger value, int bits) {
        return value.and(getBigMask(bits));
    }

    public static long maskSigned(long value, int bits) {
        return value << (64 - bits) >> (64 - bits); //removes top bits and copies sign bits back down
    }

    public static long modInverse(long value) {
        return modInverse(value, 64);
    }

    public static long modInverse(long value, int bits) {
        long x = ((((value << 1) ^ value) & 4) << 1) ^ value;
        x *= 2 - value * x;
        x *= 2 - value * x;
        x *= 2 - value * x;
        x *= 2 - value * x;
        return mask(x, bits);
    }

    public static int min(int... values) {
        int min = values[0];

        for(int i = 1; i < values.length; i++) {
            min = Math.min(min, values[i]);
        }

        return min;
    }

    public static float min(float... values) {
        float min = values[0];

        for(int i = 1; i < values.length; i++) {
            min = Math.min(min, values[i]);
        }

        return min;
    }

    public static long min(long... values) {
        long min = values[0];

        for(int i = 1; i < values.length; i++) {
            min = Math.min(min, values[i]);
        }

        return min;
    }

    public static double min(double... values) {
        double min = values[0];

        for(int i = 1; i < values.length; i++) {
            min = Math.min(min, values[i]);
        }

        return min;
    }

    @SafeVarargs
    public static <T extends Comparable<T>> T getMin(T... values) {
        T min = values[0];

        for(int i = 1; i < values.length; i++) {
            min = min.compareTo(values[i]) <= 0 ? min : values[i];
        }

        return min;
    }

    public static int max(int... values) {
        int max = values[0];

        for(int i = 1; i < values.length; i++) {
            max = Math.max(max, values[i]);
        }

        return max;
    }

    public static float max(float... values) {
        float max = values[0];

        for(int i = 1; i < values.length; i++) {
            max = Math.max(max, values[i]);
        }

        return max;
    }

    public static long max(long... values) {
        long max = values[0];

        for(int i = 1; i < values.length; i++) {
            max = Math.max(max, values[i]);
        }

        return max;
    }

    public static double max(double... values) {
        double max = values[0];

        for(int i = 1; i < values.length; i++) {
            max = Math.max(max, values[i]);
        }

        return max;
    }

    @SafeVarargs
    public static <T extends Comparable<T>> T getMax(T... values) {
        T max = values[0];

        for(int i = 1; i < values.length; i++) {
            max = max.compareTo(values[i]) >= 0 ? max : values[i];
        }

        return max;
    }

    public static long clamp(long value, long min, long max) {
        if(value < min) return min;
        return Math.min(value, max);
    }

    public static <T extends Comparable<T>> T clamp(T value, T min, T max) {
        if(value.compareTo(min) < 0) return min;
        return value.compareTo(max) <= 0 ? value : max;
    }

}
