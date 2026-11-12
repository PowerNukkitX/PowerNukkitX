package org.powernukkitx.wizard;

import org.powernukkitx.lang.BaseLang;
import org.powernukkitx.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import net.minecrell.terminalconsole.TerminalConsoleAppender;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.*;

/**
 * First-run setup wizard for PowerNukkitX.
 * <p>
 * The flow is: language selection, license acceptance, then an offer to skip. When the user does not skip,
 * seven questions are asked in turn and a recap lists every answer; entering the number of a setting in the
 * recap asks that question again. When no person can answer (no console, CI), nothing is read and the
 * defaults are used.
 * <p>
 * Not thread-safe; it runs on the main thread before the server is created.
 *
 * @author AzaleeX
 * @author xRookieFight
 *
 * @since 17/12/2025
 */
@Slf4j
public class SetupWizard implements AutoCloseable {
    private final WizardConsole console;
    private final WizardEnvironment environment;
    private final Map<String, String> availableLanguages;
    private final WizardConfig wizardConfig = new WizardConfig();
    private final boolean interactive;
    private final boolean unicodeOutput;
    private boolean skipWizard = false;
    protected BaseLang baseLang;
    private List<WizardStep> steps = List.of();
    private String localAddress;
    private String externalAddress;

    public SetupWizard() throws IOException {
        this(loadAvailableLanguages());
    }

    private SetupWizard(Map<String, String> languages) throws IOException {
        this(openConsole(languages.keySet()), detectEnvironment(languages));
    }

    SetupWizard(WizardConsole console, WizardEnvironment environment) {
        this.console = console;
        this.environment = environment;
        this.availableLanguages = environment.languages();
        this.interactive = environment.interactive();
        this.unicodeOutput = environment.unicodeOutput();
    }

    private static WizardConsole openConsole(Set<String> languageCodes) throws IOException {
        Terminal sharedTerminal = TerminalConsoleAppender.getTerminal();
        Terminal terminal = sharedTerminal != null ? sharedTerminal : TerminalBuilder.builder()
                .system(true)
                .jna(false)
                .dumb(isDumbTerminal())
                .build();
        return new JLineWizardConsole(terminal, sharedTerminal == null, languageCodes);
    }

    private static WizardEnvironment detectEnvironment(Map<String, String> languages) {
        return new WizardEnvironment(
                System.console() != null && !SetupWizardSupport.isAutomatedEnvironment(),
                supportsUnicodeOutput(),
                languages,
                BaseLang::new,
                SetupWizard::lookupLocalAddress,
                Utils::getExternalIP
        );
    }

