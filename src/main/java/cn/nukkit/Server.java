package cn.nukkit;

import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockstate.BlockStateRegistry;
import cn.nukkit.command.*;
import cn.nukkit.command.function.FunctionManager;
import cn.nukkit.console.NukkitConsole;
import cn.nukkit.dispenser.DispenseBehaviorRegister;
import cn.nukkit.entity.Attribute;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.entity.data.profession.Profession;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.level.LevelInitEvent;
import cn.nukkit.event.level.LevelLoadEvent;
import cn.nukkit.event.server.*;
import cn.nukkit.inventory.CraftingManager;
import cn.nukkit.inventory.Recipe;
import cn.nukkit.item.Item;
import cn.nukkit.item.RuntimeItems;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.lang.BaseLang;
import cn.nukkit.lang.LangCode;
import cn.nukkit.lang.TextContainer;
import cn.nukkit.level.*;
import cn.nukkit.level.biome.EnumBiome;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.level.format.LevelProviderManager;
import cn.nukkit.level.format.anvil.Anvil;
import cn.nukkit.level.format.generic.BaseRegionLoader;
import cn.nukkit.level.generator.*;
import cn.nukkit.level.terra.PNXPlatform;
import cn.nukkit.level.tickingarea.manager.SimpleTickingAreaManager;
import cn.nukkit.level.tickingarea.manager.TickingAreaManager;
import cn.nukkit.level.tickingarea.storage.JSONTickingAreaStorage;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.metadata.EntityMetadataStore;
import cn.nukkit.metadata.LevelMetadataStore;
import cn.nukkit.metadata.PlayerMetadataStore;
import cn.nukkit.metrics.NukkitMetrics;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.CompressBatchedTask;
import cn.nukkit.network.Network;
import cn.nukkit.network.RakNetInterface;
import cn.nukkit.network.SourceInterface;
import cn.nukkit.network.protocol.BatchPacket;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.PlayerListPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.network.query.QueryHandler;
import cn.nukkit.network.rcon.RCON;
import cn.nukkit.permission.BanEntry;
import cn.nukkit.permission.BanList;
import cn.nukkit.permission.DefaultPermissions;
import cn.nukkit.permission.Permissible;
import cn.nukkit.plugin.*;
import cn.nukkit.plugin.js.JSFeatures;
import cn.nukkit.plugin.js.JSIInitiator;
import cn.nukkit.plugin.service.NKServiceManager;
import cn.nukkit.plugin.service.ServiceManager;
import cn.nukkit.positiontracking.PositionTrackingService;
import cn.nukkit.potion.Effect;
import cn.nukkit.potion.Potion;
import cn.nukkit.resourcepacks.ResourcePackManager;
import cn.nukkit.resourcepacks.loader.JarPluginResourcePackLoader;
import cn.nukkit.resourcepacks.loader.ZippedResourcePackLoader;
import cn.nukkit.scheduler.ServerScheduler;
import cn.nukkit.scheduler.Task;
import cn.nukkit.scoreboard.manager.IScoreboardManager;
import cn.nukkit.scoreboard.manager.ScoreboardManager;
import cn.nukkit.scoreboard.storage.JSONScoreboardStorage;
import cn.nukkit.spark.SparkInstaller;
import cn.nukkit.utils.*;
import cn.nukkit.utils.bugreport.ExceptionHandler;
import cn.nukkit.utils.collection.FreezableArrayManager;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;
import io.netty.buffer.ByteBuf;
import io.netty.util.internal.EmptyArrays;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.longs.LongLists;
import lombok.extern.log4j.Log4j2;
import org.iq80.leveldb.CompressionType;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.impl.Iq80DBFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 代表着服务器对象，全局单例.
 * <p>在{@link Nukkit}中被实例化，后通过{@link cn.nukkit.Server#getInstance}获取实例对象.
 * {@link cn.nukkit.Server}的构造方法进行了一系列操作，包括但不限于初始化配置文件，创建线程、线程池，开启插件，注册配方、方块、实体、物品等.
 * <p>
 * Represents a server object, global singleton.
 * <p>is instantiated in {@link Nukkit} and later the instance object is obtained via {@link cn.nukkit.Server#getInstance}.
 * The constructor method of {@link cn.nukkit.Server} performs a number of operations, including but not limited to initializing configuration files, creating threads, thread pools, start plugins, registering recipes, blocks, entities, items, etc.
 *
 * @author MagicDroidX
 * @author Box
 */
@Log4j2
public class Server {

    public static final String BROADCAST_CHANNEL_ADMINISTRATIVE = "nukkit.broadcast.admin";
    public static final String BROADCAST_CHANNEL_USERS = "nukkit.broadcast.user";

    private static Server instance = null;

    private BanList banByName;

    private BanList banByIP;

    private Config operators;

    private Config whitelist;

    private AtomicBoolean isRunning = new AtomicBoolean(true);

    private LongList busyingTime = LongLists.synchronize(new LongArrayList(0));

    private boolean hasStopped = false;

    private PluginManager pluginManager;

    private int profilingTickrate = 20;

    private ServerScheduler scheduler;

    /**
     * 一个tick计数器,记录服务器已经经过的tick数
     */
    private int tickCounter;

    private long nextTick;

    private final float[] tickAverage = {20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20};

    private final float[] useAverage = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    private float maxTick = 20;

    private float maxUse = 0;

    private int sendUsageTicker = 0;

    private boolean dispatchSignals = false;

    private final NukkitConsole console;
    private final ConsoleThread consoleThread;

    /**
     * 负责地形生成，数据压缩等计算任务的FJP线程池<br/>
     * <p>
     * FJP thread pool responsible for terrain generation, data compression and other computing tasks
     */
    public final ForkJoinPool computeThreadPool;

    private SimpleCommandMap commandMap;

    private CraftingManager craftingManager;

    private ResourcePackManager resourcePackManager;

    private ConsoleCommandSender consoleSender;

    private IScoreboardManager scoreboardManager;

    private FunctionManager functionManager;

    private TickingAreaManager tickingAreaManager;

    private int maxPlayers;

    private boolean autoSave = true;

    private boolean redstoneEnabled = true;


    /**
     * 配置项是否检查登录时间.<P>Does the configuration item check the login time.
     */
    public boolean checkLoginTime = true;

    private RCON rcon;

    private EntityMetadataStore entityMetadata;

    private PlayerMetadataStore playerMetadata;

    private LevelMetadataStore levelMetadata;

    private Network network;

    private boolean networkCompressionAsync = true;
    /**
     * 网络压缩级别<P>Network compression level
     */
    public int networkCompressionLevel = 7;
    private int networkZlibProvider = 0;

    @PowerNukkitXOnly
    @Since("1.19.30-r2")
    private int maximumStaleDatagrams = 512;

    @PowerNukkitXOnly
    @Since("1.19.30-r2")
    private int maximumSizePerChunk = 1048576;

    @Since("1.19.60-r1")
    @PowerNukkitXOnly
    private int serverAuthoritativeMovementMode = 0;

    private boolean autoTickRate = true;
    private int autoTickRateLimit = 20;
    private boolean alwaysTickPlayers = false;
    private int baseTickRate = 1;
    private Boolean getAllowFlight = null;
    private int difficulty = Integer.MAX_VALUE;
    private int defaultGamemode = Integer.MAX_VALUE;

    private int autoSaveTicker = 0;
    private int autoSaveTicks = 6000;

    private BaseLang baseLang;
    private LangCode baseLangCode;

    private boolean forceLanguage = false;

    private UUID serverID;

    private final String filePath;
    private final String dataPath;
    private final String pluginPath;
    private final String commandDataPath;

    private final Set<UUID> uniquePlayers = new HashSet<>();

    private QueryHandler queryHandler;

    private QueryRegenerateEvent queryRegenerateEvent;

    private Config properties;
    private Config config;

    private final Map<InetSocketAddress, Player> players = new HashMap<>();

    private final Map<UUID, Player> playerList = new HashMap<>();

    private PositionTrackingService positionTrackingService;

    private final Map<Integer, Level> levels = new HashMap<Integer, Level>() {
        @Override
        public Level put(Integer key, Level value) {
            Level result = super.put(key, value);
            levelArray = levels.values().toArray(Level.EMPTY_ARRAY);
            return result;
        }

        @Override
        public boolean remove(Object key, Object value) {
            boolean result = super.remove(key, value);
            levelArray = levels.values().toArray(Level.EMPTY_ARRAY);
            return result;
        }

        @Override
        public Level remove(Object key) {
            Level result = super.remove(key);
            levelArray = levels.values().toArray(Level.EMPTY_ARRAY);
            return result;
        }
    };

    private Level[] levelArray;

    private final ServiceManager serviceManager = new NKServiceManager();

    private Level defaultLevel = null;

    private boolean allowNether;

    private final Thread currentThread;

    private final long launchTime;

    private Watchdog watchdog;

    private DB nameLookup;

    private PlayerDataSerializer playerDataSerializer;

    private final Set<String> ignoredPackets = new HashSet<>();

    private boolean safeSpawn;

    private boolean forceSkinTrusted = false;

    private boolean checkMovement = true;

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    private boolean allowTheEnd;

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    private boolean useTerra;

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    private boolean enableExperimentMode;

    @PowerNukkitXOnly
    @Since("1.19.50-r1")
    private FreezableArrayManager freezableArrayManager;

    @PowerNukkitXOnly
    @Since("1.19.80-r2")
    public boolean enabledNetworkEncryption;


    /**
     * 最小初始化测试
     * Minimal initializer for testing
     */
    @SuppressWarnings("UnstableApiUsage")
    Server(File tempDir) throws IOException {
        if (tempDir.isFile() && !tempDir.delete()) {
            throw new IOException("Failed to delete " + tempDir);
        }
        instance = this;
        config = new Config();
        levelArray = Level.EMPTY_ARRAY;
        launchTime = System.currentTimeMillis();
        BatchPacket batchPacket = new BatchPacket();
        batchPacket.payload = EmptyArrays.EMPTY_BYTES;
        CraftingManager.setCraftingPacket(batchPacket);

        currentThread = Thread.currentThread();
        File abs = tempDir.getAbsoluteFile();
        filePath = abs.getPath();
        dataPath = filePath;

        File dir = new File(tempDir, "plugins");
        pluginPath = dir.getPath();

        File cmdDir = new File(tempDir, "command_data");
        commandDataPath = cmdDir.getPath();

        Files.createParentDirs(dir);
        Files.createParentDirs(new File(tempDir, "worlds"));
        Files.createParentDirs(new File(tempDir, "players"));

        baseLang = new BaseLang(BaseLang.FALLBACK_LANGUAGE);
        baseLangCode = mapInternalLang(BaseLang.FALLBACK_LANGUAGE);
        console = new NukkitConsole(this);
        consoleThread = new ConsoleThread();
        this.computeThreadPool = new ForkJoinPool(Math.min(0x7fff, Runtime.getRuntime().availableProcessors()), new ComputeThreadPoolThreadFactory(), null, false);
        freezableArrayManager = new FreezableArrayManager(true, 32, 32, 0, -256, 1024, 16, 1, 32);
        properties = new Config();
        banByName = new BanList(dataPath + "banned-players.json");
        banByIP = new BanList(dataPath + "banned-ips.json");
        operators = new Config();
        whitelist = new Config();
        scoreboardManager = new ScoreboardManager(new JSONScoreboardStorage(this.commandDataPath + "/scoreboard.json"));
        functionManager = new FunctionManager(this.commandDataPath + "/functions");
        tickingAreaManager = new SimpleTickingAreaManager(new JSONTickingAreaStorage(this.dataPath + "worlds/"));
        commandMap = new SimpleCommandMap(this);

        setMaxPlayers(10);

        this.registerProfessions();
        this.registerEntities();
        this.registerBlockEntities();
    }

    Server(final String filePath, String dataPath, String pluginPath, String predefinedLanguage) {
        Preconditions.checkState(instance == null, "Already initialized!");
        launchTime = System.currentTimeMillis();
        currentThread = Thread.currentThread(); // Saves the current thread instance as a reference, used in Server#isPrimaryThread()
        instance = this;

        this.filePath = filePath;
        if (!new File(dataPath + "worlds/").exists()) {
            new File(dataPath + "worlds/").mkdirs();
        }

        if (!new File(dataPath + "players/").exists()) {
            new File(dataPath + "players/").mkdirs();
        }

        if (!new File(pluginPath).exists()) {
            new File(pluginPath).mkdirs();
        }

        this.dataPath = new File(dataPath).getAbsolutePath() + "/";
        this.pluginPath = new File(pluginPath).getAbsolutePath() + "/";
        this.commandDataPath = new File(dataPath).getAbsolutePath() + "/command_data";

        if (!new File(commandDataPath).exists()) {
            new File(commandDataPath).mkdirs();
        }

        this.console = new NukkitConsole(this);
        this.consoleThread = new ConsoleThread();
        this.consoleThread.start();
        this.computeThreadPool = new ForkJoinPool(Math.min(0x7fff, Runtime.getRuntime().availableProcessors()), new ComputeThreadPoolThreadFactory(), null, false);

        this.playerDataSerializer = new DefaultPlayerDataSerializer(this);

        //todo: VersionString 现在不必要

        if (!new File(this.dataPath + "nukkit.yml").exists()) {
            log.info(TextFormat.GREEN + "Welcome! Please choose a language first!");
            String languagesCommaList;
            try {
                InputStream languageList = this.getClass().getModule().getResourceAsStream("language/language.list");
                if (languageList == null) {
                    throw new IllegalStateException("language/language.list is missing. If you are running a development version, make sure you have run 'git submodule update --init'.");
                }
                String[] lines = Utils.readFile(languageList).split("\n");
                for (String line : lines) {
                    log.info(line);
                }
                languagesCommaList = Stream.of(lines)
                        .filter(line -> !line.isEmpty())
                        .map(line -> line.substring(0, 3))
                        .collect(Collectors.joining(", "));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            String fallback = BaseLang.FALLBACK_LANGUAGE;
            String language = null;
            try {
                while (language == null) {
                    String lang;
                    if (predefinedLanguage != null) {
                        log.info("Trying to load language from predefined language: {}", predefinedLanguage);
                        lang = predefinedLanguage;
                    } else {
                        lang = this.console.readLine();
                    }

                    InputStream conf = null;
                    conf = this.getClass().getModule().getResourceAsStream("language/" + lang + "/lang.ini");
                    if (conf != null) {
                        language = lang;
                    } else if (predefinedLanguage != null) {
                        log.warn("No language found for predefined language: {}, please choose a valid language", predefinedLanguage);
                        predefinedLanguage = null;
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            Properties nukkitYmlLang = new Properties();
            InputStream nukkitYmlLangIS;

            try {
                nukkitYmlLangIS = this.getClass().getModule().getResourceAsStream("language/" + language + "/nukkit.yml.properties");
                if (nukkitYmlLangIS == null) {
                    nukkitYmlLangIS = this.getClass().getModule().getResourceAsStream("language/" + fallback + "/nukkit.yml.properties");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            if (nukkitYmlLangIS == null) {
                try {
                    Utils.writeFile(this.dataPath + "nukkit.yml", Server.class.getResourceAsStream("/default-nukkit.yml"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                try {
                    nukkitYmlLang.load(new InputStreamReader(nukkitYmlLangIS, StandardCharsets.UTF_8));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } finally {
                    try {
                        nukkitYmlLangIS.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                StringBuilder result = new StringBuilder();

                if (nukkitYmlLang.containsKey("nukkit.yml.header") && !nukkitYmlLang.getProperty("nukkit.yml.header").trim().isEmpty()) {
                    for (String header : nukkitYmlLang.getProperty("nukkit.yml.header").trim().split("\n")) {
                        result.append("# ").append(header).append(System.lineSeparator());
                    }
                    result.append(System.lineSeparator());
                }

                StringBuilder keyBuilder = new StringBuilder();
                try (BufferedReader in = new BufferedReader(new InputStreamReader(Server.class.getResourceAsStream("/default-nukkit.yml"), StandardCharsets.UTF_8))) {
                    String line;
                    LinkedList<String[]> path = new LinkedList<>();
                    Pattern pattern = Pattern.compile("^( *)([a-z-]+):");
                    int lastIdent = 0;
                    String[] last = null;
                    while ((line = in.readLine()) != null) {
                        Matcher matcher = pattern.matcher(line);
                        if (!matcher.find()) {
                            result.append(line).append(System.lineSeparator());
                            continue;
                        }

                        String current = matcher.group(2);
                        String ident = matcher.group(1);
                        int newIdent = ident.length();

                        if (newIdent < lastIdent) {
                            int reduced = lastIdent - newIdent;
                            int i = 0;
                            while (i < reduced) {
                                path.pollLast();
                                i++;
                            }
                            lastIdent = lastIdent - reduced;
                        }
                        if (newIdent > lastIdent) {
                            path.add(last);
                            lastIdent = newIdent;
                        }
                        last = new String[]{current, ident};

                        keyBuilder.setLength(0);
                        keyBuilder.append("nukkit.yml");
                        for (String[] part : path) {
                            keyBuilder.append('.').append(part[0]);
                        }
                        keyBuilder.append('.').append(current);
                        String key = keyBuilder.toString();
                        if (!nukkitYmlLang.containsKey(key) || nukkitYmlLang.getProperty(key).trim().isEmpty()) {
                            result.append(line).append(System.lineSeparator());
                            continue;
                        }

                        String[] comments = nukkitYmlLang.getProperty(key).trim().split("\n");
                        if (key.equals("nukkit.yml.aliases") || key.equals("nukkit.yml.worlds")) {
                            result.append(line).append(System.lineSeparator());
                            for (String comment : comments) {
                                result.append(ident).append(" # ").append(comment).append(System.lineSeparator());
                            }
                        } else if (key.equals("nukkit.yml.settings.language")) {
                            for (String comment : comments) {
                                comment = comment.replace("%1", languagesCommaList);
                                result.append(ident).append("# ").append(comment).append(System.lineSeparator());
                            }
                            result.append(ident).append("language: ").append(language).append(System.lineSeparator());
                        } else {
                            for (String comment : comments) {
                                result.append(ident).append("# ").append(comment).append(System.lineSeparator());
                            }
                            result.append(line).append(System.lineSeparator());
                        }
                    }

                    Utils.writeFile(this.dataPath + "nukkit.yml", result.toString());
                } catch (IOException e) {
                    throw new AssertionError("Failed to create nukkit.yml", e);
                }
            }

        }

        this.console.setExecutingCommands(true);

        log.info("Loading {} ...", TextFormat.GREEN + "nukkit.yml" + TextFormat.WHITE);
        this.config = new Config(this.dataPath + "nukkit.yml", Config.YAML);
        levelArray = Level.EMPTY_ARRAY;

        Nukkit.DEBUG = NukkitMath.clamp(this.getConfig("debug.level", 1), 1, 3);

        int logLevel = (Nukkit.DEBUG + 3) * 100;
        org.apache.logging.log4j.Level currentLevel = Nukkit.getLogLevel();
        for (org.apache.logging.log4j.Level level : org.apache.logging.log4j.Level.values()) {
            if (level.intLevel() == logLevel && level.intLevel() > currentLevel.intLevel()) {
                Nukkit.setLogLevel(level);
                break;
            }
        }

        ignoredPackets.addAll(getConfig().getStringList("debug.ignored-packets"));
        ignoredPackets.add("BatchPacket");

        log.info("Loading {} ...", TextFormat.GREEN + "server.properties" + TextFormat.WHITE);
        this.properties = new Config(this.dataPath + "server.properties", Config.PROPERTIES, new ConfigSection() {
            {
                put("motd", "PowerNukkitX Server");
                put("sub-motd", "https://powernukkitx.cn");
                put("server-port", 19132);
                put("server-ip", "0.0.0.0");
                put("view-distance", 12);
                put("white-list", false);
                put("achievements", true);
                put("announce-player-achievements", true);
                put("spawn-protection", 16);
                put("max-players", 20);
                put("allow-flight", false);
                put("spawn-animals", true);
                put("spawn-mobs", true);
                put("gamemode", 0);
                put("force-gamemode", false);
                put("hardcore", false);
                put("pvp", true);
                put("difficulty", 1);
                put("generator-settings", "");
                put("level-name", "world");
                put("level-seed", "");
                put("level-type", "DEFAULT");
                put("allow-nether", true);
                put("allow-the_end", true);
                put("use-terra", false);
                put("enable-experiment-mode", true);
                put("enable-query", true);
                put("enable-rcon", false);
                put("rcon.password", Base64.getEncoder().encodeToString(UUID.randomUUID().toString().replace("-", "").getBytes()).substring(3, 13));
                put("auto-save", true);
                put("force-resources", false);
                put("force-resources-allow-client-packs", false);
                put("xbox-auth", true);
                put("check-login-time", true);
                put("disable-auto-bug-report", false);
                put("allow-shaded", false);
                put("server-authoritative-movement", "server-auth");// Allowed values: "client-auth", "server-auth", "server-auth-with-rewind"
                put("network-encryption", true);
            }
        });
        // Allow Nether? (determines if we create a nether world if one doesn't exist on startup)
        this.allowNether = this.properties.getBoolean("allow-nether", true);

        this.allowTheEnd = this.properties.getBoolean("allow-the_end", true);

        this.useTerra = this.properties.getBoolean("use-terra", false);

        this.enableExperimentMode = this.properties.getBoolean("enable-experiment-mode", true);

        this.checkLoginTime = this.properties.getBoolean("check-login-time", true);

        if (this.isWaterdogCapable()) {
            this.checkLoginTime = false;
        }

        this.forceLanguage = this.getConfig("settings.force-language", false);
        var langName = this.getConfig("settings.language", BaseLang.FALLBACK_LANGUAGE);
        this.baseLang = new BaseLang(langName);
        this.baseLangCode = mapInternalLang(langName);

        var isShaded = StartArgUtils.isShaded();
        // 检测启动参数
        if (!StartArgUtils.isValidStart() || (JarStart.isUsingJavaJar() && !isShaded)) {
            log.fatal(getLanguage().tr("nukkit.start.invalid"));
            return;
        }

        // 检测非法使用shaded包启动
        if (!this.properties.getBoolean("allow-shaded", false) && isShaded) {
            log.fatal(getLanguage().tr("nukkit.start.shaded1"));
            log.fatal(getLanguage().tr("nukkit.start.shaded2"));
            log.fatal(getLanguage().tr("nukkit.start.shaded3"));
            return;
        }

        log.info(this.getLanguage().tr("language.selected", new String[]{getLanguage().getName(), getLanguage().getLang()}));
        log.info(getLanguage().tr("nukkit.server.start", TextFormat.AQUA + this.getVersion() + TextFormat.RESET));

        Object poolSize = this.getConfig("settings.async-workers", (Object) "auto");
        if (!(poolSize instanceof Integer)) {
            try {
                poolSize = Integer.valueOf((String) poolSize);
            } catch (Exception e) {
                poolSize = Math.max(Runtime.getRuntime().availableProcessors(), 4);
            }
        }

        ServerScheduler.WORKERS = (int) poolSize;

        this.networkZlibProvider = this.getConfig("network.zlib-provider", 2);
        Zlib.setProvider(this.networkZlibProvider);

        this.maximumStaleDatagrams = this.getConfig("network.maximum-stale-datagrams", 512);
        this.networkCompressionLevel = this.getConfig("network.compression-level", 7);
        this.networkCompressionAsync = this.getConfig("network.async-compression", true);

        this.autoTickRate = this.getConfig("level-settings.auto-tick-rate", true);
        this.autoTickRateLimit = this.getConfig("level-settings.auto-tick-rate-limit", 20);
        this.alwaysTickPlayers = this.getConfig("level-settings.always-tick-players", false);
        this.baseTickRate = this.getConfig("level-settings.base-tick-rate", 1);
        this.redstoneEnabled = this.getConfig("level-settings.tick-redstone", true);
        this.safeSpawn = this.getConfig().getBoolean("settings.safe-spawn", true);
        this.forceSkinTrusted = this.getConfig().getBoolean("player.force-skin-trusted", false);
        this.checkMovement = this.getConfig().getBoolean("player.check-movement", true);
        this.serverAuthoritativeMovementMode = switch (this.properties.get("server-authoritative-movement", "client-auth")) {
            case "client-auth" -> 0;
            case "server-auth" -> 1;
            case "server-auth-with-rewind" -> 2;
            default -> throw new IllegalArgumentException();
        };
        this.enabledNetworkEncryption = this.properties.getBoolean("network-encryption", true);

        this.maximumSizePerChunk = this.getConfig("chunk-saving.maximum-size-per-chunk", 1048576);
        //unlimited if value == -1
        if (this.maximumSizePerChunk < 0) this.maximumSizePerChunk = Integer.MAX_VALUE;

        this.scheduler = new ServerScheduler();

        if (this.getPropertyBoolean("enable-rcon", false)) {
            try {
                this.rcon = new RCON(this, this.getPropertyString("rcon.password", ""), (!this.getIp().equals("")) ? this.getIp() : "0.0.0.0", this.getPropertyInt("rcon.port", this.getPort()));
            } catch (IllegalArgumentException e) {
                log.error(getLanguage().tr(e.getMessage(), e.getCause().getMessage()));
            }
        }

        this.entityMetadata = new EntityMetadataStore();
        this.playerMetadata = new PlayerMetadataStore();
        this.levelMetadata = new LevelMetadataStore();

        this.operators = new Config(this.dataPath + "ops.txt", Config.ENUM);
        this.whitelist = new Config(this.dataPath + "white-list.txt", Config.ENUM);
        this.banByName = new BanList(this.dataPath + "banned-players.json");
        this.banByName.load();
        this.banByIP = new BanList(this.dataPath + "banned-ips.json");
        this.banByIP.load();

        this.maxPlayers = this.getPropertyInt("max-players", 20);
        this.setAutoSave(this.getPropertyBoolean("auto-save", true));

        if (this.getPropertyBoolean("hardcore", false) && this.getDifficulty() < 3) {
            this.setPropertyInt("difficulty", 3);
        }

        boolean bugReport;
        if (this.getConfig().exists("settings.bug-report")) {
            bugReport = this.getConfig().getBoolean("settings.bug-report");
            this.getProperties().remove("bug-report");
        } else {
            bugReport = this.getPropertyBoolean("bug-report", true); //backwards compat
        }
        if (bugReport) {
            ExceptionHandler.registerExceptionHandler();
        }

        log.info(this.getLanguage().tr("nukkit.server.networkStart", new String[]{this.getIp().equals("") ? "*" : this.getIp(), String.valueOf(this.getPort())}));
        this.serverID = UUID.randomUUID();

        this.network = new Network(this);
        this.network.setName(this.getMotd());
        this.network.setSubName(this.getSubMotd());

        log.info(this.getLanguage().tr("nukkit.server.info", this.getName(), TextFormat.YELLOW + this.getNukkitVersion() + " (" + this.getGitCommit() + ")" + TextFormat.WHITE, TextFormat.AQUA + this.getCodename() + TextFormat.WHITE, this.getApiVersion()));
        log.info(this.getLanguage().tr("nukkit.server.license", this.getName()));

        this.consoleSender = new ConsoleCommandSender();

        // Initialize metrics
        NukkitMetrics.startNow(this);

        this.registerEntities();
        this.registerProfessions();
        this.registerBlockEntities();

        Block.init();
        Enchantment.init();
        RuntimeItems.init();
        Potion.init();
        Item.init();
        EnumBiome.values(); //load class, this also registers biomes
        Effect.init();
        Attribute.init();
        DispenseBehaviorRegister.init();
        GlobalBlockPalette.getOrCreateRuntimeId(0, 0); //Force it to load

        freezableArrayManager = new FreezableArrayManager(
                this.getConfig("memory-compression.enable", true),
                this.getConfig("memory-compression.slots", 32),
                this.getConfig("memory-compression.default-temperature", 32),
                this.getConfig("memory-compression.threshold.freezing-point", 0),
                this.getConfig("memory-compression.threshold.absolute-zero", -256),
                this.getConfig("memory-compression.threshold.boiling-point", 1024),
                this.getConfig("memory-compression.heat.melting", 16),
                this.getConfig("memory-compression.heat.single-operation", 1),
                this.getConfig("memory-compression.heat.batch-operation", 32));

        scoreboardManager = new ScoreboardManager(new JSONScoreboardStorage(this.commandDataPath + "/scoreboard.json"));

        functionManager = new FunctionManager(this.commandDataPath + "/functions");

        tickingAreaManager = new SimpleTickingAreaManager(new JSONTickingAreaStorage(this.dataPath + "worlds/"));

        // Convert legacy data before plugins get the chance to mess with it.
        try {
            nameLookup = Iq80DBFactory.factory.open(new File(dataPath, "players"), new Options()
                    .createIfMissing(true)
                    .compressionType(CompressionType.ZLIB_RAW));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        convertLegacyPlayerData();

        this.craftingManager = new CraftingManager();
        this.resourcePackManager = new ResourcePackManager(
                new ZippedResourcePackLoader(new File(Nukkit.DATA_PATH, "resource_packs")),
                new JarPluginResourcePackLoader(new File(this.pluginPath))
        );

        this.commandMap = new SimpleCommandMap(this);
        this.pluginManager = new PluginManager(this, this.commandMap);
        this.pluginManager.subscribeToPermission(Server.BROADCAST_CHANNEL_ADMINISTRATIVE, this.consoleSender);

        this.pluginManager.registerInterface(JavaPluginLoader.class);
        this.pluginManager.registerInterface(JSPluginLoader.class);

        this.queryRegenerateEvent = new QueryRegenerateEvent(this, 5);

        this.network.registerInterface(new RakNetInterface(this));

        try {
            log.debug("Loading position tracking service");
            this.positionTrackingService = new PositionTrackingService(new File(Nukkit.DATA_PATH, "services/position_tracking_db"));
            //getScheduler().scheduleRepeatingTask(null, positionTrackingService::forceRecheckAllPlayers, 20 * 5);
        } catch (IOException e) {
            log.fatal("Failed to start the Position Tracking DB service!", e);
        }

        this.pluginManager.loadInternalPlugin();
        this.pluginManager.loadPlugins(this.pluginPath);
        Block.initCustomBlock();

        this.enablePlugins(PluginLoadOrder.STARTUP);

        LevelProviderManager.addProvider(this, Anvil.class);

        Generator.addGenerator(Flat.class, "flat", Generator.TYPE_FLAT);
        Generator.addGenerator(Normal.class, "normal", Generator.TYPE_INFINITE);
        if (useTerra) {
            Generator.addGenerator(TerraGeneratorWrapper.class, "terra");
            PNXPlatform.getInstance();
        }
        Generator.addGenerator(Normal.class, "default", Generator.TYPE_INFINITE);
        Generator.addGenerator(Nether.class, "nether", Generator.TYPE_NETHER);
        Generator.addGenerator(TheEnd.class, "the_end", Generator.TYPE_THE_END);
        //todo: add old generator and hell generator

        for (String name : this.getConfig("worlds", new HashMap<String, Object>()).keySet()) {
            if (!this.loadLevel(name)) {
                long seed;
                try {
                    seed = ((Number) this.getConfig("worlds." + name + ".seed", ThreadLocalRandom.current().nextLong())).longValue();
                } catch (Exception e) {
                    try {
                        seed = this.getConfig("worlds." + name + ".seed").toString().hashCode();
                    } catch (Exception e2) {
                        seed = System.currentTimeMillis();
                        e2.addSuppressed(e);
                        log.warn("Failed to load the world seed for \"{}\". Generating a random seed", name, e2);
                    }
                }

                Map<String, Object> options = new HashMap<>();
                String[] opts = (this.getConfig("worlds." + name + ".generator", Generator.getGenerator("default").getSimpleName())).split(":");
                Class<? extends Generator> generator = Generator.getGenerator(opts[0]);
                if (opts.length > 1) {
                    StringBuilder preset = new StringBuilder();
                    for (int i = 1; i < opts.length; i++) {
                        preset.append(opts[i]).append(":");
                    }
                    preset = new StringBuilder(preset.substring(0, preset.length() - 1));

                    options.put("preset", preset.toString());
                }

                this.generateLevel(name, seed, generator, options);
            }
        }

        if (this.getDefaultLevel() == null) {
            String defaultName = this.getPropertyString("level-name", "world");
            if (defaultName == null || defaultName.trim().isEmpty()) {
                log.warn("level-name cannot be null, using default");
                defaultName = "world";
                this.setPropertyString("level-name", defaultName);
            }

            if (!this.loadLevel(defaultName)) {
                long seed;
                String seedString = String.valueOf(this.getProperty("level-seed", System.currentTimeMillis()));
                try {
                    seed = Long.parseLong(seedString);
                } catch (NumberFormatException e) {
                    seed = seedString.hashCode();
                }
                this.generateLevel(defaultName, seed == 0 ? System.currentTimeMillis() : seed);
            }

            this.setDefaultLevel(this.getLevelByName(defaultName));
        }

        this.getTickingAreaManager().loadAllTickingArea();

        this.properties.save(true);

        if (this.getDefaultLevel() == null) {
            log.fatal(this.getLanguage().tr("nukkit.level.defaultError"));
            this.forceShutdown();

            return;
        }

        EnumLevel.initLevels();

        if (this.getConfig("ticks-per.autosave", 6000) > 0) {
            this.autoSaveTicks = this.getConfig("ticks-per.autosave", 6000);
        }

        this.enablePlugins(PluginLoadOrder.POSTWORLD);

        if (this.getConfig("settings.download-spark", false)) {
            SparkInstaller.initSpark(this);
        }

        if (/*Nukkit.DEBUG < 2 && */!Boolean.parseBoolean(System.getProperty("disableWatchdog", "false"))) {
            this.watchdog = new Watchdog(this, 60000);
            this.watchdog.start();
        }
        System.runFinalization();
        this.start();
    }

    // region lifecycle & ticking - 生命周期与游戏刻

    /**
     * 重载服务器
     * <p>
     * Reload Server
     */
    public void reload() {
        log.info("Reloading...");

        log.info("Saving levels...");

        for (Level level : this.levelArray) {
            level.save();
        }

        this.scoreboardManager.save();

        this.pluginManager.disablePlugins();
        this.pluginManager.clearPlugins();
        this.commandMap.clearCommands();

        log.info("Reloading properties...");
        this.properties.reload();
        this.maxPlayers = this.getPropertyInt("max-players", 20);

        if (this.getPropertyBoolean("hardcore", false) && this.getDifficulty() < 3) {
            this.setPropertyInt("difficulty", difficulty = 3);
        }

        this.banByIP.load();
        this.banByName.load();
        this.reloadWhitelist();
        this.operators.reload();

        for (BanEntry entry : this.getIPBans().getEntires().values()) {
            try {
                this.getNetwork().blockAddress(InetAddress.getByName(entry.getName()), -1);
            } catch (UnknownHostException e) {
                // ignore
            }
        }

        this.pluginManager.registerInterface(JavaPluginLoader.class);
        JSIInitiator.reset();
        JSFeatures.clearFeatures();
        JSFeatures.initInternalFeatures();
        this.scoreboardManager.read();
        this.pluginManager.registerInterface(JSPluginLoader.class);
        this.pluginManager.loadPlugins(this.pluginPath);
        this.functionManager.reload();
        this.enablePlugins(PluginLoadOrder.STARTUP);
        this.enablePlugins(PluginLoadOrder.POSTWORLD);
        ServerStartedEvent serverStartedEvent = new ServerStartedEvent();
        getPluginManager().callEvent(serverStartedEvent);
    }


    /**
     * 关闭服务器
     * <p>
     * Shut down the server
     */
    public void shutdown() {
        isRunning.compareAndSet(true, false);
    }

    /**
     * 强制关闭服务器
     * <p>
     * Force Shut down the server
     */
    public void forceShutdown() {
        if (this.hasStopped) {
            return;
        }

        try {
            isRunning.compareAndSet(true, false);

            this.hasStopped = true;

            ServerStopEvent serverStopEvent = new ServerStopEvent();
            getPluginManager().callEvent(serverStopEvent);

            if (this.rcon != null) {
                this.rcon.close();
            }

            for (Player player : new ArrayList<>(this.players.values())) {
                player.close(player.getLeaveMessage(), this.getConfig("settings.shutdown-message", "Server closed"));
            }

            log.debug("Disabling all plugins");
            this.pluginManager.disablePlugins();

            log.debug("Removing event handlers");
            HandlerList.unregisterAll();

            log.debug("Saving scoreboards data");
            this.scoreboardManager.save();

            BlockStateRegistry.close();

            log.debug("Stopping all tasks");
            this.scheduler.cancelAllTasks();
            this.scheduler.mainThreadHeartbeat(Integer.MAX_VALUE);

            log.debug("Unloading all levels");
            for (Level level : this.levelArray) {
                this.unloadLevel(level, true);
            }

            if (positionTrackingService != null) {
                log.debug("Closing position tracking service");
                positionTrackingService.close();
            }

            log.debug("Closing console");
            this.consoleThread.interrupt();

            log.debug("Stopping network interfaces");
            for (SourceInterface interfaz : this.network.getInterfaces()) {
                interfaz.shutdown();
                this.network.unregisterInterface(interfaz);
            }

            if (nameLookup != null) {
                nameLookup.close();
            }
            //close watchdog and metrics
            if (this.watchdog != null) {
                this.watchdog.running = false;
            }
            NukkitMetrics.closeNow(this);
            //close computeThreadPool
            this.computeThreadPool.shutdownNow();
            //todo other things
        } catch (Exception e) {
            log.fatal("Exception happened while shutting down, exiting the process", e);
            System.exit(1);
        }
    }

    public void start() {
        if (this.getPropertyBoolean("enable-query", true)) {
            this.queryHandler = new QueryHandler();
        }

        for (BanEntry entry : this.getIPBans().getEntires().values()) {
            try {
                this.network.blockAddress(InetAddress.getByName(entry.getName()), -1);
            } catch (UnknownHostException e) {
                // ignore
            }
        }

        //todo send usage setting
        this.tickCounter = 0;

        log.info(this.getLanguage().tr("nukkit.server.defaultGameMode", getGamemodeString(this.getGamemode())));

        log.info(this.getLanguage().tr("nukkit.server.startFinished", String.valueOf((double) (System.currentTimeMillis() - Nukkit.START_TIME) / 1000)));

        ServerStartedEvent serverStartedEvent = new ServerStartedEvent();
        getPluginManager().callEvent(serverStartedEvent);
        this.tickProcessor();
        this.forceShutdown();
    }

    private int lastLevelGC;

    public void tickProcessor() {
        getScheduler().scheduleDelayedTask(new Task() {
            @Override
            public void onRun(int currentTick) {
                System.runFinalization();
                System.gc();
            }
        }, 60);

        this.nextTick = System.currentTimeMillis();
        try {
            while (this.isRunning.get()) {
                try {
                    this.tick();

                    long next = this.nextTick;
                    long current = System.currentTimeMillis();

                    if (next - 0.1 > current) {
                        long allocated = next - current - 1;

                        { // Instead of wasting time, do something potentially useful
                            int offset = 0;
                            for (int i = 0; i < levelArray.length; i++) {
                                offset = (i + lastLevelGC) % levelArray.length;
                                Level level = levelArray[offset];
                                level.doGarbageCollection(allocated - 1);
                                allocated = next - System.currentTimeMillis();
                                if (allocated <= 0) {
                                    break;
                                }
                            }
                            lastLevelGC = offset + 1;
                        }

                        if (allocated > 0) {
                            //noinspection BusyWait
                            Thread.sleep(allocated, 900000);
                        }
                    }
                } catch (RuntimeException e) {
                    log.error("A RuntimeException happened while ticking the server", e);
                }
            }
        } catch (Throwable e) {
            log.fatal("Exception happened while ticking server\n{}", Utils.getAllThreadDumps(), e);
        }
    }

    private void checkTickUpdates(int currentTick, long tickTime) {
        if (this.alwaysTickPlayers) {
            for (Player p : new ArrayList<>(this.players.values())) {
                p.onUpdate(currentTick);
            }
        }

        //Do level ticks
        for (Level level : this.levelArray) {
            if (level.getTickRate() > this.baseTickRate && --level.tickRateCounter > 0) {
                continue;
            }

            try {
                long levelTime = System.currentTimeMillis();
                level.doTick(currentTick);
                int tickMs = (int) (System.currentTimeMillis() - levelTime);
                level.tickRateTime = tickMs;
                if ((currentTick & 511) == 0) { // % 511
                    level.tickRateOptDelay = level.recalcTickOptDelay();
                }

                if (this.autoTickRate) {
                    if (tickMs < 50 && level.getTickRate() > this.baseTickRate) {
                        int r;
                        level.setTickRate(r = level.getTickRate() - 1);
                        if (r > this.baseTickRate) {
                            level.tickRateCounter = level.getTickRate();
                        }
                        log.debug("Raising level \"{}\" tick rate to {} ticks", level.getName(), level.getTickRate());
                    } else if (tickMs >= 50) {
                        if (level.getTickRate() == this.baseTickRate) {
                            level.setTickRate(Math.max(this.baseTickRate + 1, Math.min(this.autoTickRateLimit, tickMs / 50)));
                            log.debug("Level \"{}\" took {}ms, setting tick rate to {} ticks", level.getName(), NukkitMath.round(tickMs, 2), level.getTickRate());
                        } else if ((tickMs / level.getTickRate()) >= 50 && level.getTickRate() < this.autoTickRateLimit) {
                            level.setTickRate(level.getTickRate() + 1);
                            log.debug("Level \"{}\" took {}ms, setting tick rate to {} ticks", level.getName(), NukkitMath.round(tickMs, 2), level.getTickRate());
                        }
                        level.tickRateCounter = level.getTickRate();
                    }
                }
            } catch (Exception e) {
                log.error(this.getLanguage().tr("nukkit.level.tickError",
                        level.getFolderName(), Utils.getExceptionMessage(e)), e);
            }
        }
    }

    public void doAutoSave() {
        if (this.getAutoSave()) {
            for (Player player : new ArrayList<>(this.players.values())) {
                if (player.isOnline()) {
                    player.save(true);
                } else if (!player.isConnected()) {
                    this.removePlayer(player);
                }
            }

            for (Level level : this.levelArray) {
                level.save();
            }
            this.getScoreboardManager().save();
        }
    }

    private boolean tick() {
        long tickTime = System.currentTimeMillis();

        // TODO
        long time = tickTime - this.nextTick;
        if (time < -25) {
            try {
                Thread.sleep(Math.max(5, -time - 25));
            } catch (InterruptedException e) {
                log.debug("The thread {} got interrupted", Thread.currentThread().getName(), e);
            }
        }

        long tickTimeNano = System.nanoTime();
        if ((tickTime - this.nextTick) < -25) {
            return false;
        }

        ++this.tickCounter;
        this.network.processInterfaces();

        if (this.rcon != null) {
            this.rcon.check();
        }

        this.scheduler.mainThreadHeartbeat(this.tickCounter);

        this.checkTickUpdates(this.tickCounter, tickTime);

        for (Player player : new ArrayList<>(this.players.values())) {
            player.checkNetwork();
        }

        if ((this.tickCounter & 0b1111) == 0) {
            this.titleTick();
            this.network.resetStatistics();
            this.maxTick = 20;
            this.maxUse = 0;

            if ((this.tickCounter & 0b111111111) == 0) {
                try {
                    this.getPluginManager().callEvent(this.queryRegenerateEvent = new QueryRegenerateEvent(this, 5));
                    if (this.queryHandler != null) {
                        this.queryHandler.regenerateInfo();
                    }
                } catch (Exception e) {
                    log.error(e);
                }
            }

            this.getNetwork().updateName();
        }

        if (this.autoSave && ++this.autoSaveTicker >= this.autoSaveTicks) {
            this.autoSaveTicker = 0;
            this.doAutoSave();
        }

        if (this.sendUsageTicker > 0 && --this.sendUsageTicker == 0) {
            this.sendUsageTicker = 6000;
            //todo sendUsage
        }

        if (this.tickCounter % 100 == 0) {
            CompletableFuture.allOf(Arrays.stream(this.levelArray).parallel()
                    .flatMap(l -> l.asyncChunkGarbageCollection().stream())
                    .toArray(CompletableFuture[]::new));
        }

        // 处理可冻结数组
        int freezableArrayCompressTime = (int) (50 - (System.currentTimeMillis() - tickTime));
        if (freezableArrayCompressTime > 4) {
            freezableArrayManager.setMaxCompressionTime(freezableArrayCompressTime).tick();
        }

        //long now = System.currentTimeMillis();
        long nowNano = System.nanoTime();
        //float tick = Math.min(20, 1000 / Math.max(1, now - tickTime));
        //float use = Math.min(1, (now - tickTime) / 50);

        float tick = (float) Math.min(20, 1000000000 / Math.max(1000000, ((double) nowNano - tickTimeNano)));
        float use = (float) Math.min(1, ((double) (nowNano - tickTimeNano)) / 50000000);

        if (this.maxTick > tick) {
            this.maxTick = tick;
        }

        if (this.maxUse < use) {
            this.maxUse = use;
        }

        System.arraycopy(this.tickAverage, 1, this.tickAverage, 0, this.tickAverage.length - 1);
        this.tickAverage[this.tickAverage.length - 1] = tick;

        System.arraycopy(this.useAverage, 1, this.useAverage, 0, this.useAverage.length - 1);
        this.useAverage[this.useAverage.length - 1] = use;

        if ((this.nextTick - tickTime) < -1000) {
            this.nextTick = tickTime;
        } else {
            this.nextTick += 50;
        }

        return true;
    }

    public long getNextTick() {
        return nextTick;
    }

    /**
     * @return 返回服务器经历过的tick数<br>Returns the number of ticks recorded by the server
     */
    public int getTick() {
        return tickCounter;
    }

    public float getTicksPerSecond() {
        return ((float) Math.round(this.maxTick * 100)) / 100;
    }

    public float getTicksPerSecondAverage() {
        float sum = 0;
        int count = this.tickAverage.length;
        for (float aTickAverage : this.tickAverage) {
            sum += aTickAverage;
        }
        return (float) NukkitMath.round(sum / count, 2);
    }

    public float getTickUsage() {
        return (float) NukkitMath.round(this.maxUse * 100, 2);
    }

    public float getTickUsageAverage() {
        float sum = 0;
        int count = this.useAverage.length;
        for (float aUseAverage : this.useAverage) {
            sum += aUseAverage;
        }
        return ((float) Math.round(sum / count * 100)) / 100;
    }

    // TODO: Fix title tick
    public void titleTick() {
        if (!Nukkit.ANSI || !Nukkit.TITLE) {
            return;
        }

        Runtime runtime = Runtime.getRuntime();
        double used = NukkitMath.round((double) (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024, 2);
        double max = NukkitMath.round(((double) runtime.maxMemory()) / 1024 / 1024, 2);
        String usage = Math.round(used / max * 100) + "%";
        String title = (char) 0x1b + "]0;" + this.getName() + " "
                + this.getNukkitVersion()
                + " | " + this.getGitCommit()
                + " | Online " + this.players.size() + "/" + this.getMaxPlayers()
                + " | Memory " + usage;
        if (!Nukkit.shortTitle) {
            title += " | U " + NukkitMath.round((this.network.getUpload() / 1024 * 1000), 2)
                    + " D " + NukkitMath.round((this.network.getDownload() / 1024 * 1000), 2) + " kB/s";
        }
        title += " | TPS " + this.getTicksPerSecond()
                + " | Load " + this.getTickUsage() + "%" + (char) 0x07;

        System.out.print(title);
    }

    public boolean isRunning() {
        return isRunning.get();
    }

    /**
     * 将服务器设置为繁忙状态，这可以阻止相关代码认为服务器处于无响应状态。
     * 请牢记，必须在设置之后清除。
     *
     * @param busyTime 单位为毫秒
     * @return id
     */
    public int addBusying(long busyTime) {
        this.busyingTime.add(busyTime);
        return this.busyingTime.size() - 1;
    }

    public void removeBusying(int index) {
        this.busyingTime.removeLong(index);
    }

    public long getBusyingTime() {
        if (this.busyingTime.isEmpty()) {
            return -1;
        }
        return this.busyingTime.getLong(this.busyingTime.size() - 1);
    }

    public TickingAreaManager getTickingAreaManager() {
        return tickingAreaManager;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public long getLaunchTime() {
        return launchTime;
    }

    // endregion

    // region server singleton - Server 单例

    public static Server getInstance() {
        return instance;
    }

    // endregion

    // region chat & commands - 聊天与命令

    /**
     * 广播一条消息给所有玩家<p>Broadcast a message to all players
     *
     * @param message 消息
     * @return int 玩家数量<br>Number of players
     */
    public int broadcastMessage(String message) {
        return this.broadcast(message, BROADCAST_CHANNEL_USERS);
    }


    /**
     * @see #broadcastMessage(String)
     */
    public int broadcastMessage(TextContainer message) {
        return this.broadcast(message, BROADCAST_CHANNEL_USERS);
    }

    /**
     * 广播一条消息给指定的{@link CommandSender recipients}<p>Broadcast a message to the specified {@link CommandSender recipients}
     *
     * @param message 消息
     * @return int {@link CommandSender recipients}数量<br>Number of {@link CommandSender recipients}
     */
    public int broadcastMessage(String message, CommandSender[] recipients) {
        for (CommandSender recipient : recipients) {
            recipient.sendMessage(message);
        }

        return recipients.length;
    }


    /**
     * @see #broadcastMessage(String, CommandSender[])
     */
    public int broadcastMessage(String message, Collection<? extends CommandSender> recipients) {
        for (CommandSender recipient : recipients) {
            recipient.sendMessage(message);
        }

        return recipients.size();
    }

    /**
     * @see #broadcastMessage(String, CommandSender[])
     */
    public int broadcastMessage(TextContainer message, Collection<? extends CommandSender> recipients) {
        for (CommandSender recipient : recipients) {
            recipient.sendMessage(message);
        }

        return recipients.size();
    }


    /**
     * 从指定的许可名获取发送者们，广播一条消息给他们.可以指定多个许可名，以<b> ; </b>分割.<br>
     * 一个permission在{@link PluginManager#permSubs}对应一个{@link CommandSender 发送者}Set.<p>
     * Get the sender to broadcast a message from the specified permission name, multiple permissions can be specified, split by <b> ; </b><br>
     * The permission corresponds to a {@link CommandSender Sender} set in {@link PluginManager#permSubs}.
     *
     * @param message     消息内容<br>Message content
     * @param permissions 许可名，需要先通过{@link PluginManager#subscribeToPermission subscribeToPermission}注册<br>Permissions name, need to register first through {@link PluginManager#subscribeToPermission subscribeToPermission}
     * @return int 接受到消息的{@link CommandSender 发送者}数量<br>Number of {@link CommandSender senders} who received the message
     */
    public int broadcast(String message, String permissions) {
        Set<CommandSender> recipients = new HashSet<>();

        for (String permission : permissions.split(";")) {
            for (Permissible permissible : this.pluginManager.getPermissionSubscriptions(permission)) {
                if (permissible instanceof CommandSender && permissible.hasPermission(permission)) {
                    recipients.add((CommandSender) permissible);
                }
            }
        }

        for (CommandSender recipient : recipients) {
            recipient.sendMessage(message);
        }

        return recipients.size();
    }


    /**
     * @see #broadcast(String, String)
     */
    public int broadcast(TextContainer message, String permissions) {
        Set<CommandSender> recipients = new HashSet<>();

        for (String permission : permissions.split(";")) {
            for (Permissible permissible : this.pluginManager.getPermissionSubscriptions(permission)) {
                if (permissible instanceof CommandSender && permissible.hasPermission(permission)) {
                    recipients.add((CommandSender) permissible);
                }
            }
        }

        for (CommandSender recipient : recipients) {
            recipient.sendMessage(message);
        }

        return recipients.size();
    }


    @Deprecated
    @DeprecationDetails(since = "1.19.60-r1", reason = "use Server#executeCommand")
    public boolean dispatchCommand(CommandSender sender, String commandLine) throws ServerException {
        return this.executeCommand(sender, commandLine) > 0;
    }

    /**
     * 以sender身份执行一行命令
     * <p>
     * Execute one line of command as sender
     *
     * @param sender      命令执行者
     * @param commandLine 一行命令
     * @return 返回0代表执行失败, 返回大于等于1代表执行成功<br>Returns 0 for failed execution, greater than or equal to 1 for successful execution
     * @throws ServerException 服务器异常
     */
    public int executeCommand(CommandSender sender, String commandLine) throws ServerException {
        // First we need to check if this command is on the main thread or not, if not, warn the user
        if (!this.isPrimaryThread()) {
            log.warn("Command Dispatched Async: {}\nPlease notify author of plugin causing this execution to fix this bug!", commandLine,
                    new ConcurrentModificationException("Command Dispatched Async: " + commandLine));

            this.scheduler.scheduleTask(null, () -> executeCommand(sender, commandLine));
            return 1;
        }
        if (sender == null) {
            throw new ServerException("CommandSender is not valid");
        }
        //pre
        var cmd = commandLine.stripLeading();
        cmd = cmd.charAt(0) == '/' ? cmd.substring(1) : cmd;

        return this.commandMap.executeCommand(sender, cmd);
    }

    /**
     * 以该控制台身份静音执行这些命令，无视权限
     * <p>
     * Execute these commands silently as the console, ignoring permissions.
     *
     * @param commands the commands
     * @throws ServerException 服务器异常
     */
    public void silentExecuteCommand(String... commands) {
        this.silentExecuteCommand(null, commands);
    }

    /**
     * 以该玩家身份静音执行这些命令无视权限
     * <p>
     * Execute these commands silently as this player, ignoring permissions.
     *
     * @param sender   命令执行者<br>command sender
     * @param commands the commands
     * @throws ServerException 服务器异常
     */
    public void silentExecuteCommand(@Nullable Player sender, String... commands) {
        final var revert = new ArrayList<Level>();
        final var server = Server.getInstance();
        for (var level : server.getLevels().values()) {
            if (level.getGameRules().getBoolean(GameRule.SEND_COMMAND_FEEDBACK)) {
                level.getGameRules().setGameRule(GameRule.SEND_COMMAND_FEEDBACK, false);
                revert.add(level);
            }
        }
        if (sender == null) {
            for (var cmd : commands) {
                server.executeCommand(server.getConsoleSender(), cmd);
            }
        } else {
            for (var cmd : commands) {
                server.executeCommand(server.getConsoleSender(), "execute as " + "\"" + sender.getName() + "\" run " + cmd);
            }
        }

        for (var level : revert) {
            level.getGameRules().setGameRule(GameRule.SEND_COMMAND_FEEDBACK, true);
        }
    }

    /**
     * 得到控制台发送者
     * <p>
     * Get the console sender
     *
     * @return {@link ConsoleCommandSender}
     */
    //todo: use ticker to check console
    public ConsoleCommandSender getConsoleSender() {
        return consoleSender;
    }

    public SimpleCommandMap getCommandMap() {
        return commandMap;
    }

    public PluginIdentifiableCommand getPluginCommand(String name) {
        Command command = this.commandMap.getCommand(name);
        if (command instanceof PluginIdentifiableCommand) {
            return (PluginIdentifiableCommand) command;
        } else {
            return null;
        }
    }

    public Map<String, List<String>> getCommandAliases() {
        Object section = this.getConfig("aliases");
        Map<String, List<String>> result = new LinkedHashMap<>();
        if (section instanceof Map) {
            for (Map.Entry entry : (Set<Map.Entry>) ((Map) section).entrySet()) {
                List<String> commands = new ArrayList<>();
                String key = (String) entry.getKey();
                Object value = entry.getValue();
                if (value instanceof List) {
                    commands.addAll((List<String>) value);
                } else {
                    commands.add((String) value);
                }

                result.put(key, commands);
            }
        }

        return result;

    }

    public IScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    public FunctionManager getFunctionManager() {
        return functionManager;
    }

    // endregion

    // region networking - 网络相关

    /**
     * @see #broadcastPacket(Player[], DataPacket)
     */
    public static void broadcastPacket(Collection<Player> players, DataPacket packet) {
        packet.tryEncode();

        for (Player player : players) {
            player.dataPacket(packet);
        }
    }

    /**
     * 广播一个数据包给指定的玩家们.<p>Broadcast a packet to the specified players.
     *
     * @param players 接受数据包的所有玩家<br>All players receiving the data package
     * @param packet  数据包
     */
    public static void broadcastPacket(Player[] players, DataPacket packet) {
        packet.tryEncode();

        for (Player player : players) {
            player.dataPacket(packet);
        }
    }

    @DeprecationDetails(since = "1.4.0.0-PN", by = "Cloudburst Nukkit",
            reason = "Packet management was refactored, batching is done automatically near the RakNet layer")
    @Deprecated
    public void batchPackets(Player[] players, DataPacket[] packets) {
        this.batchPackets(players, packets, false);
    }

    @DeprecationDetails(since = "1.4.0.0-PN", by = "Cloudburst Nukkit",
            reason = "Packet management was refactored, batching is done automatically near the RakNet layer")
    @Deprecated
    public void batchPackets(Player[] players, DataPacket[] packets, boolean forceSync) {
        if (players == null || packets == null || players.length == 0 || packets.length == 0) {
            return;
        }

        BatchPacketsEvent ev = new BatchPacketsEvent(players, packets, forceSync);
        getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return;
        }

        byte[][] payload = new byte[packets.length * 2][];
        for (int i = 0; i < packets.length; i++) {
            DataPacket p = packets[i];
            int idx = i * 2;
            p.tryEncode();
            byte[] buf = p.getBuffer();
            payload[idx] = Binary.writeUnsignedVarInt(buf.length);
            payload[idx + 1] = buf;
            packets[i] = null;
        }

        List<InetSocketAddress> targets = new ArrayList<>();
        for (Player p : players) {
            if (p.isConnected()) {
                targets.add(p.getRawSocketAddress());
            }
        }

        if (!forceSync && this.networkCompressionAsync) {
            this.getScheduler().scheduleAsyncTask(new CompressBatchedTask(payload, targets, this.networkCompressionLevel));
        } else {
            try {
                byte[] data = Binary.appendBytes(payload);
                if (Server.getInstance().isEnableSnappy()) {
                    this.broadcastPacketsCallback(SnappyCompression.compress(data), targets);
                } else {
                    this.broadcastPacketsCallback(Network.deflateRaw(data, this.networkCompressionLevel), targets);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void broadcastPacketsCallback(byte[] data, List<InetSocketAddress> targets) {
        BatchPacket pk = new BatchPacket();
        pk.payload = data;

        for (InetSocketAddress i : targets) {
            if (this.players.containsKey(i)) {
                this.players.get(i).dataPacket(pk);
            }
        }
    }

    public void handlePacket(InetSocketAddress address, ByteBuf payload) {
        try {
            if (!payload.isReadable(3)) {
                return;
            }
            byte[] prefix = new byte[2];
            payload.readBytes(prefix);

            if (!Arrays.equals(prefix, new byte[]{(byte) 0xfe, (byte) 0xfd})) {
                return;
            }
            if (this.queryHandler != null) {
                this.queryHandler.handle(address, payload);
            }
        } catch (Exception e) {
            log.error("Error whilst handling packet", e);

            this.network.blockAddress(address.getAddress(), -1);
        }
    }

    public QueryRegenerateEvent getQueryInformation() {
        return this.queryRegenerateEvent;
    }

    public Network getNetwork() {
        return network;
    }

    // endregion

    // region plugins - 插件相关

    /**
     * 以指定插件加载顺序启用插件<p>
     * Enable plugins in the specified plugin loading order
     *
     * @param type 插件加载顺序<br>Plugin loading order
     */
    public void enablePlugins(PluginLoadOrder type) {
        for (Plugin plugin : new ArrayList<>(this.pluginManager.getPlugins().values())) {
            if (!plugin.isEnabled() && type == plugin.getDescription().getOrder()) {
                this.enablePlugin(plugin);
            }
        }

        if (type == PluginLoadOrder.POSTWORLD) {
            this.commandMap.registerServerAliases();
            DefaultPermissions.registerCorePermissions();
        }
    }

    /**
     * 启用一个指定插件<p>
     * Enable a specified plugin
     *
     * @param plugin 插件实例<br>Plugin instance
     */
    public void enablePlugin(Plugin plugin) {
        this.pluginManager.enablePlugin(plugin);
    }

    /**
     * 禁用全部插件<p>Disable all plugins
     */
    public void disablePlugins() {
        this.pluginManager.disablePlugins();
    }

    public PluginManager getPluginManager() {
        return this.pluginManager;
    }

    public ServiceManager getServiceManager() {
        return serviceManager;
    }

    // endregion

    // region Players - 玩家相关

    public void onPlayerCompleteLoginSequence(Player player) {
        this.sendFullPlayerListData(player);
    }

    public void onPlayerLogin(Player player) {
        if (this.sendUsageTicker > 0) {
            this.uniquePlayers.add(player.getUniqueId());
        }
    }

    public void addPlayer(InetSocketAddress socketAddress, Player player) {
        this.players.put(socketAddress, player);
    }

    public void addOnlinePlayer(Player player) {
        this.playerList.put(player.getUniqueId(), player);
        this.updatePlayerListData(player.getUniqueId(), player.getId(), player.getDisplayName(), player.getSkin(), player.getLoginChainData().getXUID());
    }

    public void removeOnlinePlayer(Player player) {
        if (this.playerList.containsKey(player.getUniqueId())) {
            this.playerList.remove(player.getUniqueId());

            PlayerListPacket pk = new PlayerListPacket();
            pk.type = PlayerListPacket.TYPE_REMOVE;
            pk.entries = new PlayerListPacket.Entry[]{new PlayerListPacket.Entry(player.getUniqueId())};

            Server.broadcastPacket(this.playerList.values(), pk);
        }
    }

    /**
     * @see #updatePlayerListData(UUID, long, String, Skin, String, Player[])
     */
    public void updatePlayerListData(UUID uuid, long entityId, String name, Skin skin) {
        this.updatePlayerListData(uuid, entityId, name, skin, "", this.playerList.values());
    }

    /**
     * @see #updatePlayerListData(UUID, long, String, Skin, String, Player[])
     */
    public void updatePlayerListData(UUID uuid, long entityId, String name, Skin skin, String xboxUserId) {
        this.updatePlayerListData(uuid, entityId, name, skin, xboxUserId, this.playerList.values());
    }

    /**
     * @see #updatePlayerListData(UUID, long, String, Skin, String, Player[])
     */
    public void updatePlayerListData(UUID uuid, long entityId, String name, Skin skin, Player[] players) {
        this.updatePlayerListData(uuid, entityId, name, skin, "", players);
    }


    /**
     * 更新指定玩家们(players)的{@link PlayerListPacket}数据包(即玩家列表数据)
     * <p>
     * Update {@link PlayerListPacket} data packets (i.e. player list data) for specified players
     *
     * @param uuid       uuid
     * @param entityId   实体id
     * @param name       名字
     * @param skin       皮肤
     * @param xboxUserId xbox用户id
     * @param players    指定接受数据包的玩家
     */
    public void updatePlayerListData(UUID uuid, long entityId, String name, Skin skin, String xboxUserId, Player[] players) {
        PlayerListPacket pk = new PlayerListPacket();
        pk.type = PlayerListPacket.TYPE_ADD;
        pk.entries = new PlayerListPacket.Entry[]{new PlayerListPacket.Entry(uuid, entityId, name, skin, xboxUserId)};
        Server.broadcastPacket(players, pk);
    }

    /**
     * @see #updatePlayerListData(UUID, long, String, Skin, String, Player[])
     */
    public void updatePlayerListData(UUID uuid, long entityId, String name, Skin skin, String xboxUserId, Collection<Player> players) {
        this.updatePlayerListData(uuid, entityId, name, skin, xboxUserId, players.toArray(Player.EMPTY_ARRAY));
    }

    public void removePlayerListData(UUID uuid) {
        this.removePlayerListData(uuid, this.playerList.values());
    }

    /**
     * 移除玩家数组中所有玩家的玩家列表数据.<p>
     * Remove player list data for all players in the array.
     *
     * @param players 玩家数组
     */
    public void removePlayerListData(UUID uuid, Player[] players) {
        PlayerListPacket pk = new PlayerListPacket();
        pk.type = PlayerListPacket.TYPE_REMOVE;
        pk.entries = new PlayerListPacket.Entry[]{new PlayerListPacket.Entry(uuid)};
        Server.broadcastPacket(players, pk);
    }

    /**
     * 移除这个玩家的玩家列表数据.<p>
     * Remove this player's player list data.
     *
     * @param player 玩家
     */
    @Since("1.4.0.0-PN")
    public void removePlayerListData(UUID uuid, Player player) {
        PlayerListPacket pk = new PlayerListPacket();
        pk.type = PlayerListPacket.TYPE_REMOVE;
        pk.entries = new PlayerListPacket.Entry[]{new PlayerListPacket.Entry(uuid)};
        player.dataPacket(pk);
    }

    public void removePlayerListData(UUID uuid, Collection<Player> players) {
        this.removePlayerListData(uuid, players.toArray(Player.EMPTY_ARRAY));
    }

    /**
     * 发送玩家列表数据包给一个玩家.<p>
     * Send a player list packet to a player.
     *
     * @param player 玩家
     */
    public void sendFullPlayerListData(Player player) {
        PlayerListPacket pk = new PlayerListPacket();
        pk.type = PlayerListPacket.TYPE_ADD;
        pk.entries = this.playerList.values().stream()
                .map(p -> new PlayerListPacket.Entry(
                        p.getUniqueId(),
                        p.getId(),
                        p.getDisplayName(),
                        p.getSkin(),
                        p.getLoginChainData().getXUID()))
                .toArray(PlayerListPacket.Entry[]::new);

        player.dataPacket(pk);
    }

    /**
     * 从指定的UUID得到玩家实例.
     * <p>
     * Get the player instance from the specified UUID.
     *
     * @param uuid uuid
     * @return 玩家实例，可为空<br>Player example, can be empty
     */
    public Optional<Player> getPlayer(UUID uuid) {
        Preconditions.checkNotNull(uuid, "uuid");
        return Optional.ofNullable(playerList.get(uuid));
    }

    /**
     * 从数据库中查找指定玩家名对应的UUID.
     * <p>
     * Find the UUID corresponding to the specified player name from the database.
     *
     * @param name 玩家名<br>player name
     * @return 玩家的UUID，可为空.<br>The player's UUID, which can be empty.
     */
    public Optional<UUID> lookupName(String name) {
        byte[] nameBytes = name.toLowerCase().getBytes(StandardCharsets.UTF_8);
        byte[] uuidBytes = nameLookup.get(nameBytes);
        if (uuidBytes == null) {
            return Optional.empty();
        }

        if (uuidBytes.length != 16) {
            log.warn("Invalid uuid in name lookup database detected! Removing");
            nameLookup.delete(nameBytes);
            return Optional.empty();
        }

        ByteBuffer buffer = ByteBuffer.wrap(uuidBytes);
        return Optional.of(new UUID(buffer.getLong(), buffer.getLong()));
    }


    /**
     * 更新数据库中指定玩家名的UUID，若不存在则添加.
     * <p>
     * Update the UUID of the specified player name in the database, or add it if it does not exist.
     *
     * @param uuid uuid
     * @param name 名字
     */
    void updateName(UUID uuid, String name) {
        byte[] nameBytes = name.toLowerCase().getBytes(StandardCharsets.UTF_8);

        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.putLong(uuid.getMostSignificantBits());
        buffer.putLong(uuid.getLeastSignificantBits());

        nameLookup.put(nameBytes, buffer.array());
    }

    public IPlayer getOfflinePlayer(final String name) {
        IPlayer result = this.getPlayerExact(name.toLowerCase());
        if (result != null) {
            return result;
        }

        return lookupName(name).map(uuid -> new OfflinePlayer(this, uuid))
                .orElse(new OfflinePlayer(this, name));
    }

    /**
     * 从指定的UUID得到一个玩家实例,可以是在线玩家也可以是离线玩家.
     * <p>
     * Get a player instance from the specified UUID, either online or offline.
     *
     * @param uuid uuid
     * @return 玩家<br>player
     */
    public IPlayer getOfflinePlayer(UUID uuid) {
        Preconditions.checkNotNull(uuid, "uuid");
        Optional<Player> onlinePlayer = getPlayer(uuid);
        if (onlinePlayer.isPresent()) {
            return onlinePlayer.get();
        }

        return new OfflinePlayer(this, uuid);
    }

    /**
     * create为false
     * <p>
     * create is false
     *
     * @see #getOfflinePlayerData(UUID, boolean)
     */
    public CompoundTag getOfflinePlayerData(UUID uuid) {
        return getOfflinePlayerData(uuid, false);
    }

    /**
     * 获得UUID指定的玩家的NBT数据
     *
     * @param uuid   要获取数据的玩家UUID<br>UUID of the player to get data from
     * @param create 如果玩家数据不存在是否创建<br>If player data does not exist whether to create.
     * @return {@link CompoundTag}
     */
    public CompoundTag getOfflinePlayerData(UUID uuid, boolean create) {
        return getOfflinePlayerDataInternal(uuid.toString(), true, create);
    }

    @Deprecated
    public CompoundTag getOfflinePlayerData(String name) {
        return getOfflinePlayerData(name, false);
    }

    @Deprecated
    public CompoundTag getOfflinePlayerData(String name, boolean create) {
        Optional<UUID> uuid = lookupName(name);
        return getOfflinePlayerDataInternal(uuid.map(UUID::toString).orElse(name), true, create);
    }

    private CompoundTag getOfflinePlayerDataInternal(String name, boolean runEvent, boolean create) {
        Preconditions.checkNotNull(name, "name");

        PlayerDataSerializeEvent event = new PlayerDataSerializeEvent(name, playerDataSerializer);
        if (runEvent) {
            pluginManager.callEvent(event);
        }

        Optional<InputStream> dataStream = Optional.empty();
        try {
            dataStream = event.getSerializer().read(name, event.getUuid().orElse(null));
            if (dataStream.isPresent()) {
                return NBTIO.readCompressed(dataStream.get());
            }
        } catch (IOException e) {
            log.warn(this.getLanguage().tr("nukkit.data.playerCorrupted", name), e);
        } finally {
            if (dataStream.isPresent()) {
                try {
                    dataStream.get().close();
                } catch (IOException e) {
                    log.catching(e);
                }
            }
        }
        CompoundTag nbt = null;
        if (create) {
            if (this.shouldSavePlayerData()) {
                log.info(this.getLanguage().tr("nukkit.data.playerNotFound", name));
            }
            Position spawn = this.getDefaultLevel().getSafeSpawn();
            nbt = new CompoundTag()
                    .putLong("firstPlayed", System.currentTimeMillis() / 1000)
                    .putLong("lastPlayed", System.currentTimeMillis() / 1000)
                    .putList(new ListTag<DoubleTag>("Pos")
                            .add(new DoubleTag("0", spawn.x))
                            .add(new DoubleTag("1", spawn.y))
                            .add(new DoubleTag("2", spawn.z)))
                    .putString("Level", this.getDefaultLevel().getName())
                    .putList(new ListTag<>("Inventory"))
                    .putCompound("Achievements", new CompoundTag())
                    .putInt("playerGameType", this.getGamemode())
                    .putList(new ListTag<DoubleTag>("Motion")
                            .add(new DoubleTag("0", 0))
                            .add(new DoubleTag("1", 0))
                            .add(new DoubleTag("2", 0)))
                    .putList(new ListTag<FloatTag>("Rotation")
                            .add(new FloatTag("0", 0))
                            .add(new FloatTag("1", 0)))
                    .putFloat("FallDistance", 0)
                    .putShort("Fire", 0)
                    .putShort("Air", 300)
                    .putBoolean("OnGround", true)
                    .putBoolean("Invulnerable", false);

            this.saveOfflinePlayerData(name, nbt, true, runEvent);
        }
        return nbt;
    }


    /**
     * @see #saveOfflinePlayerData(String, CompoundTag, boolean)
     */
    public void saveOfflinePlayerData(UUID uuid, CompoundTag tag) {
        this.saveOfflinePlayerData(uuid, tag, false);
    }

    /**
     * @see #saveOfflinePlayerData(String, CompoundTag, boolean)
     */
    public void saveOfflinePlayerData(String name, CompoundTag tag) {
        this.saveOfflinePlayerData(name, tag, false);
    }

    /**
     * @see #saveOfflinePlayerData(String, CompoundTag, boolean)
     */
    public void saveOfflinePlayerData(UUID uuid, CompoundTag tag, boolean async) {
        this.saveOfflinePlayerData(uuid.toString(), tag, async);
    }


    /**
     * 保存玩家数据，玩家在线离线都行.
     * <p>
     * Save player data, players can be offline.
     *
     * @param name  玩家名<br>player name
     * @param tag   NBT数据<br>nbt data
     * @param async 是否异步保存<br>Whether to save asynchronously
     */
    public void saveOfflinePlayerData(String name, CompoundTag tag, boolean async) {
        Optional<UUID> uuid = lookupName(name);
        saveOfflinePlayerData(uuid.map(UUID::toString).orElse(name), tag, async, true);
    }

    private void saveOfflinePlayerData(String name, CompoundTag tag, boolean async, boolean runEvent) {
        String nameLower = name.toLowerCase();
        if (this.shouldSavePlayerData()) {
            PlayerDataSerializeEvent event = new PlayerDataSerializeEvent(nameLower, playerDataSerializer);
            if (runEvent) {
                pluginManager.callEvent(event);
            }

            this.getScheduler().scheduleTask(new Task() {
                boolean hasRun = false;

                @Override
                public void onRun(int currentTick) {
                    this.onCancel();
                }

                //doing it like this ensures that the playerdata will be saved in a server shutdown
                @Override
                public void onCancel() {
                    if (!this.hasRun) {
                        this.hasRun = true;
                        saveOfflinePlayerDataInternal(event.getSerializer(), tag, nameLower, event.getUuid().orElse(null));
                    }
                }
            }, async);
        }
    }

    private void saveOfflinePlayerDataInternal(PlayerDataSerializer serializer, CompoundTag tag, String name, UUID uuid) {
        try (OutputStream dataStream = serializer.write(name, uuid)) {
            NBTIO.writeGZIPCompressed(tag, dataStream, ByteOrder.BIG_ENDIAN);
        } catch (Exception e) {
            log.error(this.getLanguage().tr("nukkit.data.saveError", name, e));
        }
    }

    private void convertLegacyPlayerData() {
        File dataDirectory = new File(getDataPath(), "players/");
        Pattern uuidPattern = Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}.dat$");

        File[] files = dataDirectory.listFiles(file -> {
            String name = file.getName();
            return !uuidPattern.matcher(name).matches() && name.endsWith(".dat");
        });

        if (files == null) {
            return;
        }

        for (File legacyData : files) {
            String name = legacyData.getName();
            // Remove file extension
            name = name.substring(0, name.length() - 4);

            log.debug("Attempting legacy player data conversion for {}", name);

            CompoundTag tag = getOfflinePlayerDataInternal(name, false, false);

            if (tag == null || !tag.contains("UUIDLeast") || !tag.contains("UUIDMost")) {
                // No UUID so we cannot convert. Wait until player logs in.
                continue;
            }

            UUID uuid = new UUID(tag.getLong("UUIDMost"), tag.getLong("UUIDLeast"));
            if (!tag.contains("NameTag")) {
                tag.putString("NameTag", name);
            }

            if (new File(getDataPath() + "players/" + uuid.toString() + ".dat").exists()) {
                // We don't want to overwrite existing data.
                continue;
            }

            saveOfflinePlayerData(uuid.toString(), tag, false, false);

            // Add name to lookup table
            updateName(uuid, name);

            // Delete legacy data
            if (!legacyData.delete()) {
                log.warn("Unable to delete legacy data for {}", name);
            }
        }
    }


    /**
     * 从玩家名获得一个在线玩家，这个方法是模糊匹配，只要玩家名带有name前缀就会被返回.
     * <p>
     * Get an online player from the player name, this method is a fuzzy match and will be returned as long as the player name has the name prefix.
     *
     * @param name 玩家名<br>player name
     * @return 玩家实例对象，获取失败为null<br>Player instance object,failed to get null
     */
    public Player getPlayer(String name) {
        Player found = null;
        name = name.toLowerCase();
        int delta = Integer.MAX_VALUE;
        for (Player player : this.getOnlinePlayers().values()) {
            if (player.getName().toLowerCase().startsWith(name)) {
                int curDelta = player.getName().length() - name.length();
                if (curDelta < delta) {
                    found = player;
                    delta = curDelta;
                }
                if (curDelta == 0) {
                    break;
                }
            }
        }

        return found;
    }

    /**
     * 从玩家名获得一个在线玩家，这个方法是精确匹配，当玩家名字符串完全相同时返回.
     * <p>
     * Get an online player from a player name, this method is an exact match and returns when the player name string is identical.
     *
     * @param name 玩家名<br>player name
     * @return 玩家实例对象，获取失败为null<br>Player instance object,failed to get null
     */
    public Player getPlayerExact(String name) {
        name = name.toLowerCase();
        for (Player player : this.getOnlinePlayers().values()) {
            if (player.getName().toLowerCase().equals(name)) {
                return player;
            }
        }

        return null;
    }

    /**
     * 指定一个部分玩家名，返回所有包含或者等于该名称的玩家.
     * <p>
     * Specify a partial player name and return all players with or equal to that name.
     *
     * @param partialName 部分玩家名<br>partial name
     * @return 匹配到的所有玩家, 若匹配不到则为一个空数组<br>All players matched, if not matched then an empty array
     */
    public Player[] matchPlayer(String partialName) {
        partialName = partialName.toLowerCase();
        List<Player> matchedPlayer = new ArrayList<>();
        for (Player player : this.getOnlinePlayers().values()) {
            if (player.getName().toLowerCase().equals(partialName)) {
                return new Player[]{player};
            } else if (player.getName().toLowerCase().contains(partialName)) {
                matchedPlayer.add(player);
            }
        }

        return matchedPlayer.toArray(Player.EMPTY_ARRAY);
    }


    /**
     * 删除一个玩家，可以让一个玩家离线.
     * <p>
     * Delete a player to take a player offline.
     *
     * @param player 需要删除的玩家<br>Players who need to be deleted
     */
    public void removePlayer(Player player) {
        Player toRemove = this.players.remove(player.getRawSocketAddress());
        if (toRemove != null) {
            return;
        }

        for (InetSocketAddress socketAddress : new ArrayList<>(this.players.keySet())) {
            Player p = this.players.get(socketAddress);
            if (player == p) {
                this.players.remove(socketAddress);
                break;
            }
        }
    }

    public PlayerDataSerializer getPlayerDataSerializer() {
        return playerDataSerializer;
    }

    public void setPlayerDataSerializer(PlayerDataSerializer playerDataSerializer) {
        this.playerDataSerializer = Preconditions.checkNotNull(playerDataSerializer, "playerDataSerializer");
    }

    /**
     * 获得所有在线的玩家Map.
     * <p>
     * Get all online players Map.
     *
     * @return 所有的在线玩家Map
     */
    public Map<UUID, Player> getOnlinePlayers() {
        return ImmutableMap.copyOf(playerList);
    }

    // endregion

    // region constants - 常量

    /**
     * @return 服务器名称<br>The name of server
     */
    public String getName() {
        return "Nukkit";
    }

    public String getNukkitVersion() {
        return Nukkit.VERSION;
    }

    public String getBStatsNukkitVersion() {
        return Nukkit.VERSION;
    }

    @PowerNukkitOnly
    public String getGitCommit() {
        return Nukkit.GIT_COMMIT;
    }

    public String getCodename() {
        return Nukkit.CODENAME;
    }

    public String getVersion() {
        return ProtocolInfo.MINECRAFT_VERSION;
    }

    public String getApiVersion() {
        return Nukkit.API_VERSION;
    }

    // endregion

    public String getFilePath() {
        return filePath;
    }

    public String getDataPath() {
        return dataPath;
    }

    public String getPluginPath() {
        return pluginPath;
    }

    /**
     * @return 服务器UUID<br>server UUID
     */
    public UUID getServerUniqueId() {
        return this.serverID;
    }

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", by = "PowerNukkit", reason = "Use your own logger, sharing loggers makes bug analyses harder.",
            replaceWith = "@Log4j2 annotation in the class and use the `log` static field that is generated by lombok, " +
                    "also make sure to log the exception as the last argument, don't concatenate it or use it as parameter replacement. " +
                    "Just put it as last argument and SLF4J will understand that the log message was caused by that exception/throwable.")
    public MainLogger getLogger() {
        return MainLogger.getLogger();
    }

    public EntityMetadataStore getEntityMetadata() {
        return entityMetadata;
    }

    public PlayerMetadataStore getPlayerMetadata() {
        return playerMetadata;
    }

    public LevelMetadataStore getLevelMetadata() {
        return levelMetadata;
    }


    public ResourcePackManager getResourcePackManager() {
        return resourcePackManager;
    }

    public FreezableArrayManager getFreezableArrayManager() {
        return freezableArrayManager;
    }

    // TODO: update PNX Junit5 test framework to remove dependency on this method
    private void registerProfessions() {
        Profession.init();
    }

    // TODO: update PNX Junit5 test framework to remove dependency on this method
    private void registerEntities() {
        Entity.init();
    }

    // TODO: update PNX Junit5 test framework to remove dependency on this method
    private void registerBlockEntities() {
        BlockEntity.init();
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @NotNull
    public PositionTrackingService getPositionTrackingService() {
        return positionTrackingService;
    }

    // region crafting & recipe - 合成与配方

    /**
     * 发送配方列表数据包给一个玩家.<p>
     * Send a recipe list packet to a player.
     *
     * @param player 玩家
     */
    public void sendRecipeList(Player player) {
        player.dataPacket(CraftingManager.getCraftingPacket());
    }

    /**
     * 注册配方到配方管理器
     * <p>
     * Register Recipe to Recipe Manager
     *
     * @param recipe 配方
     */
    public void addRecipe(Recipe recipe) {
        this.craftingManager.registerRecipe(recipe);
    }

    public CraftingManager getCraftingManager() {
        return craftingManager;
    }

    // endregion

    // region Levels - 游戏世界相关

    /**
     * @return 获得所有游戏世界<br>Get all the game world
     */
    public Map<Integer, Level> getLevels() {
        return levels;
    }

    /**
     * @return 获得默认游戏世界<br>Get the default world
     */
    public Level getDefaultLevel() {
        return defaultLevel;
    }


    /**
     * 设置默认游戏世界
     * <p>
     * Set default game world
     *
     * @param defaultLevel 默认游戏世界<br>default game world
     */
    public void setDefaultLevel(Level defaultLevel) {
        if (defaultLevel == null || (this.isLevelLoaded(defaultLevel.getFolderName()) && defaultLevel != this.defaultLevel)) {
            this.defaultLevel = defaultLevel;
        }
    }

    /**
     * @param name 世界名字
     * @return 世界是否已经加载<br>Is the world already loaded
     */
    public boolean isLevelLoaded(String name) {
        return this.getLevelByName(name) != null;
    }


    /**
     * 从世界id得到世界,0主世界 1 地狱 2 末地
     * <p>
     * Get world from world id,0 OVERWORLD 1 NETHER 2 THE_END
     *
     * @param levelId 世界id<br>world id
     * @return level实例<br>level instance
     */
    public Level getLevel(int levelId) {
        if (this.levels.containsKey(levelId)) {
            return this.levels.get(levelId);
        }
        return null;
    }

    /**
     * 从世界名得到世界,overworld 主世界 nether 地狱 the_end 末地
     * <p>
     * Get world from world name,{@code overworld nether the_end}
     *
     * @param name 世界名<br>world name
     * @return level实例<br>level instance
     */
    public Level getLevelByName(String name) {
        for (Level level : this.levelArray) {
            if (level.getFolderName().equalsIgnoreCase(name)) {
                return level;
            }
        }

        return null;
    }

    public boolean unloadLevel(Level level) {
        return this.unloadLevel(level, false);
    }

    /**
     * 卸载世界
     * <p>
     * unload level
     *
     * @param level       世界
     * @param forceUnload 是否强制卸载<br>whether to force uninstallation.
     * @return 卸载是否成功
     */
    public boolean unloadLevel(Level level, boolean forceUnload) {
        if (level == this.getDefaultLevel() && !forceUnload) {
            throw new IllegalStateException("The default level cannot be unloaded while running, please switch levels.");
        }

        return level.unload(forceUnload);

    }

    public boolean loadLevel(String name) {
        if (Objects.equals(name.trim(), "")) {
            throw new LevelException("Invalid empty level name");
        }
        if (this.isLevelLoaded(name)) {
            return true;
        } else if (!this.isLevelGenerated(name)) {
            log.warn(this.getLanguage().tr("nukkit.level.notFound", name));

            return false;
        }

        String path;

        if (name.contains("/") || name.contains("\\")) {
            path = name;
        } else {
            path = this.getDataPath() + "worlds/" + name + "/";
        }

        Class<? extends LevelProvider> provider = LevelProviderManager.getProvider(path);

        if (provider == null) {
            log.error(this.getLanguage().tr("nukkit.level.loadError", new String[]{name, "Unknown provider"}));

            return false;
        }

        Level level;
        try {
            level = new Level(this, name, path, provider);
        } catch (Exception e) {
            log.error(this.getLanguage().tr("nukkit.level.loadError", name, e.getMessage()), e);
            return false;
        }

        this.levels.put(level.getId(), level);

        level.initLevel();

        //convert old Nukkit World
        if (level.getProvider() instanceof Anvil anvil && anvil.isOldAnvil() && level.isOverWorld()) {
            log.info(Server.getInstance().getLanguage().tr("nukkit.anvil.converter.update"));
            var scan = new Scanner(System.in);
            var result = scan.next();
            if (result.equalsIgnoreCase("true") || result.equalsIgnoreCase("t")) {
                File file = new File(Path.of(path).resolve("region").toUri());
                if (file.exists()) {
                    var regions = file.listFiles();
                    if (regions != null) {
                        var bid = Server.getInstance().addBusying(System.currentTimeMillis());
                        final Method loadRegion;
                        try {
                            loadRegion = Anvil.class.getDeclaredMethod("loadRegion", int.class, int.class);
                            loadRegion.setAccessible(true);
                        } catch (NoSuchMethodException e) {
                            throw new RuntimeException(e);
                        }
                        var allTask = new ArrayList<CompletableFuture<?>>();
                        for (var region : regions) {
                            allTask.add(CompletableFuture.runAsync(() -> {
                                var regionPos = region.getName().split("\\.");
                                var regionX = Integer.parseInt(regionPos[1]);
                                var regionZ = Integer.parseInt(regionPos[2]);
                                BaseRegionLoader loader;
                                try {
                                    loader = (BaseRegionLoader) loadRegion.invoke(anvil, regionX, regionZ);
                                } catch (IllegalAccessException | InvocationTargetException e) {
                                    throw new RuntimeException(e);
                                }
                                OldNukkitLevelConvert.convertToPNXWorld(anvil, loader);
                            }, this.computeThreadPool));
                        }
                        CompletableFuture.allOf(allTask.toArray(new CompletableFuture<?>[]{})).join();
                        Server.getInstance().removeBusying(bid);
                        loadRegion.setAccessible(false);
                    }
                }
            } else System.exit(0);
        }

        this.getPluginManager().callEvent(new LevelLoadEvent(level));
        level.setTickRate(this.baseTickRate);
        return true;
    }

    public boolean generateLevel(String name) {
        return this.generateLevel(name, new java.util.Random().nextLong());
    }

    public boolean generateLevel(String name, long seed) {
        return this.generateLevel(name, seed, null);
    }

    public boolean generateLevel(String name, long seed, Class<? extends Generator> generator) {
        return this.generateLevel(name, seed, generator, new HashMap<>());
    }

    public boolean generateLevel(String name, long seed, Class<? extends Generator> generator, Map<String, Object> options) {
        return generateLevel(name, seed, generator, options, null);
    }

    @PowerNukkitXOnly
    @Since("1.19.20-r3")
    public boolean generateLevel(String name, long seed, Class<? extends Generator> generator, Map<String, Object> options, DimensionData givenDimensionData) {
        return generateLevel(name, seed, generator, options, null, null);
    }

    public boolean generateLevel(String name, long seed, Class<? extends Generator> generator, Map<String, Object> options, DimensionData givenDimensionData, Class<? extends LevelProvider> provider) {
        if (Objects.equals(name.trim(), "") || this.isLevelGenerated(name)) {
            return false;
        }

        if (!options.containsKey("preset")) {
            options.put("preset", this.getPropertyString("generator-settings", ""));
        }

        if (generator == null) {
            generator = Generator.getGenerator(this.getLevelType());
        }

        if (provider == null) {
            provider = LevelProviderManager.getProviderByName(this.getConfig().get("level-settings.default-format", "anvil"));
        }

        String path;

        if (name.contains("/") || name.contains("\\")) {
            path = name;
        } else {
            path = this.getDataPath() + "worlds/" + name + "/";
        }

        Level level;
        try {
            provider.getMethod("generate", String.class, String.class, long.class, Class.class, Map.class).invoke(null, path, name, seed, generator, options);

            level = new Level(this, name, path, provider);
            this.levels.put(level.getId(), level);

            level.initLevel(givenDimensionData);
            level.setTickRate(this.baseTickRate);
        } catch (Exception e) {
            log.error(this.getLanguage().tr("nukkit.level.generationError", new String[]{name, Utils.getExceptionMessage(e)}), e);
            return false;
        }

        this.getPluginManager().callEvent(new LevelInitEvent(level));

        this.getPluginManager().callEvent(new LevelLoadEvent(level));

        /*this.getLogger().notice(this.getLanguage().tr("nukkit.level.backgroundGeneration", name));

        int centerX = (int) level.getSpawnLocation().getX() >> 4;
        int centerZ = (int) level.getSpawnLocation().getZ() >> 4;

        TreeMap<String, Integer> order = new TreeMap<>();

        for (int X = -3; X <= 3; ++X) {
            for (int Z = -3; Z <= 3; ++Z) {
                int distance = X * X + Z * Z;
                int chunkX = X + centerX;
                int chunkZ = Z + centerZ;
                order.put(Level.chunkHash(chunkX, chunkZ), distance);
            }
        }

        List<Map.Entry<String, Integer>> sortList = new ArrayList<>(order.entrySet());

        Collections.sort(sortList, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o2.getValue() - o1.getValue();
            }
        });

        for (String index : order.keySet()) {
            Chunk.Entry entry = Level.getChunkXZ(index);
            level.populateChunk(entry.chunkX, entry.chunkZ, true);
        }*/
        return true;
    }

    public boolean isLevelGenerated(String name) {
        if (Objects.equals(name.trim(), "")) {
            return false;
        }

        if (this.getLevelByName(name) == null) {
            String path;

            if (name.contains("/") || name.contains("\\")) {
                path = name;
            } else {
                path = this.getDataPath() + "worlds/" + name + "/";
            }

            return LevelProviderManager.getProvider(path) != null;
        }

        return true;
    }

    // endregion

    // region Ban, OP and whitelist - Ban，OP与白名单

    public BanList getNameBans() {
        return this.banByName;
    }

    public BanList getIPBans() {
        return this.banByIP;
    }

    public void addOp(String name) {
        this.operators.set(name.toLowerCase(), true);
        Player player = this.getPlayerExact(name);
        if (player != null) {
            player.recalculatePermissions();
            player.getAdventureSettings().onOpChange(true);
            player.getAdventureSettings().update();
            player.sendCommandData();
        }
        this.operators.save(true);
    }

    public void removeOp(String name) {
        this.operators.remove(name.toLowerCase());
        Player player = this.getPlayerExact(name);
        if (player != null) {
            player.recalculatePermissions();
            player.getAdventureSettings().onOpChange(false);
            player.getAdventureSettings().update();
            player.sendCommandData();
        }
        this.operators.save();
    }

    public void addWhitelist(String name) {
        this.whitelist.set(name.toLowerCase(), true);
        this.whitelist.save(true);
    }

    public void removeWhitelist(String name) {
        this.whitelist.remove(name.toLowerCase());
        this.whitelist.save(true);
    }

    public boolean isWhitelisted(String name) {
        return !this.hasWhitelist() || this.operators.exists(name, true) || this.whitelist.exists(name, true);
    }

    public boolean isOp(String name) {
        return name != null && this.operators.exists(name, true);
    }

    public Config getWhitelist() {
        return whitelist;
    }

    public Config getOps() {
        return operators;
    }

    public void reloadWhitelist() {
        this.whitelist.reload();
    }

    // endregion

    // region configs - 配置相关

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    /**
     * @return 服务器端口<br>server port
     */
    public int getPort() {
        return this.getPropertyInt("server-port", 19132);
    }

    /**
     * @return 可视距离<br>server view distance
     */
    public int getViewDistance() {
        return this.getPropertyInt("view-distance", 10);
    }

    /**
     * @return 服务器网络地址<br>server ip
     */
    public String getIp() {
        return this.getPropertyString("server-ip", "0.0.0.0");
    }

    /**
     * @return 服务器是否会自动保存<br>Does the server automatically save
     */
    public boolean getAutoSave() {
        return this.autoSave;
    }

    /**
     * 设置服务器自动保存
     * <p>
     * Set server autosave
     *
     * @param autoSave 是否自动保存<br>Whether to save automatically
     */
    public void setAutoSave(boolean autoSave) {
        this.autoSave = autoSave;
        for (Level level : this.levelArray) {
            level.setAutoSave(this.autoSave);
        }
    }

    public String getLevelType() {
        return this.getPropertyString("level-type", "DEFAULT");
    }

    /**
     * @return 服务器是否生成结构<br>Whether the server generate the structure.
     */
    public boolean getGenerateStructures() {
        return this.getPropertyBoolean("generate-structures", true);
    }

    /**
     * 得到服务器的gamemode
     * <p>
     * Get the gamemode of the server
     *
     * @return gamemode id
     */
    public int getGamemode() {
        try {
            return this.getPropertyInt("gamemode", 0) & 0b11;
        } catch (NumberFormatException exception) {
            return getGamemodeFromString(this.getPropertyString("gamemode")) & 0b11;
        }
    }

    public boolean getForceGamemode() {
        return this.getPropertyBoolean("force-gamemode", false);
    }


    /**
     * 默认{@code direct=false}
     *
     * @see #getGamemodeString(int, boolean)
     */
    public static String getGamemodeString(int mode) {
        return getGamemodeString(mode, false);
    }

    /**
     * 从gamemode id获取游戏模式字符串.
     * <p>
     * Get game mode string from gamemode id.
     *
     * @param mode   gamemode id
     * @param direct 如果为true就直接返回字符串,为false返回代表游戏模式的硬编码字符串.<br>If true, the string is returned directly, and if false, the hard-coded string representing the game mode is returned.
     * @return 游戏模式字符串<br>Game Mode String
     */
    public static String getGamemodeString(int mode, boolean direct) {
        switch (mode) {
            case Player.SURVIVAL:
                return direct ? "Survival" : "%gameMode.survival";
            case Player.CREATIVE:
                return direct ? "Creative" : "%gameMode.creative";
            case Player.ADVENTURE:
                return direct ? "Adventure" : "%gameMode.adventure";
            case Player.SPECTATOR:
                return direct ? "Spectator" : "%gameMode.spectator";
        }
        return "UNKNOWN";
    }

    /**
     * 从字符串获取gamemode
     * <p>
     * Get gamemode from string
     *
     * @param str 代表游戏模式的字符串，例如0,survival...<br>A string representing the game mode, e.g. 0,survival...
     * @return 游戏模式id<br>gamemode id
     */
    public static int getGamemodeFromString(String str) {
        switch (str.trim().toLowerCase()) {
            case "0":
            case "survival":
            case "s":
                return Player.SURVIVAL;

            case "1":
            case "creative":
            case "c":
                return Player.CREATIVE;

            case "2":
            case "adventure":
            case "a":
                return Player.ADVENTURE;

            case "3":
            case "spectator":
            case "spc":
            case "view":
            case "v":
                return Player.SPECTATOR;
        }
        return -1;
    }

    /**
     * 从字符串获取游戏难度
     * <p>
     * Get game difficulty from string
     *
     * @param str 代表游戏难度的字符串，例如0,peaceful...<br>A string representing the game difficulty, e.g. 0,peaceful...
     * @return 游戏难度id<br>game difficulty id
     */
    public static int getDifficultyFromString(String str) {
        switch (str.trim().toLowerCase()) {
            case "0":
            case "peaceful":
            case "p":
                return 0;

            case "1":
            case "easy":
            case "e":
                return 1;

            case "2":
            case "normal":
            case "n":
                return 2;

            case "3":
            case "hard":
            case "h":
                return 3;
        }
        return -1;
    }

    /**
     * 获得服务器游戏难度
     * <p>
     * Get server game difficulty
     *
     * @return 游戏难度id<br>game difficulty id
     */
    public int getDifficulty() {
        if (this.difficulty == Integer.MAX_VALUE) {
            this.difficulty = getDifficultyFromString(this.getPropertyString("difficulty", "1"));
        }
        return this.difficulty;
    }

    /**
     * 设置服务器游戏难度
     * <p>
     * set server game difficulty
     *
     * @param difficulty 游戏难度id<br>game difficulty id
     */
    public void setDifficulty(int difficulty) {
        int value = difficulty;
        if (value < 0) value = 0;
        if (value > 3) value = 3;
        this.difficulty = value;
        this.setPropertyInt("difficulty", value);
    }

    /**
     * @return 是否开启白名单<br>Whether to start server whitelist
     */
    public boolean hasWhitelist() {
        return this.getPropertyBoolean("white-list", false);
    }

    /**
     * @return 得到服务器出生点保护半径<br>Get server birth point protection radius
     */
    public int getSpawnRadius() {
        return this.getPropertyInt("spawn-protection", 16);
    }

    /**
     * @return 服务器是否允许飞行<br>Whether the server allows flying
     */
    public boolean getAllowFlight() {
        if (getAllowFlight == null) {
            getAllowFlight = this.getPropertyBoolean("allow-flight", false);
        }
        return getAllowFlight;
    }

    /**
     * @return 服务器是否为硬核模式<br>Whether the server is in hardcore mode
     */
    public boolean isHardcore() {
        return this.getPropertyBoolean("hardcore", false);
    }

    /**
     * @return 获取默认gamemode<br>Get default gamemode
     */
    public int getDefaultGamemode() {
        if (this.defaultGamemode == Integer.MAX_VALUE) {
            this.defaultGamemode = this.getGamemode();
        }
        return this.defaultGamemode;
    }

    /**
     * @return 得到服务器标题<br>Get server motd
     */
    public String getMotd() {
        return this.getPropertyString("motd", "PowerNukkitX Server");
    }

    /**
     * @return 得到服务器子标题<br>Get the server subheading
     */
    public String getSubMotd() {
        String subMotd = this.getPropertyString("sub-motd", "https://powernukkitx.cn");
        if (subMotd.isEmpty()) {
            subMotd = "https://powernukkitx.cn"; // The client doesn't allow empty sub-motd in 1.16.210
        }
        return subMotd;
    }

    /**
     * @return 是否强制使用服务器资源包<br>Whether to force the use of server resourcepack
     */
    public boolean getForceResources() {
        return this.getPropertyBoolean("force-resources", false);
    }

    /**
     * @return 是否强制使用服务器资源包的同时允许加载客户端资源包<br>Whether to force the use of server resourcepack while allowing the loading of client resourcepack
     */
    public boolean getForceResourcesAllowOwnPacks() {
        return this.getPropertyBoolean("force-resources-allow-client-packs", false);
    }

    public BaseLang getLanguage() {
        return baseLang;
    }

    public LangCode getLanguageCode() {
        return baseLangCode;
    }

    private LangCode mapInternalLang(String langName) {
        return switch (langName) {
            case "bra" -> LangCode.valueOf("pt_BR");
            case "chs" -> LangCode.valueOf("zh_CN");
            case "cht" -> LangCode.valueOf("zh_TW");
            case "cze" -> LangCode.valueOf("cs_CZ");
            case "deu" -> LangCode.valueOf("de_DE");
            case "fin" -> LangCode.valueOf("fi_FI");
            case "eng" -> LangCode.valueOf("en_US");
            case "fra" -> LangCode.valueOf("en_US");
            case "idn" -> LangCode.valueOf("id_ID");
            case "jpn" -> LangCode.valueOf("ja_JP");
            case "kor" -> LangCode.valueOf("ko_KR");
            case "ltu" -> LangCode.valueOf("en_US");
            case "pol" -> LangCode.valueOf("pl_PL");
            case "rus" -> LangCode.valueOf("ru_RU");
            case "spa" -> LangCode.valueOf("es_ES");
            case "tur" -> LangCode.valueOf("tr_TR");
            case "ukr" -> LangCode.valueOf("uk_UA");
            case "vie" -> LangCode.valueOf("en_US");
            default -> throw new IllegalArgumentException();
        };
    }

    public boolean isLanguageForced() {
        return forceLanguage;
    }

    @PowerNukkitOnly
    public boolean isRedstoneEnabled() {
        return redstoneEnabled;
    }

    @PowerNukkitOnly
    public void setRedstoneEnabled(boolean redstoneEnabled) {
        this.redstoneEnabled = redstoneEnabled;
    }

    //Revising later...
    public Config getConfig() {
        return this.config;
    }

    public <T> T getConfig(String variable) {
        return this.getConfig(variable, null);
    }

    @SuppressWarnings("unchecked")
    public <T> T getConfig(String variable, T defaultValue) {
        Object value = this.config.get(variable);
        return value == null ? defaultValue : (T) value;
    }

    public Config getProperties() {
        return this.properties;
    }

    public Object getProperty(String variable) {
        return this.getProperty(variable, null);
    }

    public Object getProperty(String variable, Object defaultValue) {
        return this.properties.exists(variable) ? this.properties.get(variable) : defaultValue;
    }

    public void setPropertyString(String variable, String value) {
        this.properties.set(variable, value);
        this.properties.save();
    }

    public String getPropertyString(String variable) {
        return this.getPropertyString(variable, null);
    }

    public String getPropertyString(String variable, String defaultValue) {
        return this.properties.exists(variable) ? this.properties.get(variable).toString() : defaultValue;
    }

    public int getPropertyInt(String variable) {
        return this.getPropertyInt(variable, null);
    }

    public int getPropertyInt(String variable, Integer defaultValue) {
        return this.properties.exists(variable) ? (!this.properties.get(variable).equals("") ? Integer.parseInt(String.valueOf(this.properties.get(variable))) : defaultValue) : defaultValue;
    }

    public void setPropertyInt(String variable, int value) {
        this.properties.set(variable, value);
        this.properties.save();
    }

    public boolean getPropertyBoolean(String variable) {
        return this.getPropertyBoolean(variable, null);
    }

    public boolean getPropertyBoolean(String variable, Object defaultValue) {
        Object value = this.properties.exists(variable) ? this.properties.get(variable) : defaultValue;
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        switch (String.valueOf(value)) {
            case "on":
            case "true":
            case "1":
            case "yes":
                return true;
        }
        return false;
    }

    public void setPropertyBoolean(String variable, boolean value) {
        this.properties.set(variable, value ? "1" : "0");
        this.properties.save();
    }

    public boolean shouldSavePlayerData() {
        return this.getConfig("player.save-player-data", true);
    }

    public int getPlayerSkinChangeCooldown() {
        return this.getConfig("player.skin-change-cooldown", 30);
    }

    public boolean isNetherAllowed() {
        return this.allowNether;
    }

    @Since("1.3.0.0-PN")
    public boolean isIgnoredPacket(Class<? extends DataPacket> clazz) {
        return this.ignoredPackets.contains(clazz.getSimpleName());
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean isSafeSpawn() {
        return safeSpawn;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean isForceSkinTrusted() {
        return forceSkinTrusted;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean isCheckMovement() {
        return checkMovement;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean isTheEndAllowed() {
        return this.allowTheEnd;
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public boolean isEnableExperimentMode() {
        return this.enableExperimentMode;
    }

    @PowerNukkitXOnly
    @Since("1.19.21-r4")
    public boolean isWaterdogCapable() {
        return this.getConfig("settings.waterdogpe", false);
    }

    @PowerNukkitXOnly
    @Since("1.19.30-r2")
    public int getMaximumStaleDatagrams() {
        return this.maximumStaleDatagrams;
    }

    @PowerNukkitXOnly
    @Since("1.19.30-r2")
    public int getMaximumSizePerChunk() {
        return maximumSizePerChunk;
    }

    @PowerNukkitXOnly
    @Since("1.19.40-r3")
    public int getServerAuthoritativeMovement() {
        return serverAuthoritativeMovementMode;
    }

    @PowerNukkitXOnly
    @Since("1.20.0-r2")
    public boolean isEnableSnappy() {
        return this.getConfig("network.snappy", false);
    }

    // endregion

    // region threading - 并发基础设施

    /**
     * Checks the current thread against the expected primary thread for the
     * server.
     * <p>
     * <b>Note:</b> this method should not be used to indicate the current
     * synchronized state of the runtime. A current thread matching the main
     * thread indicates that it is synchronized, but a mismatch does not
     * preclude the same assumption.
     *
     * @return true if the current thread matches the expected primary thread,
     * false otherwise
     */
    public final boolean isPrimaryThread() {
        return (Thread.currentThread() == currentThread);
    }

    public Thread getPrimaryThread() {
        return currentThread;
    }

    public ServerScheduler getScheduler() {
        return scheduler;
    }

    //todo NukkitConsole 会阻塞关不掉
    private class ConsoleThread extends Thread implements InterruptibleThread {
        public ConsoleThread() {
            super("Console Thread");
        }

        @Override
        public void run() {
            console.start();
        }
    }

    private static class ComputeThread extends ForkJoinWorkerThread {
        /**
         * Creates a ForkJoinWorkerThread operating in the given pool.
         *
         * @param pool the pool this thread works in
         * @throws NullPointerException if pool is null
         */
        ComputeThread(ForkJoinPool pool, AtomicInteger threadCount) {
            super(pool);
            this.setName("ComputeThreadPool-thread-" + threadCount.getAndIncrement());
        }
    }

    private static class ComputeThreadPoolThreadFactory implements ForkJoinPool.ForkJoinWorkerThreadFactory {
        private static final AtomicInteger threadCount = new AtomicInteger(0);
        @SuppressWarnings("removal")
        private static final AccessControlContext ACC = contextWithPermissions(
                new RuntimePermission("getClassLoader"),
                new RuntimePermission("setContextClassLoader"));

        @SuppressWarnings("removal")
        static AccessControlContext contextWithPermissions(@NotNull Permission... perms) {
            Permissions permissions = new Permissions();
            for (var perm : perms)
                permissions.add(perm);
            return new AccessControlContext(new ProtectionDomain[]{new ProtectionDomain(null, permissions)});
        }

        @SuppressWarnings("removal")
        public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
            return AccessController.doPrivileged((PrivilegedAction<ForkJoinWorkerThread>) () -> new ComputeThread(pool, threadCount), ACC);
        }
    }

    // endregion

}
