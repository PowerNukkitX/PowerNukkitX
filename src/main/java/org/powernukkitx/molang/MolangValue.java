package org.powernukkitx.molang;

import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * Coercion rules for the values Molang expressions pass around.
 * <p>
 * Molang has one numeric type and treats booleans as {@code 1} and {@code 0}. Strings
 * exist only as literals and as values handed back by a {@link MolangContext} - block
 * states are the common case - and compare by value rather than coercing to a number.
 * Anything unknown reads as {@code 0}, which is what keeps a malformed or unsupported
 * expression from failing an evaluation outright.
 */
public final class MolangValue {

    private MolangValue() {
    }

    /**
     * Normalises a value handed in from outside into the representation the evaluator
     * works with: {@link Double}, {@link Boolean} or {@link String}.
     *
     * @param value any value, typically a block state read from a {@link MolangContext}
     * @return the normalised value; null and unrecognised types become {@code 0.0}, and
     * an enum becomes its lower-cased name so that it compares against a string literal
     * the way a block state does
     */
    public static Object normalize(@Nullable Object value) {
        return switch (value) {
            case null -> 0.0d;
            case Double d -> d;
            case Boolean b -> b;
            case String s -> s;
            case Number n -> n.doubleValue();
            case Enum<?> e -> e.name().toLowerCase(Locale.ENGLISH);
            default -> 0.0d;
        };
    }

    /**
     * @return the value as a number; booleans are {@code 1}/{@code 0}, and a string that
     * is not a number reads as {@code 0}
     */
    public static double asDouble(@Nullable Object value) {
        return switch (normalize(value)) {
            case Double d -> d;
            case Boolean b -> b ? 1.0d : 0.0d;
            case String s -> parseDouble(s);
            default -> 0.0d;
        };
    }

    /**
     * @return the value as a condition; a number is true when it is not {@code 0}, and a
     * string is true when it is not empty
     */
    public static boolean asBoolean(@Nullable Object value) {
        return switch (normalize(value)) {
            case Boolean b -> b;
            case Double d -> d != 0.0d;
            case String s -> !s.isEmpty();
            default -> false;
        };
    }

    /**
     * Compares two values the way Molang's {@code ==} does.
     * <p>
     * Two strings compare by value, so {@code 'north' == 'north'} holds. A string against
     * a non-string compares numerically, which makes {@code '1' == 1} hold. Everything
     * else compares as numbers, so {@code true == 1} holds.
     */
    public static boolean equals(@Nullable Object left, @Nullable Object right) {
        Object a = normalize(left);
        Object b = normalize(right);
        if (a instanceof String first && b instanceof String second) {
            return first.equals(second);
        }
        return asDouble(a) == asDouble(b);
    }

    private static double parseDouble(String value) {
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return 0.0d;
        }
    }
}
