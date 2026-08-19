package org.powernukkitx.command;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class SimpleCommandMapParseArgumentsTest {

    @Test
    void splitsOnSpacesAndDropsEmptyArguments() {
        assertEquals(new ArrayList<>(java.util.List.of("give", "a", "b")), SimpleCommandMap.parseArguments("give a b"));
        assertEquals(new ArrayList<>(java.util.List.of("a", "b")), SimpleCommandMap.parseArguments("  a  b  "));
        assertEquals(new ArrayList<>(), SimpleCommandMap.parseArguments(""));
    }

    @Test
    void quotesGroupArgumentsAndAreStripped() {
        assertEquals(new ArrayList<>(java.util.List.of("say", "hello world")), SimpleCommandMap.parseArguments("say \"hello world\""));
        assertEquals(new ArrayList<>(java.util.List.of("tp", "na me", "1")), SimpleCommandMap.parseArguments("tp \"na me\" 1"));
        assertEquals(new ArrayList<>(java.util.List.of("ab")), SimpleCommandMap.parseArguments("a\"\"b"));
    }

    @Test
    void squareBracketsAndBracesKeepTheirContentTogether() {
        assertEquals(new ArrayList<>(java.util.List.of("tp", "@e[type=cow x=1]", "1")), SimpleCommandMap.parseArguments("tp @e[type=cow x=1] 1"));
        assertEquals(new ArrayList<>(java.util.List.of("a", "{b c}", "d")), SimpleCommandMap.parseArguments("a {b c} d"));
    }

    @Test
    void unbalancedSquareBracketFallsBackToUngroupedParsing() {
        assertEquals(new ArrayList<>(java.util.List.of("give", "[a", "b")), SimpleCommandMap.parseArguments("give [a b"));
    }

    @Test
    void leadingBraceIsParsedInsteadOfThrowing() {
        assertDoesNotThrow(() -> SimpleCommandMap.parseArguments("{"));
        assertEquals(new ArrayList<>(java.util.List.of("{a}")), SimpleCommandMap.parseArguments("{a}"));
    }

    @Test
    @Timeout(value = 60, unit = TimeUnit.SECONDS)
    void parsingManyQuotesStaysLinear() {
        int size = 100_000;
        parseQuotes(size);
        parseQuotes(2 * size);

        long single = timeParseQuotes(size);
        long that = timeParseQuotes(2 * size);
        double growth = (double) that / Math.max(single, 1);

        assertTrue(growth < 3.0,
                "doubling the quote count multiplied the parsing cost by " + String.format("%.1f", growth)
                        + ", expected about 2 for linear parsing (" + single + " ns then " + that + " ns)");
    }

    private static long timeParseQuotes(int quotes) {
        long start = System.nanoTime();
        parseQuotes(quotes);
        return System.nanoTime() - start;
    }

    private static void parseQuotes(int quotes) {
        assertNotNull(SimpleCommandMap.parseArguments("help [" + "\"".repeat(quotes)));
    }
}
