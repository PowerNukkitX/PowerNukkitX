package cn.nukkit;

import cn.nukkit.nbt.stream.PGZIPOutputStream;
import cn.nukkit.utils.exception.GitInfoException;
import com.google.common.base.Preconditions;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Log4J2LoggerFactory;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import static cn.nukkit.utils.Utils.dynamic;

/*
 *   _____                       _   _       _    _    _ _  __   __
 *  |  __ \                     | \ | |     | |  | |  (_) | \ \ / /
 *  | |__) |____      _____ _ __|  \| |_   _| | _| | ___| |_ \ V /
 *  |  ___/ _ \ \ /\ / / _ \ '__| . ` | | | | |/ / |/ / | __| > <
 *  | |  | (_) \ V  V /  __/ |  | |\  | |_| |   <|   <| | |_ / . \
 *  |_|   \___/ \_/\_/ \___|_|  |_| \_|\__,_|_|\_\_|\_\_|\__/_/ \_\
 *
 * PowerNukkitX is a fork of PowerNukkit, which is a Minecraft: Bedrock Edition server software written in Java.
 * PowerNukkitX is open-source software and can be modified under the terms of its license.
 *
 * @authors PowerNukkitX Team
 * @link https://powernukkit.org | docs: https://docs.powernukkit.org
 * @license GNU Lesser General Public License v3.0
*/


/**
 * PowerNukkitX launcher class, including the {@code main nukkit} function.
 * @since Nukkit 1.0 | Nukkit API 1.0.0
 */
@Slf4j
public class PowerNukkitX {
    public static final Properties GIT_INFO = getGitInfo();
    public static final String VERSION = getVersion();
    public static final String CODENAME = dynamic("PowerNukkitX");
    public static final String GIT_COMMIT = getGitCommit();
    public static final String API_VERSION = dynamic("2.0.0");
    public static final String PATH = System.getProperty("user.dir") + "/";
    public static final String DATA_PATH = System.getProperty("user.dir") + "/";
    public static final String PLUGIN_PATH = DATA_PATH + "plugins";
    public static final long START_TIME = System.currentTimeMillis();
    public static boolean ANSI = true;
    public static boolean TITLE = false;
    public static boolean shortTitle = requiresShortTitle();
    public static int DEBUG = 1;
    public static int CHROME_DEBUG_PORT = -1;
    public static List<String> JS_DEBUG_LIST = new LinkedList<>();

