package org.powernukkitx.molang;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Covers compiling and evaluating Molang. The block-state cases mirror the shape behavior
 * packs actually use for permutation conditions, which is overwhelmingly
 * {@code q.block_state(...)} compared against a string, boolean or number.
 */
class MolangExpressionTest {

    /** Answers {@code query.block_state('name')} from a map, like a block's states. */
    private static final class BlockStateContext implements MolangContext {

        private final Map<String, Object> states = new HashMap<>();
        private final Map<String, Object> variables = new HashMap<>();

        BlockStateContext set(String name, Object value) {
            states.put(name, value);
            return this;
        }

        @Override
        public @Nullable Object query(@NotNull String name, @NotNull Object[] arguments) {
            if (("block_state".equals(name) || "block_property".equals(name)) && arguments.length == 1) {
                return states.get(String.valueOf(arguments[0]));
            }
            return null;
        }

        @Override
        public @Nullable Object variable(@NotNull String name) {
            return variables.get(name);
        }

        @Override
        public void setVariable(@NotNull String name, @Nullable Object value) {
            variables.put(name, value);
        }
    }

    private static boolean condition(String source, BlockStateContext context) {
        return MolangExpression.compile(source).evaluateAsBoolean(context);
    }

    private static double number(String source) {
        return MolangExpression.compile(source).evaluateAsDouble(MolangContext.EMPTY);
    }

    @Test
    void evaluatesArithmeticWithPrecedence() {
        assertEquals(7.0d, number("1 + 2 * 3"));
        assertEquals(9.0d, number("(1 + 2) * 3"));
        assertEquals(-5.0d, number("-5"));
        assertEquals(2.5d, number("5 / 2"));
        assertEquals(1.0d, number("7 - 3 * 2"));
    }

    @Test
    void treatsDivisionByZeroAsZero() {
        assertEquals(0.0d, number("5 / 0"));
        assertEquals(0.0d, number("math.mod(5, 0)"));
    }

    @Test
    void evaluatesComparisonAndLogic() {
        assertTrue(number("1 == 1") != 0);
        assertTrue(number("2 >= 2") != 0);
        assertTrue(number("1 < 2 && 3 > 2") != 0);
        assertTrue(number("1 > 2 || 3 > 2") != 0);
        assertFalse(number("!1") != 0);
        assertTrue(number("!0") != 0);
        assertTrue(number("1 != 2") != 0);
    }

    @Test
    void comparesBlockStateAgainstStringLiteral() {
        BlockStateContext context = new BlockStateContext().set("minecraft:cardinal_direction", "north");

        assertTrue(condition("query.block_state('minecraft:cardinal_direction') == 'north'", context));
        assertFalse(condition("query.block_state('minecraft:cardinal_direction') == 'south'", context));
        assertTrue(condition("q.block_state('minecraft:cardinal_direction') != 'south'", context));
    }

    @Test
    void comparesBlockStateAgainstBooleanAndNumber() {
        BlockStateContext context = new BlockStateContext()
                .set("ff:top_bit", Boolean.TRUE)
                .set("ff:bottom_bit", Boolean.FALSE)
                .set("ef:colors", 7);

        assertTrue(condition("q.block_state('ff:top_bit') == true", context));
        assertTrue(condition("q.block_state('ff:bottom_bit') == false", context));
        assertTrue(condition("q.block_state('ef:colors') == 7", context));
        assertFalse(condition("q.block_state('ef:colors') == 8", context));
    }

    @Test
    void treatsBareBlockStateAsCondition() {
        BlockStateContext context = new BlockStateContext()
                .set("ff:has_slab", Boolean.TRUE)
                .set("ff:has_flower", Boolean.FALSE);

        assertTrue(condition("q.block_state('ff:has_slab')", context));
        assertFalse(condition("q.block_state('ff:has_flower')", context));
        assertTrue(condition("!q.block_state('ff:has_flower')", context));
    }

    @Test
    void evaluatesRealPackCondition() {
        BlockStateContext context = new BlockStateContext()
                .set("ff:has_flower", "small")
                .set("minecraft:cardinal_direction", "south")
                .set("ff:has_slab", Boolean.TRUE)
                .set("minecraft:vertical_half", "bottom");

        String source = "(q.block_state('ff:has_flower') == 'small' || q.block_state('ff:has_flower') == 'none')"
                + " && q.block_state('minecraft:cardinal_direction') == 'south'"
                + " && q.block_state('ff:has_slab')"
                + " && q.block_state('minecraft:vertical_half') == 'bottom'";

        assertTrue(condition(source, context));

        context.set("minecraft:vertical_half", "top");
        assertFalse(condition(source, context));

        context.set("minecraft:vertical_half", "bottom").set("ff:has_flower", "large");
        assertFalse(condition(source, context));
    }

    @Test
    void unknownQueryReadsAsZero() {
        BlockStateContext context = new BlockStateContext();

        assertFalse(condition("q.block_state('does:not_exist')", context));
        assertTrue(condition("q.block_state('does:not_exist') == 0", context));
        assertFalse(condition("q.nonexistent_accessor", context));
    }

