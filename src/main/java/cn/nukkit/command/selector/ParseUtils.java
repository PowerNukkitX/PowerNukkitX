package cn.nukkit.command.selector;

import cn.nukkit.Server;
import cn.nukkit.command.exceptions.SelectorSyntaxException;

/**
 * Some commonly used static functions for target selector parsing
 */


public class ParseUtils {
    /**
     * Parse offset int value
     * @param value text
     * @param base Base Value
     * @return Offset value
     */
    public static int parseOffsetInt(String value, int base) throws SelectorSyntaxException {
        try {
            if (value.startsWith("~")) {
                return value.length() != 1 ? base + Integer.parseInt(value.substring(1)) : base;
            } else {
                return Integer.parseInt(value);
            }
        } catch (NumberFormatException e) {
            throw new SelectorSyntaxException("Error parsing target selector", e);
        }
    }

    /**
     * Parsing offset double value
     * @param value text
     * @param base Base Value
     * @return Offset value
     */
    public static double parseOffsetDouble(String value, double base) throws SelectorSyntaxException {
        try {
            if (value.startsWith("~")) {
                return value.length() != 1 ? base + Double.parseDouble(value.substring(1)) : base;
            } else {
                return Double.parseDouble(value);
            }
        } catch (NumberFormatException e) {
            throw new SelectorSyntaxException("Error parsing target selector", e);
        }
    }

    /**
     * Check if the argument is inverted
     * @param value Given a string
     * @return Whether to reverse
     */
    public static boolean checkReversed(String value) {
        return value.startsWith("!");
    }

    /**
     * Parameters cannot be negated. If they are negated, an{@link SelectorSyntaxException}
     * @param value Given a string
     */
    public static void cannotReversed(String value) throws SelectorSyntaxException {
        if (checkReversed(value))
            throw new SelectorSyntaxException("Argument cannot be reversed!");
    }

    /**
     * The required parameter cannot be more than 1
     * @param args Parameter List
     * @param keyName Parameter key name
     */
    public static void singleArgument(String[] args, String keyName) throws SelectorSyntaxException {
        if (args.length > 1)
            throw new SelectorSyntaxException("Multiple arguments is not allow in arg " + keyName);
    }

    /**
     * Checks if a given value is between two given numbers
     * @param bound1 Boundary 1
     * @param bound2 Boundary 2
     * @param value Value
     * @return Is the given value between two given numbers?
     */
    public static boolean checkBetween(double bound1, double bound2, double value) {
        return bound1 < bound2 ?
                (value >= bound1 && value <= bound2) :
                (value >= bound2 && value <= bound1);
    }

    /**
     * Parses a gamemode number from a given gamemode string<p/>
     * This method can match parameters with the same predetermined value as the original selector parameter m.
     * @param token String
     * @return Game Mode Numbers
     */
    public static int parseGameMode(String token) throws SelectorSyntaxException {
        return switch (token) {
            case "s", "survival", "0" -> 0;
            case "c", "creative", "1" -> 1;
            case "a", "adventure", "2" -> 2;
            case "spectator", "3" -> 3;
            case "d", "default" -> Server.getInstance().getDefaultGamemode();
            default -> throw new SelectorSyntaxException("Unknown gamemode token: " + token);
        };
    }
}