    /**
     * Main method to launch the PowerNukkitX server.
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.DISABLED);

        AtomicBoolean disableSentry = new AtomicBoolean(false);
        disableSentry.set(Boolean.parseBoolean(System.getProperty("disableSentry", "false")));

        Path propertiesPath = Paths.get(DATA_PATH, "server.properties");
        if (!disableSentry.get() && Files.isRegularFile(propertiesPath)) {
            Properties properties = new Properties();
            try (FileReader reader = new FileReader(propertiesPath.toFile())) {
                properties.load(reader);
                String value = properties.getProperty("disable-auto-bug-report", "false");
                if (value.equalsIgnoreCase("on") || value.equals("1")) {
                    value = "true";
                }
                disableSentry.set(Boolean.parseBoolean(value.toLowerCase(Locale.ENGLISH)));
            } catch (IOException e) {
                log.error("Failed to load server.properties to check disable-auto-bug-report.", e);
            }
        }

        System.setProperty("java.net.preferIPv4Stack", "true");
        System.setProperty("log4j.skipJansi", "false");
        System.getProperties().putIfAbsent("io.netty.allocator.type", "unpooled");
        System.setProperty("leveldb.mmap", "true");
        InternalLoggerFactory.setDefaultFactory(Log4J2LoggerFactory.INSTANCE);

        OptionParser parser = new OptionParser();
        parser.allowsUnrecognizedOptions();
        OptionSpec<Void> helpSpec = parser.accepts("help", "Shows this page").forHelp();
        OptionSpec<Void> ansiSpec = parser.accepts("disable-ansi", "Disables console coloring");
        OptionSpec<Void> titleSpec = parser.accepts("enable-title", "Enables title at the top of the window");
        OptionSpec<String> vSpec = parser.accepts("v", "Set verbosity of logging").withRequiredArg().ofType(String.class);
        OptionSpec<String> verbositySpec = parser.accepts("verbosity", "Set verbosity of logging").withRequiredArg().ofType(String.class);
        OptionSpec<String> languageSpec = parser.accepts("language", "Set a predefined language").withOptionalArg().ofType(String.class);
        OptionSpec<Integer> chromeDebugPortSpec = parser.accepts("chrome-debug", "Debug javascript using chrome dev tool with specific port.").withRequiredArg().ofType(Integer.class);
        OptionSpec<String> jsDebugPortSpec = parser.accepts("js-debug", "Debug javascript using chrome dev tool with specific port.").withRequiredArg().ofType(String.class);

        OptionSet options = parser.parse(args);

        if (options.has(helpSpec)) {
            try {
                parser.printHelpOn(System.out);
            } catch (IOException e) {
                // ignore
            }
            return;
        }

        ANSI = !options.has(ansiSpec);
        TITLE = options.has(titleSpec);

        String verbosity = options.valueOf(vSpec);
        if (verbosity == null) {
            verbosity = options.valueOf(verbositySpec);
        }
        if (verbosity != null) {
            try {
                Level level = Level.valueOf(verbosity);
                setLogLevel(level);
            } catch (Exception e) {
                // ignore
            }
        }

        String language = options.valueOf(languageSpec);

        if (options.has(chromeDebugPortSpec)) {
            CHROME_DEBUG_PORT = options.valueOf(chromeDebugPortSpec);
        }

        if (options.has(jsDebugPortSpec)) {
            JS_DEBUG_LIST = Arrays.stream(options.valueOf(jsDebugPortSpec).split(",")).toList();
        }

        try {
            if (TITLE) {
                System.out.print((char) 0x1b + "]0;PowerNukkitX is starting up..." + (char) 0x07);
            }
            new Server(PATH, DATA_PATH, PLUGIN_PATH, language);
        } catch (Throwable t) {
            log.error("", t);
        }

        if (TITLE) {
            System.out.print((char) 0x1b + "]0;Stopping Server..." + (char) 0x07);
        }
        log.info("Stopping other threads");

        PGZIPOutputStream.getSharedThreadPool().shutdownNow();
        for (Thread thread : java.lang.Thread.getAllStackTraces().keySet()) {
            if (thread instanceof InterruptibleThread) {
                log.debug("Stopping {} thread", thread.getClass().getSimpleName());
                if (thread.isAlive()) {
                    thread.interrupt();
                }
            }
        }

        if (TITLE) {
            System.out.print((char) 0x1b + "]0;Server Stopped" + (char) 0x07);
        }
        LogManager.shutdown();
        Runtime.getRuntime().halt(0);
    }

    /**
     * Checks if a short title is required based on the operating system.
     *
     * @return true if a short title is required, false otherwise
     */
    private static boolean requiresShortTitle() {
        String osName = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
        return osName.contains("windows") && (osName.contains("windows 8") || osName.contains("2012"));
    }

    /**
     * Retrieves Git information from the `git.properties` file.
     *
     * @return Properties object containing Git information
     */
    private static Properties getGitInfo() {
        try (InputStream gitFileStream = PowerNukkitX.class.getModule().getResourceAsStream("git.properties")) {
            if (gitFileStream == null) {
                return null;
            }
            Properties properties = new Properties();
            properties.load(gitFileStream);
            return properties;
        } catch (IOException e) {
            throw new GitInfoException("Failed to load git.properties", e);
        }
    }

    /**
     * Retrieves the version from the `git.properties` file.
     *
     * @return Version string
     */
    private static String getVersion() {
        try (InputStream resourceAsStream = PowerNukkitX.class.getModule().getResourceAsStream("git.properties");
             InputStreamReader reader = new InputStreamReader(resourceAsStream);
             BufferedReader buffered = new BufferedReader(reader)) {
            if (resourceAsStream == null) {
                return "Unknown-PNX-SNAPSHOT";
            }
            Properties properties = new Properties();
            properties.load(buffered);
            String line = properties.getProperty("git.build.version");
            return "${project.version}".equalsIgnoreCase(line) ? "Unknown-PNX-SNAPSHOT" : line;
        } catch (IOException e) {
            return "Unknown-PNX-SNAPSHOT";
        }
    }

    /**
     * Retrieves the Git commit ID from the `git.properties` file.
     *
     * @return Git commit ID string
     */
    private static String getGitCommit() {
        StringBuilder version = new StringBuilder("git-");
        String commitId = GIT_INFO == null ? null : GIT_INFO.getProperty("git.commit.id.abbrev");
        return version.append(commitId == null ? "null" : commitId).toString();
    }

    /**
     * Sets the log level for the application.
     *
     * @param level Log level to set
     */
    public static void setLogLevel(Level level) {
        Preconditions.checkNotNull(level, "level");
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration log4jConfig = ctx.getConfiguration();
        LoggerConfig loggerConfig = log4jConfig.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
        loggerConfig.setLevel(level);
        ctx.updateLoggers();
    }

    /**
     * Retrieves the current log level for the application.
     *
     * @return Current log level
     */
    public static Level getLogLevel() {
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration log4jConfig = ctx.getConfiguration();
        LoggerConfig loggerConfig = log4jConfig.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
        return loggerConfig.getLevel();
    }
}