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
    @Timeout(value = 30, unit = TimeUnit.SECONDS)
    void parsingManyQuotesStaysLinear() {
        // quadratic parsing needs ~10^12 character moves for this input and cannot finish in time
        assertNotNull(SimpleCommandMap.parseArguments("help [" + "\"".repeat(1_000_000)));
    }
}
