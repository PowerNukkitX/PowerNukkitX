package org.powernukkitx.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TextFormatTest {

    @Test
    void getByCharReturnsMatchingFormat() {
        assertEquals(TextFormat.RED, TextFormat.getByChar('c'));
        assertEquals(TextFormat.BOLD, TextFormat.getByChar('l'));
        assertEquals(TextFormat.RESET, TextFormat.getByChar('r'));
    }

    @Test
    void getByCharUnknownReturnsNull() {
        assertNull(TextFormat.getByChar('z'));
    }

    @Test
    void getByCharStringUsesFirstChar() {
        assertEquals(TextFormat.GOLD, TextFormat.getByChar("6x"));
        assertNull(TextFormat.getByChar((String) null));
        assertNull(TextFormat.getByChar("x"));
    }

    @Test
    void getCharMatchesConstructorCode() {
        assertEquals('c', TextFormat.RED.getChar());
        assertEquals('r', TextFormat.RESET.getChar());
    }

    @Test
    void toStringIsEscapePlusChar() {
        assertEquals("" + TextFormat.ESCAPE + 'c', TextFormat.RED.toString());
    }

    @Test
    void isColorAndIsFormatAreMutuallyExclusive() {
        assertTrue(TextFormat.RED.isColor());
        assertFalse(TextFormat.RED.isFormat());

        assertTrue(TextFormat.BOLD.isFormat());
        assertFalse(TextFormat.BOLD.isColor());

        // RESET is neither a color nor a format
        assertFalse(TextFormat.RESET.isColor());
        assertFalse(TextFormat.RESET.isFormat());
    }

    @Test
    void cleanRemovesFormatCodes() {
        String colored = TextFormat.RED + "Hello" + TextFormat.RESET + "World";
        assertEquals("HelloWorld", TextFormat.clean(colored));
    }

    @Test
    void cleanNullReturnsNull() {
        assertNull(TextFormat.clean(null));
    }

    @Test
    void colorizeTranslatesAmpersandCodes() {
        String result = TextFormat.colorize("&cHi");
        assertEquals(TextFormat.ESCAPE + "cHi", result);
    }

    @Test
    void colorizeLowercasesAndOnlyTouchesValidCodes() {
        assertEquals(TextFormat.ESCAPE + "cX", TextFormat.colorize("&CX"));
        // & not followed by a valid code stays untouched
        assertEquals("&zX", TextFormat.colorize("&zX"));
    }

    @Test
    void colorizeCustomChar() {
        assertEquals(TextFormat.ESCAPE + "aY", TextFormat.colorize('$', "$aY"));
    }

    @Test
    void getLastColorsReturnsTrailingColor() {
        String input = TextFormat.RED + "a" + TextFormat.GREEN + "b";
        assertEquals(TextFormat.GREEN.toString(), TextFormat.getLastColors(input));
    }

    @Test
    void getLastColorsEmptyWhenNoCodes() {
        assertEquals("", TextFormat.getLastColors("plain"));
    }
}
