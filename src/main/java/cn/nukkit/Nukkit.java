package cn.nukkit;

import cn.nukkit.nbt.stream.PGZIPOutputStream;
import cn.nukkit.plugin.js.JSIInitiator;
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
 * `_   _       _    _    _ _
 * | \ | |     | |  | |  (_) |
 * |  \| |_   _| | _| | ___| |_
 * | . ` | | | | |/ / |/ / | __|
 * | |\  | |_| |   <|   <| | |_
 * |_| \_|\__,_|_|\_\_|\_\_|\__|
 */

/**
 * Nukkit启动类，包含{@code main}函数。<br>
 * The launcher class of Nukkit, including the {@code main} function.
 *
 * @author MagicDroidX(code) @ Nukkit Project
 * @author 粉鞋大妈(javadoc) @ Nukkit Project
 * @since Nukkit 1.0 | Nukkit API 1.0.0
 */
@Slf4j
public class Nukkit {
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

    public static void main(String[] args) {
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.DISABLED);

        AtomicBoolean disableSentry = new AtomicBoolean(Boolean.parseBoolean(System.getProperty("disableSentry", "false")));

        Path propertiesPath = Paths.get(DATA_PATH, "server.properties");
        if (!disableSentry.get() && Files.isRegularFile(propertiesPath)) {
            Properties properties = new Properties();
            try (FileReader reader = new FileReader(propertiesPath.toFile())) {
                properties.load(reader);
                String value = properties.getProperty("disable-auto-bug-report", "false");
                disableSentry.set(value.equalsIgnoreCase("on") || value.equals("1") || Boolean.parseBoolean(value.toLowerCase(Locale.ENGLISH)));
            } catch (IOException e) {
                log.error("Failed to load server.properties to check disable-auto-bug-report.", e);
            }
        }

        // Force IPv4 and other system properties
        System.setProperty("java.net.preferIPv4Stack", "true");
        System.setProperty("log4j.skipJansi", "false");
        System.getProperties().putIfAbsent("io.netty.allocator.type", "unpooled");
        System.setProperty("leveldb.mmap", "true");
        InternalLoggerFactory.setDefaultFactory(Log4J2LoggerFactory.INSTANCE);

        // Command line argument parser setup
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

        // Apply command line options
        ANSI = !options.has(ansiSpec);
        TITLE = options.has(titleSpec);

        String verbosity = options.valueOf(vSpec);
        if (verbosity == null) {
            verbosity = options.valueOf(verbositySpec);
        }
        if (verbosity != null) {
            try {
                Level level = Level.valueOf(verbosity.toUpperCase(Locale.ENGLISH));
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
            JS_DEBUG_LIST = Arrays.asList(options.valueOf(jsDebugPortSpec).split(","));
        }

        try {
            if (TITLE) {
                System.out.print((char) 0x1b + "]0;PowerNukkitX is starting up..." + (char) 0x07);
            }
            new Server(PATH, DATA_PATH, PLUGIN_PATH, language);
        } catch (Throwable t) {
            log.error("Server initialization failed", t);
        }

        if (TITLE) {
            System.out.print((char) 0x1b + "]0;Stopping Server..." + (char) 0x07);
        }
        log.info("Stopping other threads");

        if (Server.getInstance().getPluginManager().getFileAssociations().containsKey("cn.nukkit.plugin.JSPluginLoader")) {
            JSIInitiator.jsTimer.cancel();
        }
        PGZIPOutputStream.getSharedThreadPool().shutdownNow();
        for (Thread thread : Thread.getAllStackTraces().keySet()) {
            if (thread instanceof InterruptibleThread && thread.isAlive()) {
                log.debug("Stopping {} thread", thread.getClass().getSimpleName());
                thread.interrupt();
            }
        }

        if (TITLE) {
            System.out.print((char) 0x1b + "]0;Server Stopped" + (char) 0x07);
        }
        LogManager.shutdown();
        Runtime.getRuntime().halt(0); // force exit
    }

    private static boolean requiresShortTitle() {
        // Shorter title for Windows 8/2012
        String osName = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
        return osName.contains("windows") && (osName.contains("windows 8") || osName.contains("2012"));
    }

    private static Properties getGitInfo() {
        try (InputStream gitFileStream = Nukkit.class.getModule().getResourceAsStream("git.properties")) {
            if (gitFileStream == null) {
                return null;
            }
            Properties properties = new Properties();
            properties.load(gitFileStream);
            return properties;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load git properties", e);
        }
    }

    private static String getVersion() {
        try (InputStream resourceAsStream = Nukkit.class.getModule().getResourceAsStream("git.properties")) {
            if (resourceAsStream == null) {
                return "Unknown-PNX-SNAPSHOT";
            }
            Properties properties = new Properties();
            properties.load(new BufferedReader(new InputStreamReader(resourceAsStream)));
            String version = properties.getProperty("git.build.version");
            return "${project.version}".equalsIgnoreCase(version) ? "Unknown-PNX-SNAPSHOT" : version;
        } catch (IOException e) {
            return "Unknown-PNX-SNAPSHOT";
        }
    }

    private static String getGitCommit() {
        if (GIT_INFO == null) {
            return "git-null";
        }
        return "git-" + GIT_INFO.getProperty("git.commit.id.abbrev", "null");
    }

    public static void setLogLevel(Level level) {
        Preconditions.checkNotNull(level, "Level must not be null");
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();
        LoggerConfig loggerConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
        loggerConfig.setLevel(level);
        ctx.updateLoggers();
    }

    public static Level getLogLevel() {
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();
        LoggerConfig loggerConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
        return loggerConfig.getLevel();
    }
}