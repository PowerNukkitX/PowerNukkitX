package cn.nukkit;

import cn.nukkit.block.BlockComposter;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.ConsoleCommandSender;
import cn.nukkit.command.PluginIdentifiableCommand;
import cn.nukkit.command.SimpleCommandMap;
import cn.nukkit.command.defaults.WorldCommand;
import cn.nukkit.command.function.FunctionManager;
import cn.nukkit.compression.ZlibChooser;
import cn.nukkit.config.ServerProperties;
import cn.nukkit.config.ServerPropertiesKeys;
import cn.nukkit.config.ServerSettings;
import cn.nukkit.config.YamlSnakeYamlConfigurer;
import cn.nukkit.console.NukkitConsole;
import cn.nukkit.dispenser.DispenseBehaviorRegister;
import cn.nukkit.entity.Attribute;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.entity.data.profession.Profession;
import cn.nukkit.entity.data.property.EntityProperty;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.level.LevelInitEvent;
import cn.nukkit.event.level.LevelLoadEvent;
import cn.nukkit.event.player.PlayerLoginEvent;
import cn.nukkit.event.server.QueryRegenerateEvent;
import cn.nukkit.event.server.ServerStartedEvent;
import cn.nukkit.event.server.ServerStopEvent;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.lang.BaseLang;
import cn.nukkit.lang.LangCode;
import cn.nukkit.lang.TextContainer;
import cn.nukkit.level.DimensionEnum;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.LevelConfig;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.level.format.LevelProviderManager;
import cn.nukkit.level.format.leveldb.LevelDBProvider;
import cn.nukkit.level.generator.terra.PNXPlatform;
import cn.nukkit.level.tickingarea.manager.SimpleTickingAreaManager;
import cn.nukkit.level.tickingarea.manager.TickingAreaManager;
import cn.nukkit.level.tickingarea.storage.JSONTickingAreaStorage;
import cn.nukkit.level.updater.Updater;
import cn.nukkit.level.updater.block.BlockStateUpdaterBase;
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
import cn.nukkit.network.Network;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.PlayerListPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.network.protocol.types.PlayerInfo;
import cn.nukkit.network.protocol.types.XboxLivePlayerInfo;
import cn.nukkit.network.rcon.RCON;
import cn.nukkit.permission.BanEntry;
import cn.nukkit.permission.BanList;
import cn.nukkit.permission.DefaultPermissions;
import cn.nukkit.permission.Permissible;
import cn.nukkit.plugin.InternalPlugin;
import cn.nukkit.plugin.JavaPluginLoader;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginLoadOrder;
import cn.nukkit.plugin.PluginManager;
import cn.nukkit.plugin.service.NKServiceManager;
import cn.nukkit.plugin.service.ServiceManager;
import cn.nukkit.positiontracking.PositionTrackingService;
import cn.nukkit.recipe.Recipe;
import cn.nukkit.registry.RecipeRegistry;
import cn.nukkit.registry.Registries;
import cn.nukkit.resourcepacks.ResourcePackManager;
import cn.nukkit.resourcepacks.loader.JarPluginResourcePackLoader;
import cn.nukkit.resourcepacks.loader.ZippedResourcePackLoader;
import cn.nukkit.scheduler.ServerScheduler;
import cn.nukkit.scheduler.Task;
import cn.nukkit.scoreboard.manager.IScoreboardManager;
import cn.nukkit.scoreboard.manager.ScoreboardManager;
import cn.nukkit.scoreboard.storage.JSONScoreboardStorage;
import cn.nukkit.tags.BiomeTags;
import cn.nukkit.tags.BlockTags;
import cn.nukkit.tags.ItemTags;
import cn.nukkit.utils.*;
import cn.nukkit.utils.collection.FreezableArrayManager;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import eu.okaeri.configs.ConfigManager;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.longs.LongLists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.iq80.leveldb.CompressionType;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.impl.Iq80DBFactory;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.Permission;
import java.security.Permissions;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents a server object, global singleton.
 * <p>is instantiated in {@link Nukkit} and later the instance object is obtained via {@link cn.nukkit.Server#getInstance}.
 * The constructor method of {@link cn.nukkit.Server} performs a number of operations, including but not limited to initializing configuration files, creating threads, thread pools, start plugins, registering recipes, blocks, entities, items, etc.
 *
 * @author MagicDroidX
 * @author Box
 */

@Slf4j
public class Server {
    public static final String BROADCAST_CHANNEL_ADMINISTRATIVE = "nukkit.broadcast.admin";
    public static final String BROADCAST_CHANNEL_USERS = "nukkit.broadcast.user";
    private static Server instance = null;

    private BanList banByName;
    private BanList banByIP;
    private Config operators;
    private Config whitelist;
    private final AtomicBoolean isRunning = new AtomicBoolean(true);
    private final LongList busyingTime = LongLists.synchronize(new LongArrayList(0));
    private boolean hasStopped = false;
    private PluginManager pluginManager;
    private ServerScheduler scheduler;
    /**
     * A tick counter that records the number of ticks that have passed through the server.
     */
    private int tickCounter;
    private long nextTick;
    private final float[] tickAverage = {20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20};
    private final float[] useAverage = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private float maxTick = 20;
    private float maxUse = 0;
    private int sendUsageTicker = 0;
    private final NukkitConsole console;
    private final ConsoleThread consoleThread;
    /**
     * ForkJoinPool (FJP) thread pool responsible for terrain generation, data compression,
     * and other compute-intensive tasks. This thread pool allows parallelization of heavy
     * operations to improve the overall server performance.
     */
    public final ForkJoinPool computeThreadPool;
    private SimpleCommandMap commandMap;
    private ResourcePackManager resourcePackManager;
    private ConsoleCommandSender consoleSender;
    private IScoreboardManager scoreboardManager;
    private FunctionManager functionManager;
    private TickingAreaManager tickingAreaManager;
    private int maxPlayers;
    private boolean autoSave = true;
    /**
     * Indicates whether the configuration item checks the login time.
     */
    public boolean checkLoginTime = false;
    private RCON rcon;
    private EntityMetadataStore entityMetadata;
    private PlayerMetadataStore playerMetadata;
    private LevelMetadataStore levelMetadata;
    private Network network;
    private int serverAuthoritativeMovementMode = 0;
    private Boolean getAllowFlight = null;
    private int difficulty = Integer.MAX_VALUE;
    private int defaultGamemode = Integer.MAX_VALUE;
    private int autoSaveTicker = 0;
    private int autoSaveTicks = 6000;
    private BaseLang baseLang;
    private LangCode baseLangCode;
    private UUID serverID;
    private final String filePath;
    private final String dataPath;
    private final String pluginPath;
    private final Set<UUID> uniquePlayers = new HashSet<>();
    private final ServerProperties properties;
    private final Map<InetSocketAddress, Player> players = new ConcurrentHashMap<>();
    private final Map<UUID, Player> playerList = new ConcurrentHashMap<>();
    private QueryRegenerateEvent queryRegenerateEvent;
    private PositionTrackingService positionTrackingService;

    private final Map<Integer, Level> levels = new HashMap<>() {
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
    private final Thread currentThread;
    private final long launchTime;
    private final ServerSettings settings;
    private Watchdog watchdog;
    private DB playerDataDB;
    private boolean useTerra;
    private FreezableArrayManager freezableArrayManager;
    public boolean enabledNetworkEncryption;

    // ─────────────────────────────────────────────────────
    // ──────── Start of [Default Level Region] ────────────
    // ─────────────────────────────────────────────────────
    private Level defaultLevel = null;
    private Level defaultNether = null;
    private Level defaultEnd = null;
    private boolean allowNether;
    private boolean allowTheEnd;
    // ─────────────────────────────────────────────────────

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
        String commandDataPath = new File(dataPath).getAbsolutePath() + "/command_data";
        if (!new File(commandDataPath).exists()) {
            new File(commandDataPath).mkdirs();
        }

        this.console = new NukkitConsole(this);
        this.consoleThread = new ConsoleThread();
        this.consoleThread.start();

        File config = new File(this.dataPath + "nukkit.yml");
        String chooseLanguage = null;
        if (!config.exists()) {
            log.info("{}Welcome! Please choose a language first!", TextFormat.GREEN);
            try {
                InputStream languageList = this.getClass().getModule().getResourceAsStream("language/language.list");
                if (languageList == null) {
                    throw new IllegalStateException("language/language.list is missing. If you are running a development version, make sure you have run 'git submodule update --init'.");
                }
                String[] lines = Utils.readFile(languageList).split("\n");
                for (String line : lines) {
                    log.info(line);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            while (chooseLanguage == null) {
                String lang;
                if (predefinedLanguage != null) {
                    log.info("Trying to load language from predefined language: {}", predefinedLanguage);
                    lang = predefinedLanguage;
                } else {
                    lang = this.console.readLine();
                }

                try (InputStream conf = this.getClass().getClassLoader().getResourceAsStream("language/" + lang + "/lang.json")) {
                    if (conf != null) {
                        chooseLanguage = lang;
                    } else if (predefinedLanguage != null) {
                        log.warn("No language found for predefined language: {}, please choose a valid language", predefinedLanguage);
                        predefinedLanguage = null;
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            Config configInstance = new Config(config);
            chooseLanguage = configInstance.getString("settings.language", "eng");
        }
        this.baseLang = new BaseLang(chooseLanguage);
        this.baseLangCode = mapInternalLang(chooseLanguage);
        log.info("Loading {}...", TextFormat.GREEN + "nukkit.yml" + TextFormat.RESET);
        this.settings = ConfigManager.create(ServerSettings.class, it -> {
            it.withConfigurer(new YamlSnakeYamlConfigurer());
            it.withBindFile(config);
            it.withRemoveOrphans(true);
            it.saveDefaults();
            it.load(true);
        });
        this.settings.baseSettings().language(chooseLanguage);

        this.computeThreadPool = new ForkJoinPool(Math.min(0x7fff, Runtime.getRuntime().availableProcessors()), new ComputeThreadPoolThreadFactory(), null, false);

        levelArray = Level.EMPTY_ARRAY;

        org.apache.logging.log4j.Level targetLevel = org.apache.logging.log4j.Level.getLevel(this.settings.debugSettings().level());
        org.apache.logging.log4j.Level currentLevel = Nukkit.getLogLevel();
        if (targetLevel != null && targetLevel.intLevel() > currentLevel.intLevel()) {
            Nukkit.setLogLevel(targetLevel);
        }

        log.info("Loading {}...", TextFormat.GREEN + "server.properties" + TextFormat.RESET);
        this.properties = new ServerProperties(this.dataPath);

        var isShaded = StartArgUtils.isShaded();
        if (!StartArgUtils.isValidStart() || (JarStart.isUsingJavaJar() && !isShaded)) {
            log.error(getLanguage().tr("nukkit.start.invalid"));
            return;
        }
        if (!this.properties.get(ServerPropertiesKeys.ALLOW_SHADED, false) && isShaded) {
            log.error(getLanguage().tr("nukkit.start.shaded1"));
            log.error(getLanguage().tr("nukkit.start.shaded2"));
            log.error(getLanguage().tr("nukkit.start.shaded3"));
            return;
        }

        this.allowNether = this.properties.get(ServerPropertiesKeys.ALLOW_NETHER, true);
        this.allowTheEnd = this.properties.get(ServerPropertiesKeys.ALLOW_THE_END, true);
        this.useTerra = this.properties.get(ServerPropertiesKeys.USE_TERRA, false);
        this.checkLoginTime = this.properties.get(ServerPropertiesKeys.CHECK_LOGIN_TIME, false);

        log.info(this.getLanguage().tr("language.selected", getLanguage().getName(), getLanguage().getLang()));
        log.info(getLanguage().tr("nukkit.server.start", TextFormat.AQUA + this.getVersion() + TextFormat.RESET));

        String poolSize = settings.baseSettings().asyncWorkers();
        int poolSizeNumber;
        try {
            poolSizeNumber = Integer.parseInt(poolSize);
        } catch (Exception e) {
            poolSizeNumber = Math.max(Runtime.getRuntime().availableProcessors(), 4);
        }
        ServerScheduler.WORKERS = poolSizeNumber;
        this.scheduler = new ServerScheduler();

        ZlibChooser.setProvider(settings.networkSettings().zlibProvider());

        this.serverAuthoritativeMovementMode = switch (this.properties.get(ServerPropertiesKeys.SERVER_AUTHORITATIVE_MOVEMENT, "server-auth")) {
            case "client-auth" -> 0;
            case "server-auth" -> 1;
            case "server-auth-with-rewind" -> 2;
            default -> throw new IllegalArgumentException();
        };
        this.enabledNetworkEncryption = this.properties.get(ServerPropertiesKeys.NETWORK_ENCRYPTION, true);
        if (this.getSettings().baseSettings().waterdogpe()) {
            this.checkLoginTime = false;
        }

        if (this.properties.get(ServerPropertiesKeys.ENABLE_RCON, false)) {
            try {
                this.rcon = new RCON(this, this.properties.get(ServerPropertiesKeys.RCON_PASSWORD, ""), (!this.getIp().equals("")) ? this.getIp() : "0.0.0.0", this.getPort());
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
        this.maxPlayers = this.properties.get(ServerPropertiesKeys.MAX_PLAYERS, 20);
        this.setAutoSave(this.properties.get(ServerPropertiesKeys.AUTO_SAVE, true));
        if (this.properties.get(ServerPropertiesKeys.HARDCORE, false) && this.getDifficulty() < 3) {
            this.properties.get(ServerPropertiesKeys.DIFFICULTY, 3);
        }

        log.info(this.getLanguage().tr("nukkit.server.info", this.getName(), TextFormat.YELLOW + this.getNukkitVersion() + TextFormat.RESET + " (" + TextFormat.YELLOW + this.getGitCommit() + TextFormat.RESET + ")" + TextFormat.RESET, this.getApiVersion()));
        log.info(this.getLanguage().tr("nukkit.server.license"));
        this.consoleSender = new ConsoleCommandSender();

        // Initialize server performance and usage metrics
        NukkitMetrics.startNow(this);

        // Initialize the server's default
        {
            Registries.POTION.init();
            Registries.PACKET.init();
            Registries.ENTITY.init();
            Registries.BLOCKENTITY.init();
            Registries.BLOCKSTATE_ITEMMETA.init();
            Registries.BLOCKSTATE.init();
            Registries.ITEM_RUNTIMEID.init();
            Registries.BLOCK.init();
            Registries.ITEM.init();
            Registries.CREATIVE.init();
            Registries.BIOME.init();
            Registries.FUEL.init();
            Registries.GENERATOR.init();
            Registries.GENERATE_STAGE.init();
            Registries.EFFECT.init();
            Registries.RECIPE.init();
            Profession.init();
            String a = BlockTags.ACACIA;
            String b = ItemTags.ARROW;
            String c = BiomeTags.WARM;
            Updater d = BlockStateUpdaterBase.INSTANCE;
            Enchantment.init();
            Attribute.init();
            BlockComposter.init();
            DispenseBehaviorRegister.init();
        }

        // Load Terra generator if enabled in the server properties
        if (useTerra) {
            PNXPlatform instance = PNXPlatform.getInstance();
            if (instance == null) {
                log.warn("Failed to initialize Terra generator: PNXPlatform instance is null.");
            }
        }

        freezableArrayManager = new FreezableArrayManager(
                this.settings.freezeArraySettings().enable(),
                this.settings.freezeArraySettings().slots(),
                this.settings.freezeArraySettings().defaultTemperature(),
                this.settings.freezeArraySettings().freezingPoint(),
                this.settings.freezeArraySettings().absoluteZero(),
                this.settings.freezeArraySettings().boilingPoint(),
                this.settings.freezeArraySettings().melting(),
                this.settings.freezeArraySettings().singleOperation(),
                this.settings.freezeArraySettings().batchOperation());
        scoreboardManager = new ScoreboardManager(new JSONScoreboardStorage(commandDataPath + "/scoreboard.json"));
        functionManager = new FunctionManager(commandDataPath + "/functions");
        tickingAreaManager = new SimpleTickingAreaManager(new JSONTickingAreaStorage(this.dataPath + "worlds/"));

        // Convert legacy data before plugins get the chance to mess with it.
        try {
            playerDataDB = Iq80DBFactory.factory.open(new File(dataPath, "players"), new Options()
                    .createIfMissing(true)
                    .compressionType(CompressionType.ZLIB_RAW));
        } catch (IOException e) {
            log.error("", e);
            System.exit(1);
        }
        this.resourcePackManager = new ResourcePackManager(
                new ZippedResourcePackLoader(new File(Nukkit.DATA_PATH, "resource_packs")),
                new JarPluginResourcePackLoader(new File(this.pluginPath))
        );
        this.commandMap = new SimpleCommandMap(this);
        this.pluginManager = new PluginManager(this, this.commandMap);
        this.pluginManager.subscribeToPermission(Server.BROADCAST_CHANNEL_ADMINISTRATIVE, this.consoleSender);
        this.pluginManager.registerInterface(JavaPluginLoader.class);
        //this.pluginManager.registerInterface(JSPluginLoader.class);
        this.console.setExecutingCommands(true);

        try {
            log.debug("Loading position tracking service");
            this.positionTrackingService = new PositionTrackingService(new File(Nukkit.DATA_PATH, "services/position_tracking_db"));
        } catch (IOException e) {
            log.error("Failed to start the Position Tracking DB service!", e);
        }
        this.pluginManager.loadInternalPlugin();

        this.serverID = UUID.randomUUID();
        this.pluginManager.loadPlugins(this.pluginPath);
        {//trim
            Registries.POTION.trim();
            Registries.PACKET.trim();
            Registries.ENTITY.trim();
            Registries.BLOCKENTITY.trim();
            Registries.BLOCKSTATE_ITEMMETA.trim();
            Registries.BLOCKSTATE.trim();
            Registries.ITEM_RUNTIMEID.trim();
            Registries.BLOCK.trim();
            Registries.ITEM.trim();
            Registries.CREATIVE.trim();
            Registries.BIOME.trim();
            Registries.FUEL.trim();
            Registries.GENERATOR.trim();
            Registries.GENERATE_STAGE.trim();
            Registries.EFFECT.trim();
            Registries.RECIPE.trim();
        }

        this.enablePlugins(PluginLoadOrder.STARTUP);

        LevelProviderManager.addProvider("leveldb", LevelDBProvider.class);

        loadLevels();

        this.queryRegenerateEvent = new QueryRegenerateEvent(this, 5);
        this.network = new Network(this);
        this.getTickingAreaManager().loadAllTickingArea();

        this.properties.save();

        if (this.getDefaultLevel() == null) {
            log.error(this.getLanguage().tr("nukkit.level.defaultError"));
            this.forceShutdown();

            return;
        }

        this.autoSaveTicks = settings.baseSettings().autosave();

        this.enablePlugins(PluginLoadOrder.POSTWORLD);

        EntityProperty.buildPacketData();
        EntityProperty.buildPlayerProperty();

        if (settings.baseSettings().installSpark()) {
            SparkInstaller.initSpark(this);
        }

        if (!Boolean.parseBoolean(System.getProperty("disableWatchdog", "false"))) {
            this.watchdog = new Watchdog(this, 60000);//60s
            this.watchdog.start();
        }
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
        this.start();
    }

    private void loadLevels() {
        File file = new File(this.getDataPath() + "/worlds");
        if (!file.isDirectory()) throw new RuntimeException("worlds isn't directory");
        // Load all worlds from the `worlds` folder
        for (var f : Objects.requireNonNull(file.listFiles(File::isDirectory))) {
            LevelConfig levelConfig = getLevelConfig(f.getName());
            if (levelConfig != null && !levelConfig.enable()) {
                continue;
            }

            if (!this.loadLevel(f.getName())) {
                this.generateLevel(f.getName(), null);
            }
        }

        if (this.getDefaultLevel() == null) {
            String levelFolder = this.properties.get(ServerPropertiesKeys.LEVEL_NAME, "world");
            if (levelFolder == null || levelFolder.trim().isEmpty()) {
                log.warn("level-name cannot be null, using default");
                levelFolder = "world";
                this.properties.get(ServerPropertiesKeys.LEVEL_NAME, levelFolder);
            }

            if (!this.loadLevel(levelFolder)) {
                // Default world does not exist
                // Generate the default world
                HashMap<Integer, LevelConfig.GeneratorConfig> generatorConfig = new HashMap<>();
                // Spawn seed
                long seed;
                String seedString = String.valueOf(this.properties.get(ServerPropertiesKeys.LEVEL_SEED, System.currentTimeMillis()));
                try {
                    seed = Long.parseLong(seedString);
                } catch (NumberFormatException e) {
                    seed = seedString.hashCode();
                }
                //todo nether the_end overworld
                generatorConfig.put(0, new LevelConfig.GeneratorConfig("flat", seed, false, LevelConfig.AntiXrayMode.LOW, true, DimensionEnum.OVERWORLD.getDimensionData(), Collections.emptyMap()));
                LevelConfig levelConfig = new LevelConfig("leveldb", true, generatorConfig);
                this.generateLevel(levelFolder, levelConfig);
            }
            this.setDefaultLevel(this.getLevelByName(levelFolder + " Dim0"));
        }
    }
    // ─────────────────────────────────────────────────────
    // ──────── Start of [Lifecycle & Ticking Region] ──────
    // ─────────────────────────────────────────────────────

    /**
     * Reloads the server by saving the current state, disabling plugins, reloading properties, and re-enabling plugins.
     * This method performs the following steps:
     * 1. Saves all loaded levels.
     * 2. Saves the scoreboard manager state.
     * 3. Disables and clears all plugins.
     * 4. Clears all registered commands.
     * 5. Reloads server properties and updates the maximum number of players.
     * 6. Reloads ban lists and whitelist.
     * 7. Blocks IP addresses from the ban list.
     * 8. Registers the Java plugin loader.
     * 9. Reloads the scoreboard manager state.
     * 10. Reloads various registries.
     * 11. Loads plugins from the plugin path.
     * 12. Reloads the function manager.
     * 13. Enables plugins in the specified load order.
     * 14. Calls the ServerStartedEvent.
     */
    public void reload() {
        log.info("Reloading...");
        log.info("Saving levels...");

        // Save all loaded levels
        for (Level level : this.levelArray) {
            level.save();
        }

        // Save the scoreboard manager state
        this.scoreboardManager.save();

        // Disable and clear all plugins
        this.pluginManager.disablePlugins();
        this.pluginManager.clearPlugins();

        // Clear all registered commands
        this.commandMap.clearCommands();

        log.info("Reloading properties...");
        // Reload server properties
        this.properties.reload();
        this.maxPlayers = this.properties.get(ServerPropertiesKeys.MAX_PLAYERS, 20);
        if (this.properties.get(ServerPropertiesKeys.HARDCORE, false) && this.getDifficulty() < 3) {
            this.properties.get(ServerPropertiesKeys.DIFFICULTY, difficulty = 3);
        }

        // Reload ban lists and whitelist
        this.banByIP.load();
        this.banByName.load();
        this.reloadWhitelist();
        this.operators.reload();

        // Block IP addresses from the ban list
        for (BanEntry entry : this.getIPBans().getEntires().values()) {
            try {
                this.getNetwork().blockAddress(InetAddress.getByName(entry.getName()), -1);
            } catch (UnknownHostException e) {
                // ignore
            }
        }

        // Register the Java plugin loader
        this.pluginManager.registerInterface(JavaPluginLoader.class);
        // todo enable js plugin when adapt
        // JSIInitiator.reset();
        // JSFeatures.clearFeatures();
        // JSFeatures.initInternalFeatures();
        // this.pluginManager.registerInterface(JSPluginLoader.class);

        // Reload the scoreboard manager state
        this.scoreboardManager.read();

        log.info("Reloading Registries...");
        // Reload various registries
        {
            Registries.POTION.reload();
            Registries.PACKET.reload();
            Registries.ENTITY.reload();
            Registries.BLOCKENTITY.reload();
            Registries.BLOCKSTATE_ITEMMETA.reload();
            Registries.BLOCKSTATE.reload();
            Registries.ITEM_RUNTIMEID.reload();
            Registries.BLOCK.reload();
            Registries.ITEM.reload();
            Registries.CREATIVE.reload();
            Registries.BIOME.reload();
            Registries.FUEL.reload();
            Registries.GENERATOR.reload();
            Registries.GENERATE_STAGE.reload();
            Registries.EFFECT.reload();
            Registries.RECIPE.reload();
            Enchantment.reload();
        }

        // Load plugins from the plugin path
        this.pluginManager.loadPlugins(this.pluginPath);

        // Reload the function manager
        this.functionManager.reload();

        // Enable plugins in the specified load order
        this.enablePlugins(PluginLoadOrder.STARTUP);
        {
            // Trim various registries to remove unused entries
            Registries.POTION.trim();
            Registries.PACKET.trim();
            Registries.ENTITY.trim();
            Registries.BLOCKENTITY.trim();
            Registries.BLOCKSTATE_ITEMMETA.trim();
            Registries.BLOCKSTATE.trim();
            Registries.ITEM_RUNTIMEID.trim();
            Registries.BLOCK.trim();
            Registries.ITEM.trim();
            Registries.CREATIVE.trim();
            Registries.BIOME.trim();
            Registries.FUEL.trim();
            Registries.GENERATOR.trim();
            Registries.GENERATE_STAGE.trim();
            Registries.EFFECT.trim();
            Registries.RECIPE.trim();
        }
        this.enablePlugins(PluginLoadOrder.POSTWORLD);

        // Call the ServerStartedEvent
        ServerStartedEvent serverStartedEvent = new ServerStartedEvent();
        getPluginManager().callEvent(serverStartedEvent);
    }

    /**
     * Shuts down the server by setting the running state to false.
     */
    public void shutdown() {
        isRunning.compareAndSet(true, false);
    }

    /**
     * Forcefully shuts down the server by stopping all tasks, saving data, and closing network interfaces.
     * This method performs the following steps:
     * 1. Sets the running state to false.
     * 2. Calls the ServerStopEvent.
     * 3. Closes the RCON connection if it exists.
     * 4. Closes all player connections with a shutdown message.
     * 5. Saves server settings.
     * 6. Disables all plugins.
     * 7. Removes all event handlers.
     * 8. Saves scoreboard data.
     * 9. Stops all scheduled tasks.
     * 10. Unloads all levels.
     * 11. Closes the position tracking service if it exists.
     * 12. Closes the console thread.
     * 13. Shuts down network interfaces.
     * 14. Closes the player data database.
     * 15. Stops the watchdog and metrics.
     * 16. Shuts down the compute thread pool.
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
                player.close(player.getLeaveMessage(), getSettings().baseSettings().shutdownMessage());
            }

            this.getSettings().save();

            log.debug("Disabling all plugins");
            this.pluginManager.disablePlugins();

            log.debug("Removing event handlers");
            HandlerList.unregisterAll();

            log.debug("Saving scoreboards data");
            this.scoreboardManager.save();

            log.debug("Stopping all tasks");
            this.scheduler.cancelAllTasks();
            this.scheduler.mainThreadHeartbeat((int) (this.getNextTick() + 10000));

            log.debug("Unloading all levels");
            for (Level level : this.levelArray) {
                this.unloadLevel(level, true);
                while(level.isThreadRunning()) Thread.sleep(1);
            }
            if (positionTrackingService != null) {
                log.debug("Closing position tracking service");
                positionTrackingService.close();
            }

            log.debug("Closing console");
            this.consoleThread.interrupt();

            log.debug("Stopping network interfaces");
            network.shutdown();
            playerDataDB.close();
            // Close the watchdog and stop the metrics
            if (this.watchdog != null) {
                this.watchdog.running = false;
            }
            NukkitMetrics.closeNow(this);
            // Close the computeThreadPool to ensure all tasks are completed and resources are released
            ForkJoinPool.commonPool().shutdownNow();
            this.computeThreadPool.shutdownNow();
            //todo other things
        } catch (Exception e) {
            log.error("Exception happened while shutting down, exiting the process", e);
            System.exit(1);
        }
    }

    /**
     * Starts the server by initializing network interfaces, setting the tick counter, and calling the ServerStartedEvent.
     * This method performs the following steps:
     * 1. Blocks IP addresses from the ban list.
     * 2. Initializes the tick counter.
     * 3. Logs the default game mode and network start information.
     * 4. Calls the ServerStartedEvent.
     * 5. Starts the tick processor.
     * 6. Forcefully shuts down the server.
     */
    public void start() {
        for (BanEntry entry : this.getIPBans().getEntires().values()) {
            try {
                this.network.blockAddress(InetAddress.getByName(entry.getName()));
            } catch (UnknownHostException ignore) {
            }
        }
        this.tickCounter = 0;

        log.info(this.getLanguage().tr("nukkit.server.defaultGameMode", getGamemodeString(this.getGamemode(), true)));
        log.info(this.getLanguage().tr("nukkit.server.networkStart", TextFormat.YELLOW + (this.getIp().isEmpty() ? "*" : this.getIp()), TextFormat.YELLOW + String.valueOf(this.getPort())));
        log.info(this.getLanguage().tr("nukkit.server.startFinished", String.valueOf((double) (System.currentTimeMillis() - Nukkit.START_TIME) / 1000)));

        ServerStartedEvent serverStartedEvent = new ServerStartedEvent();
        getPluginManager().callEvent(serverStartedEvent);
        this.tickProcessor();
        this.forceShutdown();
    }

    /**
     * Processes server ticks by scheduling a garbage collection task and continuously ticking the server.
     * This method performs the following steps:
     * 1. Schedules a garbage collection task to run after 60 ticks.
     * 2. Continuously ticks the server while it is running.
     * 3. Sleeps for the allocated time to ensure consistent tick intervals.
     */
    public void tickProcessor() {
        getScheduler().scheduleDelayedTask(InternalPlugin.INSTANCE, new Task() {
            @Override
            public void onRun(int currentTick) {
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
                        if (allocated > 0) {
                            // \@SuppressWarnings("BusyWait") - Intentional busy wait to ensure consistent tick intervals
                            Thread.sleep(allocated, 900000);
                        }
                    }
                } catch (RuntimeException e) {
                    log.error("A RuntimeException happened while ticking the server", e);
                }
            }
        } catch (Throwable e) {
            log.error("Exception happened while ticking server\n{}", Utils.getAllThreadDumps(), e);
        }
    }

    /**
     * Checks and updates the tick rate for each level based on the current tick and tick time.
     * This method performs the following steps:
     * 1. Updates players if alwaysTickPlayers is enabled.
     * 2. Processes ticks for each level.
     * 3. Adjusts the tick rate for each level based on the tick time.
     *
     * @param currentTick The current tick count.
     */
    private void checkTickUpdates(int currentTick) {
        if (getSettings().levelSettings().alwaysTickPlayers()) {
            for (Player p : new ArrayList<>(this.players.values())) {
                p.onUpdate(currentTick);
            }
        }

        int baseTickRate = getSettings().levelSettings().baseTickRate();
        //Do level ticks if level threading is disabled
        if(!this.getSettings().levelSettings().levelThread()) {
            for (Level level : this.getLevels().values()) {
                if (level.getTickRate() > baseTickRate && --level.tickRateCounter > 0) {
                    continue;
                }

                try {
                    long levelTime = System.currentTimeMillis();
                    //Ensures that the server won't try to tick a level without providers.
                    if (level.getProvider().getLevel() == null) {
                        log.warn("Tried to tick Level " + level.getName() + " without a provider!");
                        continue;
                    }
                    level.doTick(currentTick);
                    int tickMs = (int) (System.currentTimeMillis() - levelTime);
                    level.tickRateTime = tickMs;
                    if ((currentTick & 511) == 0) { // % 511
                        level.tickRateOptDelay = level.recalcTickOptDelay();
                    }

                    if (getSettings().levelSettings().autoTickRate()) {
                        if (tickMs < 50 && level.getTickRate() > baseTickRate) {
                            int r;
                            level.setTickRate(r = level.getTickRate() - 1);
                            if (r > baseTickRate) {
                                level.tickRateCounter = level.getTickRate();
                            }
                            log.debug("Raising level \"{}\" tick rate to {} ticks", level.getName(), level.getTickRate());
                        } else if (tickMs >= 50) {
                            int autoTickRateLimit = getSettings().levelSettings().autoTickRateLimit();
                            if (level.getTickRate() == baseTickRate) {
                                level.setTickRate(Math.max(baseTickRate + 1, Math.min(autoTickRateLimit, tickMs / 50)));
                                log.debug("Level \"{}\" took {}ms, setting tick rate to {} ticks", level.getName(), NukkitMath.round(tickMs, 2), level.getTickRate());
                            } else if ((tickMs / level.getTickRate()) >= 50 && level.getTickRate() < autoTickRateLimit) {
                                level.setTickRate(level.getTickRate() + 1);
                                log.debug("Level \"{}\" took {}ms, setting tick rate to {} ticks", level.getName(), NukkitMath.round(tickMs, 2), level.getTickRate());
                            }
                            level.tickRateCounter = level.getTickRate();
                        }
                    }
                } catch (Exception e) {
                    log.error(this.getLanguage().tr("nukkit.level.tickError",
                            level.getFolderPath(), Utils.getExceptionMessage(e)), e);
                }
            }
        }
    }

    /**
     * Performs an automatic save of all online players, levels, and the scoreboard manager.
     * <p>
     * This method checks if auto-save is enabled and then iterates through all players and levels,
     * saving their current state. It also saves the state of the scoreboard manager.
     */
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

    /**
     * Main server tick method, responsible for processing server ticks and managing tick timing.
     * <p>
     * This method performs various operations including processing network interfaces, checking RCON,
     * updating players and levels, handling auto-save, and managing tick timing to ensure consistent intervals.
     */
    private void tick() {
        long tickTime = System.currentTimeMillis();

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
            return;
        }

        ++this.tickCounter;
        this.network.processInterfaces();

        if (this.rcon != null) {
            this.rcon.check();
        }

        this.getScheduler().mainThreadHeartbeat(this.tickCounter);

        this.checkTickUpdates(this.tickCounter);

        if ((this.tickCounter & 0b1111) == 0) {
            this.titleTick();
            this.maxTick = 20;
            this.maxUse = 0;

            if ((this.tickCounter & 0b111111111) == 0) {
                try {
                    this.getPluginManager().callEvent(this.queryRegenerateEvent = new QueryRegenerateEvent(this, 5));
                } catch (Exception e) {
                    log.error("", e);
                }
            }
        }

        if (this.autoSave && ++this.autoSaveTicker >= this.autoSaveTicks) {
            this.autoSaveTicker = 0;
            this.doAutoSave();
        }

        if (this.sendUsageTicker > 0 && --this.sendUsageTicker == 0) {
            this.sendUsageTicker = 6000;
            //todo sendUsage
        }

        // Handling freezable arrays
        int freezableArrayCompressTime = (int) (50 - (System.currentTimeMillis() - tickTime));
        if (freezableArrayCompressTime > 4) {
            getFreezableArrayManager().setMaxCompressionTime(freezableArrayCompressTime).tick();
        }

        long nowNano = System.nanoTime();
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
    }

    /**
     * Retrieves the time of the next scheduled tick.
     *
     * @return The time of the next scheduled tick in milliseconds.
     */
    public long getNextTick() {
        return nextTick;
    }

    /**
     * Retrieves the number of ticks recorded by the server.
     *
     * @return The number of ticks recorded by the server.
     */
    public int getTick() {
        return tickCounter;
    }

    /**
     * Retrieves the current ticks per second (TPS) of the server.
     *
     * @return The current TPS of the server.
     */
    public float getTicksPerSecond() {
        return ((float) Math.round(this.maxTick * 100)) / 100;
    }

    /**
     * Retrieves the average ticks per second (TPS) of the server.
     *
     * @return The average TPS of the server.
     */
    public float getTicksPerSecondAverage() {
        float sum = 0;
        int count = this.tickAverage.length;
        for (float aTickAverage : this.tickAverage) {
            sum += aTickAverage;
        }
        return (float) NukkitMath.round(sum / count, 2);
    }

    /**
     * Retrieves the current tick usage percentage of the server.
     *
     * @return The current tick usage percentage.
     */
    public float getTickUsage() {
        return (float) NukkitMath.round(this.maxUse * 100, 2);
    }

    /**
     * Retrieves the average tick usage percentage of the server.
     *
     * @return The average tick usage percentage.
     */
    public float getTickUsageAverage() {
        float sum = 0;
        int count = this.useAverage.length;
        for (float aUseAverage : this.useAverage) {
            sum += aUseAverage;
        }
        return ((float) Math.round(sum / count * 100)) / 100;
    }

    /**
     * Updates the console title with server statistics.
     * <p>
     * This method updates the console title with information such as server version, online players,
     * memory usage, TPS, and network usage.
     */
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

    /**
     * Checks if the server is currently running.
     *
     * @return True if the server is running, false otherwise.
     */
    public boolean isRunning() {
        return isRunning.get();
    }

    /**
     * Sets the server to a busy state, which can prevent related code from considering the server as unresponsive.
     * Please remember to clear it after setting.
     *
     * @param busyTime Time in milliseconds
     * @return The index of the busy state in the busying time list.
     */
    public int addBusying(long busyTime) {
        this.busyingTime.add(busyTime);
        return this.busyingTime.size() - 1;
    }

    /**
     * Removes the busy state at the specified index.
     *
     * @param index The index of the busy state to be removed.
     */
    public void removeBusying(int index) {
        this.busyingTime.removeLong(index);
    }

    /**
     * Retrieves the most recent busying time.
     *
     * @return The most recent busying time in milliseconds, or -1 if there are no busying times.
     */
    public long getBusyingTime() {
        if (this.busyingTime.isEmpty()) {
            return -1;
        }
        return this.busyingTime.getLong(this.busyingTime.size() - 1);
    }

    /**
     * Retrieves the ticking area manager.
     *
     * @return The TickingAreaManager instance.
     */
    public TickingAreaManager getTickingAreaManager() {
        return tickingAreaManager;
    }

    /**
     * Retrieves the server launch time.
     *
     * @return The server launch time in milliseconds.
     */
    public long getLaunchTime() {
        return launchTime;
    }

    // ─────────────────────────────────────────────────────
    // ──────── Start of [Server Singleton Region] ─────────
    // ─────────────────────────────────────────────────────

    /**
     * Retrieves the singleton instance of the Server.
     *
     * @return The singleton instance of the Server.
     */
    public static Server getInstance() {
        return instance;
    }

    // ─────────────────────────────────────────────────────
    // ──────── Start of [Chat & Commands Region] ──────────
    // ─────────────────────────────────────────────────────

    /**
     * Broadcasts a message to all players.
     *
     * @param message The message to be broadcasted.
     * @return The number of players who received the message.
     */
    public int broadcastMessage(String message) {
        return this.broadcast(message, BROADCAST_CHANNEL_USERS);
    }

    /**
     * Broadcasts a message to all players.
     *
     * @param message The message to be broadcasted.
     * @return The number of players who received the message.
     * @see #broadcastMessage(String)
     */
    public int broadcastMessage(TextContainer message) {
        return this.broadcast(message, BROADCAST_CHANNEL_USERS);
    }

    /**
     * Broadcasts a message to the specified recipients.
     *
     * @param message    The message to be broadcasted.
     * @param recipients The array of CommandSender recipients.
     * @return The number of recipients who received the message.
     */
    public int broadcastMessage(String message, CommandSender[] recipients) {
        for (CommandSender recipient : recipients) {
            recipient.sendMessage(message);
        }

        return recipients.length;
    }

    /**
     * Broadcasts a message to the specified recipients.
     *
     * @param message    The message to be broadcasted.
     * @param recipients The collection of CommandSender recipients.
     * @return The number of recipients who received the message.
     * @see #broadcastMessage(String, CommandSender[])
     */
    public int broadcastMessage(String message, Collection<? extends CommandSender> recipients) {
        for (CommandSender recipient : recipients) {
            recipient.sendMessage(message);
        }

        return recipients.size();
    }

    /**
     * Broadcasts a message to the specified recipients.
     *
     * @param message    The message to be broadcasted.
     * @param recipients The collection of CommandSender recipients.
     * @return The number of recipients who received the message.
     * @see #broadcastMessage(String, CommandSender[])
     */
    public int broadcastMessage(TextContainer message, Collection<? extends CommandSender> recipients) {
        for (CommandSender recipient : recipients) {
            recipient.sendMessage(message);
        }

        return recipients.size();
    }

    /**
     * Broadcasts a message to recipients with the specified permissions.
     *
     * @param message     The message content to be broadcasted.
     * @param permissions The permissions required to receive the message, separated by semicolons.
     * @return The number of recipients who received the message.
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
     * Broadcasts a message to recipients with the specified permissions.
     *
     * @param message     The message content to be broadcasted.
     * @param permissions The permissions required to receive the message, separated by semicolons.
     * @return The number of recipients who received the message.
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

    /**
     * Executes a command as the specified sender.
     *
     * @param sender      The command executor.
     * @param commandLine The command to be executed.
     * @return 0 if the execution failed, 1 or greater if the execution succeeded.
     * @throws ServerException If the sender is invalid or an error occurs during execution.
     */
    public int executeCommand(CommandSender sender, String commandLine) throws ServerException {
        // First we need to check if this command is on the main thread or not, if not, merge it in the main thread.
        if (!this.isPrimaryThread()) {
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
     * Executes commands silently as the console, ignoring permissions.
     *
     * @param commands The commands to be executed.
     * @throws ServerException If an error occurs during execution.
     */
    public void silentExecuteCommand(String... commands) {
        this.silentExecuteCommand(null, commands);
    }

    /**
     * Executes commands silently as the specified player, ignoring permissions.
     *
     * @param sender   The command sender.
     * @param commands The commands to be executed.
     * @throws ServerException If an error occurs during execution.
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
     * Retrieves the console sender.
     *
     * @return The ConsoleCommandSender instance.
     */
    public ConsoleCommandSender getConsoleSender() {
        return consoleSender;
    }

    /**
     * Retrieves the command map.
     *
     * @return The SimpleCommandMap instance.
     */
    public SimpleCommandMap getCommandMap() {
        return commandMap;
    }

    /**
     * Retrieves the plugin command by name.
     *
     * @param name The name of the command.
     * @return The PluginIdentifiableCommand instance, or null if not found.
     */
    public PluginIdentifiableCommand getPluginCommand(String name) {
        Command command = this.commandMap.getCommand(name);
        if (command instanceof PluginIdentifiableCommand) {
            return (PluginIdentifiableCommand) command;
        } else {
            return null;
        }
    }

    /**
     * Retrieves the scoreboard manager.
     *
     * @return The IScoreboardManager instance.
     */
    public IScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    /**
     * Retrieves the function manager.
     *
     * @return The FunctionManager instance.
     */
    public FunctionManager getFunctionManager() {
        return functionManager;
    }

    // ─────────────────────────────────────────────────────
    // ──────── Start of [Networking Region] ───────────────
    // ─────────────────────────────────────────────────────

    /**
     * Broadcasts a data packet to a collection of players.
     *
     * @param players The collection of players to receive the data packet.
     * @param packet  The data packet to be broadcasted.
     * @see #broadcastPacket(Player[], DataPacket)
     */
    public static void broadcastPacket(Collection<Player> players, DataPacket packet) {
        for (Player player : players) {
            player.dataPacket(packet);
        }
    }

    /**
     * Broadcasts a data packet to an array of players.
     *
     * @param players The array of players to receive the data packet.
     * @param packet  The data packet to be broadcasted.
     */
    public static void broadcastPacket(Player[] players, DataPacket packet) {
        for (Player player : players) {
            player.dataPacket(packet);
        }
    }

    /**
     * Retrieves the current query information event.
     *
     * @return The current QueryRegenerateEvent instance.
     */
    public QueryRegenerateEvent getQueryInformation() {
        return this.queryRegenerateEvent;
    }

    /**
     * Retrieves the network instance.
     *
     * @return The Network instance.
     */
    public Network getNetwork() {
        return network;
    }

    // ─────────────────────────────────────────────────────
    // ──────── Start of [Plugins Region] ──────────────────
    // ─────────────────────────────────────────────────────

    /**
     * Enables plugins in the specified plugin loading order.
     *
     * @param type The plugin loading order.
     */
    public void enablePlugins(PluginLoadOrder type) {
        for (Plugin plugin : new ArrayList<>(this.pluginManager.getPlugins().values())) {
            if (!plugin.isEnabled() && type == plugin.getDescription().getOrder()) {
                this.enablePlugin(plugin);
            }
        }

        if (type == PluginLoadOrder.POSTWORLD) {
            DefaultPermissions.registerCorePermissions();
        }
    }

    /**
     * Enables a specified plugin.
     *
     * @param plugin The plugin instance to enable.
     */
    public void enablePlugin(Plugin plugin) {
        this.pluginManager.enablePlugin(plugin);
    }

    /**
     * Disables all plugins.
     */
    public void disablePlugins() {
        this.pluginManager.disablePlugins();
    }

    /**
     * Retrieves the plugin manager.
     *
     * @return The plugin manager instance.
     */
    public PluginManager getPluginManager() {
        return this.pluginManager;
    }

    /**
     * Retrieves the service manager.
     *
     * @return The service manager instance.
     */
    public ServiceManager getServiceManager() {
        return serviceManager;
    }

    // ─────────────────────────────────────────────────────
    // ──────── Start of [Players Region] ──────────────────
    // ─────────────────────────────────────────────────────

    /**
     * Completes the login sequence for a player by sending the full player list data.
     *
     * @param player The player who has completed the login sequence.
     */
    public void onPlayerCompleteLoginSequence(Player player) {
        this.sendFullPlayerListData(player);
    }

    /**
     * Handles the player login process, including event calling and player registration.
     *
     * @param socketAddress The socket address of the player.
     * @param player        The player who is logging in.
     */
    public void onPlayerLogin(InetSocketAddress socketAddress, Player player) {
        PlayerLoginEvent ev;
        this.getPluginManager().callEvent(ev = new PlayerLoginEvent(player, "Plugin reason"));
        if (ev.isCancelled()) {
            player.close(player.getLeaveMessage(), ev.getKickMessage());
            return;
        }

        this.players.put(socketAddress, player);
        if (this.sendUsageTicker > 0) {
            this.uniquePlayers.add(player.getUniqueId());
        }
    }

    /**
     * Adds a player to the list of online players and updates the player list data.
     *
     * @param player The player to add to the online players list.
     */
    @ApiStatus.Internal
    public void addOnlinePlayer(Player player) {
        this.playerList.put(player.getUniqueId(), player);
        this.updatePlayerListData(player.getUniqueId(), player.getId(), player.getDisplayName(), player.getSkin(), player.getLoginChainData().getXUID());
        this.getNetwork().getPong().playerCount(playerList.size()).update();
    }

    /**
     * Removes a player from the list of online players and updates the player list data.
     *
     * @param player The player to remove from the online players list.
     */
    @ApiStatus.Internal
    public void removeOnlinePlayer(Player player) {
        if (this.playerList.containsKey(player.getUniqueId())) {
            this.playerList.remove(player.getUniqueId());

            PlayerListPacket pk = new PlayerListPacket();
            pk.type = PlayerListPacket.TYPE_REMOVE;
            pk.entries = new PlayerListPacket.Entry[]{new PlayerListPacket.Entry(player.getUniqueId())};

            Server.broadcastPacket(this.playerList.values(), pk);
            this.getNetwork().getPong().playerCount(playerList.size()).update();
        }
    }

    /**
     * Updates the player list data for all players with the specified player's information.
     *
     * @param uuid     The UUID of the player.
     * @param entityId The entity ID of the player.
     * @param name     The name of the player.
     * @param skin     The skin of the player.
     */
    public void updatePlayerListData(UUID uuid, long entityId, String name, Skin skin) {
        this.updatePlayerListData(uuid, entityId, name, skin, "", this.playerList.values());
    }

    /**
     * Updates the player list data for all players with the specified player's information, including Xbox user ID.
     *
     * @param uuid       The UUID of the player.
     * @param entityId   The entity ID of the player.
     * @param name       The name of the player.
     * @param skin       The skin of the player.
     * @param xboxUserId The Xbox user ID of the player.
     */
    public void updatePlayerListData(UUID uuid, long entityId, String name, Skin skin, String xboxUserId) {
        this.updatePlayerListData(uuid, entityId, name, skin, xboxUserId, this.playerList.values());
    }

    /**
     * Updates the player list data for the specified players with the specified player's information.
     *
     * @param uuid     The UUID of the player.
     * @param entityId The entity ID of the player.
     * @param name     The name of the player.
     * @param skin     The skin of the player.
     * @param players  The players to receive the updated player list data.
     */
    public void updatePlayerListData(UUID uuid, long entityId, String name, Skin skin, Player[] players) {
        this.updatePlayerListData(uuid, entityId, name, skin, "", players);
    }

    /**
     * Updates the player list data for the specified players with the specified player's information, including Xbox user ID.
     *
     * @param uuid       The UUID of the player.
     * @param entityId   The entity ID of the player.
     * @param name       The name of the player.
     * @param skin       The skin of the player.
     * @param xboxUserId The Xbox user ID of the player.
     * @param players    The players to receive the updated player list data.
     */
    public void updatePlayerListData(UUID uuid, long entityId, String name, Skin skin, String xboxUserId, Player[] players) {
        // In some circumstances, the game sends confidential data in this string,
        // so under no circumstances should it be sent to all players on the server.
        // @Zwuiix
        skin.setSkinId("");

        PlayerListPacket pk = new PlayerListPacket();
        pk.type = PlayerListPacket.TYPE_ADD;
        pk.entries = new PlayerListPacket.Entry[]{new PlayerListPacket.Entry(uuid, entityId, name, skin, xboxUserId)};
        Server.broadcastPacket(players, pk);
    }

    /**
     * Updates the player list data for the specified players with the specified player's information, including Xbox user ID.
     *
     * @param uuid       The UUID of the player.
     * @param entityId   The entity ID of the player.
     * @param name       The name of the player.
     * @param skin       The skin of the player.
     * @param xboxUserId The Xbox user ID of the player.
     * @param players    The players to receive the updated player list data.
     */
    public void updatePlayerListData(UUID uuid, long entityId, String name, Skin skin, String xboxUserId, Collection<Player> players) {
        // In some circumstances, the game sends confidential data in this string,
        // so under no circumstances should it be sent to all players on the server.
        // @Zwuiix
        skin.setSkinId("");
        this.updatePlayerListData(uuid, entityId, name, skin, xboxUserId, players.toArray(Player.EMPTY_ARRAY));
    }

    /**
     * Removes the player list data for all players.
     *
     * @param uuid The UUID of the player to remove from the player list data.
     */
    public void removePlayerListData(UUID uuid) {
        this.removePlayerListData(uuid, this.playerList.values());
    }

    /**
     * Removes the player list data for the specified players.
     *
     * @param uuid    The UUID of the player to remove from the player list data.
     * @param players The players to remove the player list data from.
     */
    public void removePlayerListData(UUID uuid, Player[] players) {
        PlayerListPacket pk = new PlayerListPacket();
        pk.type = PlayerListPacket.TYPE_REMOVE;
        pk.entries = new PlayerListPacket.Entry[]{new PlayerListPacket.Entry(uuid)};
        Server.broadcastPacket(players, pk);
    }

    /**
     * Removes the player list data for the specified player.
     *
     * @param uuid   The UUID of the player to remove from the player list data.
     * @param player The player to remove the player list data from.
     */
    public void removePlayerListData(UUID uuid, Player player) {
        PlayerListPacket pk = new PlayerListPacket();
        pk.type = PlayerListPacket.TYPE_REMOVE;
        pk.entries = new PlayerListPacket.Entry[]{new PlayerListPacket.Entry(uuid)};
        player.dataPacket(pk);
    }

    /**
     * Removes the player list data for the specified players.
     *
     * @param uuid    The UUID of the player to remove from the player list data.
     * @param players The players to remove the player list data from.
     */
    public void removePlayerListData(UUID uuid, Collection<Player> players) {
        this.removePlayerListData(uuid, players.toArray(Player.EMPTY_ARRAY));
    }

    /**
     * Sends the full player list data to the specified player.
     *
     * @param player The player to send the full player list data to.
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
     * Retrieves the player instance from the specified UUID.
     *
     * @param uuid The UUID of the player.
     * @return An Optional containing the Player instance, or empty if not found.
     */
    public Optional<Player> getPlayer(UUID uuid) {
        Preconditions.checkNotNull(uuid, "uuid");
        return Optional.ofNullable(playerList.get(uuid));
    }

    /**
     * Finds the UUID corresponding to the specified player name from the database.
     *
     * @param name The name of the player.
     * @return An Optional containing the player's UUID, or empty if not found.
     */
    public Optional<UUID> lookupName(String name) {
        byte[] nameBytes = name.toLowerCase(Locale.ENGLISH).getBytes(StandardCharsets.UTF_8);
        byte[] uuidBytes = playerDataDB.get(nameBytes);
        if (uuidBytes == null) {
            return Optional.empty();
        }

        if (uuidBytes.length != 16) {
            log.warn("Invalid UUID in name lookup database detected! Removing");
            playerDataDB.delete(nameBytes);
            return Optional.empty();
        }

        ByteBuffer buffer = ByteBuffer.wrap(uuidBytes);
        return Optional.of(new UUID(buffer.getLong(), buffer.getLong()));
    }

    /**
     * Updates the UUID of the specified player name in the database, or adds it if it does not exist.
     *
     * @param info The player info containing the UUID and username.
     */
    void updateName(PlayerInfo info) {
        var uniqueId = info.getUniqueId();
        var name = info.getUsername();

        byte[] nameBytes = name.toLowerCase(Locale.ENGLISH).getBytes(StandardCharsets.UTF_8);

        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.putLong(uniqueId.getMostSignificantBits());
        buffer.putLong(uniqueId.getLeastSignificantBits());
        byte[] array = buffer.array();
        byte[] bytes = playerDataDB.get(array);
        if (bytes == null) {
            playerDataDB.put(nameBytes, array);
        }
        boolean xboxAuthEnabled = this.properties.get(ServerPropertiesKeys.XBOX_AUTH, false);
        if (info instanceof XboxLivePlayerInfo || !xboxAuthEnabled) {
            playerDataDB.put(nameBytes, array);
        }
    }

    /**
     * Retrieves an offline player by name.
     *
     * @param name The name of the player.
     * @return An IPlayer instance representing the offline player.
     */
    public IPlayer getOfflinePlayer(final String name) {
        IPlayer result = this.getPlayerExact(name.toLowerCase(Locale.ENGLISH));
        if (result != null) {
            return result;
        }

        return lookupName(name).map(uuid -> new OfflinePlayer(this, uuid))
                .orElse(new OfflinePlayer(this, name));
    }

    /**
     * Retrieves a player instance from the specified UUID, either online or offline.
     *
     * @param uuid The UUID of the player.
     * @return An IPlayer instance representing the player.
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
     * Retrieves the NBT data of the player specified by UUID.
     * If the data does not exist, it will not be created.
     *
     * @param uuid The UUID of the player.
     * @return The CompoundTag containing the player's data.
     */
    public CompoundTag getOfflinePlayerData(UUID uuid) {
        return getOfflinePlayerData(uuid, false);
    }

    /**
     * Retrieves the NBT data of the player specified by UUID.
     * If the data does not exist and the create flag is true, it will create a new entry.
     *
     * @param uuid   The UUID of the player.
     * @param create Whether to create a new entry if the data does not exist.
     * @return The CompoundTag containing the player's data.
     */
    public CompoundTag getOfflinePlayerData(UUID uuid, boolean create) {
        return getOfflinePlayerDataInternal(uuid, create);
    }

    /**
     * Retrieves the NBT data of the player specified by name.
     * If the data does not exist, it will not be created.
     *
     * @param name The name of the player.
     * @return The CompoundTag containing the player's data.
     */
    public CompoundTag getOfflinePlayerData(String name) {
        return getOfflinePlayerData(name, false);
    }

    /**
     * Retrieves the NBT data of the player specified by name.
     * If the data does not exist and the create flag is true, it will create a new entry.
     *
     * @param name   The name of the player.
     * @param create Whether to create a new entry if the data does not exist.
     * @return The CompoundTag containing the player's data, or null if not found and not created.
     */
    public CompoundTag getOfflinePlayerData(String name, boolean create) {
        Optional<UUID> uuid = lookupName(name);
        if (uuid.isEmpty()) {
            log.warn("Invalid UUID in name lookup database detected! Removing");
            playerDataDB.delete(name.getBytes(StandardCharsets.UTF_8));
            return null;
        }
        return getOfflinePlayerDataInternal(uuid.get(), create);
    }

    /**
     * Checks if the offline player data exists for the specified name.
     *
     * @param name The name of the player.
     * @return True if the data exists, false otherwise.
     */
    public boolean hasOfflinePlayerData(String name) {
        Optional<UUID> uuid = lookupName(name);
        if (uuid.isEmpty()) {
            log.warn("Invalid UUID in name lookup database detected! Removing");
            playerDataDB.delete(name.getBytes(StandardCharsets.UTF_8));
            return false;
        }
        return hasOfflinePlayerData(uuid.get());
    }

    /**
     * Checks if the offline player data exists for the specified UUID.
     *
     * @param uuid The UUID of the player.
     * @return True if the data exists, false otherwise.
     */
    public boolean hasOfflinePlayerData(UUID uuid) {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.putLong(uuid.getMostSignificantBits());
        buffer.putLong(uuid.getLeastSignificantBits());
        byte[] bytes = playerDataDB.get(buffer.array());
        return bytes != null;
    }

    /**
     * Retrieves the offline player data for the specified UUID.
     * If the data does not exist and the `create` flag is true, it will create a new entry.
     *
     * @param uuid   The UUID of the player.
     * @param create Whether to create a new entry if the data does not exist.
     * @return The CompoundTag containing the player's data, or null if not found and not created.
     */
    private CompoundTag getOfflinePlayerDataInternal(UUID uuid, boolean create) {
        if (uuid == null) {
            log.error("UUID is empty, cannot query player data");
            return null;
        }
        try {
            ByteBuffer buffer = ByteBuffer.allocate(16);
            buffer.putLong(uuid.getMostSignificantBits());
            buffer.putLong(uuid.getLeastSignificantBits());
            byte[] bytes = playerDataDB.get(buffer.array());
            if (bytes != null) {
                return NBTIO.readCompressed(bytes);
            }
        } catch (IOException e) {
            log.warn(this.getLanguage().tr("nukkit.data.playerCorrupted", uuid), e);
        }

        if (create) {
            if (this.getSettings().playerSettings().savePlayerData()) {
                log.info(this.getLanguage().tr("nukkit.data.playerNotFound", uuid));
            }
            Position spawn = this.getDefaultLevel().getSafeSpawn();
            CompoundTag nbt = new CompoundTag()
                    .putLong("firstPlayed", System.currentTimeMillis() / 1000)
                    .putLong("lastPlayed", System.currentTimeMillis() / 1000)
                    .putList("Pos", new ListTag<DoubleTag>()
                            .add(new DoubleTag(spawn.x))
                            .add(new DoubleTag(spawn.y))
                            .add(new DoubleTag(spawn.z)))
                    .putString("Level", this.getDefaultLevel().getName())
                    .putList("Inventory", new ListTag<>())
                    .putCompound("Achievements", new CompoundTag())
                    .putInt("playerGameType", this.getGamemode())
                    .putList("Motion", new ListTag<DoubleTag>()
                            .add(new DoubleTag(0))
                            .add(new DoubleTag(0))
                            .add(new DoubleTag(0)))
                    .putList("Rotation", new ListTag<FloatTag>()
                            .add(new FloatTag(0))
                            .add(new FloatTag(0)))
                    .putFloat("FallDistance", 0)
                    .putShort("Fire", 0)
                    .putShort("Air", 300)
                    .putBoolean("OnGround", true)
                    .putBoolean("Invulnerable", false);

            this.saveOfflinePlayerData(uuid, nbt, true);
            return nbt;
        } else {
            log.error("Player {} does not exist and cannot read playerdata", uuid);
            return null;
        }
    }

    /**
     * Saves the offline player data for the specified UUID.
     *
     * @param uuid The UUID of the player.
     * @param tag  The CompoundTag containing the player's data.
     */
    public void saveOfflinePlayerData(UUID uuid, CompoundTag tag) {
        this.saveOfflinePlayerData(uuid, tag, false);
    }

    /**
     * Saves the offline player data for the specified UUID, optionally asynchronously.
     *
     * @param uuid  The UUID of the player.
     * @param tag   The CompoundTag containing the player's data.
     * @param async Whether to save the data asynchronously.
     */
    public void saveOfflinePlayerData(UUID uuid, CompoundTag tag, boolean async) {
        this.saveOfflinePlayerData(uuid.toString(), tag, async);
    }

    /**
     * Saves the offline player data for the specified name.
     *
     * @param name The name of the player.
     * @param tag  The CompoundTag containing the player's data.
     */
    public void saveOfflinePlayerData(String name, CompoundTag tag) {
        this.saveOfflinePlayerData(name, tag, false);
    }

    /**
     * Saves the offline player data, players can be offline.
     *
     * @param nameOrUUid The name or UUID of the player.
     * @param tag        The CompoundTag containing the player's data.
     * @param async      Whether to save the data asynchronously.
     */
    public void saveOfflinePlayerData(String nameOrUUid, CompoundTag tag, boolean async) {
        UUID uuid = lookupName(nameOrUUid).orElse(UUID.fromString(nameOrUUid));
        if (this.getSettings().playerSettings().savePlayerData()) {
            this.getScheduler().scheduleTask(InternalPlugin.INSTANCE, new Task() {
                final AtomicBoolean hasRun = new AtomicBoolean(false);

                @Override
                public void onRun(int currentTick) {
                    this.onCancel();
                }

                // Ensures that the player data will be saved during a server shutdown
                @Override
                public void onCancel() {
                    if (!hasRun.getAndSet(true)) {
                        saveOfflinePlayerDataInternal(tag, uuid);
                    }
                }
            }, async);
        }
    }

    /**
     * Internal method to save offline player data.
     *
     * @param tag  The CompoundTag containing the player's data.
     * @param uuid The UUID of the player.
     */
    private void saveOfflinePlayerDataInternal(CompoundTag tag, UUID uuid) {
        try {
            byte[] bytes = NBTIO.writeGZIPCompressed(tag, ByteOrder.BIG_ENDIAN);
            ByteBuffer buffer = ByteBuffer.allocate(16);
            buffer.putLong(uuid.getMostSignificantBits());
            buffer.putLong(uuid.getLeastSignificantBits());
            playerDataDB.put(buffer.array(), bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves an online player by name using a fuzzy match.
     * Returns the player whose name starts with the specified name.
     *
     * @param name The name of the player.
     * @return The Player instance, or null if not found.
     */
    public Player getPlayer(String name) {
        Player found = null;
        name = name.toLowerCase(Locale.ENGLISH);
        int delta = Integer.MAX_VALUE;
        for (Player player : this.getOnlinePlayers().values()) {
            if (player.getName().toLowerCase(Locale.ENGLISH).startsWith(name)) {
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
     * Retrieves an online player by name using an exact match.
     * Returns the player whose name exactly matches the specified name.
     *
     * @param name The name of the player.
     * @return The Player instance, or null if not found.
     */
    public Player getPlayerExact(String name) {
        name = name.toLowerCase(Locale.ENGLISH);
        for (Player player : this.getOnlinePlayers().values()) {
            if (player.getName().toLowerCase(Locale.ENGLISH).equals(name)) {
                return player;
            }
        }

        return null;
    }

    /**
     * Retrieves all players whose names contain the specified partial name.
     *
     * @param partialName The partial name to match.
     * @return An array of matched players, or an empty array if no matches are found.
     */
    public Player[] matchPlayer(String partialName) {
        partialName = partialName.toLowerCase(Locale.ENGLISH);
        List<Player> matchedPlayer = new ArrayList<>();
        for (Player player : this.getOnlinePlayers().values()) {
            if (player.getName().toLowerCase(Locale.ENGLISH).equals(partialName)) {
                return new Player[]{player};
            } else if (player.getName().toLowerCase(Locale.ENGLISH).contains(partialName)) {
                matchedPlayer.add(player);
            }
        }

        return matchedPlayer.toArray(Player.EMPTY_ARRAY);
    }

    /**
     * Removes a player from the server.
     *
     * @param player The player to remove.
     */
    @ApiStatus.Internal
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

    /**
     * Retrieves a map of all online players.
     *
     * @return An immutable map of all online players.
     */
    public Map<UUID, Player> getOnlinePlayers() {
        return ImmutableMap.copyOf(playerList);
    }

    // ─────────────────────────────────────────────────────
    // ──────── Start of [Information Region] ──────────────
    // ─────────────────────────────────────────────────────

    /**
     * Retrieves the name of the server.
     *
     * @return The name of the server, which is "PowerNukkitX".
     */
    public String getName() {
        return "PowerNukkitX";
    }

    /**
     * Retrieves the version of Nukkit.
     *
     * @return The version of Nukkit.
     */
    public String getNukkitVersion() {
        return Nukkit.VERSION;
    }

    /**
     * Retrieves the version of Nukkit used for bStats.
     *
     * @return The version of Nukkit used for bStats.
     */
    public String getBStatsNukkitVersion() {
        return Nukkit.VERSION;
    }

    /**
     * Retrieves the Git commit hash of the current build.
     *
     * @return The Git commit hash of the current build.
     */
    public String getGitCommit() {
        return Nukkit.GIT_COMMIT;
    }

    /**
     * Retrieves the codename of the current build.
     *
     * @return The codename of the current build.
     */
    public String getCodename() {
        return Nukkit.CODENAME;
    }

    /**
     * Retrieves the Minecraft version supported by this server.
     *
     * @return The Minecraft version supported by this server.
     */
    public String getVersion() {
        return ProtocolInfo.MINECRAFT_VERSION;
    }

    /**
     * Retrieves the API version of Nukkit.
     *
     * @return The API version of Nukkit.
     */
    public String getApiVersion() {
        return Nukkit.API_VERSION;
    }

    // ─────────────────────────────────────────────────────
    // ──────── Start of [File Region] ─────────────────────
    // ─────────────────────────────────────────────────────

    /**
     * Retrieves the file path of the server.
     *
     * @return the file path as a String.
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Retrieves the data path of the server.
     *
     * @return the data path as a String.
     */
    public String getDataPath() {
        return dataPath;
    }

    /**
     * Retrieves the plugin path of the server.
     *
     * @return the plugin path as a String.
     */
    public String getPluginPath() {
        return pluginPath;
    }

    /**
     * Retrieves the unique identifier (UUID) of the server.
     *
     * @return the server's UUID.
     */
    public UUID getServerUniqueId() {
        return this.serverID;
    }

    /**
     * Retrieves the main logger instance for the server.
     *
     * @return the MainLogger instance.
     */
    public MainLogger getLogger() {
        return MainLogger.getLogger();
    }

    /**
     * Retrieves the entity metadata store.
     *
     * @return the EntityMetadataStore instance.
     */
    public EntityMetadataStore getEntityMetadata() {
        return entityMetadata;
    }

    /**
     * Retrieves the player metadata store.
     *
     * @return the PlayerMetadataStore instance.
     */
    public PlayerMetadataStore getPlayerMetadata() {
        return playerMetadata;
    }

    /**
     * Retrieves the level metadata store.
     *
     * @return the LevelMetadataStore instance.
     */
    public LevelMetadataStore getLevelMetadata() {
        return levelMetadata;
    }

    /**
     * Retrieves the resource pack manager.
     *
     * @return the ResourcePackManager instance.
     */
    public ResourcePackManager getResourcePackManager() {
        return resourcePackManager;
    }

    /**
     * Retrieves the freezable array manager.
     *
     * @return the FreezableArrayManager instance.
     */
    public FreezableArrayManager getFreezableArrayManager() {
        return freezableArrayManager;
    }

    /**
     * Retrieves the position tracking service.
     *
     * @return the PositionTrackingService instance.
     */
    @NotNull
    public PositionTrackingService getPositionTrackingService() {
        return positionTrackingService;
    }

    // ─────────────────────────────────────────────────────
    // ──────── Start of [Crafting & Recipe Region] ────────
    // ─────────────────────────────────────────────────────

    /**
     * Sends a recipe list packet to the specified player.
     *
     * @param player The player to send the recipe list packet to.
     */
    public void sendRecipeList(Player player) {
        player.getSession().sendRawPacket(ProtocolInfo.CRAFTING_DATA_PACKET, Registries.RECIPE.getCraftingPacket());
    }

    /**
     * Registers a recipe with the Recipe Manager.
     *
     * @param recipe The recipe to register.
     */
    public void addRecipe(Recipe recipe) {
        Registries.RECIPE.register(recipe);
    }

    /**
     * Retrieves the Recipe Registry.
     *
     * @return The RecipeRegistry instance.
     */
    public RecipeRegistry getRecipeRegistry() {
        return Registries.RECIPE;
    }

    // ─────────────────────────────────────────────────────
    // ──────── Start of [Levels Region] ───────────────────
    // ─────────────────────────────────────────────────────

    /**
     * Retrieves all the game worlds.
     *
     * @return a map containing all the game worlds, keyed by their IDs.
     */
    public Map<Integer, Level> getLevels() {
        return levels;
    }

    /**
     * Retrieves the default overworld.
     *
     * @return the default overworld level.
     */
    public Level getDefaultLevel() {
        return defaultLevel;
    }

    /**
     * Sets the default overworld.
     *
     * @param defaultLevel the level to set as the default overworld.
     */
    public void setDefaultLevel(Level defaultLevel) {
        if (defaultLevel == null || (this.isLevelLoaded(defaultLevel.getName()) && defaultLevel != this.defaultLevel)) {
            this.defaultLevel = defaultLevel;
        }
    }

    /**
     * Retrieves the default nether level.
     *
     * @return the default nether level.
     */
    public Level getDefaultNetherLevel() {
        return defaultNether;
    }

    /**
     * Sets the default nether level.
     *
     * @param defaultLevel the level to set as the default nether.
     */
    public void setDefaultNetherLevel(Level defaultLevel) {
        if (defaultLevel == null || (this.isLevelLoaded(defaultLevel.getName()) && defaultLevel != this.defaultNether)) {
            this.defaultNether = defaultLevel;
        }
    }

    /**
     * Retrieves the default end level.
     *
     * @return the default end level.
     */
    public Level getDefaultEndLevel() {
        return defaultLevel;
    }

    /**
     * Sets the default end level.
     *
     * @param defaultLevel the level to set as the default end.
     */
    public void setDefaultEndLevel(Level defaultLevel) {
        if (defaultLevel == null || (this.isLevelLoaded(defaultLevel.getName()) && defaultLevel != this.defaultEnd)) {
            this.defaultEnd = defaultLevel;
        }
    }

    public static final String levelDimPattern = "^.*Dim[0-9]$";

    /**
     * Checks if a world is already loaded.
     *
     * @param name the name of the world.
     * @return true if the world is loaded, false otherwise.
     */
    public boolean isLevelLoaded(String name) {
        if (!name.matches(levelDimPattern)) {
            for (int i = 0; i < 3; i++) {
                if (this.getLevelByName(name + " Dim" + i) != null) {
                    return true;
                }
            }
            return false;
        } else {
            return this.getLevelByName(name) != null;
        }
    }

    /**
     * Retrieves a world by its ID.
     *
     * @param levelId the ID of the world (0 for OVERWORLD, 1 for NETHER, 2 for THE_END).
     * @return the level instance, or null if not found.
     */
    public Level getLevel(int levelId) {
        if (this.levels.containsKey(levelId)) {
            return this.levels.get(levelId);
        }
        return null;
    }

    /**
     * Retrieves a world by its name.
     *
     * @param name the name of the world (e.g., "overworld", "nether", "the_end").
     * @return the level instance, or null if not found.
     */
    public Level getLevelByName(String name) {
        if (!name.matches(levelDimPattern)) {
            name = name + " Dim0";
        }
        for (Level level : this.levelArray) {
            if (level.getName().equalsIgnoreCase(name)) {
                return level;
            }
        }
        return null;
    }

    /**
     * Unloads a specified level.
     *
     * @param level the level to unload.
     * @return true if the level was successfully unloaded, false otherwise.
     */
    public boolean unloadLevel(Level level) {
        return this.unloadLevel(level, false);
    }

    /**
     * Unloads the specified level.
     *
     * @param level       The level to unload.
     * @param forceUnload Whether to force unload the level.
     * @return true if the level was successfully unloaded, false otherwise.
     * @throws IllegalStateException if attempting to unload the default level without forcing.
     */
    public boolean unloadLevel(Level level, boolean forceUnload) {
        if (level == this.getDefaultLevel() && !forceUnload) {
            throw new IllegalStateException("The default level cannot be unloaded while running, please switch levels.");
        }

        return level.unload(forceUnload);
    }

    /**
     * Retrieves the configuration for the specified level.
     *
     * @param levelFolderName The name of the level folder.
     * @return The LevelConfig object for the specified level, or null if not found.
     * @throws LevelException if the level name is empty.
     */
    @Nullable
    public LevelConfig getLevelConfig(String levelFolderName) {
        if (Objects.equals(levelFolderName.trim(), "")) {
            throw new LevelException("Invalid empty level name");
        }
        String path;
        if (levelFolderName.contains("/") || levelFolderName.contains("\\")) {
            path = levelFolderName;
        } else {
            path = new File(this.getDataPath(), "worlds/" + levelFolderName).getAbsolutePath();
        }
        Path jpath = Path.of(path);
        path = jpath.toString();
        if (!jpath.toFile().exists()) {
            log.warn(this.getLanguage().tr("nukkit.level.notFound", levelFolderName));
            return null;
        }

        File config = jpath.resolve("config.json").toFile();
        LevelConfig levelConfig;
        if (config.exists()) {
            try {
                levelConfig = JSONUtils.from(config, LevelConfig.class);
                FileUtils.write(config, JSONUtils.toPretty(levelConfig), StandardCharsets.UTF_8);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            // Verify the provider
            Class<? extends LevelProvider> provider = LevelProviderManager.getProvider(path);
            if (provider == null) {
                log.error(this.getLanguage().tr("nukkit.level.loadError", levelFolderName, "Unknown provider"));
                return null;
            }
            Map<Integer, LevelConfig.GeneratorConfig> map = new HashMap<>();
            // TODO: Add configurations for nether, the_end, and overworld
            map.put(0, new LevelConfig.GeneratorConfig("flat", System.currentTimeMillis(), false, LevelConfig.AntiXrayMode.LOW, true, DimensionEnum.OVERWORLD.getDimensionData(), Collections.emptyMap()));
            levelConfig = new LevelConfig(LevelProviderManager.getProviderName(provider), true, map);
            try {
                config.createNewFile();
                FileUtils.write(config, JSONUtils.toPretty(levelConfig), StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return levelConfig;
    }

    /**
     * Loads the specified level.
     *
     * @param levelFolderName The name of the level folder.
     * @return true if the level was successfully loaded, false otherwise.
     */
    public boolean loadLevel(String levelFolderName) {
        if (levelFolderName.matches(levelDimPattern)) {
            levelFolderName = levelFolderName.replaceFirst("\\sDim\\d$", "");
        }
        LevelConfig levelConfig = getLevelConfig(levelFolderName);
        if (levelConfig == null) return false;

        String path;
        if (levelFolderName.contains("/") || levelFolderName.contains("\\")) {
            path = levelFolderName;
        } else {
            path = new File(this.getDataPath(), "worlds/" + levelFolderName).getAbsolutePath();
        }
        String pathS = Path.of(path).toString();
        Class<? extends LevelProvider> provider = LevelProviderManager.getProvider(pathS);

        Map<Integer, LevelConfig.GeneratorConfig> generators = levelConfig.generators();
        for (var entry : generators.entrySet()) {
            String levelName = levelFolderName + " Dim" + entry.getKey();
            if (this.isLevelLoaded(levelName)) {
                return true;
            }
            Level level;
            try {
                if (provider == null) {
                    log.error(this.getLanguage().tr("nukkit.level.loadError", levelFolderName, "the level does not exist"));
                    return false;
                }
                level = new Level(this, levelName, pathS, generators.size(), provider, entry.getValue());
            } catch (Exception e) {
                log.error(this.getLanguage().tr("nukkit.level.loadError", levelFolderName, e.getMessage()), e);
                return false;
            }
            this.levels.put(level.getId(), level);
            level.initLevel();
            this.getPluginManager().callEvent(new LevelLoadEvent(level));
            level.setTickRate(getSettings().levelSettings().baseTickRate());
        }
        if (tickCounter != 0) { // Update world enum when load
            WorldCommand.WORLD_NAME_ENUM.updateSoftEnum();
        }
        return true;
    }

    /**
     * Generates a new level with the specified name and configuration.
     *
     * @param name        The name of the new level.
     * @param levelConfig The configuration for the new level, or null to use default configuration.
     * @return true if the level was successfully generated, false otherwise.
     */
    public boolean generateLevel(String name, @Nullable LevelConfig levelConfig) {
        if (name.isBlank()) {
            return false;
        }
        String path;
        if (name.contains("/") || name.contains("\\")) {
            path = name;
        } else {
            path = this.getDataPath() + "worlds/" + name + "/";
        }

        Path jpath = Path.of(path);
        path = jpath.toString();
        File config = jpath.resolve("config.json").toFile();
        if (config.exists()) {
            try {
                levelConfig = JSONUtils.from(new FileReader(config), LevelConfig.class);
                FileUtils.write(config, JSONUtils.toPretty(levelConfig), StandardCharsets.UTF_8);
            } catch (Exception e) {
                log.error("The levelConfig does not exist under the {} path", path);
                return false;
            }
        } else if (levelConfig != null) {
            try {
                jpath.toFile().mkdirs();
                config.createNewFile();
                FileUtils.write(config, JSONUtils.toPretty(levelConfig), StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            log.error("The levelConfig is not specified and no config.json exists under the {} path", path);
            return false;
        }

        for (var entry : levelConfig.generators().entrySet()) {
            LevelConfig.GeneratorConfig generatorConfig = entry.getValue();
            var provider = LevelProviderManager.getProviderByName(levelConfig.format());
            Level level;
            try {
                provider.getMethod("generate", String.class, String.class, LevelConfig.GeneratorConfig.class).invoke(null, path, name, generatorConfig);
                String levelName = name + " Dim" + entry.getKey();
                if (this.isLevelLoaded(levelName)) {
                    log.warn("Level {} has already been loaded!", levelName);
                    continue;
                }
                level = new Level(this, levelName, path, levelConfig.generators().size(), provider, generatorConfig);

                this.getLevels().put(level.getId(), level);
                level.initLevel();
                level.setTickRate(getSettings().levelSettings().baseTickRate());
                this.getPluginManager().callEvent(new LevelInitEvent(level));
                this.getPluginManager().callEvent(new LevelLoadEvent(level));
            } catch (Exception e) {
                log.error(this.getLanguage().tr("nukkit.level.generationError", name, Utils.getExceptionMessage(e)), e);
                return false;
            }
        }
        return true;
    }

    // ─────────────────────────────────────────────────────
    // ──────── Start of [Ban, OP, Whitelist Region] ───────
    // ─────────────────────────────────────────────────────

    /**
     * Retrieves the list of name bans.
     *
     * @return the BanList containing name bans
     */
    public BanList getNameBans() {
        return this.banByName;
    }

    /**
     * Retrieves the list of IP bans.
     *
     * @return the BanList containing IP bans
     */
    public BanList getIPBans() {
        return this.banByIP;
    }

    /**
     * Adds a player to the operator list.
     * <p>
     * This method sets the specified player as an operator, updates their permissions,
     * and synchronizes their available commands.
     *
     * @param name the name of the player to add as an operator
     */
    public void addOp(String name) {
        this.operators.set(name.toLowerCase(Locale.ENGLISH), true);
        Player player = this.getPlayerExact(name);
        if (player != null) {
            player.recalculatePermissions();
            player.getAdventureSettings().onOpChange(true);
            player.getAdventureSettings().update();
            player.getSession().syncAvailableCommands();
        }
        this.operators.save(true);
    }

    /**
     * Removes a player from the operator list.
     * <p>
     * This method removes the specified player from the operator list, updates their permissions,
     * and synchronizes their available commands.
     *
     * @param name the name of the player to remove from the operator list
     */
    public void removeOp(String name) {
        this.operators.remove(name.toLowerCase(Locale.ENGLISH));
        Player player = this.getPlayerExact(name);
        if (player != null) {
            player.recalculatePermissions();
            player.getAdventureSettings().onOpChange(false);
            player.getAdventureSettings().update();
            player.getSession().syncAvailableCommands();
        }
        this.operators.save();
    }

    /**
     * Adds a player to the whitelist.
     * <p>
     * This method adds the specified player to the whitelist and saves the whitelist configuration.
     *
     * @param name the name of the player to add to the whitelist
     */
    public void addWhitelist(String name) {
        this.whitelist.set(name.toLowerCase(Locale.ENGLISH), true);
        this.whitelist.save(true);
    }

    /**
     * Removes a player from the whitelist.
     * <p>
     * This method removes the specified player from the whitelist and saves the whitelist configuration.
     *
     * @param name the name of the player to remove from the whitelist
     */
    public void removeWhitelist(String name) {
        this.whitelist.remove(name.toLowerCase(Locale.ENGLISH));
        this.whitelist.save(true);
    }

    /**
     * Checks if a player is whitelisted.
     * <p>
     * This method checks if the specified player is in the whitelist or is an operator.
     *
     * @param name the name of the player to check
     * @return true if the player is whitelisted or an operator, false otherwise
     */
    public boolean isWhitelisted(String name) {
        return !this.hasWhitelist() || this.operators.exists(name, true) || this.whitelist.exists(name, true);
    }

    /**
     * Checks if a player is an operator.
     * <p>
     * This method checks if the specified player is in the operator list.
     *
     * @param name the name of the player to check
     * @return true if the player is an operator, false otherwise
     */
    public boolean isOp(String name) {
        return name != null && this.operators.exists(name, true);
    }

    /**
     * Retrieves the whitelist configuration.
     *
     * @return the Config object representing the whitelist
     */
    public Config getWhitelist() {
        return whitelist;
    }

    /**
     * Retrieves the operator list configuration.
     *
     * @return the Config object representing the operator list
     */
    public Config getOps() {
        return operators;
    }

    /**
     * Reloads the whitelist configuration.
     * <p>
     * This method reloads the whitelist configuration from the file.
     */
    public void reloadWhitelist() {
        this.whitelist.reload();
    }

    // ─────────────────────────────────────────────────────
    // ──────── Start of [Configs Region] ──────────────────
    // ─────────────────────────────────────────────────────

    /**
     * Gets the maximum number of players allowed on the server.
     *
     * @return the maximum number of players
     */
    public int getMaxPlayers() {
        return maxPlayers;
    }

    /**
     * Sets the maximum number of players allowed on the server.
     * <p>
     * This method updates the maximum player count and notifies the network pong
     * to reflect the new player limit.
     *
     * @param maxPlayers the maximum number of players
     */
    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
        this.getNetwork().getPong().maximumPlayerCount(maxPlayers).update();
    }

    /**
     * Gets the server port.
     * <p>
     * This method retrieves the port number on which the server is running.
     *
     * @return the server port
     */
    public int getPort() {
        return this.properties.get(ServerPropertiesKeys.SERVER_PORT, 19132);
    }

    /**
     * Gets the server view distance.
     * <p>
     * This method retrieves the view distance setting of the server, which
     * determines how far players can see in the game.
     *
     * @return the server view distance
     */
    public int getViewDistance() {
        return this.properties.get(ServerPropertiesKeys.VIEW_DISTANCE, 10);
    }

    /**
     * Gets the server IP address.
     * <p>
     * This method retrieves the IP address on which the server is running.
     *
     * @return the server IP address
     */
    public String getIp() {
        return this.properties.get(ServerPropertiesKeys.SERVER_IP, "0.0.0.0");
    }

    /**
     * Checks if the server automatically saves.
     * <p>
     * This method returns whether the server is configured to automatically save
     * its state at regular intervals.
     *
     * @return true if the server automatically saves, false otherwise
     */
    public boolean getAutoSave() {
        return this.autoSave;
    }

    /**
     * Sets whether the server automatically saves.
     * <p>
     * This method configures the server to automatically save its state at regular
     * intervals and updates the auto-save setting for all loaded levels.
     *
     * @param autoSave true to enable auto-save, false to disable it
     */
    public void setAutoSave(boolean autoSave) {
        this.autoSave = autoSave;
        for (Level level : this.levelArray) {
            level.setAutoSave(this.autoSave);
        }
    }

    /**
     * Retrieves the gamemode of the server.
     * <p>
     * This method attempts to get the gamemode from the server properties. If the value is not a valid integer,
     * it falls back to parsing the gamemode from a string.
     *
     * @return the gamemode id
     */
    public int getGamemode() {
        try {
            return this.properties.get(ServerPropertiesKeys.GAMEMODE, 0) & 0b11;
        } catch (NumberFormatException exception) {
            return getGamemodeFromString(this.properties.get(ServerPropertiesKeys.GAMEMODE, "survival")) & 0b11;
        }
    }

    /**
     * Checks if the server forces a specific gamemode.
     * <p>
     * This method returns whether the server is configured to force players into a specific gamemode.
     *
     * @return true if the server forces a specific gamemode, false otherwise
     */
    public boolean getForceGamemode() {
        return this.properties.get(ServerPropertiesKeys.FORCE_GAMEMODE, false);
    }

    /**
     * Converts a gamemode id to its corresponding string representation.
     * <p>
     * This method returns the string representation of a gamemode based on its id. If the `direct` parameter is true,
     * it returns the direct string, otherwise it returns a localized string.
     *
     * @param mode   the gamemode id
     * @param direct if true, returns the direct string representation; if false, returns the localized string
     * @return the string representation of the gamemode
     */
    public static String getGamemodeString(int mode, boolean direct) {
        return switch (mode) {
            case Player.SURVIVAL -> direct ? "Survival" : "%gameMode.survival";
            case Player.CREATIVE -> direct ? "Creative" : "%gameMode.creative";
            case Player.ADVENTURE -> direct ? "Adventure" : "%gameMode.adventure";
            case Player.SPECTATOR -> direct ? "Spectator" : "%gameMode.spectator";
            default -> "UNKNOWN";
        };
    }

    /**
     * Converts a string to its corresponding gamemode id.
     * <p>
     * This method parses a string to determine the gamemode id. It supports various string representations
     * such as "0", "survival", "s", etc.
     *
     * @param str the string representing the gamemode
     * @return the gamemode id
     */
    public static int getGamemodeFromString(String str) {
        return switch (str.trim().toLowerCase(Locale.ENGLISH)) {
            case "0", "survival", "s" -> Player.SURVIVAL;
            case "1", "creative", "c" -> Player.CREATIVE;
            case "2", "adventure", "a" -> Player.ADVENTURE;
            case "3", "spectator", "spc", "view", "v" -> Player.SPECTATOR;
            default -> -1;
        };
    }

    /**
     * Converts a string to its corresponding game difficulty id.
     * <p>
     * This method parses a string to determine the game difficulty id. It supports various string representations
     * such as "0", "peaceful", "p", etc.
     *
     * @param str the string representing the game difficulty
     * @return the game difficulty id
     */
    public static int getDifficultyFromString(String str) {
        switch (str.trim().toLowerCase(Locale.ENGLISH)) {
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
     * Retrieves the game difficulty of the server.
     * <p>
     * This method gets the game difficulty from the server properties. If the difficulty is not set,
     * it defaults to "1" (easy).
     *
     * @return the game difficulty id
     */
    public int getDifficulty() {
        if (this.difficulty == Integer.MAX_VALUE) {
            this.difficulty = getDifficultyFromString(this.properties.get(ServerPropertiesKeys.DIFFICULTY, "1"));
        }
        return this.difficulty;
    }

    /**
     * Sets the server game difficulty.
     * <p>
     * This method sets the difficulty level of the server. The difficulty value
     * is clamped between 0 and 3.
     *
     * @param difficulty the game difficulty id
     */
    public void setDifficulty(int difficulty) {
        int value = difficulty;
        if (value < 0) value = 0;
        if (value > 3) value = 3;
        this.difficulty = value;
        this.properties.get(ServerPropertiesKeys.DIFFICULTY, value);
    }

    /**
     * Checks if the server whitelist is enabled.
     * <p>
     * This method returns whether the server has the whitelist feature enabled.
     *
     * @return true if the whitelist is enabled, false otherwise
     */
    public boolean hasWhitelist() {
        return this.properties.get(ServerPropertiesKeys.WHITE_LIST, false);
    }

    /**
     * Gets the server spawn protection radius.
     * <p>
     * This method returns the radius around the spawn point that is protected
     * from player modifications.
     *
     * @return the spawn protection radius
     */
    public int getSpawnRadius() {
        return this.properties.get(ServerPropertiesKeys.SPAWN_PROTECTION, 16);
    }

    /**
     * Checks if flying is allowed on the server.
     * <p>
     * This method returns whether players are allowed to fly on the server.
     *
     * @return true if flying is allowed, false otherwise
     */
    public boolean getAllowFlight() {
        if (getAllowFlight == null) {
            getAllowFlight = this.properties.get(ServerPropertiesKeys.ALLOW_FLIGHT, false);
        }
        return getAllowFlight;
    }

    /**
     * Checks if the server is in hardcore mode.
     * <p>
     * This method returns whether the server is set to hardcore mode.
     *
     * @return true if the server is in hardcore mode, false otherwise
     */
    public boolean isHardcore() {
        return this.properties.get(ServerPropertiesKeys.HARDCORE, false);
    }

    /**
     * Gets the default game mode of the server.
     * <p>
     * This method returns the default game mode that players will have when they
     * join the server.
     *
     * @return the default game mode
     */
    public int getDefaultGamemode() {
        if (this.defaultGamemode == Integer.MAX_VALUE) {
            this.defaultGamemode = this.getGamemode();
        }
        return this.defaultGamemode;
    }

    /**
     * Sets the default game mode for the server.
     * <p>
     * This method sets the default game mode that players will have when they
     * join the server.
     *
     * @param defaultGamemode the default game mode
     */
    public void setDefaultGamemode(int defaultGamemode) {
        this.defaultGamemode = defaultGamemode;
        this.getNetwork().getPong().gameType(Server.getGamemodeString(defaultGamemode, true)).update();
    }

    /**
     * Gets the server message of the day (MOTD).
     * <p>
     * This method returns the message of the day that is displayed to players
     * when they join the server.
     *
     * @return the server MOTD
     */
    public String getMotd() {
        return this.properties.get(ServerPropertiesKeys.MOTD, "PowerNukkitX Server");
    }

    /**
     * Sets the server message of the day (MOTD).
     * <p>
     * This method sets the message of the day that is displayed to players
     * when they join the server.
     *
     * @param motd the message of the day
     */
    public void setMotd(String motd) {
        this.properties.get(ServerPropertiesKeys.MOTD, motd);
        this.getNetwork().getPong().motd(motd).update();
    }

    /**
     * Gets the server subheading (sub-MOTD).
     * <p>
     * This method returns the subheading that is displayed to players when they
     * join the server.
     *
     * @return the server subheading
     */
    public String getSubMotd() {
        String subMotd = this.properties.get(ServerPropertiesKeys.SUB_MOTD, "v2.powernukkitx.com");
        if (subMotd.isEmpty()) {
            subMotd = "v2.powernukkitx.com";
        }
        return subMotd;
    }

    /**
     * Sets the server subheading (sub-MOTD).
     * <p>
     * This method sets the subheading that is displayed to players when they
     * join the server.
     *
     * @param subMotd the subheading
     */
    public void setSubMotd(String subMotd) {
        this.properties.get(ServerPropertiesKeys.SUB_MOTD, subMotd);
        this.getNetwork().getPong().subMotd(subMotd).update();
    }

    /**
     * Checks if the server forces the use of a resource pack.
     * <p>
     * This method returns whether the server forces players to use a specific
     * resource pack.
     *
     * @return true if the server forces the use of a resource pack, false otherwise
     */
    public boolean getForceResources() {
        return this.properties.get(ServerPropertiesKeys.FORCE_RESOURCES, false);
    }

    /**
     * Checks if the server allows client resource packs while forcing the use of a server resource pack.
     * <p>
     * This method returns whether the server allows players to use their own
     * resource packs in addition to the server's resource pack.
     *
     * @return true if client resource packs are allowed, false otherwise
     */
    public boolean getForceResourcesAllowOwnPacks() {
        return this.properties.get(ServerPropertiesKeys.FORCE_RESOURCES_ALLOW_CLIENT_PACKS, false);
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
            case "fra" -> LangCode.valueOf("fr_FR");
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

    /**
     * Returns the current language of the server.
     * <p>
     * This method retrieves the `BaseLang` instance representing the current language
     * configuration of the server.
     *
     * @return the current language of the server
     */
    public BaseLang getLanguage() {
        return baseLang;
    }

    /**
     * Returns the language code of the server.
     * <p>
     * This method retrieves the `LangCode` instance representing the language code
     * configuration of the server.
     *
     * @return the language code of the server
     */
    public LangCode getLanguageCode() {
        return baseLangCode;
    }

    /**
     * Returns the server settings.
     * <p>
     * This method retrieves the `ServerSettings` instance containing various
     * configuration settings of the server.
     *
     * @return the server settings
     */
    public ServerSettings getSettings() {
        return settings;
    }

    /**
     * Returns the server properties.
     * <p>
     * This method retrieves the `ServerProperties` instance containing various
     * properties of the server.
     *
     * @return the server properties
     */
    public ServerProperties getProperties() {
        return properties;
    }

    /**
     * Checks if the Nether dimension is allowed on the server.
     * <p>
     * This method determines whether the Nether dimension is enabled and can be
     * accessed by players on the server.
     *
     * @return true if the Nether is allowed, false otherwise
     */
    public boolean isNetherAllowed() {
        return this.allowNether;
    }

    /**
     * Checks if the End dimension is allowed on the server.
     * <p>
     * This method determines whether the End dimension is enabled and can be
     * accessed by players on the server.
     *
     * @return true if the End is allowed, false otherwise
     */
    public boolean isTheEndAllowed() {
        return this.allowTheEnd;
    }

    /**
     * Checks if the specified packet class is ignored by the server.
     * <p>
     * This method determines whether the server is configured to ignore packets
     * of the specified class. Ignored packets are not processed by the server.
     *
     * @param clazz the class of the packet to check
     * @return true if the packet class is ignored, false otherwise
     */
    public boolean isIgnoredPacket(Class<? extends DataPacket> clazz) {
        return this.getSettings().debugSettings().ignoredPackets().contains(clazz.getSimpleName());
    }

    /**
     * Returns the server authoritative movement mode.
     * <p>
     * The server authoritative movement mode determines how the server handles
     * player movement and synchronization. This can be used to enforce stricter
     * movement rules and prevent cheating.
     *
     * @return the server authoritative movement mode
     */
    public int getServerAuthoritativeMovement() {
        return serverAuthoritativeMovementMode;
    }

    // ─────────────────────────────────────────────────────
    // ──────── Start of [Threading Region] ────────────────
    // ─────────────────────────────────────────────────────

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

    /**
     * Returns the primary thread of the server.
     * <p>
     * The primary thread is the main thread on which the server runs.
     * This method can be used to retrieve the thread instance for
     * operations that need to be performed on the main server thread.
     *
     * @return the primary thread of the server
     */
    public Thread getPrimaryThread() {
        return currentThread;
    }

    /**
     * Returns the server scheduler.
     * <p>
     * The server scheduler is responsible for managing and executing scheduled tasks
     * on the server. It handles the timing and execution of tasks that need to run
     * periodically or after a certain delay.
     *
     * @return the server scheduler
     */
    public ServerScheduler getScheduler() {
        return scheduler;
    }

    /**
     * Returns the compute thread pool used by the server.
     * <p>
     * The compute thread pool is a ForkJoinPool that is used for parallel computations
     * and tasks that can be divided into smaller subtasks. This pool helps in efficiently
     * utilizing multiple CPU cores for concurrent processing.
     *
     * @return the compute thread pool
     */
    public ForkJoinPool getComputeThreadPool() {
        return computeThreadPool;
    }

    //todo NukkitConsole

    /**
     * Thread for handling console input and output.
     * <p>
     * This thread is responsible for starting the console and managing
     * console interactions.
     */
    private class ConsoleThread extends Thread implements InterruptibleThread {
        public ConsoleThread() {
            super("Console Thread");
        }

        @Override
        public void run() {
            console.start();
        }
    }

    /**
     * Thread for handling tasks in the compute thread pool.
     * <p>
     * This thread operates within a ForkJoinPool and is used for parallel
     * computations and tasks that can be divided into smaller subtasks.
     */
    private static class ComputeThread extends ForkJoinWorkerThread {
        /**
         * Creates a ForkJoinWorkerThread operating in the given pool.
         *
         * @param pool        the pool this thread works in
         * @param threadCount the counter for naming threads
         * @throws NullPointerException if pool is null
         */
        ComputeThread(ForkJoinPool pool, AtomicInteger threadCount) {
            super(pool);
            this.setName("ComputeThreadPool-thread-" + threadCount.getAndIncrement());
        }
    }

    /**
     * Factory for creating compute threads in a ForkJoinPool.
     * <p>
     * This factory is responsible for creating and initializing
     * ComputeThread instances with the necessary permissions.
     */
    private static class ComputeThreadPoolThreadFactory implements ForkJoinPool.ForkJoinWorkerThreadFactory {
        private static final AtomicInteger threadCount = new AtomicInteger(0);
        @SuppressWarnings("removal")
        private static final AccessControlContext ACC = contextWithPermissions(
                new RuntimePermission("getClassLoader"),
                new RuntimePermission("setContextClassLoader"));

        /**
         * Creates an AccessControlContext with the specified permissions.
         *
         * @param perms the permissions to include in the context
         * @return the created AccessControlContext
         */
        @SuppressWarnings("removal")
        static AccessControlContext contextWithPermissions(@NotNull Permission... perms) {
            Permissions permissions = new Permissions();
            for (var perm : perms)
                permissions.add(perm);
            return new AccessControlContext(new ProtectionDomain[]{new ProtectionDomain(null, permissions)});
        }

        /**
         * Creates a new ComputeThread for the given ForkJoinPool.
         *
         * @param pool the pool this thread works in
         * @return the created ComputeThread
         */
        @SuppressWarnings("removal")
        public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
            return AccessController.doPrivileged((PrivilegedAction<ForkJoinWorkerThread>) () -> new ComputeThread(pool, threadCount), ACC);
        }
    }

}
