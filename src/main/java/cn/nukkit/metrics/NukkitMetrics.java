package cn.nukkit.metrics;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.LoginChainData;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static cn.nukkit.utils.NukkitCollectors.countingInt;
import static java.util.stream.Collectors.groupingBy;


@Slf4j
public class NukkitMetrics {
    private static final AtomicReference<Map<Server, NukkitMetrics>> metricsStarted = new AtomicReference<>(Collections.emptyMap());

    private final Server server;

    private boolean enabled;
    private String serverUUID;
    private boolean logFailedRequests;

    private Metrics metrics;

    private NukkitMetrics(Server server, boolean start) {
        this.server = server;

        try {
            this.loadConfig();
        } catch (Exception e) {
            log.warn("Failed to load the bStats configuration file", e);
        }

        if (start && enabled) {
            startNow(server);
        }
    }

    /**
     * Setup the nukkit metrics and starts it if it hadn't started yet.
     *
     * @param server The Nukkit server
     */
    public static boolean startNow(Server server) {
        NukkitMetrics nukkitMetrics = getOrCreateMetrics(server);
        return nukkitMetrics.metrics != null;
    }

    private static NukkitMetrics getOrCreateMetrics(@NotNull final Server server) {
        Map<Server, NukkitMetrics> current = metricsStarted.get();
        NukkitMetrics metrics = current.get(server);
        if (metrics != null) {
            return metrics;
        }

        current = metricsStarted.updateAndGet(before -> {
            Map<Server, NukkitMetrics> mutable = before;
            if (before.isEmpty()) {
                mutable = new WeakHashMap<>(1);
            }
            mutable.computeIfAbsent(server, NukkitMetrics::createMetrics);
            return mutable;
        });

        metrics = current.get(server);
        assert metrics != null;
        return metrics;
    }

    private static String pnxCliVersion = null;

    private static String getPNXCLIVersion() {
        if (pnxCliVersion != null) {
            return pnxCliVersion;
        }
        var version = System.getProperty("pnx.cli.version");
        if (version != null && !version.isBlank()) {
            return pnxCliVersion = version;
        }
        var cliPath = System.getProperty("pnx.cli.path");
        if (cliPath == null || cliPath.isBlank()) {
            return pnxCliVersion = "No PNX-CLI";
        }
        try {
            var process = new ProcessBuilder(cliPath, "-V").start();
            process.waitFor(10, TimeUnit.MICROSECONDS);
            var content = new String(process.getInputStream().readAllBytes()).replace("\n", "");
            if (content.isBlank() || !content.contains(".")) {
                return pnxCliVersion = "Unknown";
            }
            return pnxCliVersion = content;
        } catch (IOException | InterruptedException ignored) {
            return pnxCliVersion = "Unknown";
        }
    }

    @NotNull
    private static NukkitMetrics createMetrics(@NotNull final Server server) {
        NukkitMetrics nukkitMetrics = new NukkitMetrics(server, false);
        if (!nukkitMetrics.enabled) {
            return nukkitMetrics;
        }

        final Metrics metrics = new Metrics("PowerNukkitX", nukkitMetrics.serverUUID, nukkitMetrics.logFailedRequests);
        nukkitMetrics.metrics = metrics;

        metrics.addCustomChart(new Metrics.SingleLineChart("players", () -> server.getOnlinePlayers().size()));
        metrics.addCustomChart(new Metrics.SimplePie("minecraft_version", server::getVersion));
        metrics.addCustomChart(new Metrics.SimplePie("pnx_version", server::getBStatsNukkitVersion));
        metrics.addCustomChart(new Metrics.SimplePie("xbox_auth", () -> server.getSettings().baseSettings().xboxAuth() ? "Required" : "Not required"));

        metrics.addCustomChart(new Metrics.AdvancedPie("player_platform_pie", () -> server.getOnlinePlayers().values().stream()
                .map(Player::getLoginChainData)
                .map(LoginChainData::getDeviceOS)
                .collect(groupingBy(nukkitMetrics::mapDeviceOSToString, countingInt()))));

        metrics.addCustomChart(new Metrics.AdvancedPie("player_game_version_pie", () -> server.getOnlinePlayers().values().stream()
                .map(Player::getLoginChainData)
                .collect(groupingBy(LoginChainData::getGameVersion, countingInt()))));

        metrics.addCustomChart(new Metrics.DrilldownPie("java_version_pie", new JavaVersionRetriever()));

        metrics.addCustomChart(new Metrics.SimplePie("pnx_cli_version", NukkitMetrics::getPNXCLIVersion));
        return nukkitMetrics;
    }

