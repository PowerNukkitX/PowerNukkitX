package cn.nukkit.wizard;

import cn.nukkit.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.ParsedLine;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.*;

/**
 * Interactive setup wizard for PowerNukkitX using JLine for better user experience.
 * Provides language selection and configuration options with navigation and auto-completion.
 * Implements AutoCloseable for proper resource management.
 *
 * @author AzaleeX
 * @author xRookieFight
 *
 * @since 17/12/2025
 */
@Slf4j
public class SetupWizard implements AutoCloseable {
    /** Regex pattern for validating language codes (3 lowercase letters) */
    private static final String LANGUAGE_CODE_PATTERN = "^[a-z]{3}$";

    private final Terminal terminal;
    private final LineReader reader;
    private final Map<String, String> availableLanguages;
    private final WizardConfig wizardConfig = new WizardConfig();
    private boolean skipWizard = false;

    public SetupWizard() throws IOException {
        this.terminal = TerminalBuilder.builder()
                .system(true)
                .jna(false)
                .dumb(true)
                .build();

        this.availableLanguages = loadAvailableLanguages();
        this.reader = LineReaderBuilder.builder()
                .terminal(terminal)
                .completer(new LanguageCompleter(availableLanguages.keySet()))
                .option(LineReader.Option.CASE_INSENSITIVE, true)
                .option(LineReader.Option.AUTO_LIST, true)
                .option(LineReader.Option.LIST_PACKED, true)
                .build();
    }

