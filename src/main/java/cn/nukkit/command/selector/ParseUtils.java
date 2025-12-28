package cn.nukkit.command.selector;

import cn.nukkit.Server;
import cn.nukkit.command.exceptions.SelectorSyntaxException;

/**
 * Utility class providing static methods for parsing and validating target selector arguments in PowerNukkitX.
 * <p>
 * This class contains helper functions commonly used in the parsing and validation of Minecraft selectors (e.g., @p, @a, @e)
 * and their arguments. It supports offset parsing for coordinates, inversion checks, argument count validation,
 * value range checks, and gamemode parsing. All methods are static and designed for use in selector and command parsing logic.
 * <p>
 * Features:
 * <ul>
 *   <li>Parses offset integer and double values (e.g., ~5, ~, 10) for relative and absolute coordinates.</li>
 *   <li>Checks for argument inversion (negation with '!') and enforces non-negatable parameters.</li>
 *   <li>Validates that only a single argument is provided for required parameters.</li>
 *   <li>Checks if a value is within a specified range, regardless of bound order.</li>
 *   <li>Parses gamemode tokens (e.g., s, survival, 0, c, creative, 1, etc.) to their numeric IDs, including default mode.</li>
 *   <li>Throws {@link cn.nukkit.command.exceptions.SelectorSyntaxException} for invalid syntax or values.</li>
 * </ul>
 * <p>
 * Usage:
 * <ul>
 *   <li>Call static methods for parsing and validation during selector argument processing.</li>
 *   <li>Use {@link #parseOffsetInt(String, int)} and {@link #parseOffsetDouble(String, double)} for coordinate parsing.</li>
 *   <li>Use {@link #checkReversed(String)} and {@link #cannotReversed(String)} for inversion checks.</li>
 *   <li>Use {@link #singleArgument(String[], String)} to enforce single-value arguments.</li>
 *   <li>Use {@link #checkBetween(double, double, double)} for range validation.</li>
 *   <li>Use {@link #parseGameMode(String)} to convert gamemode tokens to numeric IDs.</li>
 * </ul>
 * <p>
 * Example:
 * <pre>
 * int x = ParseUtils.parseOffsetInt("~5", 100); // 105
 * boolean inverted = ParseUtils.checkReversed("!zombie"); // true
 * ParseUtils.cannotReversed("!player"); // throws SelectorSyntaxException
 * ParseUtils.singleArgument(args, "type");
 * boolean inRange = ParseUtils.checkBetween(1, 10, 5); // true
 * int gamemode = ParseUtils.parseGameMode("creative"); // 1
 * </pre>
 *
 * @author PowerNukkitX Project Team
 * @since PowerNukkitX 2.0.0
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
