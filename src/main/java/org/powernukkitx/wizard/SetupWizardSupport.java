package org.powernukkitx.wizard;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Pure validation and parsing helpers for the setup wizard.
 */
public final class SetupWizardSupport {

    public static final String DEFAULT_LANGUAGE = "eng";
    public static final int DEFAULT_PORT = 19132;
    public static final int DEFAULT_GAMEMODE = 0;
    public static final int DEFAULT_MAX_PLAYERS = 20;
    private static final String LANGUAGE_CODE_PATTERN = "^[a-z]{3}$";

    private SetupWizardSupport() {
    }

    public static String normalizeLanguageCode(String input) {
        return input == null ? "" : input.trim().toLowerCase(Locale.ENGLISH);
    }

    public static boolean isLanguageCodeFormat(String languageCode) {
        return languageCode != null && languageCode.matches(LANGUAGE_CODE_PATTERN);
    }

    public static boolean isKnownLanguage(String languageCode, Set<String> availableLanguages) {
        String normalized = normalizeLanguageCode(languageCode);
        return isLanguageCodeFormat(normalized) && availableLanguages.contains(normalized);
    }

    public static int parsePortOrDefault(String input, int defaultPort) {
        String normalized = normalize(input);
        if (normalized.isEmpty()) {
            return defaultPort;
        }
        int port = Integer.parseInt(normalized);
        if (port < 1 || port > 65535) {
            throw new IllegalArgumentException("Port must be between 1 and 65535");
        }
        return port;
    }

    public static int parseGamemodeOrDefault(String input, int defaultGamemode) {
        String normalized = normalize(input);
        if (normalized.isEmpty()) {
            return defaultGamemode;
        }
        int gamemode = Integer.parseInt(normalized);
        if (gamemode < 0 || gamemode > 3) {
            throw new IllegalArgumentException("Gamemode must be between 0 and 3");
        }
        return gamemode;
    }

    public static int parseMaxPlayersOrDefault(String input, int defaultMaxPlayers) {
        String normalized = normalize(input);
        if (normalized.isEmpty()) {
            return defaultMaxPlayers;
        }
        int maxPlayers = Integer.parseInt(normalized);
        if (maxPlayers < 1) {
            throw new IllegalArgumentException("Max players must be positive");
        }
        return maxPlayers;
    }

    public static Boolean parseYesNoOrDefault(String input, boolean defaultValue) {
        String normalized = normalize(input).toLowerCase(Locale.ENGLISH);
        if (normalized.isEmpty()) {
            return defaultValue;
        }
        if (normalized.equals("y") || normalized.equals("yes")) {
            return true;
        }
        if (normalized.equals("n") || normalized.equals("no")) {
            return false;
        }
        return null;
    }

    public static List<String> parseCommaSeparatedNames(String input) {
        List<String> names = new ArrayList<>();
        if (input == null || input.trim().isEmpty()) {
            return names;
        }
        for (String name : input.split(",")) {
            String cleanedName = name.trim();
            if (!cleanedName.isEmpty()) {
                names.add(cleanedName);
            }
        }
        return names;
    }

    public static boolean supportsUnicodeOutput(Charset charset) {
        return charset.newEncoder().canEncode("✓═─»");
    }

    public static boolean isAutomatedEnvironment() {
        return Boolean.getBoolean("pnx.setupWizard.nonInteractive")
                || isAutomatedEnvironment(System.getenv());
    }

    static boolean isAutomatedEnvironment(Map<String, String> environment) {
        return isTruthy(environment.get("PNX_SETUP_NON_INTERACTIVE"))
                || isTruthy(environment.get("CI"))
                || isTruthy(environment.get("GITHUB_ACTIONS"))
                || isTruthy(environment.get("GITLAB_CI"))
                || isTruthy(environment.get("JENKINS_URL"))
                || isTruthy(environment.get("TEAMCITY_VERSION"));
    }

    static boolean isTruthy(String value) {
        if (value == null) {
            return false;
        }
        String normalized = value.trim().toLowerCase(Locale.ENGLISH);
        return normalized.equals("1")
                || normalized.equals("true")
                || normalized.equals("yes")
                || normalized.equals("on")
                || (!normalized.isEmpty()
                && !normalized.equals("false")
                && !normalized.equals("0")
                && !normalized.equals("no")
                && !normalized.equals("off"));
    }

    private static String normalize(String input) {
        return input == null ? "" : input.trim();
    }
}
