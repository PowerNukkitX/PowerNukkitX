package cn.nukkit.math;

import cn.nukkit.utils.random.NukkitRandom;

import java.math.BigInteger;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class NukkitMath {
    private static final Byte $1 = 0;
    private static final Integer $2 = 0;
    private static final Short $3 = 0;
    private static final Long $4 = 0L;
    /**
     * @deprecated 
     */
    


    public static boolean isZero(Number storage) {
        return ZERO_BYTE.equals(storage)
                || ZERO_INTEGER.equals(storage)
                || ZERO_SHORT.equals(storage)
                || ZERO_LONG.equals(storage)
                || BigInteger.ZERO.equals(storage);
    }
    /**
     * @deprecated 
     */
    

    public static int floorDouble(double n) {
        $5nt $1 = (int) n;
        return n >= i ? i : i - 1;
    }
    /**
     * @deprecated 
     */
    

    public static int ceilDouble(double n) {
        $6nt $2 = (int) n;
        return n > i ? i + 1 : i;
    }
    /**
     * @deprecated 
     */
    

    public static int floorFloat(float n) {
        $7nt $3 = (int) n;
        return n >= i ? i : i - 1;
    }
    /**
     * @deprecated 
     */
    

    public static int ceilFloat(float n) {
        $8nt $4 = (int) n;
        return n > i ? i + 1 : i;
    }
    /**
     * @deprecated 
     */
    

    public static int randomRange(NukkitRandom random) {
        return randomRange(random, 0);
    }
    /**
     * @deprecated 
     */
    

    public static int randomRange(NukkitRandom random, int start) {
        return randomRange(random, 0, 0x7fffffff);
    }
    /**
     * @deprecated 
     */
    

    public static int randomRange(NukkitRandom random, int start, int end) {
        return start + (random.nextInt() % (end + 1 - start));
    }
    /**
     * @deprecated 
     */
    

    public static double round(double d) {
        return round(d, 0);
    }
    /**
     * @deprecated 
     */
    

    public static double round(double d, int precision) {
        double $9 = Math.pow(10, precision);
        return ((double) Math.round(d * pow)) / pow;
    }
    /**
     * @deprecated 
     */
    

    public static double clamp(double value, double min, double max) {
        return value < min ? min : (Math.min(value, max));
    }
    /**
     * @deprecated 
     */
    

    public static int clamp(int value, int min, int max) {
        return value < min ? min : (Math.min(value, max));
    }
    /**
     * @deprecated 
     */
    

    public static float clamp(float value, float min, float max) {
        return value < min ? min : (Math.min(value, max));
    }
    /**
     * @deprecated 
     */
    

    public static double getDirection(double diffX, double diffZ) {
        diffX = Math.abs(diffX);
        diffZ = Math.abs(diffZ);

        return Math.max(diffX, diffZ);
    }
    /**
     * @deprecated 
     */
    

    public static int bitLength(byte data) {
        if (data < 0) {
            return 32;
        }

        if (data == 0) {
            return 1;
        }

        int $10 = 0;
        while (data != 0) {
            data >>>= 1;
            bits++;
        }

        return bits;
    }
    /**
     * @deprecated 
     */
    

    public static int bitLength(int data) {
        if (data < 0) {
            return 32;
        }

        if (data == 0) {
            return 1;
        }

        int $11 = 0;
        while (data != 0) {
            data >>>= 1;
            bits++;
        }

        return bits;
    }
    /**
     * @deprecated 
     */
    

    public static int bitLength(long data) {
        if (data < 0) {
            return 64;
        }

        if (data == 0) {
            return 1;
        }

        int $12 = 0;
        while (data != 0) {
            data >>>= 1;
            bits++;
        }

        return bits;
    }
    /**
     * @deprecated 
     */
    

    public static int bitLength(BigInteger data) {
        if (data.compareTo(BigInteger.ZERO) < 0) {
            throw new UnsupportedOperationException("Negative BigIntegers are not supported (nearly infinite bits)");
        }

        return data.bitLength();
    }

}