    private static String lookupLocalAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.error("Error reading IP address", e);
            return "unknown";
        }
    }

    /**
     * Loads available languages from the 'language.list' resource file.
     *
     * @return Map of language codes to language names
     */
    private static Map<String, String> loadAvailableLanguages() {
        Map<String, String> languages = new LinkedHashMap<>();
        try (InputStream languageList = SetupWizard.class.getClassLoader().getResourceAsStream("language/language.list")) {
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
            console.disableCompletion();
            wizardConfig.setLanguage(selectedLanguage);
            baseLang = environment.langLoader().apply(selectedLanguage);

            if (!acceptLicense(forceAcceptLicense)) {
                console.println("");
                refuse(baseLang.tr("pnx.setupWizard.license.terminating"));
                console.flush();
                return wizardConfig;
            }
            wizardConfig.setLicenseAccepted(true);

            // Set server name and port if provided
            boolean motdProvidedByArg = serverName != null && !serverName.isEmpty();
            if (motdProvidedByArg) {
                wizardConfig.setMotd(serverName);
            }
            boolean portProvidedByArg = port != null;
            if (portProvidedByArg) {
                wizardConfig.setPort(port);
            }
            steps = buildSteps(motdProvidedByArg, portProvidedByArg);

            if (forceSkip) {
                skipWizard = true;
                console.println("");
                console.println(baseLang.tr("pnx.setupWizard.skipped"));
                console.flush();
            } else {
                askSkipWizard();

                if (!skipWizard) {
                    notice(baseLang.tr("pnx.setupWizard.modifyLater"));
                    console.flush();

                    configureServerComplete();
                }
            }

            if (skipWizard) {
                displaySummaryAndWaitForStart();
            } else {
                reviewAndWaitForStart();
            }

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
        console.println("");
        console.println(borderLine());
        console.println(centerText("PowerNukkitX Setup Wizard - Language Selection", 59));
        console.println(borderLine());
        console.println("");
        console.println("Welcome! Please choose a language first!");
        console.println("");

        if (predefinedLanguage != null && !predefinedLanguage.isEmpty()) {
            String normalizedLanguage = SetupWizardSupport.normalizeLanguageCode(predefinedLanguage);
            if (validateLanguage(normalizedLanguage)) {
                accept("Using predefined language: " + normalizedLanguage);
                console.flush();
                return normalizedLanguage;
            } else {
                refuse("Invalid predefined language: " + predefinedLanguage);
                console.println("  Please choose a valid language from the list.");
                console.flush();
            }
        }

        List<Map.Entry<String, String>> languageList = new ArrayList<>(availableLanguages.entrySet());
        String defaultLanguage = languageList.isEmpty() ? "eng" : languageList.get(0).getKey();
        if (!interactive) {
            accept("Automated environment detected. Language selected: " + defaultLanguage);
            console.println("");
            console.flush();
            return defaultLanguage;
        }

        notice("Enter a language code from the list below (press Enter for default).");
        for (Map.Entry<String, String> entry : languageList) {
            console.println("  [" + entry.getKey() + "] " + entry.getValue());
        }
        console.println("");

        try {
            while (true) {
                String input = SetupWizardSupport.normalizeLanguageCode(console.readLine(promptText("Language code [" + defaultLanguage + "]: ")));
                if (input.isEmpty()) {
                    accept("Language selected: " + defaultLanguage + " (" + availableLanguages.get(defaultLanguage) + ")");
                    console.println("");
                    console.flush();
                    return defaultLanguage;
                } else if (validateLanguage(input)) {
                    accept("Language selected: " + input + " (" + availableLanguages.get(input) + ")");
                    console.println("");
                    console.flush();
                    return input;
                } else {
                    warn("Invalid input. Enter a valid language code from the list.");
                    console.flush();
                }
            }
        } catch (Exception e) {
            log.error("Error reading language input", e);
            return "eng"; // Default to English on error
        }
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
            console.println("");
            console.println(baseLang.tr("pnx.setupWizard.license.autoAccepted"));
            console.flush();
            return true;
        }
        if (!interactive) {
            console.println("");
            warn(baseLang.tr("pnx.setupWizard.automated"));
            console.flush();
            return true;
        }
        console.println("");
        console.println(borderLine());
        console.println("          GNU Lesser General Public License v3.0");
        console.println(borderLine());
        console.println("");
        console.println("PowerNukkitX is licensed under the GNU LGPL v3.0");
        console.println("");
        console.println("This program is free software: you can redistribute it and/or modify");
        console.println("it under the terms of the GNU Lesser General Public License as published");
        console.println("by the Free Software Foundation, either version 3 of the License, or");
        console.println("(at your option) any later version.");
        console.println("");
        console.println("This program is distributed in the hope that it will be useful,");
        console.println("but WITHOUT ANY WARRANTY; without even the implied warranty of");
        console.println("MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.");
        console.println("");
        console.println("See the GNU Lesser General Public License for more details:");
        console.println("https://www.gnu.org/licenses/lgpl-3.0.html");
        console.println("");
        warn(baseLang.tr("pnx.setupWizard.license.notice"));
        console.println("");
        console.flush();
        boolean accepted = readYesNo(promptText(baseLang.tr("pnx.setupWizard.license.question")), false);
        if (accepted) {
            accept(baseLang.tr("pnx.setupWizard.license.accept"));
        } else {
            refuse(baseLang.tr("pnx.setupWizard.license.no_accept"));
        }
        console.flush();
        return accepted;
    }

    /**
     * Handles the user confirmation for skipping the wizard.
     */
    private void askSkipWizard() {
        console.println("");
        console.println(borderLine());
        console.println(centerText(baseLang.tr("pnx.setupWizard.category.additionnal", "Additional Setup Configuration"), 59));
        console.println(borderLine());
        console.println(baseLang.tr("pnx.setupWizard.skip_prompt"));
        console.println("");
        console.flush();
        if (!interactive) {
            skipWizard = true;
            accept(baseLang.tr("pnx.setupWizard.skip"));
            console.println("");
            console.flush();
            return;
        }
        skipWizard = readYesNo(promptText(baseLang.tr("pnx.setupWizard.skip.question")), true);
        accept(baseLang.tr(skipWizard ? "pnx.setupWizard.skip" : "pnx.setupWizard.noskip"));
        console.println("");
        console.flush();
    }

    /**
     * Configures all server settings through interactive prompts in the correct order.
     */
    private void configureServerComplete() {
        console.println("");
        console.println(borderLine());
        console.println(centerText(baseLang.tr("pnx.setupWizard.category.serverconfig", "Server Configuration"), 59));
        console.println(borderLine());
        console.println("");
        console.flush();

        for (int i = 0; i < steps.size(); i++) {
            runStep(i, !steps.get(i).presetByArg());
        }
    }

    /**
     * Prints the header of a step, asks its question when requested, then confirms the stored value.
     *
     * @param index zero-based position in {@link #steps}
     * @param ask   false to keep the current value without prompting
     */
    private void runStep(int index, boolean ask) {
        WizardStep step = steps.get(index);
        console.println(separatorLine());
        console.println(baseLang.tr("pnx.setupWizard.step.header",
                String.valueOf(index + 1), String.valueOf(steps.size()), baseLang.tr(step.labelKey())));
        if (ask) {
            step.ask().run();
        }
        step.confirm().run();
        console.println("");
        console.flush();
    }

    /**
     * Builds the questions in the order they are asked.
     *
     * @param motdPreset whether the server name came from the command line
     * @param portPreset whether the port came from the command line
     */
    private List<WizardStep> buildSteps(boolean motdPreset, boolean portPreset) {
        return List.of(
                new WizardStep("pnx.setupWizard.motd.label", motdPreset,
                        wizardConfig::getMotd,
                        () -> wizardConfig.setMotd(askText("pnx.setupWizard.motd.label", wizardConfig.getMotd())),
                        () -> accept(baseLang.tr("pnx.setupWizard.motd.accept", wizardConfig.getMotd()))),
                new WizardStep("pnx.setupWizard.port.label", portPreset,
                        () -> String.valueOf(wizardConfig.getPort()),
                        () -> wizardConfig.setPort(askNumber("pnx.setupWizard.port.label", wizardConfig.getPort(),
                                SetupWizardSupport::parsePortOrDefault,
                                "pnx.setupWizard.port.error.number", "pnx.setupWizard.port.invalid")),
                        () -> accept(baseLang.tr("pnx.setupWizard.port.accept", wizardConfig.getPort()))),
                new WizardStep("pnx.setupWizard.gamemode.label", false,
                        this::gamemodeName,
                        this::askGamemode,
                        () -> accept(baseLang.tr("pnx.setupWizard.gamemode.accept", gamemodeName()))),
                new WizardStep("pnx.setupWizard.maxPlayers.label", false,
                        () -> String.valueOf(wizardConfig.getMaxPlayers()),
                        () -> wizardConfig.setMaxPlayers(askNumber("pnx.setupWizard.maxPlayers.label", wizardConfig.getMaxPlayers(),
                                SetupWizardSupport::parseMaxPlayersOrDefault,
                                "pnx.setupWizard.maxPlayers.invalid", "pnx.setupWizard.maxPlayers.invalid.positive")),
                        () -> accept(baseLang.tr("pnx.setupWizard.maxPlayers.set", String.valueOf(wizardConfig.getMaxPlayers())))),
                new WizardStep("pnx.setupWizard.operators.label", false,
                        () -> namesOrNone(wizardConfig.getOperators()),
                        this::askOperators,
                        this::confirmOperators),
                new WizardStep("pnx.setupWizard.whitelist.label", false,
                        this::whitelistSummary,
                        this::askWhitelist,
                        this::confirmWhitelist),
                new WizardStep("pnx.setupWizard.query.label", false,
                        () -> baseLang.tr(wizardConfig.isQueryEnabled() ? "pnx.setupWizard.value.enabled" : "pnx.setupWizard.value.disabled"),
                        () -> wizardConfig.setQueryEnabled(askYesNo("pnx.setupWizard.query.ask", wizardConfig.isQueryEnabled())),
                        () -> accept(baseLang.tr(wizardConfig.isQueryEnabled() ? "pnx.setupWizard.query.enabled" : "pnx.setupWizard.query.disabled")))
        );
    }

    private void askGamemode() {
        notice(baseLang.tr("pnx.setupWizard.gamemode"));
        prompt("[0] " + baseLang.tr("pnx.setupWizard.gamemode.survival"));
        prompt("[1] " + baseLang.tr("pnx.setupWizard.gamemode.creative"));
        prompt("[2] " + baseLang.tr("pnx.setupWizard.gamemode.adventure"));
        prompt("[3] " + baseLang.tr("pnx.setupWizard.gamemode.spectator"));
        console.println("");
        console.flush();
        wizardConfig.setGamemode(askNumber("pnx.setupWizard.gamemode.label", wizardConfig.getGamemode(),
                SetupWizardSupport::parseGamemodeOrDefault,
                "pnx.setupWizard.gamemode.error.number", "pnx.setupWizard.gamemode.invalid"));
    }

    private String gamemodeName() {
        return switch (wizardConfig.getGamemode()) {
            case 1 -> baseLang.tr("pnx.setupWizard.gamemode.creative");
            case 2 -> baseLang.tr("pnx.setupWizard.gamemode.adventure");
            case 3 -> baseLang.tr("pnx.setupWizard.gamemode.spectator");
            default -> baseLang.tr("pnx.setupWizard.gamemode.survival");
        };
    }

    private void askOperators() {
        prompt(baseLang.tr("pnx.setupWizard.operators.enter"));
        notice(baseLang.tr("pnx.setupWizard.operators.example"));
        console.flush();
        wizardConfig.setOperators(askNames("pnx.setupWizard.operators.label", wizardConfig.getOperators()));
    }

    private void confirmOperators() {
        List<String> operators = wizardConfig.getOperators();
        if (operators.isEmpty()) {
            warn(baseLang.tr("pnx.setupWizard.operators.none"));
        } else {
            accept(baseLang.tr("pnx.setupWizard.operators.added", String.join(", ", operators)));
        }
    }

    /**
     * Asks whether the whitelist is enabled and, if so, who is on it. Disabling it drops the names already
     * entered, because the server writes them to the whitelist file whether or not the whitelist is enabled.
     */
    private void askWhitelist() {
        boolean enabled = askYesNo("pnx.setupWizard.whitelist.ask", wizardConfig.isWhitelistEnabled());
        wizardConfig.setWhitelistEnabled(enabled);
        if (!enabled) {
            wizardConfig.setWhitelistedPlayers(new ArrayList<>());
            return;
        }
        accept(baseLang.tr("pnx.setupWizard.whitelist.enabled"));
        console.println("");
        prompt(baseLang.tr("pnx.setupWizard.whitelist.enter"));
        notice(baseLang.tr("pnx.setupWizard.whitelist.example"));
        console.flush();
        wizardConfig.setWhitelistedPlayers(askNames("pnx.setupWizard.whitelist.players.label", wizardConfig.getWhitelistedPlayers()));
    }

    private void confirmWhitelist() {
        List<String> players = wizardConfig.getWhitelistedPlayers();
        if (!wizardConfig.isWhitelistEnabled()) {
            accept(baseLang.tr("pnx.setupWizard.whitelist.disabled"));
        } else if (players.isEmpty()) {
            warn(baseLang.tr("pnx.setupWizard.whitelist.none"));
        } else {
            accept(baseLang.tr("pnx.setupWizard.whitelist.added", String.join(", ", players)));
        }
    }

    private String whitelistSummary() {
        if (!wizardConfig.isWhitelistEnabled()) {
            return baseLang.tr("pnx.setupWizard.value.disabled");
        }
        return baseLang.tr("pnx.setupWizard.value.enabled") + " (" + namesOrNone(wizardConfig.getWhitelistedPlayers()) + ")";
    }

    private String namesOrNone(List<String> names) {
        return names.isEmpty() ? baseLang.tr("pnx.setupWizard.value.none") : String.join(", ", names);
    }

    /**
     * Displays a summary of configuration and waits for user to press ENTER to start the server.
     */
    private void displaySummaryAndWaitForStart() {
        console.println("");
        console.println(borderLine());
        console.println(centerText(baseLang.tr("pnx.setupWizard.summary.title"), 59));
        console.println(borderLine());
        console.println("");
        accept(baseLang.tr("pnx.setupWizard.summary.ready"));
        console.println("");
        printAddresses();
        console.println("");
        console.println(borderLine());
        console.println("");
        console.flush();
        if (!interactive) {
            accept(baseLang.tr("pnx.setupWizard.summary.starting"));
            console.println("");
            console.flush();
            return;
        }
        try {
            console.readLine(baseLang.tr("pnx.setupWizard.summary.startPrompt"));
            console.println("");
            accept(baseLang.tr("pnx.setupWizard.summary.starting"));
            console.println("");
            console.flush();
        } catch (Exception e) {
            log.error("Error waiting for ENTER", e);
        }
    }

    /**
     * Prints the local and external address of the server. Both are resolved on the first call only, because
     * the external lookup is a network request; the port always reflects the current setting.
     */
    private void printAddresses() {
        try {
            if (localAddress == null) {
                String local = environment.localAddress().get();
                externalAddress = environment.externalAddress().get();
                localAddress = local;
            }
            String port = String.valueOf(wizardConfig.getPort());
            prompt(baseLang.tr("pnx.setupWizard.summary.local", localAddress, port));
            prompt(baseLang.tr("pnx.setupWizard.summary.external", externalAddress, port));
        } catch (Exception e) {
            log.error("Error reading IP address", e);
        }
    }

    /**
     * Shows every setting with its value and waits for the user to start the server. Entering the number of
     * a setting asks that question again, then shows the recap again.
     */
    private void reviewAndWaitForStart() {
        printRecap();
        while (true) {
            String input = readAnswer(promptText(baseLang.tr("pnx.setupWizard.recap.prompt")));
            if (input == null || input.trim().isEmpty()) {
                break;
            }
            int choice = parseStepChoice(input);
            if (choice == -1) {
                warn(baseLang.tr("pnx.setupWizard.recap.invalid", String.valueOf(steps.size())));
                console.flush();
                continue;
            }
            console.println("");
            runStep(choice - 1, true);
            printRecap();
        }
        console.println("");
        accept(baseLang.tr("pnx.setupWizard.summary.starting"));
        console.println("");
        console.flush();
    }

    /**
     * The values are concatenated rather than passed as translation arguments, because {@link BaseLang}
     * translates its arguments and would rewrite a server name that happens to match a translation key.
     */
    private void printRecap() {
        console.println(borderLine());
        console.println(centerText(baseLang.tr("pnx.setupWizard.recap.title"), 59));
        console.println(borderLine());
        console.println("");
        List<String> labels = steps.stream().map(step -> baseLang.tr(step.labelKey())).toList();
        int labelWidth = labels.stream().mapToInt(String::length).max().orElse(0);
        for (int i = 0; i < steps.size(); i++) {
            String label = labels.get(i);
            console.println("  " + (i + 1) + ". " + label + " ".repeat(labelWidth - label.length())
                    + ": " + steps.get(i).displayValue().get());
        }
        console.println("");
        printAddresses();
        console.println("");
        console.flush();
    }

    /**
     * @return the one-based step number entered, or -1 if the input is not the number of a step
     */
    private int parseStepChoice(String input) {
        try {
            int choice = Integer.parseInt(input.trim());
            return choice >= 1 && choice <= steps.size() ? choice : -1;
        } catch (NumberFormatException e) {
            return -1;
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
        languageCode = SetupWizardSupport.normalizeLanguageCode(languageCode);
        if (languageCode.isEmpty()) {
            return false;
        }

        if (!SetupWizardSupport.isLanguageCodeFormat(languageCode)) {
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
        console.close();
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
        console.println("[!] " + message);
    }
    private void accept(String message) {
        console.println((unicodeOutput ? "✓ " : "[OK] ") + message);
    }
    private void refuse(String message) {
        console.println("[x] " + message);
    }
    private void notice(String message) {
        console.println("[*] " + message);
    }
    private void prompt(String message) {
        console.println(promptPrefix() + message);
    }

    private String promptText(String text) {
        return promptPrefix() + text;
    }

    private String promptPrefix() {
        return unicodeOutput ? "» " : "> ";
    }

    private String borderLine() {
        return (unicodeOutput ? "═" : "=").repeat(59);
    }

    private String separatorLine() {
        return (unicodeOutput ? "─" : "-").repeat(57);
    }

    private static boolean supportsUnicodeOutput() {
        String encoding = System.getProperty("sun.stdout.encoding", Charset.defaultCharset().name());
        Charset charset;
        try {
            charset = Charset.forName(encoding);
        } catch (Exception e) {
            charset = Charset.defaultCharset();
        }
        return SetupWizardSupport.supportsUnicodeOutput(charset);
    }

    private static boolean isDumbTerminal() {
        String term = System.getenv("TERM");
        return System.console() == null || term == null || "dumb".equalsIgnoreCase(term);
    }

    public void setBaseLang(BaseLang lang) {
        this.baseLang = lang;
    }

    /**
     * Reads one line from the console.
     *
     * @return the line entered, or null when the console cannot be read (closed input, interrupt);
     * callers then keep the current value so the wizard always terminates
     */
    private String readAnswer(String prompt) {
        try {
            return console.readLine(prompt);
        } catch (Exception e) {
            log.error("Error reading setup wizard input", e);
            return null;
        }
    }

    /**
     * Asks for a free-text value. A blank answer keeps the current value.
     */
    private String askText(String labelKey, String current) {
        String input = readAnswer(promptText(SetupWizardSupport.formatValuePrompt(baseLang.tr(labelKey), current)));
        if (input == null || input.trim().isEmpty()) {
            return current;
        }
        return input.trim();
    }

    /**
     * Asks for a number until the parser accepts the answer. A blank answer keeps the current value.
     *
     * @param notANumberKey translation key of the message shown when the answer is not a number
     * @param outOfRangeKey translation key of the message shown when the parser rejects the number
     */
    private int askNumber(String labelKey, int current, NumberParser parser, String notANumberKey, String outOfRangeKey) {
        String prompt = promptText(SetupWizardSupport.formatValuePrompt(baseLang.tr(labelKey), String.valueOf(current)));
        while (true) {
            String input = readAnswer(prompt);
            if (input == null) {
                return current;
            }
            try {
                return parser.parse(input, current);
            } catch (NumberFormatException e) {
                refuse(baseLang.tr(notANumberKey));
            } catch (IllegalArgumentException e) {
                refuse(baseLang.tr(outOfRangeKey));
            }
            console.flush();
        }
    }

    /**
     * Asks a yes/no question until the answer is recognised. A blank answer selects the default.
     *
     * @param fullPrompt the prompt exactly as displayed, including its (y/n) hint
     */
    private boolean readYesNo(String fullPrompt, boolean defaultYes) {
        while (true) {
            String input = readAnswer(fullPrompt);
            if (input == null) {
                return defaultYes;
            }
            Optional<Boolean> answer = SetupWizardSupport.parseYesNo(input, defaultYes);
            if (answer.isPresent()) {
                return answer.get();
            }
            warn(baseLang.tr("pnx.setupWizard.invalid_input"));
            console.flush();
        }
    }

    private boolean askYesNo(String questionKey, boolean defaultYes) {
        return readYesNo(promptText(SetupWizardSupport.formatYesNoPrompt(baseLang.tr(questionKey), defaultYes)), defaultYes);
    }

    /**
     * Asks for comma-separated names. A blank answer, or one that contains no name, keeps the current list.
     */
    private List<String> askNames(String labelKey, List<String> current) {
        String input = readAnswer(promptText(SetupWizardSupport.formatValuePrompt(baseLang.tr(labelKey), namesOrNone(current))));
        if (input == null) {
            return current;
        }
        List<String> names = SetupWizardSupport.parseCommaSeparatedNames(input);
        return names.isEmpty() ? current : names;
    }

    @FunctionalInterface
    private interface NumberParser {
        int parse(String input, int defaultValue);
    }
}
