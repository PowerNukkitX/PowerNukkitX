package org.powernukkitx.wizard;

import org.junit.jupiter.api.Test;
import org.powernukkitx.lang.BaseLang;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Drives {@link SetupWizard} through a {@link ScriptedWizardConsole}. Answers are consumed in the order the
 * wizard asks: language, license, skip question, the questions of the path taken, then the final ENTER.
 */
class SetupWizardFlowTest {

    private final AtomicInteger localLookups = new AtomicInteger();
    private final AtomicInteger externalLookups = new AtomicInteger();

    private WizardEnvironment environment(boolean interactive, boolean unicode) {
        Map<String, String> languages = new LinkedHashMap<>();
        languages.put("eng", "English");
        return new WizardEnvironment(interactive, unicode, languages,
                code -> new BaseLang(code, "src/main/resources/language/"),
                () -> {
                    localLookups.incrementAndGet();
                    return "192.0.2.1";
                },
                () -> {
                    externalLookups.incrementAndGet();
                    return "203.0.113.7";
                });
    }

    private WizardConfig run(ScriptedWizardConsole console, String serverName, Integer port) {
        try (SetupWizard wizard = new SetupWizard(console, environment(true, true))) {
            return wizard.run(null, false, false, serverName, port);
        }
    }

    @Test
    void skippingKeepsDefaultsAndShowsFinalScreenWithoutRecap() {
        var console = new ScriptedWizardConsole("", "y", "", "");
        WizardConfig config = run(console, null, null);
        assertTrue(config.isLicenseAccepted());
        assertEquals("eng", config.getLanguage());
        assertEquals(19132, config.getPort());
        assertEquals("PowerNukkitX Server", config.getMotd());
        assertTrue(console.transcript().contains("Configuration complete!"));
        assertEquals(0, console.remaining());
    }

    @Test
    void refusingTheLicenseStopsTheWizard() {
        for (String answer : new String[]{"n", ""}) {
            var console = new ScriptedWizardConsole("", answer);
            assertFalse(run(console, null, null).isLicenseAccepted());
            assertEquals(2, console.reads());
            assertEquals(1, count(console.transcript(), "License not accepted."));
        }
    }

    private static int count(String text, String part) {
        return text.split(Pattern.quote(part), -1).length - 1;
    }

    @Test
    void nonInteractiveNeverReadsTheKeyboard() {
        var console = new ScriptedWizardConsole();
        WizardConfig config;
        try (SetupWizard wizard = new SetupWizard(console, environment(false, true))) {
            config = wizard.run(null, false, false, null, null);
        }
        assertEquals(0, console.reads());
        assertTrue(config.isLicenseAccepted());
        assertEquals(20, config.getMaxPlayers());
    }

    @Test
    void fullWalkStoresEveryAnswer() {
        var console = new ScriptedWizardConsole("", "y", "n",
                "My server", " 25565 ", "1", "50", "Admin1, Admin2", " Y ", "Friend1", "NO", "");
        WizardConfig config = run(console, null, null);
        assertEquals("My server", config.getMotd());
        assertEquals(25565, config.getPort());
        assertEquals(1, config.getGamemode());
        assertEquals(50, config.getMaxPlayers());
        assertEquals(List.of("Admin1", "Admin2"), config.getOperators());
        assertTrue(config.isWhitelistEnabled());
        assertEquals(List.of("Friend1"), config.getWhitelistedPlayers());
        assertFalse(config.isQueryEnabled());
        assertTrue(console.transcript().contains("Step 2/7 - Server port"));
        assertTrue(console.transcript().contains("Server port [19132]: "));
        assertEquals(0, console.remaining());
    }

    @Test
    void enterOnEveryQuestionKeepsDefaults() {
        var console = new ScriptedWizardConsole("", "y", "n", "", "", "", "", "", "", "", "");
        WizardConfig config = run(console, null, null);
        assertEquals("PowerNukkitX Server", config.getMotd());
        assertEquals(19132, config.getPort());
        assertEquals(0, config.getGamemode());
        assertEquals(20, config.getMaxPlayers());
        assertTrue(config.getOperators().isEmpty());
        assertFalse(config.isWhitelistEnabled());
        assertTrue(config.isQueryEnabled());
        assertEquals(0, console.remaining());
    }

    @Test
    void invalidNumbersAreRejectedThenAccepted() {
        var console = new ScriptedWizardConsole("", "y", "n",
                "", "abc", "70000", "19133", "9", "2", "0", "10", "", "", "maybe", "y", "");
        WizardConfig config = run(console, null, null);
        assertEquals(19133, config.getPort());
        assertEquals(2, config.getGamemode());
        assertEquals(10, config.getMaxPlayers());
        String out = console.transcript();
        assertTrue(out.contains("Invalid port number. Please enter a valid number."));
        assertTrue(out.contains("between 1 and 65535"));
        assertTrue(out.contains("Invalid input. Please enter 'y' for yes or 'n' for no."));
    }

