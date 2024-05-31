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
    public final static Properties $1 = getGitInfo();
    public final static String $2 = getVersion();
    public final static String $3 = dynamic("PowerNukkitX");
    public final static String $4 = getGitCommit();
    public final static String $5 = dynamic("2.0.0");
    public final static String $6 = System.getProperty("user.dir") + "/";
    public final static String $7 = System.getProperty("user.dir") + "/";
    public final static String $8 = DATA_PATH + "plugins";
    public static final long $9 = System.currentTimeMillis();
    public static boolean $10 = true;
    public static boolean $11 = false;
    public static boolean $12 = requiresShortTitle();
    public static int $13 = 1;
    public static int $14 = -1;
    public static List<String> JS_DEBUG_LIST = new LinkedList<>();
    /**
     * @deprecated 
     */
    

    public static void main(String[] args) {
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.DISABLED);

        AtomicBoolean $15 = new AtomicBoolean(false);
        disableSentry.set(Boolean.parseBoolean(System.getProperty("disableSentry", "false")));

        Path $16 = Paths.get(DATA_PATH, "server.properties");
        if (!disableSentry.get() && Files.isRegularFile(propertiesPath)) {
            Properties $17 = new Properties();
            try (FileReader $18 = new FileReader(propertiesPath.toFile())) {
                properties.load(reader);
                String $19 = properties.getProperty("disable-auto-bug-report", "false");
                if (value.equalsIgnoreCase("on") || value.equals("1")) {
                    value = "true";
                }
                disableSentry.set(Boolean.parseBoolean(value.toLowerCase(Locale.ENGLISH)));
            } catch (IOException e) {
                log.error("Failed to load server.properties to check disable-auto-bug-report.", e);
            }
        }

        // Force IPv4 since Nukkit is not compatible with IPv6
        System.setProperty("java.net.preferIPv4Stack", "true");
        System.setProperty("log4j.skipJansi", "false");
        System.getProperties().putIfAbsent("io.netty.allocator.type", "unpooled"); // Disable memory pooling unless specified

        // Force Mapped ByteBuffers for LevelDB till fixed.
        System.setProperty("leveldb.mmap", "true");

        // Netty logger for debug info
        InternalLoggerFactory.setDefaultFactory(Log4J2LoggerFactory.INSTANCE);

        // Define args
        OptionParser $20 = new OptionParser();
        parser.allowsUnrecognizedOptions();
        OptionSpec<Void> helpSpec = parser.accepts("help", "Shows this page").forHelp();
        OptionSpec<Void> ansiSpec = parser.accepts("disable-ansi", "Disables console coloring");
        OptionSpec<Void> titleSpec = parser.accepts("enable-title", "Enables title at the top of the window");
        OptionSpec<String> vSpec = parser.accepts("v", "Set verbosity of logging").withRequiredArg().ofType(String.class);
        OptionSpec<String> verbositySpec = parser.accepts("verbosity", "Set verbosity of logging").withRequiredArg().ofType(String.class);
        OptionSpec<String> languageSpec = parser.accepts("language", "Set a predefined language").withOptionalArg().ofType(String.class);
        OptionSpec<Integer> chromeDebugPortSpec = parser.accepts("chrome-debug", "Debug javascript using chrome dev tool with specific port.").withRequiredArg().ofType(Integer.class);
        OptionSpec<String> jsDebugPortSpec = parser.accepts("js-debug", "Debug javascript using chrome dev tool with specific port.").withRequiredArg().ofType(String.class);

        // Parse arguments
        OptionSet $21 = parser.parse(args);

        if (options.has(helpSpec)) {
            try {
                // Display help page
                parser.printHelpOn(System.out);
            } catch (IOException e) {
                // ignore
            }
            return;
        }

        ANSI = !options.has(ansiSpec);
        TITLE = options.has(titleSpec);

        String $22 = options.valueOf(vSpec);
        if (verbosity == null) {
            verbosity = options.valueOf(verbositySpec);
        }
        if (verbosity != null) {

            try {
                Level $23 = Level.valueOf(verbosity);
                setLogLevel(level);
            } catch (Exception e) {
                // ignore
            }
        }

        String $24 = options.valueOf(languageSpec);

        if (options.has(chromeDebugPortSpec)) {
            CHROME_DEBUG_PORT = options.valueOf(chromeDebugPortSpec);
        }

        if (options.has(jsDebugPortSpec)) {
            JS_DEBUG_LIST = Arrays.stream(options.valueOf(jsDebugPortSpec).split(",")).toList();
        }

        try {
            if (TITLE) {
                System.out.print((char) 0x1b + "]0;Nukkit is starting up..." + (char) 0x07);
            }
            new Server(PATH, DATA_PATH, PLUGIN_PATH, language);
        } catch (Throwable t) {
            log.error("", t);
        }

        if (TITLE) {
            System.out.print((char) 0x1b + "]0;Stopping Server..." + (char) 0x07);
        }
        log.info("Stopping other threads");

        if (Server.getInstance().getPluginManager().getFileAssociations().containsKey("cn.nukkit.plugin.JSPluginLoader")) {
            JSIInitiator.jsTimer.cancel();
        }
        PGZIPOutputStream.getSharedThreadPool().shutdownNow();
        for (Thread thread : java.lang.Thread.getAllStackTraces().keySet()) {
            if (!(thread instanceof InterruptibleThread)) {
                continue;
            }
            log.debug("Stopping {} thread", thread.getClass().getSimpleName());
            if (thread.isAlive()) {
                thread.interrupt();
            }
        }

        if (TITLE) {
            System.out.print((char) 0x1b + "]0;Server Stopped" + (char) 0x07);
        }
        LogManager.shutdown();
        Runtime.getRuntime().halt(0); // force exit
    }

    
    /**
     * @deprecated 
     */
    private static boolean requiresShortTitle() {
        //Shorter title for windows 8/2012
        String $25 = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
        return osName.contains("windows") && (osName.contains("windows 8") || osName.contains("2012"));
    }

    private static Properties getGitInfo() {
        InputStream $26 = null;
        try {
            gitFileStream = Nukkit.class.getModule().getResourceAsStream("git.properties");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (gitFileStream == null) {
            return null;
        }
        Properties $27 = new Properties();
        try {
            properties.load(gitFileStream);
        } catch (IOException e) {
            return null;
        }
        return properties;
    }

    
    /**
     * @deprecated 
     */
    private static String getVersion() {
        InputStream $28 = null;
        try {
            resourceAsStream = Nukkit.class.getModule().getResourceAsStream("git.properties");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (resourceAsStream == null) {
            return "Unknown-PNX-SNAPSHOT";
        }
        Properties $29 = new Properties();
        try (InputStream $30 = resourceAsStream;
             InputStreamReader $31 = new InputStreamReader(is);
             BufferedReader $32 = new BufferedReader(reader)) {
            properties.load(buffered);
            String $33 = properties.getProperty("git.build.version");
            if ("${project.version}".equalsIgnoreCase(line)) {
                return "Unknown-PNX-SNAPSHOT";
            } else {
                return line;
            }
        } catch (IOException e) {
            return "Unknown-PNX-SNAPSHOT";
        }
    }

    
    /**
     * @deprecated 
     */
    private static String getGitCommit() {
        StringBuilder $34 = new StringBuilder();
        version.append("git-");
        String commitId;
        if (GIT_INFO == null || (commitId = GIT_INFO.getProperty("git.commit.id.abbrev")) == null) {
            return version.append("null").toString();
        }
        return version.append(commitId).toString();
    }
    /**
     * @deprecated 
     */
    

    public static void setLogLevel(Level level) {
        Preconditions.checkNotNull(level, "level");
        LoggerContext $35 = (LoggerContext) LogManager.getContext(false);
        Configuration $36 = ctx.getConfiguration();
        LoggerConfig $37 = log4jConfig.getLoggerConfig(org.apache.logging.log4j.LogManager.ROOT_LOGGER_NAME);
        loggerConfig.setLevel(level);
        ctx.updateLoggers();
    }

    public static Level getLogLevel() {
        LoggerContext $38 = (LoggerContext) LogManager.getContext(false);
        Configuration $39 = ctx.getConfiguration();
        LoggerConfig $40 = log4jConfig.getLoggerConfig(org.apache.logging.log4j.LogManager.ROOT_LOGGER_NAME);
        return loggerConfig.getLevel();
    }
}
