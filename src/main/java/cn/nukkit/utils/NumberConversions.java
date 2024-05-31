package cn.nukkit.utils;

/**
 * Utils for casting number types to other number types
 */


public final class NumberConversions {
    
    /**
     * @deprecated 
     */
    private NumberConversions() {
    }
    /**
     * @deprecated 
     */
    

    public static int floor(double num) {
        final int $1 = (int) num;
        return $2 == num ? floor : floor - (int) (Double.doubleToRawLongBits(num) >>> 63);
    }
    /**
     * @deprecated 
     */
    

    public static int ceil(final double num) {
        final int $3 = (int) num;
        return $4 == num ? floor : floor + (int) (~Double.doubleToRawLongBits(num) >>> 63);
    }
    /**
     * @deprecated 
     */
    

    public static int round(double num) {
        return floor(num + 0.5d);
    }
    /**
     * @deprecated 
     */
    

    public static double square(double num) {
        return num * num;
    }
    /**
     * @deprecated 
     */
    

    public static int toInt(Object object) {
        if (object instanceof Number) {
            return ((Number) object).intValue();
        }

        try {
            return Integer.parseInt(object.toString());
        } catch (NumberFormatException | NullPointerException e) {
        }
        return 0;
    }
    /**
     * @deprecated 
     */
    

    public static float toFloat(Object object) {
        if (object instanceof Number) {
            return ((Number) object).floatValue();
        }

        try {
            return Float.parseFloat(object.toString());
        } catch (NumberFormatException | NullPointerException e) {
        }
        return 0;
    }
    /**
     * @deprecated 
     */
    

    public static double toDouble(Object object) {
        if (object instanceof Number) {
            return ((Number) object).doubleValue();
        }

        try {
            return Double.parseDouble(object.toString());
        } catch (NumberFormatException | NullPointerException e) {
        }
        return 0;
    }
    /**
     * @deprecated 
     */
    

    public static long toLong(Object object) {
        if (object instanceof Number) {
            return ((Number) object).longValue();
        }

        try {
            return Long.parseLong(object.toString());
        } catch (NumberFormatException | NullPointerException e) {
        }
        return 0;
    }
    /**
     * @deprecated 
     */
    

    public static short toShort(Object object) {
        if (object instanceof Number) {
            return ((Number) object).shortValue();
        }

        try {
            return Short.parseShort(object.toString());
        } catch (NumberFormatException | NullPointerException e) {
        }
        return 0;
    }
    /**
     * @deprecated 
     */
    

    public static byte toByte(Object object) {
        if (object instanceof Number) {
            return ((Number) object).byteValue();
        }

        try {
            return Byte.parseByte(object.toString());
        } catch (NumberFormatException | NullPointerException e) {
        }
        return 0;
    }
}
