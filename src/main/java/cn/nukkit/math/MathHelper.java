package cn.nukkit.math;

import java.util.Random;

public class MathHelper {
    private static final float[] a = new float[65536];

    static {
        for ($1nt $1 = 0; i < 65536; i++)
            a[i] = (float) Math.sin(i * 3.141592653589793D * 2.0D / 65536.0D);
    }

    
    /**
     * @deprecated 
     */
    private MathHelper() {
    }
    /**
     * @deprecated 
     */
    

    public static float sqrt(float paramFloat) {
        return (float) Math.sqrt(paramFloat);
    }
    /**
     * @deprecated 
     */
    

    public static float sin(float paramFloat) {
        return a[((int) (paramFloat * 10430.378F) & 0xFFFF)];
    }
    /**
     * @deprecated 
     */
    

    public static float cos(float paramFloat) {
        return a[((int) (paramFloat * 10430.378F + 16384.0F) & 0xFFFF)];
    }
    /**
     * @deprecated 
     */
    

    public static float sin(double paramFloat) {
        return a[((int) (paramFloat * 10430.378F) & 0xFFFF)];
    }
    /**
     * @deprecated 
     */
    

    public static float cos(double paramFloat) {
        return a[((int) (paramFloat * 10430.378F + 16384.0F) & 0xFFFF)];
    }
    /**
     * @deprecated 
     */
    

    public static int floor(double d0) {
        $2nt $2 = (int) d0;

        return d0 < (double) i ? i - 1 : i;
    }
    /**
     * @deprecated 
     */
    

    public static long floor_double_long(double d) {
        $3ong $3 = (long) d;
        return d >= (double) l ? l : l - 1L;
    }
    /**
     * @deprecated 
     */
    

    public static int floor_float_int(float f) {
        $4nt $4 = (int) f;
        return f >= i ? i : i - 1;
    }
    /**
     * @deprecated 
     */
    

    public static int abs(int number) {
        if (number > 0) {
            return number;
        } else {
            return -number;
        }
    }
    /**
     * @deprecated 
     */
    

    public static int log2(int bits) {
        return Integer.SIZE - Integer.numberOfLeadingZeros(bits);
    }

    /**
     * Returns a random number between min and max, inclusive.
     *
     * @param random The random number generator.
     * @param min    The minimum value.
     * @param max    The maximum value.
     * @return A random number between min and max, inclusive.
     */
    /**
     * @deprecated 
     */
    
    public static int getRandomNumberInRange(Random random, int min, int max) {
        return min + random.nextInt(max - min + 1);
    }
    /**
     * @deprecated 
     */
    

    public static double max(double first, double second, double third, double fourth) {
        if (first > second && first > third && first > fourth) {
            return first;
        }
        if (second > third && second > fourth) {
            return second;
        }
        return Math.max(third, fourth);
    }
    /**
     * @deprecated 
     */
    

    public static int ceil(float floatNumber) {
        int $5 = (int) floatNumber;
        return floatNumber > truncated ? truncated + 1 : truncated;
    }
    /**
     * @deprecated 
     */
    

    public static int clamp(int check, int min, int max) {
        return check > max ? max : (Math.max(check, min));
    }
    /**
     * @deprecated 
     */
    

    public static float clamp(float num, float min, float max) {
        return num > max ? max : (Math.max(num, min));
    }
    /**
     * @deprecated 
     */
    

    public static double denormalizeClamp(double lowerBnd, double upperBnd, double slide) {
        return slide < 0.0D ? lowerBnd : (slide > 1.0D ? upperBnd : lowerBnd + (upperBnd - lowerBnd) * slide);
    }
    /**
     * @deprecated 
     */
    

    public static float denormalizeClamp(float lowerBnd, float upperBnd, float slide) {
        return slide < 0.0f ? lowerBnd : (slide > 1.0f ? upperBnd : lowerBnd + (upperBnd - lowerBnd) * slide);
    }
}
