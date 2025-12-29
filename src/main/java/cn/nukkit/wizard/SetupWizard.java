package cn.nukkit.wizard;

import cn.nukkit.lang.BaseLang;
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
    protected BaseLang baseLang;

    private boolean motdProvidedByArg = false;
    private boolean portProvidedByArg = false;

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
     * @param forceAcceptLicense If true, automatically accept the license
     * @param serverName Optional server name (MOTD) from command line
     * @param port Optional server port from command line
     * @return The wizard configuration
     */
    public WizardConfig run(String predefinedLanguage, boolean forceSkip, boolean forceAcceptLicense, String serverName, Integer port) {
        try {
            String selectedLanguage = selectLanguage(predefinedLanguage);
            wizardConfig.setLanguage(selectedLanguage);
            baseLang = new BaseLang(selectedLanguage);

            if (!acceptLicense(forceAcceptLicense)) {
                terminal.writer().println();
                refuse(baseLang.tr("pnx.setupWizard.license.no_accept"));
                refuse(baseLang.tr("pnx.setupWizard.license.terminating"));
                terminal.writer().flush();
                System.exit(1);
                return wizardConfig;
            }
            wizardConfig.setLicenseAccepted(true);

            // Set server name and port if provided
            if (serverName != null && !serverName.isEmpty()) {
                wizardConfig.setMotd(serverName);
                motdProvidedByArg = true;
            }
            if (port != null) {
                wizardConfig.setPort(port);
                portProvidedByArg = true;
            }

            if (forceSkip) {
                skipWizard = true;
                terminal.writer().println();
                terminal.writer().println(baseLang.tr("pnx.setupWizard.skipped"));
                terminal.writer().flush();
            } else {
                askSkipWizard();

                if (!skipWizard) {
                    notice(baseLang.tr("pnx.setupWizard.modifyLater"));
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
        terminal.writer().println("═".repeat(59));
        terminal.writer().println(centerText("PowerNukkitX Setup Wizard - Language Selection", 59));
        terminal.writer().println("═".repeat(59));
        terminal.writer().println();
        terminal.writer().println("Welcome! Please choose a language first!");
        terminal.writer().println();

        if (predefinedLanguage != null && !predefinedLanguage.isEmpty()) {
            if (validateLanguage(predefinedLanguage)) {
                accept("Using predefined language: " + predefinedLanguage);
                terminal.writer().flush();
                return predefinedLanguage;
            } else {
                refuse("Invalid predefined language: " + predefinedLanguage);
                terminal.writer().println("  Please choose a valid language from the list.");
                terminal.writer().flush();
            }
        }

        List<Map.Entry<String, String>> languageList = new ArrayList<>(availableLanguages.entrySet());
        int selectedIndex = 0;

        notice("Use ↑/↓ arrow keys to navigate, Enter to select, or type language code:");
        terminal.writer().println();

        try {
            while (true) {
                displayLanguageMenu(languageList, selectedIndex);

                String input = reader.readLine("» Selection [" + languageList.get(selectedIndex).getKey() + "]: ").trim();

                if (input.isEmpty()) {
                    String selected = languageList.get(selectedIndex).getKey();
                    accept("Language selected: " + selected + " (" + availableLanguages.get(selected) + ")");
                    terminal.writer().println();
                    terminal.writer().flush();
                    return selected;
                } else if (validateLanguage(input)) {
                    accept("Language selected: " + input + " (" + availableLanguages.get(input) + ")");
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
                            warn("Invalid input. Enter a language code, 'up'/'down' to navigate, or press Enter to select highlighted.");
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
     * @param forceAccept If true, accept the license automatically
     * @return true if license accepted, false otherwise
     */
    public boolean acceptLicense(boolean forceAccept) {
        if (forceAccept) {
            terminal.writer().println();
            terminal.writer().println("License automatically accepted by command line argument.");
            terminal.writer().flush();
            return true;
        }
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
        warn(baseLang.tr("pnx.setupWizard.license.notice"));
        terminal.writer().println();
        terminal.writer().flush();
        return askConfirmation(
            baseLang.tr("pnx.setupWizard.license.question"),
            baseLang.tr("pnx.setupWizard.license.accept"),
            baseLang.tr("pnx.setupWizard.license.no_accept"),
            baseLang.tr("pnx.setupWizard.invalid_input"),
            false
        );
    }

    /**
     * Handles the user confirmation for skipping the wizard.
     */
    private void askSkipWizard() {
        terminal.writer().println();
        terminal.writer().println("═".repeat(59));
        terminal.writer().println(centerText(baseLang.tr("pnx.setupWizard.category.additionnal", "Additional Setup Configuration"), 59));
        terminal.writer().println("═".repeat(59));
        terminal.writer().println(baseLang.tr("pnx.setupWizard.skip_prompt"));
        terminal.writer().println();
        terminal.writer().flush();
        handleUserInputLoop(
            baseLang.tr("pnx.setupWizard.skip.question"),
            new String[]{"y", "yes", "n", "no", ""},
            () -> { skipWizard = true; accept(baseLang.tr("pnx.setupWizard.skip")); },
            () -> warn(baseLang.tr("pnx.setupWizard.invalid_input")),
            () -> { log.error("Error reading skip wizard input"); skipWizard = true; }
        );
        terminal.writer().println();
        terminal.writer().flush();
    }

    /**
     * Configures all server settings through interactive prompts in the correct order.
     */
    private void configureServerComplete() {
        terminal.writer().println();
        terminal.writer().println("═".repeat(59));
        terminal.writer().println(centerText(baseLang.tr("pnx.setupWizard.category.serverconfig", "Server Configuration"), 59));
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
        if (motdProvidedByArg) {
            accept(baseLang.tr("pnx.setupWizard.motd.accept", wizardConfig.getMotd()));
            terminal.writer().println();
            terminal.writer().flush();
            return;
        }
        try {
            String input = reader.readLine(promptText(baseLang.tr("pnx.setupWizard.motd.question")).trim());
            if (!input.isEmpty()) {
                wizardConfig.setMotd(input);
            }
            accept(baseLang.tr("pnx.setupWizard.motd.accept", wizardConfig.getMotd()));
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
        if (portProvidedByArg) {
            accept(baseLang.tr("pnx.setupWizard.port.accept", wizardConfig.getPort()));
            terminal.writer().println();
            terminal.writer().flush();
            return;
        }
        terminal.writer().println("─".repeat(57));
        while (true) {
            try {
                String input = reader.readLine(promptText(baseLang.tr("pnx.setupWizard.port.question"))).trim();
                if (input.isEmpty()) {
                    break; // Use default
                }

                int port = Integer.parseInt(input);
                if (port < 1 || port > 65535) {
                    refuse(baseLang.tr("pnx.setupWizard.port.invalid"));
                    terminal.writer().flush();
                    continue;
                }

                wizardConfig.setPort(port);
                break;
            } catch (NumberFormatException e) {
                refuse(baseLang.tr("pnx.setupWizard.port.error.number"));
                terminal.writer().flush();
            } catch (Exception e) {
                log.error("Error reading server port", e);
                break;
            }
        }
        accept(baseLang.tr("pnx.setupWizard.port.accept", wizardConfig.getPort()));
        terminal.writer().println();
        terminal.writer().flush();
    }

    /**
     * Configures default gamemode.
     */
    private void configureGamemode() {
        terminal.writer().println("─".repeat(57));
        notice(baseLang.tr("pnx.setupWizard.gamemode"));
        prompt("[0]" + baseLang.tr("pnx.setupWizard.gamemode.survival"));
        prompt("[1]" + baseLang.tr("pnx.setupWizard.gamemode.creative"));
        prompt("[2]" + baseLang.tr("pnx.setupWizard.gamemode.adventure"));
        prompt("[3]" + baseLang.tr("pnx.setupWizard.gamemode.spectator"));
        terminal.writer().println();
        terminal.writer().flush();

        while (true) {
            try {
                String input = reader.readLine(promptText(baseLang.tr("pnx.setupWizard.gamemode.question"))).trim();
                if (input.isEmpty()) {
                    break;
                }

                int gamemode = Integer.parseInt(input);
                if (gamemode < 0 || gamemode > 3) {
                    warn(baseLang.tr("pnx.setupWizard.gamemode.invalid"));
                    terminal.writer().flush();
                    continue;
                }

                wizardConfig.setGamemode(gamemode);
                break;
            } catch (NumberFormatException e) {
                refuse(baseLang.tr("pnx.setupWizard.gamemode.error.number"));
                terminal.writer().flush();
            } catch (Exception e) {
                log.error("Error reading gamemode", e);
                break;
            }
        }

        String gamemodeName = switch (wizardConfig.getGamemode()) {
            case 1 -> baseLang.tr("pnx.setupWizard.gamemode.creative");
            case 2 -> baseLang.tr("pnx.setupWizard.gamemode.adventure");
            case 3 -> baseLang.tr("pnx.setupWizard.gamemode.spectator");
            default -> baseLang.tr("pnx.setupWizard.gamemode.survival");
        };
        accept(baseLang.tr("pnx.setupWizard.gamemode.accept", gamemodeName));
        terminal.writer().println();
        terminal.writer().flush();
    }

    /**
     * Configures whitelist settings.
     */
    private void configureWhitelist() {
        terminal.writer().println("─".repeat(57));
        handleUserInputLoop(
            baseLang.tr("pnx.setupWizard.whitelist.question"),
            new String[]{"y", "yes", "n", "no", ""},
            () -> {
                wizardConfig.setWhitelistEnabled(true);
                accept(baseLang.tr("pnx.setupWizard.whitelist.enabled"));
                terminal.writer().flush();
                configureWhitelistedPlayers();
            },
            () -> warn(baseLang.tr("pnx.setupWizard.invalid_input")),
            () -> log.error("Error reading whitelist setting")
        );
    }

    /**
     * Configures whitelisted players.
     */
    private void configureWhitelistedPlayers() {
        try {
            terminal.writer().println();
            prompt(baseLang.tr("pnx.setupWizard.whitelist.enter"));
            notice(baseLang.tr("pnx.setupWizard.whitelist.example"));
            prompt(baseLang.tr("pnx.setupWizard.whitelist.skip"));
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
                    accept(baseLang.tr("pnx.setupWizard.whitelist.added", String.join(", ", whitelisted)));
                } else {
                    warn(baseLang.tr("pnx.setupWizard.whitelist.none"));
                }
                terminal.writer().println();
                terminal.writer().flush();
            } else {
                warn(baseLang.tr("pnx.setupWizard.whitelist.none"));
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
            prompt(baseLang.tr("pnx.setupWizard.operators.enter"));
            notice(baseLang.tr("pnx.setupWizard.operators.example"));
            prompt(baseLang.tr("pnx.setupWizard.operators.skip"));
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
                    accept(baseLang.tr("pnx.setupWizard.operators.added", String.join(", ", operators)));
                } else {
                    warn(baseLang.tr("pnx.setupWizard.operators.none"));
                }
                terminal.writer().println();
                terminal.writer().flush();
            } else {
                warn(baseLang.tr("pnx.setupWizard.operators.none"));
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
                String input = reader.readLine(baseLang.tr("pnx.setupWizard.maxPlayers.prompt")).trim();
                if (input.isEmpty()) {
                    break; // Use default
                }
                int maxPlayers = Integer.parseInt(input);
                if (maxPlayers < 1) {
                    refuse(baseLang.tr("pnx.setupWizard.maxPlayers.invalid.positive"));
                    terminal.writer().flush();
                    continue;
                }
                wizardConfig.setMaxPlayers(maxPlayers);
                break;
            } catch (NumberFormatException e) {
                refuse(baseLang.tr("pnx.setupWizard.maxPlayers.invalid"));
                terminal.writer().flush();
            } catch (Exception e) {
                log.error("Error reading max players", e);
                break;
            }
        }
        accept(baseLang.tr("pnx.setupWizard.maxPlayers.set",String.valueOf(wizardConfig.getMaxPlayers())));
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
                String input = reader.readLine(baseLang.tr("pnx.setupWizard.query.prompt")).trim();
                if (input.isEmpty() || input.equals("y") || input.equals("yes")) {
                    wizardConfig.setQueryEnabled(true);
                    accept(baseLang.tr("pnx.setupWizard.query.enabled"));
                    terminal.writer().println();
                    terminal.writer().flush();
                    break;
                } else if (input.equals("n") || input.equals("no")) {
                    wizardConfig.setQueryEnabled(false);
                    accept(baseLang.tr("pnx.setupWizard.query.disabled"));
                    terminal.writer().println();
                    terminal.writer().flush();
                    break;
                } else {
                    refuse(baseLang.tr("pnx.setupWizard.query.invalid"));
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
        terminal.writer().println(centerText(baseLang.tr("pnx.setupWizard.summary.title"), 59));
        terminal.writer().println("═".repeat(59));
        terminal.writer().println();
        accept(baseLang.tr("pnx.setupWizard.summary.ready"));
        terminal.writer().println();
        try {
            String localIP = InetAddress.getLocalHost().getHostAddress();
            String externalIP = Utils.getExternalIP();
            prompt(baseLang.tr("pnx.setupWizard.summary.local", localIP, String.valueOf(wizardConfig.getPort())));
            prompt(baseLang.tr("pnx.setupWizard.summary.external", externalIP, String.valueOf(wizardConfig.getPort())));
            terminal.writer().println();
            terminal.writer().println("═".repeat(59));
            terminal.writer().println();
            terminal.writer().flush();
        } catch (Exception e) {
            log.error("Error reading IP address", e);
        }
        try {
            reader.readLine(baseLang.tr("pnx.setupWizard.summary.startPrompt"));
            terminal.writer().println();
            accept(baseLang.tr("pnx.setupWizard.summary.starting"));
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

    /**
     * Centers the given text for display purposes.
     *
     * @param text The text to center
     * @param width The total width of the line
     * @return The centered text
     */
    private String centerText(String text, int width) {
        if (text == null) return "";
        int padSize = Math.max(0, width - text.length());
        int padStart = padSize / 2;
        int padEnd = padSize - padStart;
        return " ".repeat(padStart) + text + " ".repeat(padEnd);
    }

    private void warn(String message) {
        terminal.writer().println("[!] " + message);
    }
    private void accept(String message) {
        terminal.writer().println("✓ " + message);
    }
    private void refuse(String message) {
        terminal.writer().println("[x] " + message);
    }
    private void notice(String message) {
        terminal.writer().println("[*] " + message);
    }
    private void prompt(String message) {
        terminal.writer().println("» " + message);
    }

    private String promptText(String text) {
        return "» " + text;
    }

    public void setBaseLang(BaseLang lang) {
        this.baseLang = lang;
    }

    /**
     * Displays a prompt and waits for user confirmation (yes/no).
     * Returns true if accepted, false if rejected.
     */
    private boolean askConfirmation(String prompt, String acceptMsg, String refuseMsg, String invalidMsg, boolean defaultAccept) {
        terminal.writer().flush();
        while (true) {
            try {
                String input = reader.readLine(promptText(prompt)).trim();
                if (input.isEmpty()) {
                    if (defaultAccept) {
                        accept(acceptMsg);
                        terminal.writer().flush();
                        return true;
                    } else {
                        refuse(refuseMsg);
                        terminal.writer().flush();
                        return false;
                    }
                } else if (input.equalsIgnoreCase("y") || input.equalsIgnoreCase("yes")) {
                    accept(acceptMsg);
                    terminal.writer().flush();
                    return true;
                } else if (input.equalsIgnoreCase("n") || input.equalsIgnoreCase("no")) {
                    refuse(refuseMsg);
                    terminal.writer().flush();
                    return false;
                } else {
                    warn(invalidMsg);
                    terminal.writer().flush();
                }
            } catch (Exception e) {
                log.error("Error reading confirmation input", e);
                return false;
            }
        }
    }

    /**
     * Handles a generic user input loop for confirmation or selection.
     *
     * @param prompt      The prompt to display
     * @param validInputs Array of valid inputs (case-insensitive)
     * @param onValid     Runnable to execute when input is valid
     * @param onInvalid   Runnable to execute when input is invalid
     * @param onException Runnable to execute on exception
     */
    private void handleUserInputLoop(String prompt, String[] validInputs, Runnable onValid, Runnable onInvalid, Runnable onException) {
        while (true) {
            try {
                String input = reader.readLine(promptText(prompt)).trim().toLowerCase();
                boolean isValid = Arrays.asList(validInputs).contains(input);
                if (isValid) {
                    onValid.run();
                    terminal.writer().flush();
                    return;
                } else {
                    onInvalid.run();
                    terminal.writer().flush();
                }
            } catch (Exception e) {
                onException.run();
                break;
            }
        }
    }

}
