package org.powernukkitx.molang;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * The {@code math.*} functions Molang defines.
 * <p>
 * The trigonometric functions take and return degrees, as Molang does, not radians.
 * Calling one with too few arguments yields {@code 0} rather than failing, so a pack that
 * writes a call the wrong way loses that expression and nothing else.
 */
final class MolangMath {

    private MolangMath() {
    }

    /**
     * @param name      the function name, lower-cased, without the {@code math.} prefix
     * @param arguments the parsed argument expressions
     * @param parser    the parser to raise an error through for an unknown function
     */
    static MolangExpression.Node node(String name, List<MolangExpression.Node> arguments, MolangParser parser) {
        List<MolangExpression.Node> args = List.copyOf(arguments);
        return switch (name) {
            case "pi" -> context -> Math.PI;
            case "abs" -> unary(args, Math::abs);
            case "sin" -> unary(args, value -> Math.sin(Math.toRadians(value)));
            case "cos" -> unary(args, value -> Math.cos(Math.toRadians(value)));
            case "asin" -> unary(args, value -> Math.toDegrees(Math.asin(value)));
            case "acos" -> unary(args, value -> Math.toDegrees(Math.acos(value)));
            case "atan" -> unary(args, value -> Math.toDegrees(Math.atan(value)));
            case "atan2" -> binary(args, (y, x) -> Math.toDegrees(Math.atan2(y, x)));
            case "ceil" -> unary(args, Math::ceil);
            case "floor" -> unary(args, Math::floor);
            case "round" -> unary(args, value -> (double) Math.round(value));
            case "trunc" -> unary(args, value -> (double) (long) value);
            case "sqrt" -> unary(args, Math::sqrt);
            case "exp" -> unary(args, Math::exp);
            case "ln" -> unary(args, Math::log);
            case "pow" -> binary(args, Math::pow);
            case "max" -> binary(args, Math::max);
            case "min" -> binary(args, Math::min);
            case "mod" -> binary(args, (value, divisor) -> divisor == 0.0d ? 0.0d : value % divisor);
            case "clamp" -> clamp(args);
            case "lerp" -> lerp(args);
            case "random" -> random(args);
            case "random_integer" -> randomInteger(args);
            case "die_roll" -> dieRoll(args, false);
            case "die_roll_integer" -> dieRoll(args, true);
            case "hermite_blend" -> unary(args, value -> (3.0d * value * value) - (2.0d * value * value * value));
            default -> throw parser.error("Unknown math function 'math." + name + "'");
        };
    }

    private interface DoubleUnary {
        double apply(double value);
    }

    private interface DoubleBinary {
        double apply(double left, double right);
    }

    private static MolangExpression.Node unary(List<MolangExpression.Node> args, DoubleUnary function) {
        if (args.isEmpty()) {
            return context -> 0.0d;
        }
        MolangExpression.Node argument = args.getFirst();
        return context -> function.apply(MolangValue.asDouble(argument.evaluate(context)));
    }

    private static MolangExpression.Node binary(List<MolangExpression.Node> args, DoubleBinary function) {
        if (args.size() < 2) {
            return context -> 0.0d;
        }
        MolangExpression.Node left = args.get(0);
        MolangExpression.Node right = args.get(1);
        return context -> function.apply(
                MolangValue.asDouble(left.evaluate(context)),
                MolangValue.asDouble(right.evaluate(context)));
    }

    private static MolangExpression.Node clamp(List<MolangExpression.Node> args) {
        if (args.size() < 3) {
            return context -> 0.0d;
        }
        MolangExpression.Node value = args.get(0);
        MolangExpression.Node min = args.get(1);
        MolangExpression.Node max = args.get(2);
        return context -> {
            double lower = MolangValue.asDouble(min.evaluate(context));
            double upper = MolangValue.asDouble(max.evaluate(context));
            return Math.max(lower, Math.min(upper, MolangValue.asDouble(value.evaluate(context))));
        };
    }

    private static MolangExpression.Node lerp(List<MolangExpression.Node> args) {
        if (args.size() < 3) {
            return context -> 0.0d;
        }
        MolangExpression.Node from = args.get(0);
        MolangExpression.Node to = args.get(1);
        MolangExpression.Node amount = args.get(2);
        return context -> {
            double start = MolangValue.asDouble(from.evaluate(context));
            double end = MolangValue.asDouble(to.evaluate(context));
            double blend = Math.max(0.0d, Math.min(1.0d, MolangValue.asDouble(amount.evaluate(context))));
            return start + (end - start) * blend;
        };
    }

    private static MolangExpression.Node random(List<MolangExpression.Node> args) {
        if (args.size() < 2) {
            return context -> ThreadLocalRandom.current().nextDouble();
        }
        MolangExpression.Node low = args.get(0);
        MolangExpression.Node high = args.get(1);
        return context -> {
            double min = MolangValue.asDouble(low.evaluate(context));
            double max = MolangValue.asDouble(high.evaluate(context));
            return max <= min ? min : ThreadLocalRandom.current().nextDouble(min, max);
        };
    }

    private static MolangExpression.Node randomInteger(List<MolangExpression.Node> args) {
        if (args.size() < 2) {
            return context -> 0.0d;
        }
        MolangExpression.Node low = args.get(0);
        MolangExpression.Node high = args.get(1);
        return context -> {
            long min = (long) MolangValue.asDouble(low.evaluate(context));
            long max = (long) MolangValue.asDouble(high.evaluate(context));
            return max <= min ? (double) min : (double) ThreadLocalRandom.current().nextLong(min, max + 1);
        };
    }

    private static MolangExpression.Node dieRoll(List<MolangExpression.Node> args, boolean integer) {
        if (args.size() < 3) {
            return context -> 0.0d;
        }
        MolangExpression.Node rolls = args.get(0);
        MolangExpression.Node low = args.get(1);
        MolangExpression.Node high = args.get(2);
        return context -> {
            int count = (int) MolangValue.asDouble(rolls.evaluate(context));
            double min = MolangValue.asDouble(low.evaluate(context));
            double max = MolangValue.asDouble(high.evaluate(context));
            double total = 0.0d;
            for (int i = 0; i < count; i++) {
                if (max <= min) {
                    total += min;
                } else if (integer) {
                    total += ThreadLocalRandom.current().nextLong((long) min, (long) max + 1);
                } else {
                    total += ThreadLocalRandom.current().nextDouble(min, max);
                }
            }
            return total;
        };
    }
}