    /**
     * Loads available languages from the language.list resource file.
     *
     * @return Map of language codes to language names
     */
    private Map<String, String> loadAvailableLanguages() {
        Map<String, String> languages = new LinkedHashMap<>();
        try (InputStream languageList = getClass().getClassLoader().getResourceAsStream("language/language.list")) {
            if (languageList == null) {
                throw new IllegalStateException("language/language.list is missing. If you are running a development version, make sure you have run 'git submodule update --init --recursive'.");
            }

            try (Scanner scanner = new Scanner(languageList)) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine().trim();
                    if (line.isEmpty()) continue;

                    String[] parts = line.split("=>");
                    if (parts.length == 2) {
                        String code = parts[0].trim();
                        String name = parts[1].trim();
                        languages.put(code, name);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to load language list", e);
            // Fallback to English
            languages.put("eng", "English");
        }
        return languages;
    }

    /**
     * Runs the complete setup wizard with the new flow.
     *
     * @param predefinedLanguage Optional predefined language from command line
     * @param forceSkip If true, automatically skip the wizard
     * @return The wizard configuration
     */
    public WizardConfig run(String predefinedLanguage, boolean forceSkip) {
        try {
            String selectedLanguage = selectLanguage(predefinedLanguage);
            wizardConfig.setLanguage(selectedLanguage);

            if (!acceptLicense()) {
                terminal.writer().println();
                terminal.writer().println("[x] License not accepted. The server cannot start without accepting the license.");
                terminal.writer().println("[x] Terminating...");
                terminal.writer().flush();
                System.exit(1);
                return wizardConfig;
            }
            wizardConfig.setLicenseAccepted(true);

            if (forceSkip) {
                skipWizard = true;
                terminal.writer().println();
                terminal.writer().println("Setup wizard skipped via command line flag. Using default configuration.");
                terminal.writer().flush();
            } else {
                askSkipWizard();

                if (!skipWizard) {
                    terminal.writer().println();
                    terminal.writer().println("[*] All of these settings can be modified later in the pnx.yml configuration file.");
                    terminal.writer().println();
                    terminal.writer().flush();

                    configureServerComplete();
                }
            }

            displaySummaryAndWaitForStart();

            return wizardConfig;
        } catch (Exception e) {
            log.error("Error during setup wizard", e);
            wizardConfig.setLanguage("eng"); // Default to English on error
            return wizardConfig;
        }
    }

    /**
     * Handles the language selection process.
     *
     * @param predefinedLanguage Optional predefined language from command line
     * @return Selected language code
     */
    private String selectLanguage(String predefinedLanguage) {
        terminal.writer().println();
        terminal.writer().println("═══════════════════════════════════════════════════════════");
        terminal.writer().println("          PowerNukkitX Setup Wizard");
        terminal.writer().println("═══════════════════════════════════════════════════════════");
        terminal.writer().println();
        terminal.writer().println("Welcome! Please choose a language first!");
        terminal.writer().println();

        if (predefinedLanguage != null && !predefinedLanguage.isEmpty()) {
            if (validateLanguage(predefinedLanguage)) {
                terminal.writer().println("✓ Using predefined language: " + predefinedLanguage);
                terminal.writer().flush();
                return predefinedLanguage;
            } else {
                terminal.writer().println("[x] Invalid predefined language: " + predefinedLanguage);
                terminal.writer().println("  Please choose a valid language from the list.");
                terminal.writer().flush();
            }
        }

        List<Map.Entry<String, String>> languageList = new ArrayList<>(availableLanguages.entrySet());
        int selectedIndex = 0;

        terminal.writer().println("Use ↑/↓ arrow keys to navigate, Enter to select, or type language code:");
        terminal.writer().println();

        try {
            while (true) {
                displayLanguageMenu(languageList, selectedIndex);

                String input = reader.readLine("» Selection [" + languageList.get(selectedIndex).getKey() + "]: ").trim();

                if (input.isEmpty()) {
                    String selected = languageList.get(selectedIndex).getKey();
                    terminal.writer().println("✓ Language selected: " + selected + " (" + availableLanguages.get(selected) + ")");
                    terminal.writer().println();
                    terminal.writer().flush();
                    return selected;
                } else if (validateLanguage(input)) {
                    terminal.writer().println("✓ Language selected: " + input + " (" + availableLanguages.get(input) + ")");
                    terminal.writer().println();
                    terminal.writer().flush();
                    return input;
                } else {
                    switch (input.toLowerCase()) {
                        case "up", "u" -> {
                            selectedIndex = (selectedIndex - 1 + languageList.size()) % languageList.size();
                        }
                        case "down", "d" -> {
                            selectedIndex = (selectedIndex + 1) % languageList.size();
                        }
                        default -> {
                            terminal.writer().println("[!] Invalid input. Enter a language code, 'up'/'down' to navigate, or press Enter to select highlighted.");
                            terminal.writer().flush();
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error reading language input", e);
            return "eng"; // Default to English on error
        }
    }

    /**
     * Displays the language menu with the current selection highlighted.
     */
    private void displayLanguageMenu(List<Map.Entry<String, String>> languageList, int selectedIndex) {
        terminal.writer().println("┌──────────────────────────────────────────────────────────┐");

        for (int i = 0; i < languageList.size(); i++) {
            Map.Entry<String, String> entry = languageList.get(i);
            String marker = (i == selectedIndex) ? "►" : " ";
            String highlight = (i == selectedIndex) ? "▶ " : "  ";
            terminal.writer().println("│ " + marker + " [" + entry.getKey() + "] " + highlight + entry.getValue());
        }

        terminal.writer().println("└──────────────────────────────────────────────────────────┘");
        terminal.writer().flush();
    }

    /**
     * Displays the license and asks for acceptance.
     * MANDATORY - will cause program termination if not accepted.
     *
     * @return true if license accepted, false otherwise
     */
    private boolean acceptLicense() {
        terminal.writer().println();
        terminal.writer().println("═══════════════════════════════════════════════════════════");
        terminal.writer().println("          GNU Lesser General Public License v3.0");
        terminal.writer().println("═══════════════════════════════════════════════════════════");
        terminal.writer().println();
        terminal.writer().println("PowerNukkitX is licensed under the GNU LGPL v3.0");
        terminal.writer().println();
        terminal.writer().println("This program is free software: you can redistribute it and/or modify");
        terminal.writer().println("it under the terms of the GNU Lesser General Public License as published");
        terminal.writer().println("by the Free Software Foundation, either version 3 of the License, or");
        terminal.writer().println("(at your option) any later version.");
        terminal.writer().println();
        terminal.writer().println("This program is distributed in the hope that it will be useful,");
        terminal.writer().println("but WITHOUT ANY WARRANTY; without even the implied warranty of");
        terminal.writer().println("MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.");
        terminal.writer().println();
        terminal.writer().println("See the GNU Lesser General Public License for more details:");
        terminal.writer().println("https://www.gnu.org/licenses/lgpl-3.0.html");
        terminal.writer().println();
        terminal.writer().println("[!] You MUST accept this license to continue.");
        terminal.writer().println();
        terminal.writer().flush();

        while (true) {
            try {
                String input = reader.readLine("» Do you accept the license? (yes/no): ").trim().toLowerCase();

                if (input.equals("yes") || input.equals("y")) {
                    terminal.writer().println("✓ License accepted.");
                    terminal.writer().flush();
                    return true;
                } else if (input.equals("no") || input.equals("n")) {
                    return false;
                } else {
                    terminal.writer().println("[!] Please answer 'yes' or 'no'.");
                    terminal.writer().flush();
                }
            } catch (Exception e) {
                log.error("Error reading license acceptance", e);
                return false;
            }
        }
    }

    /**
     * Asks the user if they want to skip the setup wizard.
     */
    private void askSkipWizard() {
        terminal.writer().println();
        terminal.writer().println("═══════════════════════════════════════════════════════════");
        terminal.writer().println("          Additional Setup Configuration");
        terminal.writer().println("═══════════════════════════════════════════════════════════");
        terminal.writer().println();
        terminal.writer().flush();

        while (true) {
            try {
                String input = reader.readLine("» Do you want to skip the setup wizard? (y/n): ").trim().toLowerCase();

                if (input.equalsIgnoreCase("y") || input.equalsIgnoreCase("yes")) {
                    skipWizard = true;
                    terminal.writer().println("✓ Setup wizard will be skipped. Server will use default configuration.");
                    terminal.writer().println();
                    terminal.writer().flush();
                    break;
                } else if (input.isEmpty() || input.equalsIgnoreCase("n") || input.equalsIgnoreCase("no")) {
                    skipWizard = false;
                    terminal.writer().println("✓ Proceeding with setup wizard...");
                    terminal.writer().println();
                    terminal.writer().flush();
                    break;
                } else {
                    terminal.writer().println("[!] Invalid input. Please enter 'y' for yes or 'n' for no.");
                    terminal.writer().flush();
                }
            } catch (Exception e) {
                log.error("Error reading skip wizard input", e);
                skipWizard = true;
                break;
            }
        }
    }

    /**
     * Configures all server settings through interactive prompts in the correct order.
     */
    private void configureServerComplete() {
        terminal.writer().println();
        terminal.writer().println("═".repeat(59));
        terminal.writer().println("              Server Configuration");
        terminal.writer().println("═".repeat(59));
        terminal.writer().println();
        terminal.writer().flush();

        configureServerMotd();
        configureServerPort();
        configureGamemode();
        configureMaxPlayers();
        configureOperators();
        configureWhitelist();
        configureQuery();
    }

    /**
     * Configures server MOTD.
     */
    private void configureServerMotd() {
        try {
            terminal.writer().println("─".repeat(57));
            String input = reader.readLine("» Server MOTD [PowerNukkitX Server]: ").trim();
            if (!input.isEmpty()) {
                wizardConfig.setMotd(input);
            }
            terminal.writer().println("  ✓ Server name: " + wizardConfig.getMotd());
            terminal.writer().println();
            terminal.writer().flush();
        } catch (Exception e) {
            log.error("Error reading server MOTD", e);
        }
    }

    /**
     * Configures server port.
     */
    private void configureServerPort() {
        terminal.writer().println("─".repeat(57));
        while (true) {
            try {
                String input = reader.readLine("» Server port [19132]: ").trim();
                if (input.isEmpty()) {
                    break; // Use default
                }

                int port = Integer.parseInt(input);
                if (port < 1 || port > 65535) {
                    terminal.writer().println("  [x] Invalid port. Please enter a number between 1 and 65535.");
                    terminal.writer().flush();
                    continue;
                }

                wizardConfig.setPort(port);
                break;
            } catch (NumberFormatException e) {
                terminal.writer().println("  [x] Invalid port number. Please enter a valid number.");
                terminal.writer().flush();
            } catch (Exception e) {
                log.error("Error reading server port", e);
                break;
            }
        }
        terminal.writer().println("  ✓ Server port: " + wizardConfig.getPort());
        terminal.writer().println();
        terminal.writer().flush();
    }

    /**
     * Configures default gamemode.
     */
    private void configureGamemode() {
        terminal.writer().println("─".repeat(57));
        terminal.writer().println("Available gamemodes:");
        terminal.writer().println("  [0] Survival");
        terminal.writer().println("  [1] Creative");
        terminal.writer().println("  [2] Adventure");
        terminal.writer().println("  [3] Spectator");
        terminal.writer().println();
        terminal.writer().flush();

        while (true) {
            try {
                String input = reader.readLine("» Default gamemode [0]: ").trim();
                if (input.isEmpty()) {
                    break; // Use default (0 = Survival)
                }

                int gamemode = Integer.parseInt(input);
                if (gamemode < 0 || gamemode > 3) {
                    terminal.writer().println("  [x] Invalid gamemode. Please enter a number between 0 and 3.");
                    terminal.writer().flush();
                    continue;
                }

                wizardConfig.setGamemode(gamemode);
                break;
            } catch (NumberFormatException e) {
                terminal.writer().println("  [x] Invalid number. Please enter a valid gamemode (0-3).");
                terminal.writer().flush();
            } catch (Exception e) {
                log.error("Error reading gamemode", e);
                break;
            }
        }

        String gamemodeName = switch (wizardConfig.getGamemode()) {
            case 1 -> "Creative";
            case 2 -> "Adventure";
            case 3 -> "Spectator";
            default -> "Survival";
        };
        terminal.writer().println("  ✓ Default gamemode: " + gamemodeName);
        terminal.writer().println();
        terminal.writer().flush();
    }

    /**
     * Configures whitelist settings.
     */
    private void configureWhitelist() {
        terminal.writer().println("─".repeat(57));

        while (true) {
            try {
                String input = reader.readLine("» Enable whitelist? (y/N): ").trim().toLowerCase();

                if (input.isEmpty() || input.equals("n") || input.equals("no")) {
                    wizardConfig.setWhitelistEnabled(false);
                    terminal.writer().println("  ✓ Whitelist: Disabled");
                    terminal.writer().println();
                    terminal.writer().flush();
                    break;
                } else if (input.equals("y") || input.equals("yes")) {
                    wizardConfig.setWhitelistEnabled(true);
                    terminal.writer().println("  ✓ Whitelist: Enabled");
                    terminal.writer().flush();

                    configureWhitelistedPlayers();
                    break;
                } else {
                    terminal.writer().println("  [x] Invalid input. Please enter 'y' for yes or 'n' for no.");
                    terminal.writer().flush();
                }
            } catch (Exception e) {
                log.error("Error reading whitelist setting", e);
                break;
            }
        }
    }

    /**
     * Configures whitelisted players.
     */
    private void configureWhitelistedPlayers() {
        try {
            terminal.writer().println();
            terminal.writer().println("  Enter whitelisted player names separated by commas");
            terminal.writer().println("  Example: Player1, Player2, Player3");
            terminal.writer().println("  Press Enter to skip:");
            terminal.writer().flush();

            String input = reader.readLine("  » ").trim();

            if (!input.isEmpty()) {
                String[] players = input.split(",");
                List<String> whitelisted = new ArrayList<>();
                for (String player : players) {
                    String cleanedName = player.trim();
                    if (!cleanedName.isEmpty()) {
                        whitelisted.add(cleanedName);
                    }
                }
                wizardConfig.setWhitelistedPlayers(whitelisted);

                if (!whitelisted.isEmpty()) {
                    terminal.writer().println("  ✓ Whitelisted players: " + String.join(", ", whitelisted));
                } else {
                    terminal.writer().println("  [!] No players added to whitelist.");
                }
                terminal.writer().println();
                terminal.writer().flush();
            } else {
                terminal.writer().println("  [!] No players added to whitelist.");
                terminal.writer().println();
                terminal.writer().flush();
            }
        } catch (Exception e) {
            log.error("Error reading whitelisted players", e);
        }
    }

    /**
     * Configures server operators.
     */
    private void configureOperators() {
        terminal.writer().println("─".repeat(57));
        try {
            terminal.writer().println("  Enter operator names (username) separated by commas");
            terminal.writer().println("  Example: Admin1, Admin2");
            terminal.writer().println("  Press Enter to skip:");
            terminal.writer().flush();

            String input = reader.readLine("  » ").trim();

            if (!input.isEmpty()) {
                String[] ops = input.split(",");
                List<String> operators = new ArrayList<>();
                for (String op : ops) {
                    String cleanedName = op.trim();
                    if (!cleanedName.isEmpty()) {
                        operators.add(cleanedName);
                    }
                }
                wizardConfig.setOperators(operators);

                if (!operators.isEmpty()) {
                    terminal.writer().println("  ✓ Operators: " + String.join(", ", operators));
                } else {
                    terminal.writer().println("  [!] No operators added.");
                }
                terminal.writer().println();
                terminal.writer().flush();
            } else {
                terminal.writer().println("  [!] No operators added.");
                terminal.writer().println();
                terminal.writer().flush();
            }
        } catch (Exception e) {
            log.error("Error reading operators", e);
        }
    }

    /**
     * Configures maximum number of players.
     */
    private void configureMaxPlayers() {
        terminal.writer().println("─".repeat(57));
        while (true) {
            try {
                String input = reader.readLine("» Maximum number of players [20]: ").trim();
                if (input.isEmpty()) {
                    break; // Use default
                }

                int maxPlayers = Integer.parseInt(input);
                if (maxPlayers < 1) {
                    terminal.writer().println("  [x] Invalid number. Please enter a positive number.");
                    terminal.writer().flush();
                    continue;
                }

                wizardConfig.setMaxPlayers(maxPlayers);
                break;
            } catch (NumberFormatException e) {
                terminal.writer().println("  [x] Invalid number. Please enter a valid number.");
                terminal.writer().flush();
            } catch (Exception e) {
                log.error("Error reading max players", e);
                break;
            }
        }
        terminal.writer().println("  ✓ Maximum players: " + wizardConfig.getMaxPlayers());
        terminal.writer().println();
        terminal.writer().flush();
    }

    /**
     * Configures query settings.
     */
    private void configureQuery() {
        terminal.writer().println("─".repeat(57));

        while (true) {
            try {
                String input = reader.readLine("» Enable Query? (Y/n): ").trim().toLowerCase();

                if (input.isEmpty() || input.equals("y") || input.equals("yes")) {
                    wizardConfig.setQueryEnabled(true);
                    terminal.writer().println("  ✓ Query: Enabled");
                    terminal.writer().println();
                    terminal.writer().flush();
                    break;
                } else if (input.equals("n") || input.equals("no")) {
                    wizardConfig.setQueryEnabled(false);
                    terminal.writer().println("  ✓ Query: Disabled");
                    terminal.writer().println();
                    terminal.writer().flush();
                    break;
                } else {
                    terminal.writer().println("  [x] Invalid input. Please enter 'y' for yes or 'n' for no.");
                    terminal.writer().flush();
                }
            } catch (Exception e) {
                log.error("Error reading query setting", e);
                break;
            }
        }
    }

    /**
     * Displays a summary of configuration and waits for user to press ENTER to start the server.
     */
    private void displaySummaryAndWaitForStart() {
        terminal.writer().println();
        terminal.writer().println("═".repeat(59));
        terminal.writer().println("          Configuration Complete!");
        terminal.writer().println("═".repeat(59));
        terminal.writer().println();
        terminal.writer().println("✓ Your PowerNukkitX server is now configured and available at:");
        terminal.writer().println();

        try {
            String localIP = InetAddress.getLocalHost().getHostAddress();
            String externalIP = Utils.getExternalIP();

            terminal.writer().println("    Local Server Address: " + localIP + ":" + wizardConfig.getPort());
            terminal.writer().println("    External Server Address: " + externalIP + ":" + wizardConfig.getPort());
            terminal.writer().println();
            terminal.writer().println("═".repeat(59));
            terminal.writer().println();
            terminal.writer().flush();
        } catch (Exception e) {
            log.error("Error reading IP address", e);
        }

        try {
            reader.readLine("Press ENTER to start the server...");
            terminal.writer().println();
            terminal.writer().println("✓ Starting server...");
            terminal.writer().println();
            terminal.writer().flush();
        } catch (Exception e) {
            log.error("Error waiting for ENTER", e);
        }
    }

    /**
     * Validates if the given language code exists.
     * Includes security check to prevent path traversal attacks.
     *
     * @param languageCode Language code to validate
     * @return true if valid, false otherwise
     */
    private boolean validateLanguage(String languageCode) {
        if (languageCode == null || languageCode.isEmpty()) {
            return false;
        }

        if (!languageCode.matches(LANGUAGE_CODE_PATTERN)) {
            log.warn("Invalid language code format (must be 3 lowercase letters): {}", languageCode);
            return false;
        }

        if (availableLanguages.containsKey(languageCode)) {
            return true;
        }

        String resourcePath = String.format("language/%s/lang.json", languageCode);
        try (InputStream conf = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            return conf != null;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Gets the wizard configuration.
     *
     * @return Wizard configuration with all settings
     */
    public WizardConfig getConfig() {
        return wizardConfig;
    }

    /**
     * Closes the terminal and releases resources.
     */
    @Override
    public void close() {
        try {
            if (terminal != null) {
                terminal.close();
            }
        } catch (IOException e) {
            log.error("Error closing terminal", e);
        }
    }
         /**
          * Language completer for auto-completion support.
          */
        private record LanguageCompleter(Set<String> languageCodes) implements Completer {

        @Override
            public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
                String word = line.word();
                String wordLower = word.toLowerCase();
                for (String langCode : languageCodes) {
                    if (langCode.startsWith(wordLower)) {
                        candidates.add(new Candidate(langCode));
                    }
                }
            }
        }
}

