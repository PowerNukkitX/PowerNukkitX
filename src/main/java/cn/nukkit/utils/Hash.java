package cn.nukkit.utils;

import cn.nukkit.math.Vector3;

public class Hash {
    public static long hashBlock(int x, int y, int z) {
        return ((long) y << 52) + (((long) z & 0x3ffffff) << 26) + ((long) x & 0x3ffffff);
        //return y + (((long) x & 0x3FFFFFF) << 8) + (((long) z & 0x3FFFFFF) << 34);
    }

    public static int hashBlockX(long triple) {
        return (int) (((triple & 0x3ffffff) << 38) >> 38);
        //return (int) ((((triple >> 8) & 0x3FFFFFF) << 38) >> 38);
    }

    public static int hashBlockY(long triple) {
        return (int) (triple >> 52);
        //return (int) (triple & 0xFF);
    }

    public static int hashBlockZ(long triple) {
        return (int) ((((triple >> 26) & 0x3ffffff) << 38) >> 38);
        //return (int) ((((triple >> 34) & 0x3FFFFFF) << 38) >> 38);
    }

    public static long hashBlock(Vector3 blockPos) {
        return hashBlock(blockPos.getFloorX(), blockPos.getFloorY(), blockPos.getFloorZ());
    }
}