    private static class JavaVersionRetriever implements Callable<Map<String, Map<String, Integer>>> {
        // The following code can be attributed to the PaperMC project
        // https://github.com/PaperMC/Paper/blob/master/Spigot-Server-Patches/0005-Paper-Metrics.patch#L614
        @Override
        public Map<String, Map<String, Integer>> call() {
            Map<String, Map<String, Integer>> map = new HashMap<>();
            String javaVersion = System.getProperty("java.version");
            Map<String, Integer> entry = new HashMap<>();
            entry.put(javaVersion, 1);

            // http://openjdk.java.net/jeps/223
            // Java decided to change their versioning scheme and in doing so modified the java.version system
            // property to return $major[.$minor][.$secuity][-ea], as opposed to 1.$major.0_$identifier
            // we can handle pre-9 by checking if the "major" is equal to "1", otherwise, 9+
            String majorVersion = javaVersion.split("\\.")[0];
            String release;

            int indexOf = javaVersion.lastIndexOf('.');

            if (majorVersion.equals("1")) {
                release = "Java " + javaVersion.substring(0, indexOf);
            } else {
                // of course, it really wouldn't be all that simple if they didn't add a quirk, now would it
                // valid strings for the major may potentially include values such as -ea to deannotate a pre release
                Matcher versionMatcher = Pattern.compile("\\d+").matcher(majorVersion);
                if (versionMatcher.find()) {
                    majorVersion = versionMatcher.group(0);
                }
                release = "Java " + majorVersion;
            }
            map.put(release, entry);
            return map;
        }
    }

    /**
     * Loads the bStats configuration.
     */
    private void loadConfig() throws IOException {
        File bStatsFolder = new File(server.getPluginPath(), "bStats");

        if (!bStatsFolder.exists() && !bStatsFolder.mkdirs()) {
            log.warn("Failed to create bStats metrics directory");
            return;
        }

        File configFile = new File(bStatsFolder, "config.yml");
        if (!configFile.exists()) {
            writeFile(configFile,
                    "# bStats collects some data for plugin authors like how many servers are using their plugins.",
                    "# To honor their work, you should not disable it.",
                    "# This has nearly no effect on the server performance!",
                    "# Check out https://bStats.org/ to learn more :)",
                    "enabled: true",
                    "serverUuid: \"" + UUID.randomUUID() + "\"",
                    "logFailedRequests: false");
        }

        Config config = new Config(configFile, Config.YAML);

        // Load configuration
        this.enabled = config.getBoolean("enabled", true);
        this.serverUUID = config.getString("serverUuid");
        this.logFailedRequests = config.getBoolean("logFailedRequests", false);
    }

    private void writeFile(File file, String... lines) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        }
    }

    private String mapDeviceOSToString(int os) {
        return switch (os) {
            case 1 -> "Android";
            case 2 -> "iOS";
            case 3 -> "macOS";
            case 4 -> "Fire OS";
            case 5 -> "Gear VR";
            case 6 -> "HoloLens";
            case 7, 8 -> "Windows";
            case 9 -> "Dedicated";
            case 10 -> "tvOS";
            case 11 -> "PlayStation";
            case 12 -> "Switch";
            case 13 -> "Xbox";
            case 14 -> "Windows Phone";
            default -> "Unknown";
        };
    }

    public static void closeNow(Server server) {
        NukkitMetrics nukkitMetrics = getOrCreateMetrics(server);
        if (nukkitMetrics.metrics != null) nukkitMetrics.metrics.close();
    }
}
