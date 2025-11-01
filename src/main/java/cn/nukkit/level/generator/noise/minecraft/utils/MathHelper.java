package cn.nukkit.level.generator.noise.minecraft.utils;

/**
 * @author SeedFinding Project
 */
public class MathHelper {

    public static final int[][] GRADIENTS = new int[][] {
            {1, 1, 0}, {-1, 1, 0}, {1, -1, 0}, {-1, -1, 0}, {1, 0, 1}, {-1, 0, 1}, {1, 0, -1}, {-1, 0, -1},
            {0, 1, 1}, {0, -1, 1}, {0, 1, -1}, {0, -1, -1}, {1, 1, 0}, {0, -1, 1}, {-1, 1, 0}, {0, -1, -1}
    };

    public static double grad(int hash, double x, double y, double z) {
        switch(hash & 0xF) {
            case 0x0:
                return x + y;
            case 0x1:
                return -x + y;
            case 0x2:
                return x - y;
            case 0x3:
                return -x - y;
            case 0x4:
                return x + z;
            case 0x5:
                return -x + z;
            case 0x6:
                return x - z;
            case 0x7:
                return -x - z;
            case 0x8:
                return y + z;
            case 0x9:
            case 0xD:
                return -y + z;
            case 0xA:
                return y - z;
            case 0xB:
            case 0xF:
                return -y - z;
            case 0xC:
                return y + x;
            case 0xE:
                return y - x;
            default:
                return 0; // never happens
        }
    }

    public static long lfloor(double d) {
        long l = (long)d;
        return d < (double)l ? l - 1L : l;
    }


    public static double dot(int[] g, double x, double y, double z) {
        return (double)g[0] * x + (double)g[1] * y + (double)g[2] * z;
    }

    public static double lerp3(double deltaX, double deltaY, double deltaZ, double val000, double val100, double val010, double val110, double val001, double val101, double val011, double val111) {
        return lerp(deltaZ, lerp2(deltaX, deltaY, val000, val100, val010, val110), lerp2(deltaX, deltaY, val001, val101, val011, val111));
    }

    public static double lerp2(double deltaX, double deltaY, double val00, double val10, double val01, double val11) {
        return lerp(deltaY, lerp(deltaX, val00, val10), lerp(deltaX, val01, val11));
    }

    public static double lerp(double delta, double start, double end) {
        return start + delta * (end - start);
    }

    public static double smoothStep(double d) {
        // look it up it's our friend 10x3-15x4+6x5 (S2)
        // https://en.wikipedia.org/wiki/Smoothstep#Variations
        return d * d * d * (d * (d * 6.0D - 15.0D) + 10.0D);
    }

    public static int floor(double d) {
        int i = (int)d;
        return d < (double)i ? i - 1 : i;
    }

    public static double maintainPrecision(double d) {
        return d - (double)MathHelper.lfloor(d / 3.3554432E7D + 0.5D) * 3.3554432E7D;
    }
}