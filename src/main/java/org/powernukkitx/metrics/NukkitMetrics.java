package org.powernukkitx.metrics;

import org.powernukkitx.Player;
import org.powernukkitx.PowerNukkitX;
import org.powernukkitx.Server;
import org.powernukkitx.network.process.auth.ClientChainData;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.powernukkitx.utils.SHAUtil;
import org.powernukkitx.utils.Utils;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.ComputerSystem;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.powernukkitx.utils.NukkitCollectors.countingInt;
import static java.util.stream.Collectors.groupingBy;


@Slf4j
public class NukkitMetrics {
    private static NukkitMetrics metricsInstance;

    private String serverUUID;
    private boolean logFailedRequests = true;

    private Metrics metrics;

    private NukkitMetrics() {
        this.serverUUID = buildServerUUID();
    }

    /**
     * Sets up the nukkit metrics and starts it if it hadn't started yet.
     */
    public static boolean startNow() {
        NukkitMetrics nukkitMetrics = getOrCreateMetrics();
        return nukkitMetrics.metrics != null;
    }

    private static synchronized NukkitMetrics getOrCreateMetrics() {
        if (metricsInstance == null) {
            metricsInstance = createMetrics(Server.getInstance());
        }
        return metricsInstance;
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
            File cliFile = new File(cliPath);
            if (!cliFile.exists() || !cliFile.canExecute()) {
                return pnxCliVersion = "Invalid PNX-CLI path";
            }

            var process = new ProcessBuilder(cliPath, "-V").start(); // nosemgrep - cliPath is an admin-set, validated executable, run without a shell
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
        NukkitMetrics nukkitMetrics = new NukkitMetrics();

        final Metrics metrics = new Metrics("PowerNukkitX", nukkitMetrics.serverUUID, nukkitMetrics.logFailedRequests);
        nukkitMetrics.metrics = metrics;

        metrics.addCustomChart(new Metrics.SingleLineChart("players", () -> server.getOnlinePlayers().size()));
        metrics.addCustomChart(new Metrics.SimplePie("minecraft_version", server::getVersion));
        metrics.addCustomChart(new Metrics.SimplePie("pnx_version", server::getBStatsNukkitVersion));
        metrics.addCustomChart(new Metrics.SimplePie("git_commit", server::getGitCommit));
        metrics.addCustomChart(new Metrics.SimplePie("xbox_auth", () -> server.getSettings().baseSettings().xboxAuth() ? "Required" : "Not required"));

        metrics.addCustomChart(new Metrics.AdvancedPie("player_platform_pie", () -> server.getOnlinePlayers().values().stream()
                .map(Player::getClientChainData)
                .map(ClientChainData::getDeviceOS)
                .collect(groupingBy(buildPlatform -> Utils.mapDeviceOSToString(buildPlatform.getId()), countingInt()))));

        metrics.addCustomChart(new Metrics.AdvancedPie("player_game_version_pie", () -> server.getOnlinePlayers().values().stream()
                .map(Player::getClientChainData)
                .collect(groupingBy(ClientChainData::getGameVersion, countingInt()))));

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
                // valid strings for the major may potentially include values such as -ea to deannotate a pre-release
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

    private String buildServerUUID() {
        List<String> identifiers = new ArrayList<>();
        identifiers.add(PowerNukkitX.DATA_PATH);
        try {
            HardwareAbstractionLayer hardware = new SystemInfo().getHardware();
            ComputerSystem computerSystem = hardware.getComputerSystem();
            CentralProcessor processor = hardware.getProcessor();

            addIdentifier(identifiers, computerSystem.getHardwareUUID());
            addIdentifier(identifiers, computerSystem.getSerialNumber());
            addIdentifier(identifiers, computerSystem.getBaseboard().getSerialNumber());
            addIdentifier(identifiers, computerSystem.getManufacturer());
            addIdentifier(identifiers, computerSystem.getModel());
            addIdentifier(identifiers, processor.getProcessorIdentifier().getIdentifier());

            hardware.getNetworkIFs().stream()
                .map(NetworkIF::getMacaddr)
                .filter(NukkitMetrics::isUsableIdentifier)
                .map(value -> value.toLowerCase(Locale.ROOT))
                .sorted()
                .forEach(identifiers::add);
        } catch (RuntimeException | LinkageError e) {
            log.warn("Could not read hardware identifiers for the bStats server UUID", e);
        }
        return UUID.nameUUIDFromBytes(SHAUtil.SHA512(String.join("", identifiers)).getBytes(StandardCharsets.UTF_8)).toString();
    }

    private static void addIdentifier(List<String> identifiers, String value) {
        if (isUsableIdentifier(value)) {
            identifiers.add(value.trim().toLowerCase(Locale.ROOT));
        }
    }

    private static boolean isUsableIdentifier(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }
        return switch (value.trim().toLowerCase(Locale.ROOT)) {
            case "unknown", "none", "null", "not specified", "not applicable",
                 "to be filled by o.e.m.", "default string", "system serial number", "03000200-0400-0500-0006-000700080009",
                 "ffffffff-ffff-ffff-ffff-ffffffffffff", "00000000-0000-0000-0000-000000000000" -> false;
            default -> true;
        };
    }

    public static synchronized void closeNow() {
        if (metricsInstance != null && metricsInstance.metrics != null) {
            metricsInstance.metrics.close();
        }
        metricsInstance = null;
    }
}
