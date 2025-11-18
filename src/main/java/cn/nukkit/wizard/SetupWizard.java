package cn.nukkit.wizard;

import cn.nukkit.console.NukkitConsole;
import cn.nukkit.utils.TextFormat;
import cn.nukkit.utils.Utils;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Locale;

/**
 * PowerNukkitX setup wizard class
 * @author xRookieFight
 */
public class SetupWizard {

    private final Logger logger;
    private final NukkitConsole console;
    private String predefinedLanguage, chosenLanguage, motd;
    private boolean isEnded = false;
    private int port, gamemode;

    private final String DEFAULT_MOTD = "PowerNukkitX Server";
    private final int DEFAULT_PORT = 19132;
    private final int DEFAULT_GAMEMODE = 0;

    public SetupWizard(Logger logger, String predefinedLanguage, NukkitConsole console) {
        this.logger = logger;
        this.predefinedLanguage = predefinedLanguage;
        this.console = console;
    }

    public void run(){
        logger.info("{}Welcome! Please choose a language first!", TextFormat.GREEN);
        try {
            InputStream languageList = this.getClass().getModule().getResourceAsStream("language/language.list");
            if (languageList == null) {
                throw new IllegalStateException("language/language.list is missing. If you are running a development version, make sure you have run 'git submodule update --init'.");
            }
            String[] lines = Utils.readFile(languageList).split("\n");
            for (String line : lines) {
                logger.info(line);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read language list", e);
        }
        while (this.chosenLanguage == null) {
            String lang;
            if (predefinedLanguage != null) {
                logger.info("Trying to load language from predefined language: {}", predefinedLanguage);
                lang = predefinedLanguage;
            } else {
                lang = this.console.readLine();
            }

            try (InputStream conf = this.getClass().getClassLoader().getResourceAsStream("language/" + lang + "/lang.json")) {
                if (conf != null) {
                    this.chosenLanguage = lang;
                } else if (predefinedLanguage != null) {
                    logger.warn("No language found for predefined language: {}, please choose a valid language", predefinedLanguage);
                    predefinedLanguage = null;
                }
            } catch (IOException e) {
                throw new IllegalStateException("Failed to read language list", e);
            }
        }

        logger.info("{}Do you want to skip the set-up wizard? (Y/n)", TextFormat.GOLD);
        String skipOption = this.console.readLine();

        switch (skipOption.toLowerCase(Locale.ENGLISH)) {
            case "n":
            case "no":
                changeOptions();
                break;
            default:
                this.motd = DEFAULT_MOTD;
                this.port = DEFAULT_PORT;
                this.gamemode = DEFAULT_GAMEMODE;

                displayIPOptions();
                this.isEnded = true;
                break;
        }
    }

    public void displayIPOptions() {
        try {
            String localIP = InetAddress.getLocalHost().getHostAddress();
            String externalIP = getExternalIP();

            logger.info("{}Your PowerNukkitX server is ready to start.", TextFormat.AQUA);
            logger.info("{}Your external IP is {}. Ensure that port forwarding is configured to your local IP: {}.", TextFormat.AQUA, externalIP, localIP);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public void changeOptions(){
        logger.info("{}Enter server MOTD (default PowerNukkitX Server):", TextFormat.YELLOW);
        String motd = this.console.readLine();
        if (!motd.isEmpty()) {
            this.motd = motd;
        } else {
            logger.warn("Invalid MOTD, using default");
            this.motd = DEFAULT_MOTD;
        }

        logger.info("{}Enter server port (default 19132):", TextFormat.YELLOW);
        String portInput = this.console.readLine();
        if (!portInput.isEmpty()) {
            try {
                int p = Integer.parseInt(portInput);
                if (p > 0 && p <= 65535) {
                    this.port = p;
                } else {
                    logger.warn("Port out of range, using default 19132");
                    this.port = DEFAULT_PORT;
                }
            } catch (NumberFormatException e) {
                logger.warn("Invalid port number, using default 19132");
                this.port = DEFAULT_PORT;
            }
        }

        logger.info("{}Enter default gamemode (0: Survival, 1: Creative, 2: Adventure):", TextFormat.YELLOW);
        String gamemodeInput = this.console.readLine();
        if (!gamemodeInput.isEmpty()) {
            try {
                int gamemode = Integer.parseInt(gamemodeInput);
                if (gamemode >= 0 && gamemode <= 2) {
                    this.gamemode = gamemode;
                } else {
                    logger.warn("Invalid gamemode, using default Survival");
                    this.gamemode = DEFAULT_GAMEMODE;
                }
            } catch (NumberFormatException e) {
                logger.warn("Invalid gamemode number, using default Survival");
                this.gamemode = DEFAULT_GAMEMODE;
            }
        }

        displayIPOptions();
        this.isEnded = true;
    }

    public String getExternalIP() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://api.ipify.org"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String ip = response.body().trim();
            return ip.isEmpty() ? "unknown" : ip;
        } catch (Exception e) {
            return "unknown";
        }
    }

    public boolean isEnded(){
        return this.isEnded;
    }

    public String getChosenLanguage() {
        return this.chosenLanguage;
    }

    public int getPort() {
        return this.port;
    }

    public String getMotd() {
        return this.motd;
    }

    public int getGamemode() {
        return this.gamemode;
    }
}
