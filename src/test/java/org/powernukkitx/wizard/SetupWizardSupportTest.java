package org.powernukkitx.wizard;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SetupWizardSupportTest {

    @Test
    void normalizesAndValidatesKnownLanguage() {
        assertEquals("eng", SetupWizardSupport.normalizeLanguageCode(" ENG "));
        assertTrue(SetupWizardSupport.isLanguageCodeFormat("eng"));
        assertFalse(SetupWizardSupport.isLanguageCodeFormat("../eng"));
        assertTrue(SetupWizardSupport.isKnownLanguage("ENG", Set.of("eng", "fra")));
        assertFalse(SetupWizardSupport.isKnownLanguage("spa", Set.of("eng", "fra")));
    }

    @Test
    void parsesPortWithDefaultAndRangeValidation() {
        assertEquals(19132, SetupWizardSupport.parsePortOrDefault("", 19132));
        assertEquals(19133, SetupWizardSupport.parsePortOrDefault("19133", 19132));
        assertThrows(IllegalArgumentException.class, () -> SetupWizardSupport.parsePortOrDefault("0", 19132));
        assertThrows(IllegalArgumentException.class, () -> SetupWizardSupport.parsePortOrDefault("65536", 19132));
        assertThrows(NumberFormatException.class, () -> SetupWizardSupport.parsePortOrDefault("abc", 19132));
    }

    @Test
    void parsesGamemodeWithDefaultAndRangeValidation() {
        assertEquals(0, SetupWizardSupport.parseGamemodeOrDefault("", 0));
        assertEquals(3, SetupWizardSupport.parseGamemodeOrDefault("3", 0));
        assertThrows(IllegalArgumentException.class, () -> SetupWizardSupport.parseGamemodeOrDefault("4", 0));
        assertThrows(NumberFormatException.class, () -> SetupWizardSupport.parseGamemodeOrDefault("survival", 0));
    }

    @Test
    void parsesMaxPlayersWithDefaultAndPositiveValidation() {
        assertEquals(20, SetupWizardSupport.parseMaxPlayersOrDefault("", 20));
        assertEquals(50, SetupWizardSupport.parseMaxPlayersOrDefault("50", 20));
        assertThrows(IllegalArgumentException.class, () -> SetupWizardSupport.parseMaxPlayersOrDefault("0", 20));
        assertThrows(NumberFormatException.class, () -> SetupWizardSupport.parseMaxPlayersOrDefault("many", 20));
    }

    @Test
    void parsesYesNoAndFallsBackToDefaultForInvalidInput() {
        assertTrue(SetupWizardSupport.parseYesNoOrDefault("", true));
        assertFalse(SetupWizardSupport.parseYesNoOrDefault("", false));
        assertTrue(SetupWizardSupport.parseYesNoOrDefault("YES", false));
        assertFalse(SetupWizardSupport.parseYesNoOrDefault("n", true));
        assertTrue(SetupWizardSupport.parseYesNoOrDefault("maybe", true));
        assertFalse(SetupWizardSupport.parseYesNoOrDefault("maybe", false));
    }

    @Test
    void parsesCommaSeparatedNames() {
        assertEquals(
                java.util.List.of("Admin1", "Admin2", "Admin3"),
                SetupWizardSupport.parseCommaSeparatedNames(" Admin1, ,Admin2,Admin3 ")
        );
        assertTrue(SetupWizardSupport.parseCommaSeparatedNames("   ").isEmpty());
        assertTrue(SetupWizardSupport.parseCommaSeparatedNames(null).isEmpty());
    }

    @Test
    void detectsAutomatedEnvironment() {
        assertTrue(SetupWizardSupport.isAutomatedEnvironment(Map.of("CI", "true")));
        assertTrue(SetupWizardSupport.isAutomatedEnvironment(Map.of("PNX_SETUP_NON_INTERACTIVE", "1")));
        assertFalse(SetupWizardSupport.isAutomatedEnvironment(Map.of("CI", "false")));
        assertFalse(SetupWizardSupport.isAutomatedEnvironment(Map.of()));
    }

    @Test
    void detectsUnicodeCapabilityFromCharset() {
        assertTrue(SetupWizardSupport.supportsUnicodeOutput(StandardCharsets.UTF_8));
        assertFalse(SetupWizardSupport.supportsUnicodeOutput(StandardCharsets.US_ASCII));
    }

    @Test
    void formatsPromptsWithCurrentValueAndYesNoHint() {
        assertEquals("Server port [19132]: ", SetupWizardSupport.formatValuePrompt("Server port", "19132"));
        assertEquals("Enable Query? (Y/n): ", SetupWizardSupport.formatYesNoPrompt("Enable Query?", true));
        assertEquals("Enable whitelist? (y/N): ", SetupWizardSupport.formatYesNoPrompt("Enable whitelist?", false));
    }

    @Test
    void parsesStrictYesNo() {
        assertEquals(Optional.of(true), SetupWizardSupport.parseYesNo("", true));
        assertEquals(Optional.of(false), SetupWizardSupport.parseYesNo("  ", false));
        assertEquals(Optional.of(true), SetupWizardSupport.parseYesNo(" YES ", false));
        assertEquals(Optional.of(true), SetupWizardSupport.parseYesNo("y", false));
        assertEquals(Optional.of(false), SetupWizardSupport.parseYesNo("No", true));
        assertEquals(Optional.empty(), SetupWizardSupport.parseYesNo("maybe", true));
        assertEquals(Optional.of(true), SetupWizardSupport.parseYesNo(null, true));
    }
}