    @Test
    void evaluatesMathFunctions() {
        assertEquals(5.0d, number("math.abs(-5)"));
        assertEquals(3.0d, number("math.floor(3.7)"));
        assertEquals(4.0d, number("math.ceil(3.2)"));
        assertEquals(4.0d, number("math.round(3.5)"));
        assertEquals(3.0d, number("math.trunc(3.9)"));
        assertEquals(4.0d, number("math.sqrt(16)"));
        assertEquals(8.0d, number("math.pow(2, 3)"));
        assertEquals(2.0d, number("math.max(1, 2)"));
        assertEquals(1.0d, number("math.min(1, 2)"));
        assertEquals(1.0d, number("math.mod(7, 2)"));
        assertEquals(5.0d, number("math.clamp(10, 1, 5)"));
        assertEquals(1.5d, number("math.lerp(1, 2, 0.5)"));
        assertEquals(Math.PI, number("math.pi"));
    }

    @Test
    void trigonometryUsesDegrees() {
        assertEquals(1.0d, number("math.sin(90)"), 1.0e-9);
        assertEquals(0.0d, number("math.cos(90)"), 1.0e-9);
        assertEquals(90.0d, number("math.asin(1)"), 1.0e-9);
    }

    @Test
    void randomStaysWithinBounds() {
        for (int i = 0; i < 100; i++) {
            double value = number("math.random(2, 5)");
            assertTrue(value >= 2.0d && value <= 5.0d, "out of range: " + value);
            double integer = number("math.random_integer(1, 3)");
            assertTrue(integer >= 1.0d && integer <= 3.0d, "out of range: " + integer);
        }
    }

    @Test
    void evaluatesTernary() {
        assertEquals(1.0d, number("1 == 1 ? 1 : 2"));
        assertEquals(2.0d, number("1 == 2 ? 1 : 2"));
        assertEquals(0.0d, number("1 == 2 ? 1"));
    }

    @Test
    void nullCoalescingDistinguishesAbsentFromZero() {
        BlockStateContext context = new BlockStateContext().set("ef:colors", 0);

        assertEquals(3.0d, MolangExpression.compile("v.unset ?? 3").evaluateAsDouble(context),
                "an unresolved accessor must fall through");
        assertEquals(3.0d, MolangExpression.compile("q.block_state('does:not_exist') ?? 3").evaluateAsDouble(context),
                "an unresolved query must fall through");
        assertEquals(0.0d, MolangExpression.compile("q.block_state('ef:colors') ?? 3").evaluateAsDouble(context),
                "a resolved zero is a value and must not fall through");

        context.setVariable("set", 4);
        assertEquals(4.0d, MolangExpression.compile("v.set ?? 3").evaluateAsDouble(context));
    }

    @Test
    void evaluatesVariablesAndAssignment() {
        BlockStateContext context = new BlockStateContext();

        assertEquals(0.0d, MolangExpression.compile("v.unset").evaluateAsDouble(context));
        assertEquals(4.0d, MolangExpression.compile("v.count = 4").evaluateAsDouble(context));
        assertEquals(4.0d, MolangExpression.compile("v.count").evaluateAsDouble(context));
        assertEquals(9.0d, MolangExpression.compile("v.count = 9; v.count").evaluateAsDouble(context));
    }

    @Test
    void evaluatesStatementsAndReturn() {
        assertEquals(2.0d, number("1; 2"));
        assertEquals(7.0d, number("return 7"));
        assertEquals(7.0d, number("return 7;"));
    }

    @Test
    void isCaseInsensitiveForAccessors() {
        BlockStateContext context = new BlockStateContext().set("ff:top_bit", Boolean.TRUE);

        assertTrue(condition("Q.BLOCK_STATE('ff:top_bit')", context));
        assertTrue(condition("Query.Block_State('ff:top_bit')", context));
        assertEquals(5.0d, number("MATH.ABS(-5)"));
    }

    @Test
    void acceptsBothQuoteStyles() {
        BlockStateContext context = new BlockStateContext().set("a", "b");

        assertTrue(condition("q.block_state('a') == 'b'", context));
        assertTrue(condition("q.block_state(\"a\") == \"b\"", context));
    }

    @Test
    void malformedExpressionCompilesToZeroInsteadOfThrowing() {
        assertSame(MolangExpression.ZERO, MolangExpression.compile("1 +"));
        assertSame(MolangExpression.ZERO, MolangExpression.compile("(1"));
        assertSame(MolangExpression.ZERO, MolangExpression.compile("'unterminated"));
        assertSame(MolangExpression.ZERO, MolangExpression.compile("math.not_a_function(1)"));
        assertSame(MolangExpression.ZERO, MolangExpression.compile(null));
        assertSame(MolangExpression.ZERO, MolangExpression.compile("   "));
        assertEquals(0.0d, MolangExpression.compile("1 +").evaluateAsDouble(MolangContext.EMPTY));
    }

    @Test
    void parseReportsMalformedExpression() {
        MolangParseException thrown = assertThrows(MolangParseException.class, () -> MolangExpression.parse("(1"));

        assertEquals("(1", thrown.getExpression());
        assertTrue(thrown.getMessage().contains("Unclosed parenthesis"));
    }

    @Test
    void compilationIsCached() {
        String source = "q.block_state('pnxtest:cached') == 1";

        MolangExpression first = MolangExpression.compile(source);
        MolangExpression second = MolangExpression.compile(source);

        assertSame(first, second, "compiling the same source twice must reuse the compilation");
    }

    @Test
    void ignoresWhitespaceAndTrailingSemicolon() {
        assertEquals(3.0d, number("  1   +   2  "));
        assertEquals(3.0d, number("1 + 2;"));
        assertEquals(3.0d, number("\n1 +\t2\n"));
    }
}