    @Test
    void commandLineNameAndPortAreNotAskedDuringTheWalk() {
        var console = new ScriptedWizardConsole("", "y", "n", "", "", "", "", "", "");
        WizardConfig config = run(console, "Arg server", 19200);
        assertEquals("Arg server", config.getMotd());
        assertEquals(19200, config.getPort());
        assertEquals(0, console.remaining());
    }

    @Test
    void readFailureDuringTheWalkKeepsDefaultsAndReturns() {
        WizardConsole failing = new ScriptedWizardConsole("", "y", "n") {
            @Override
            public String readLine(String prompt) {
                if (remaining() == 0) {
                    throw new IllegalStateException("stdin closed");
                }
                return super.readLine(prompt);
            }
        };
        WizardConfig config = assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {
            try (SetupWizard wizard = new SetupWizard(failing, environment(true, true))) {
                return wizard.run(null, false, false, null, null);
            }
        });
        assertTrue(config.isLicenseAccepted());
        assertEquals(19132, config.getPort());
    }

    @Test
    void recapEditChangesOneValueAndLooksUpAddressesOnce() {
        var console = new ScriptedWizardConsole("", "y", "n", "", "", "", "", "", "", "", "2", "19140", "");
        WizardConfig config = run(console, null, null);
        assertEquals(19140, config.getPort());
        String out = console.transcript();
        assertEquals(2, count(out, "Review your configuration"));
        assertTrue(out.contains("203.0.113.7:19140"));
        assertFalse(out.contains("Configuration complete!"));
        assertEquals(1, localLookups.get());
        assertEquals(1, externalLookups.get());
    }

    @Test
    void recapRejectsJunkChoices() {
        var console = new ScriptedWizardConsole("", "y", "n", "", "", "", "", "", "", "",
                "0", "8", "abc", "99999999999999999999", "-1", "1.5", "");
        run(console, null, null);
        String out = console.transcript();
        assertEquals(6, count(out, "Invalid choice. Enter a number between 1 and 7"));
        assertEquals(1, count(out, "Review your configuration"));
    }

    @Test
    void disablingTheWhitelistFromTheRecapClearsItsNames() {
        var console = new ScriptedWizardConsole("", "y", "n", "", "", "", "", "", "y", "A, B", "",
                "6", "n", "");
        WizardConfig config = run(console, null, null);
        assertFalse(config.isWhitelistEnabled());
        assertTrue(config.getWhitelistedPlayers().isEmpty());
    }

    @Test
    void commandLineValuesAppearInTheRecapAndCanBeEdited() {
        var console = new ScriptedWizardConsole("", "y", "n", "", "", "", "", "", "1", "Renamed", "");
        WizardConfig config = run(console, "Arg server", 19200);
        assertEquals("Renamed", config.getMotd());
        assertEquals(19200, config.getPort());
        assertTrue(console.transcript().contains("192.0.2.1:19200"));
    }

    @Test
    void namesInputWithoutAnyNameKeepsTheCurrentList() {
        var console = new ScriptedWizardConsole("", "y", "n", "", "", "", "", "Admin1", "", "",
                "5", ", ,", "");
        assertEquals(List.of("Admin1"), run(console, null, null).getOperators());
        assertEquals(0, console.remaining());
    }

    @Test
    void serverNameIsShownVerbatimInTheRecap() {
        var console = new ScriptedWizardConsole("", "y", "n", "pnx.setupWizard.skip", "", "", "", "", "", "", "");
        WizardConfig config = run(console, null, null);
        assertEquals("pnx.setupWizard.skip", config.getMotd());
        assertTrue(console.transcript().contains(": pnx.setupWizard.skip"));
    }

    @Test
    void outputIsAsciiWhenUnicodeIsUnsupported() {
        var console = new ScriptedWizardConsole("", "y", "n", "", "", "", "", "", "", "", "");
        try (SetupWizard wizard = new SetupWizard(console, environment(true, false))) {
            wizard.run(null, false, false, null, null);
        }
        assertTrue(console.transcript().contains("Review your configuration"));
        assertTrue(StandardCharsets.US_ASCII.newEncoder().canEncode(console.transcript()));
    }

    @Test
    void readFailureAtTheRecapStartsTheServer() {
        WizardConsole failing = new ScriptedWizardConsole("", "y", "n", "", "", "", "", "", "", "") {
            @Override
            public String readLine(String prompt) {
                if (remaining() == 0) {
                    throw new IllegalStateException("stdin closed");
                }
                return super.readLine(prompt);
            }
        };
        WizardConfig config = assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {
            try (SetupWizard wizard = new SetupWizard(failing, environment(true, true))) {
                return wizard.run(null, false, false, null, null);
            }
        });
        assertTrue(config.isLicenseAccepted());
        assertTrue(((ScriptedWizardConsole) failing).transcript().contains("Review your configuration"));
    }
}
