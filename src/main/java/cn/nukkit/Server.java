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
import cn.nukkit.config.ServerSettings;
import cn.nukkit.config.YamlSnakeYamlConfigurer;
import cn.nukkit.config.updater.ConfigUpdater;
import cn.nukkit.console.NukkitConsole;
import cn.nukkit.dispenser.DispenseBehaviorRegister;
import cn.nukkit.education.Education;
import cn.nukkit.entity.Attribute;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.entity.data.profession.Profession;
import cn.nukkit.entity.data.property.EntityProperty;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.level.LevelInitEvent;
import cn.nukkit.event.level.LevelLoadEvent;
import cn.nukkit.event.network.NetworkRegisterEvent;
import cn.nukkit.event.player.PlayerLoginEvent;
import cn.nukkit.event.server.QueryRegenerateEvent;
import cn.nukkit.event.server.ServerReloadEvent;
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
import cn.nukkit.math.Vector3;
import cn.nukkit.metadata.EntityMetadataStore;
import cn.nukkit.metadata.LevelMetadataStore;
import cn.nukkit.metadata.PlayerMetadataStore;
import cn.nukkit.metrics.NukkitMetrics;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.ByteTag;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.IntTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.LongTag;
import cn.nukkit.nbt.tag.ShortTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.network.Network;
import cn.nukkit.network.NetworkInterface;
import cn.nukkit.network.process.NetworkState;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.PlayerListPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.network.protocol.types.ExperimentEntry;
import cn.nukkit.network.protocol.types.PlayerInfo;
import cn.nukkit.network.protocol.types.XboxLivePlayerInfo;
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
import cn.nukkit.wizard.WizardConfig;
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
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.impl.Iq80DBFactory;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.io.UncheckedIOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

/**
 * Represents the main server singleton for PowerNukkitX.
 * <p>
 * This class is instantiated in {@link Nukkit} and can be accessed via {@link cn.nukkit.Server#getInstance()}.
 * The constructor performs various initialization tasks, including configuration loading, thread and thread pool creation,
 * plugin startup, and registration of recipes, blocks, entities, items, and more.
 *
 * <p>Key responsibilities include:
 * <ul>
 *   <li>Managing server lifecycle and configuration</li>
 *   <li>Handling plugins, commands, and events</li>
 *   <li>Managing players, levels, and networking</li>
 *   <li>Providing access to server-wide utilities and managers</li>
 * </ul>
 *
 * <p>Note: This class is a singleton and should be accessed via {@link #getInstance()}.
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
     * A tick counter that records the number of ticks the server has passed.
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
     * FJP thread pool responsible for terrain generation, data compression and other computing tasks
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
     * Does the configuration item check the login time.
     */
    public boolean checkLoginTime = false;
    private EntityMetadataStore entityMetadata;
    private PlayerMetadataStore playerMetadata;
    private LevelMetadataStore levelMetadata;
    private NetworkInterface network;
    private int serverAuthoritativeMovementMode = 0;
    private int defaultGamemode = Integer.MAX_VALUE;
    private int autoSaveTicker = 0;
    private int autoSaveTicks = 6000;
    private BaseLang baseLang;
    private LangCode baseLangCode;
    private UUID serverID;
    private final String filePath;
    private final String dataPath;
    private final String pluginPath;
    public final String structurePath;
    private final Set<UUID> uniquePlayers = new HashSet<>();
    private final Map<InetSocketAddress, Player> players = new ConcurrentHashMap<>();
    private final Map<UUID, Player> playerList = new ConcurrentHashMap<>();
    private QueryRegenerateEvent queryRegenerateEvent;
    private PositionTrackingService positionTrackingService;

    // Dynamic Properties defaults
    private static final String DP_ROOT = "DynamicProperties";
    private static volatile String DP_DEFAULT_GROUP_UUID = "00000000-0000-0000-0000-000000000000";
    private static final int DP_MAX_STRING_BYTES = 32767;
    private static final double DP_NUMBER_ABS_MAX = 9_223_372_036_854_775_807d;
    private static final Pattern DP_UUID_CANON =
            Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");

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

    // default levels
    private Level defaultLevel = null;
    private boolean allowNether;
    private boolean allowTheEnd;
    private List<ExperimentEntry> experiments;

    Server(final String filePath, String dataPath, String pluginPath, String predefinedLanguage, WizardConfig wizardConfig) {
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
        if (!new File(dataPath + "structures/").exists()) {
            new File(dataPath + "structures/").mkdirs();
        }
        if (!new File(pluginPath).exists()) {
            new File(pluginPath).mkdirs();
        }

        this.dataPath = new File(dataPath).getAbsolutePath() + "/";
        this.pluginPath = new File(pluginPath).getAbsolutePath() + "/";
        this.structurePath = new File(dataPath).getAbsolutePath() + "/structures/";
        String commandDataPath = new File(dataPath).getAbsolutePath() + "/command_data";
        if (!new File(commandDataPath).exists()) {
            new File(commandDataPath).mkdirs();
        }

        this.console = new NukkitConsole(this);
        this.consoleThread = new ConsoleThread();
        this.consoleThread.start();

        while(convertLegacyConfiguration());

        File config = new File(this.dataPath + "pnx.yml");
        String chooseLanguage = null;

        if (!config.exists()) {
            // Config doesn't exist - use wizard config if provided, otherwise use predefined or default
            if (wizardConfig != null) {
                chooseLanguage = wizardConfig.getLanguage();
                log.info("Using language from setup wizard: {}", chooseLanguage);
            } else if (predefinedLanguage != null && !predefinedLanguage.isEmpty()) {
                chooseLanguage = predefinedLanguage;
                log.info("Using predefined language: {}", chooseLanguage);
            } else {
                chooseLanguage = "eng";
                log.info("Using default language: eng");
            }
        } else {
            Config configInstance = new Config(config);
            chooseLanguage = configInstance.getString("settings.language", "eng");
        }
        this.baseLang = new BaseLang(chooseLanguage);
        this.baseLangCode = mapInternalLang(chooseLanguage);
        this.settings = ConfigManager.create(ServerSettings.class, it -> {
            log.info("Loading {}...", TextFormat.GREEN + "pnx.yml" + TextFormat.RESET);
            it.withConfigurer(new YamlSnakeYamlConfigurer());
            it.withBindFile(config);
            it.withRemoveOrphans(true);
            it.saveDefaults();
            it.load(true);
        });

        if (wizardConfig != null && !config.exists()) {
            this.settings.baseSettings().language(wizardConfig.getLanguage());
            this.settings.baseSettings().motd(wizardConfig.getMotd());
            this.settings.baseSettings().port(wizardConfig.getPort());
            this.settings.baseSettings().maxPlayers(wizardConfig.getMaxPlayers());
            this.settings.gameplaySettings().gamemode(wizardConfig.getGamemode());
            this.settings.baseSettings().allowList(wizardConfig.isWhitelistEnabled());
            this.settings.networkSettings().enableQuery(wizardConfig.isQueryEnabled());
        } else {
            this.settings.baseSettings().language(chooseLanguage);
            if (wizardConfig != null) {
                if (wizardConfig.getMotd() != null && !wizardConfig.getMotd().isEmpty()) {
                    this.settings.baseSettings().motd(wizardConfig.getMotd());
                }
                if (wizardConfig.getPort() != 19132) {
                    this.settings.baseSettings().port(wizardConfig.getPort());
                }
            }
        }
        this.settings.save();
        while(updateConfiguration());

        this.computeThreadPool = new ForkJoinPool(Math.min(0x7fff, Runtime.getRuntime().availableProcessors()), new ComputeThreadPoolThreadFactory(), null, false);

        levelArray = Level.EMPTY_ARRAY;

        org.apache.logging.log4j.Level targetLevel = org.apache.logging.log4j.Level.getLevel(this.settings.debugSettings().level());
        org.apache.logging.log4j.Level currentLevel = Nukkit.getLogLevel();
        if (targetLevel != null && targetLevel.intLevel() > currentLevel.intLevel()) {
            Nukkit.setLogLevel(targetLevel);
        }

        if (!StartArgUtils.loadModules()) {
            log.error(getLanguage().tr("nukkit.start.invalid"));
            return;
        }

        this.allowNether = this.settings.gameplaySettings().allowNether();
        this.allowTheEnd = this.settings.gameplaySettings().allowTheEnd();
        this.useTerra = this.settings.miscSettings().enableTerra();
        this.checkLoginTime = this.settings.networkSettings().checkLoginTime();

        log.info(this.getLanguage().tr("language.selected", getLanguage().getName(), getLanguage().getLang()));
        log.info(getLanguage().tr("nukkit.server.start", TextFormat.AQUA + this.getVersion() + TextFormat.RESET));

        String poolSize = settings.performanceSettings().asyncWorkers();
        int poolSizeNumber;
        try {
            poolSizeNumber = Integer.parseInt(poolSize);
        } catch (Exception e) {
            poolSizeNumber = Math.max(Runtime.getRuntime().availableProcessors(), 4);
        }
        ServerScheduler.WORKERS = poolSizeNumber;
        this.scheduler = new ServerScheduler();

        ZlibChooser.setProvider(settings.networkSettings().zlibProvider());

        this.serverAuthoritativeMovementMode = switch (this.settings.gameplaySettings().serverAuthoritativeMovement()) {
            case "client-auth" -> 0;
            case "server-auth" -> 1;
            case "server-auth-with-rewind" -> 2;
            default -> throw new IllegalArgumentException();
        };
        this.enabledNetworkEncryption = this.settings.networkSettings().networkEncryption();
        if (this.getSettings().baseSettings().waterdogpe()) {
            this.checkLoginTime = false;
        }

        this.experiments = new ArrayList<>();
        for(String experiment : settings.gameplaySettings().experiments())
            experiments.add(new ExperimentEntry(experiment, true));

        this.entityMetadata = new EntityMetadataStore();
        this.playerMetadata = new PlayerMetadataStore();
        this.levelMetadata = new LevelMetadataStore();
        this.operators = new Config(this.dataPath + "ops.txt", Config.ENUM);
        this.whitelist = new Config(this.dataPath + "white-list.txt", Config.ENUM);

        if (wizardConfig != null && !config.exists()) {
            for (String player : wizardConfig.getWhitelistedPlayers()) {
                this.whitelist.set(player, true);
            }
            this.whitelist.save();

            for (String op : wizardConfig.getOperators()) {
                this.operators.set(op, true);
            }
            this.operators.save();
        }

        this.banByName = new BanList(this.dataPath + "banned-players.json");
        this.banByName.load();
        this.banByIP = new BanList(this.dataPath + "banned-ips.json");
        this.banByIP.load();
        this.maxPlayers = this.settings.baseSettings().maxPlayers();
        this.setAutoSave(this.settings.baseSettings().autoSave());
        if (this.settings.gameplaySettings().hardcore() && this.getDifficulty() < 3) {
            this.settings.gameplaySettings().difficulty(3);
        }

        log.info(this.getLanguage().tr("nukkit.server.info", this.getName(), TextFormat.YELLOW + this.getNukkitVersion() + TextFormat.RESET + " (" + TextFormat.YELLOW + this.getGitCommit() + TextFormat.RESET + ")" + TextFormat.RESET, this.getApiVersion()));
        log.info(this.getLanguage().tr("nukkit.server.license"));
        this.consoleSender = new ConsoleCommandSender();

        // Initialize metrics
        NukkitMetrics.startNow(this);

        {//init
            Registries.POTION.init();
            Registries.PACKET.init();
            Registries.ENTITY.init();
            Registries.BLOCKENTITY.init();
            Registries.ITEM_RUNTIMEID.init();
            Registries.BLOCK.init();
            Registries.BLOCKSTATE.init();
            Registries.ITEM.init();
            Registries.CREATIVE.init();
            Registries.BIOME.init();
            Registries.FUEL.init();
            Registries.GENERATOR.init();
            Registries.GENERATE_STAGE.init();
            Registries.POPULATOR.init();
            Registries.GENERATE_FEATURE.init();
            Registries.STRUCTURE.init();
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

        if(settings.gameplaySettings().enableEducation()) {
            Education.enable();
            if(settings.baseSettings().waterdogpe())
                log.info("You have Education and WaterdogPE enabled at the same time. Make sure to enable Education on WaterdogPE as well.");
        }

        if (useTerra) {//load terra
            PNXPlatform instance = PNXPlatform.getInstance();
        }

        freezableArrayManager = new FreezableArrayManager(
                this.settings.performanceSettings().enable(),
                this.settings.performanceSettings().slots(),
                this.settings.performanceSettings().defaultTemperature(),
                this.settings.performanceSettings().freezingPoint(),
                this.settings.performanceSettings().absoluteZero(),
                this.settings.performanceSettings().boilingPoint(),
                this.settings.performanceSettings().melting(),
                this.settings.performanceSettings().singleOperation(),
                this.settings.performanceSettings().batchOperation());
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
            Registries.BLOCKSTATE.trim();
            Registries.ITEM_RUNTIMEID.trim();
            Registries.BLOCK.trim();
            Registries.ITEM.trim();
            Registries.CREATIVE.trim();
            Registries.BIOME.trim();
            Registries.FUEL.trim();
            Registries.GENERATOR.trim();
            Registries.POPULATOR.trim();
            Registries.GENERATE_STAGE.trim();
            Registries.STRUCTURE.trim();
            Registries.EFFECT.trim();
            Registries.RECIPE.trim();
        }

        this.enablePlugins(PluginLoadOrder.STARTUP);

        LevelProviderManager.addProvider("leveldb", LevelDBProvider.class);

        loadLevels();

        this.queryRegenerateEvent = new QueryRegenerateEvent(this, 5);
        this.getTickingAreaManager().loadAllTickingArea();

        if (this.getDefaultLevel() == null) {
            log.error(this.getLanguage().tr("nukkit.level.defaultError"));
            this.forceShutdown();

            return;
        }

        this.autoSaveTicks = settings.baseSettings().autosaveDelay();

        this.enablePlugins(PluginLoadOrder.POSTWORLD);

        NetworkRegisterEvent networkRegisterEvent = new NetworkRegisterEvent(new Network(this));
        this.pluginManager.callEvent(networkRegisterEvent);

        NetworkInterface networkInterface = networkRegisterEvent.getNetworkInterface();

        this.getLogger().debug("Registering network interface: " + networkInterface.getClass().getCanonicalName());
        this.network = networkInterface;

        EntityProperty.buildEntityProperty();
        EntityProperty.buildPlayerProperty();

        if(settings.gameplaySettings().enableEducation()) Education.registerCreative();

        if (settings.miscSettings().installSpark()) {
            SparkInstaller.initSpark(this);
        }

        if (!Boolean.parseBoolean(System.getProperty("disableWatchdog", "false"))) {
            this.watchdog = new Watchdog(this, 60000);//60s
            this.watchdog.start();
        }
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
        this.start();
    }

    private boolean convertLegacyConfiguration() {
        File oldNukkitYml = new File(this.dataPath + "nukkit.yml");
        File oldServerProperties = new File(this.dataPath + "server.properties");
        if(oldNukkitYml.exists() && oldServerProperties.exists()) {
            try {
                File backupFolder = new File(this.dataPath + "backup/");
                if (!backupFolder.exists()) {
                    backupFolder.mkdirs();
                }
                FileUtils.copyFile(oldNukkitYml, new File(backupFolder, "nukkit.yml"));
                FileUtils.copyFile(oldServerProperties, new File(backupFolder, "server.properties"));
                log.info("Copied nukkit.yml and server.properties to backup folder");
                log.info("Start migration now...");

                ConfigUpdater.update("1.0.0", this);

                oldNukkitYml.delete();
                oldServerProperties.delete();

                log.info("Migration completed, start server now...");
            } catch (IOException e) {
                log.error("Failed to copy nukkit.yml to server.properties", e);
            }
            return true;
        }
        return false;
    }

    private boolean updateConfiguration() {
        if(ConfigUpdater.canUpdate(this.settings.configSettings().version())) {
            log.info("New version detected, updating config...");
            ConfigUpdater.update(this.settings.configSettings().version(), this);
            log.info("Config updated to version {}", this.settings.configSettings().version());
            return true;
        }

        return false;
    }

    private void loadLevels() {
        File file = new File(this.getDataPath() + "/worlds");
        Preconditions.checkState(file.isDirectory(), "worlds isn't directory");
        //load all world from `worlds` folder
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
            String levelFolder = this.settings.baseSettings().defaultLevelName();
            if (levelFolder == null || levelFolder.trim().isEmpty()) {
                log.warn("level-name cannot be null, using default");
                levelFolder = "world";
                this.settings.baseSettings().defaultLevelName(levelFolder);
            }

            if (!this.loadLevel(levelFolder)) {
                //default world doesn't exist
                //generate the default world
                HashMap<Integer, LevelConfig.GeneratorConfig> generatorConfig = new HashMap<>();
                long seed = LevelConfig.GeneratorConfig.randomSeed();
                generatorConfig.put(0, new LevelConfig.GeneratorConfig("normal", seed, false, LevelConfig.AntiXrayMode.LOW, true, DimensionEnum.OVERWORLD.getDimensionData(), Collections.emptyMap()));
                generatorConfig.put(1, new LevelConfig.GeneratorConfig("nether", seed, false, LevelConfig.AntiXrayMode.LOW, true, DimensionEnum.NETHER.getDimensionData(), Collections.emptyMap()));
                generatorConfig.put(2, new LevelConfig.GeneratorConfig("the_end", seed, false, LevelConfig.AntiXrayMode.LOW, true, DimensionEnum.THE_END.getDimensionData(), Collections.emptyMap()));
                LevelConfig levelConfig = new LevelConfig("leveldb", true, generatorConfig);
                this.generateLevel(levelFolder, levelConfig);
            }
            this.setDefaultLevel(this.getLevelByName(levelFolder));
        }
    }

    // region lifecycle and ticking

    /**
     * Reloads the server
     */
    public void reload() {
        ServerReloadEvent serverReloadEvent = new ServerReloadEvent();
        getPluginManager().callEvent(serverReloadEvent);

        if (serverReloadEvent.isCancelled()) return;

        // Set network to starting to prevent new players from joining during the reload process
        this.network.setState(NetworkState.STARTING);

        log.info("Reloading server...");
        log.info("Saving levels...");

        for (Level level : this.levelArray) {
            level.save();
        }

        this.scoreboardManager.save();
        this.pluginManager.disablePlugins();
        this.pluginManager.clearPlugins();
        this.commandMap.clearCommands();

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
        this.scoreboardManager.read();

        log.info("Reloading registries...");
        {
            Registries.POTION.reload();
            Registries.PACKET.reload();
            Registries.ENTITY.reload();
            Registries.BLOCKENTITY.reload();
            Registries.BLOCKSTATE.reload();
            Registries.ITEM_RUNTIMEID.reload();
            Registries.BLOCK.reload();
            Registries.ITEM.reload();
            Registries.CREATIVE.reload();
            Registries.BIOME.reload();
            Registries.FUEL.reload();
            Registries.GENERATOR.reload();
            Registries.GENERATE_STAGE.reload();
            Registries.POPULATOR.reload();
            Registries.GENERATE_FEATURE.reload();
            Registries.STRUCTURE.reload();
            Registries.EFFECT.reload();
            Registries.RECIPE.reload();
            Enchantment.reload();
        }

        this.pluginManager.loadPlugins(this.pluginPath);
        this.functionManager.reload();

        this.enablePlugins(PluginLoadOrder.STARTUP);
        {
            Registries.POTION.trim();
            Registries.PACKET.trim();
            Registries.ENTITY.trim();
            Registries.BLOCKENTITY.trim();
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
        this.network.setState(NetworkState.STARTED);
    }

    /**
     * Shutdown the server
     */
    public void shutdown() {
        network.setState(NetworkState.STOPPING);
        isRunning.compareAndSet(true, false);
    }

    /**
     * Force shutdown the server
     */
    public void forceShutdown() {
        if (this.hasStopped) {
            return;
        }

        try {
            network.setState(NetworkState.STOPPING);
            isRunning.compareAndSet(true, false);

            this.hasStopped = true;

            ServerStopEvent serverStopEvent = new ServerStopEvent();
            getPluginManager().callEvent(serverStopEvent);

            for (Player player : new ArrayList<>(this.players.values())) {
                player.close(player.getLeaveMessage(), getSettings().miscSettings().shutdownMessage());
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
            //close watchdog and metrics
            if (this.watchdog != null) {
                this.watchdog.running = false;
            }
            NukkitMetrics.closeNow(this);
            //close threadPool
            ForkJoinPool.commonPool().shutdownNow();
            this.computeThreadPool.shutdownNow();
            //todo other things
        } catch (Exception e) {
            log.error("Exception happened while shutting down, exiting the process", e);
            System.exit(1);
        }
    }

    public void start() {
        for (BanEntry entry : this.getIPBans().getEntires().values()) {
            try {
                this.network.blockAddress(InetAddress.getByName(entry.getName()));
            } catch (UnknownHostException ignore) {
            }
        }
        this.tickCounter = 0;

        log.info(this.getLanguage().tr("nukkit.server.defaultGameMode", getGamemodeString(this.getGamemode())));
        log.info(this.getLanguage().tr("nukkit.server.networkStart", TextFormat.YELLOW + (this.getIp().isEmpty() ? "*" : this.getIp()), TextFormat.YELLOW + String.valueOf(this.getPort())));
        log.info(this.getLanguage().tr("nukkit.server.startFinished", String.valueOf((double) (System.currentTimeMillis() - Nukkit.START_TIME) / 1000)));

        ServerStartedEvent serverStartedEvent = new ServerStartedEvent();
        getPluginManager().callEvent(serverStartedEvent);

        this.network.setState(NetworkState.STARTED);
        this.network.getPong().update(this.network);

        this.tickProcessor();
        this.forceShutdown();
    }

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
                            //noinspection BusyWait
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

    private void checkTickUpdates(int currentTick) {
        if (getSettings().levelSettings().alwaysTickPlayers()) {
            for (Player p : this.players.values()) {
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
                        log.warn("Tried to tick Level {} without a provider!", level.getName());
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
            CompletableFuture.runAsync(() -> {
                this.doAutoSave();
            });
        }

        if (this.sendUsageTicker > 0 && --this.sendUsageTicker == 0) {
            this.sendUsageTicker = 6000;
            // TODO: sendUsage
        }

        // Handle freezable array
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

    public long getNextTick() {
        return nextTick;
    }

    /**
     * @return Returns the number of ticks recorded by the server
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

    public String getCPULoad() {
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        if (osBean instanceof com.sun.management.OperatingSystemMXBean) {
            double cpuLoad = ((com.sun.management.OperatingSystemMXBean) osBean).getProcessCpuLoad();
            if (cpuLoad < 0) {
                return "N/A";
            }
            return String.format("%.1f%%", cpuLoad * 100);
        }
        return "N/A";
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
     * Set the server to busy status, which prevents related code from detecting the server as unresponsive.
     * Remember to clear this setting after use.
     *
     * @param busyTime milliseconds
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

    public long getLaunchTime() {
        return launchTime;
    }

    // endregion

    // region server singleton

    public static Server getInstance() {
        return instance;
    }

    // endregion

    // region chat and commands

    /**
     * Broadcast a message to all players
     *
     * @param message The broadcasted message
     * @return int Number of players
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
     * Broadcast a message to the specified {@link CommandSender recipients}
     *
     * @param message The broadcasted message
     * @return int Number of {@link CommandSender recipients}
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
     * Get the sender to broadcast a message from the specified permission name, multiple permissions can be specified, split by <b> ; </b><br>
     * The permission corresponds to a {@link CommandSender Sender} set in {@code PluginManager#permSubs}.
     *
     * @param message     Message content
     * @param permissions Permissions name, need to register first through {@link PluginManager#subscribeToPermission subscribeToPermission}
     * @return            Number of {@link CommandSender senders} who received the message
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

    /**
     * Execute one line of command as sender
     *
     * @param sender      Command sender
     * @param commandLine A command
     * @return Returns 0 for failed execution, greater than or equal to 1 for successful execution
     * @throws ServerException Server exception
     */
    public int executeCommand(CommandSender sender, String commandLine) throws ServerException {
        // First, we need to check if this command is on the main thread or not, if not, merge it in the main thread.
        if (!this.isPrimaryThread()) {
            this.scheduler.scheduleTask(null, () -> executeCommand(sender, commandLine));
            return 1;
        }
        if (sender == null) {
            throw new ServerException("CommandSender is not valid");
        }

        var cmd = commandLine.stripLeading();
        if (cmd.isEmpty()) {
            return 0;
        }
        cmd = cmd.charAt(0) == '/' ? cmd.substring(1) : cmd;

        return this.commandMap.executeCommand(sender, cmd);
    }

    /**
     * Execute these commands silently as the console, ignoring permissions.
     *
     * @param commands the commands
     * @throws ServerException Server exception
     */
    public void silentExecuteCommand(String... commands) {
        this.silentExecuteCommand(null, commands);
    }

    /**
     * Execute these commands silently as this player, ignoring permissions.
     *
     * @param sender command sender
     * @param commands the commands
     * @throws ServerException Server exception
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
     * Get the console sender
     *
     * @return {@link ConsoleCommandSender}
     */
    // TODO: use ticker to check console
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

    public IScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    public FunctionManager getFunctionManager() {
        return functionManager;
    }

    // endregion

    // region networking

    /**
     * @see #broadcastPacket(Player[], DataPacket)
     */
    public static void broadcastPacket(Collection<Player> players, DataPacket packet) {
        for (Player player : players) {
            player.dataPacket(packet);
        }
    }

    /**
     * Broadcast a packet to the specified players.
     *
     * @param players All players receiving the data packet
     * @param packet  The data packet
     */
    public static void broadcastPacket(Player[] players, DataPacket packet) {
        for (Player player : players) {
            player.dataPacket(packet);
        }
    }

    public QueryRegenerateEvent getQueryInformation() {
        return this.queryRegenerateEvent;
    }

    public NetworkInterface getNetwork() {
        return network;
    }

    // endregion

    // region plugins

    /**
     * Enable plugins in the specified plugin loading order
     *
     * @param type Plugin loading order
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
     * Enable a specified plugin
     *
     * @param plugin Plugin instance
     */
    public void enablePlugin(Plugin plugin) {
        this.pluginManager.enablePlugin(plugin);
    }

    /**
     * Disable all plugins
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

    // region players

    public void onPlayerCompleteLoginSequence(Player player) {
        this.sendFullPlayerListData(player);
    }

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

    @ApiStatus.Internal
    public void addOnlinePlayer(Player player) {
        this.playerList.put(player.getUniqueId(), player);
        this.updatePlayerListData(player.getUniqueId(), player.getId(), player.getDisplayName(), player.getSkin(), player.getLoginChainData().getXUID(), player.getLocatorBarColor());
        this.getNetwork().getPong().playerCount(playerList.size()).update(this.getNetwork());
    }

    @ApiStatus.Internal
    public void removeOnlinePlayer(Player player) {
        if (this.playerList.containsKey(player.getUniqueId())) {
            this.playerList.remove(player.getUniqueId());

            PlayerListPacket pk = new PlayerListPacket();
            pk.type = PlayerListPacket.TYPE_REMOVE;
            pk.entries = new PlayerListPacket.Entry[]{new PlayerListPacket.Entry(player.getUniqueId())};

            Server.broadcastPacket(this.playerList.values(), pk);
            this.getNetwork().getPong().playerCount(playerList.size()).update(this.getNetwork());
        }
    }
    /**
     * @see #updatePlayerListData(UUID, long, String, Skin, String, Color, Player[])
     */

    public void updatePlayerListData(UUID uuid, long entityId, String name, Skin skin) {
        this.updatePlayerListData(uuid, entityId, name, skin, "", Color.WHITE, this.playerList.values());
    }

    public void updatePlayerListData(UUID uuid, long entityId, String name, Skin skin, Color color) {
        this.updatePlayerListData(uuid, entityId, name, skin, "", color, this.playerList.values());
    }

    /**
     * @see #updatePlayerListData(UUID, long, String, Skin, String, Color, Player[])
     */
    public void updatePlayerListData(UUID uuid, long entityId, String name, Skin skin, String xboxUserId, Color color) {
        this.updatePlayerListData(uuid, entityId, name, skin, xboxUserId, color, this.playerList.values());
    }

    /**
     * @see #updatePlayerListData(UUID, long, String, Skin, String, Color, Player[])
     */
    public void updatePlayerListData(UUID uuid, long entityId, String name, Skin skin, Player[] players) {
        this.updatePlayerListData(uuid, entityId, name, skin, "", Color.WHITE, players);
    }

    /**
     * @see #updatePlayerListData(UUID, long, String, Skin, String, Color, Player[])
     */
    public void updatePlayerListData(UUID uuid, long entityId, String name, Skin skin, Color color, Player[] players) {
        this.updatePlayerListData(uuid, entityId, name, skin, "", color, players);
    }

    /**
     * Update {@link PlayerListPacket} data packets (i.e., player list data) for specified players
     *
     * @param uuid       the uuid
     * @param entityId   entity id
     * @param name       player name
     * @param skin       player skin
     * @param xboxUserId xbox user id
     * @param players    players to send packet
     */
    public void updatePlayerListData(UUID uuid, long entityId, String name, Skin skin, String xboxUserId, Color color, Player[] players) {
        PlayerListPacket pk = new PlayerListPacket();
        pk.type = PlayerListPacket.TYPE_ADD;
        pk.entries = new PlayerListPacket.Entry[]{new PlayerListPacket.Entry(uuid, entityId, name, skin, xboxUserId, color)};
        Server.broadcastPacket(players, pk);
    }

    /**
     * @see #updatePlayerListData(UUID, long, String, Skin, String, Color, Player[])
     */
    public void updatePlayerListData(UUID uuid, long entityId, String name, Skin skin, String xboxUserId, Color color, Collection<Player> players) {
        this.updatePlayerListData(uuid, entityId, name, skin, xboxUserId, color, players.toArray(Player.EMPTY_ARRAY));
    }

    public void removePlayerListData(UUID uuid) {
        this.removePlayerListData(uuid, this.playerList.values());
    }

    /**
     * Remove player list data for all players in the array.
     *
     * @param players player array
     */
    public void removePlayerListData(UUID uuid, Player[] players) {
        PlayerListPacket pk = new PlayerListPacket();
        pk.type = PlayerListPacket.TYPE_REMOVE;
        pk.entries = new PlayerListPacket.Entry[]{new PlayerListPacket.Entry(uuid)};
        Server.broadcastPacket(players, pk);
    }

    /**
     * Remove this player's player list data.
     *
     * @param player The player
     */

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
     * Send a player list packet to a player.
     *
     * @param player The player
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
                        p.getLoginChainData().getXUID(),
                        p.getLocatorBarColor()))
                .toArray(PlayerListPacket.Entry[]::new);

        player.dataPacket(pk);
    }

    /**
     * Get all unique player UUIDs that have connected to the server during the current uptime.
     *
     * @return Set of UUIDs
     */
    public Set<UUID> getUniquePlayers() {
        return uniquePlayers;
    }

    /**
     * Get the player instance from the specified UUID.
     *
     * @param uuid uuid
     * @return Player example can be empty
     */
    public Optional<Player> getPlayer(UUID uuid) {
        Preconditions.checkNotNull(uuid, "uuid");
        return Optional.ofNullable(playerList.get(uuid));
    }

    /**
     * Find the UUID corresponding to the specified player name from the database.
     *
     * @param name player name
     * @return The player's UUID, which can be empty.
     */
    public Optional<UUID> lookupName(String name) {
        byte[] nameBytes = name.toLowerCase(Locale.ENGLISH).getBytes(StandardCharsets.UTF_8);
        byte[] uuidBytes = playerDataDB.get(nameBytes);
        if (uuidBytes == null) {
            return Optional.empty();
        }

        if (uuidBytes.length != 16) {
            log.warn("Invalid uuid in name lookup database detected! Removing");
            playerDataDB.delete(nameBytes);
            return Optional.empty();
        }

        ByteBuffer buffer = ByteBuffer.wrap(uuidBytes);
        return Optional.of(new UUID(buffer.getLong(), buffer.getLong()));
    }

    /**
     * Update the UUID of the specified player name in the database, or add it if it does not exist.
     *
     * @param info the player info
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
        boolean xboxAuthEnabled = this.settings.baseSettings().xboxAuth();
        if (info instanceof XboxLivePlayerInfo || !xboxAuthEnabled) {
            playerDataDB.put(nameBytes, array);
        }
    }

    public IPlayer getOfflinePlayer(final String name) {
        IPlayer result = this.getPlayerExact(name.toLowerCase(Locale.ENGLISH));
        if (result != null) {
            return result;
        }

        return lookupName(name).map(uuid -> new OfflinePlayer(this, uuid))
                .orElse(new OfflinePlayer(this, name));
    }

    /**
     * Get a player instance from the specified UUID, either online or offline.
     *
     * @param uuid uuid
     * @return player
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
     * create is false
     *
     * @see #getOfflinePlayerData(UUID, boolean)
     */
    public CompoundTag getOfflinePlayerData(UUID uuid) {
        return getOfflinePlayerData(uuid, false);
    }

    /**
     * Retrieve the NBT data for the player specified by the UUID
     *
     * @param uuid   UUID of the player to get data from
     * @param create If player data does not exist, whether to create.
     * @return {@link CompoundTag}
     */
    public CompoundTag getOfflinePlayerData(UUID uuid, boolean create) {
        return getOfflinePlayerDataInternal(uuid, create);
    }

    public CompoundTag getOfflinePlayerData(String name) {
        return getOfflinePlayerData(name, false);
    }

    public CompoundTag getOfflinePlayerData(String name, boolean create) {
        Optional<UUID> uuid = lookupName(name);
        if (uuid.isEmpty()) {
            log.warn("Invalid uuid in name lookup database detected! Removing");
            playerDataDB.delete(name.getBytes(StandardCharsets.UTF_8));
            return null;
        }
        return getOfflinePlayerDataInternal(uuid.get(), create);
    }

    public boolean hasOfflinePlayerData(String name) {
        Optional<UUID> uuid = lookupName(name);
        if (uuid.isEmpty()) {
            log.warn("Invalid uuid in name lookup database detected! Removing");
            playerDataDB.delete(name.getBytes(StandardCharsets.UTF_8));
            return false;
        }
        return hasOfflinePlayerData(uuid.get());
    }

    public boolean hasOfflinePlayerData(UUID uuid) {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.putLong(uuid.getMostSignificantBits());
        buffer.putLong(uuid.getLeastSignificantBits());
        byte[] bytes = playerDataDB.get(buffer.array());
        return bytes != null;
    }

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
     * @see #saveOfflinePlayerData(String, CompoundTag, boolean)
     */
    public void saveOfflinePlayerData(UUID uuid, CompoundTag tag) {
        this.saveOfflinePlayerData(uuid, tag, false);
    }

    /**
     * @see #saveOfflinePlayerData(String, CompoundTag, boolean)
     */
    public void saveOfflinePlayerData(UUID uuid, CompoundTag tag, boolean async) {
        this.saveOfflinePlayerData(uuid.toString(), tag, async);
    }

    /**
     * @see #saveOfflinePlayerData(String, CompoundTag, boolean)
     */
    public void saveOfflinePlayerData(String name, CompoundTag tag) {
        this.saveOfflinePlayerData(name, tag, false);
    }

    /**
     * Save player data, players can be offline.
     *
     * @param nameOrUUid the name or uuid
     * @param tag        nbt data
     * @param async      Whether to save asynchronously
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

                //doing it like this ensures that the player data will be saved in a server shutdown
                @Override
                public void onCancel() {
                    if (!hasRun.getAndSet(true)) {
                        saveOfflinePlayerDataInternal(tag, uuid);
                    }
                }
            }, async);
        }
    }

    private void saveOfflinePlayerDataInternal(CompoundTag tag, UUID uuid) {
        try {
            cleanupOfflinePlayerData(tag);
            byte[] bytes = NBTIO.writeGZIPCompressed(tag, ByteOrder.BIG_ENDIAN);
            ByteBuffer buffer = ByteBuffer.allocate(16);
            buffer.putLong(uuid.getMostSignificantBits());
            buffer.putLong(uuid.getLeastSignificantBits());
            playerDataDB.put(buffer.array(), bytes);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void cleanupOfflinePlayerData(CompoundTag tag) {
        tag.remove("Colors");
        tag.remove("PieceTintColors");
        tag.remove("Skin");
    }

    /**
     * Get an online player from the player name, this method is a fuzzy match and will be returned as long as the player name has the name prefix.
     *
     * @param name player name
     * @return Player instance object, failed to get null
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
     * Get an online player from a player name, this method is an exact match and returns when the player name string is identical.
     *
     * @param name player name
     * @return Player instance object, failed to get null
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
     * Specify a partial player name and return all players with or equal to that name.
     *
     * @param partialName partial name
     * @return All players matched, if not matched then an empty array
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
     * Get all online players Map.
     *
     * @return a map of players uuid and a player instance object
     */
    public Map<UUID, Player> getOnlinePlayers() {
        return ImmutableMap.copyOf(playerList);
    }

    /**
     * Deletes all player data (both UUID mapping and player data) for the specified player name.
     * This method handles the LevelDB structure used by PowerNukkitX where player data is stored
     * in two separate databases: one for name-to-UUID mapping and another for actual player data.
     *
     * @param name The player name to delete it (case-insensitive)
     */
    public void deletePlayerData(String name) {
        try {
            byte[] nameBytes = name.toLowerCase(Locale.ENGLISH).getBytes(StandardCharsets.UTF_8);
            byte[] uuidBytes = playerDataDB.get(nameBytes);

            if (uuidBytes != null && uuidBytes.length == 16) {
                ByteBuffer buffer = ByteBuffer.wrap(uuidBytes);
                UUID uuid = new UUID(buffer.getLong(), buffer.getLong());
                String uuidStr = uuid.toString();

                playerDataDB.delete(uuidBytes);  // Delete from player data DB
                playerDataDB.delete(nameBytes);   // Delete name-to-UUID mapping

                log.info("{} player data deleted (UUID: {})", name, uuidStr);
            } else {
                log.warn("{} player not found or invalid UUID data", name);
            }
        } catch (Exception e) {
            log.error("Error deleting player data for {}", name, e);
        }
    }

    /**
     * Deletes all player data for the specified UUID.
     *
     * @param uuid The UUID of the player to delete
     */
    public void deletePlayerData(UUID uuid) {
        try {
            ByteBuffer buffer = ByteBuffer.allocate(16);
            buffer.putLong(uuid.getMostSignificantBits());
            buffer.putLong(uuid.getLeastSignificantBits());
            byte[] uuidBytes = buffer.array();

            playerDataDB.delete(uuidBytes);

            try (DBIterator iterator = playerDataDB.iterator()) {
                for (iterator.seekToFirst(); iterator.hasNext(); iterator.next()) {
                    byte[] key = iterator.peekNext().getKey();
                    byte[] value = iterator.peekNext().getValue();

                    if (Arrays.equals(value, uuidBytes)) {
                        playerDataDB.delete(key);
                        String playerName = new String(key, StandardCharsets.UTF_8);
                        log.info("Deleted name mapping for {}", playerName);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error deleting player data for UUID {}", uuid, e);
        }
    }


    // endregion

    // region constants

    /**
     * @return The name of server
     */
    public String getName() {
        return "PowerNukkitX";
    }

    public String getNukkitVersion() {
        return Nukkit.VERSION;
    }

    public String getBStatsNukkitVersion() {
        return Nukkit.VERSION;
    }

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
     * @return server UUID
     */
    public UUID getServerUniqueId() {
        return this.serverID;
    }

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

    @NotNull
    public PositionTrackingService getPositionTrackingService() {
        return positionTrackingService;
    }

    // region crafting a recipe

    /**
     * Send a recipe list packet to a player.
     *
     * @param player the player
     */
    public void sendRecipeList(Player player) {
        player.getSession().sendRawPacket(ProtocolInfo.CRAFTING_DATA_PACKET, Registries.RECIPE.getCraftingPacket());
    }

    /**
     * Register the recipe to recipe manager
     *
     * @param recipe the recipe
     */
    public void addRecipe(Recipe recipe) {
        Registries.RECIPE.register(recipe);
    }

    public RecipeRegistry getRecipeRegistry() {
        return Registries.RECIPE;
    }

    // endregion

    // region levels

    /**
     * @return Get all the game world
     */
    public Map<Integer, Level> getLevels() {
        return levels;
    }

    /**
     * @return Get the default overworld
     */
    public Level getDefaultLevel() {
        return defaultLevel;
    }

    /**
     * Set default overworld
     */
    public void setDefaultLevel(Level defaultLevel) {
        if (defaultLevel == null || (this.isLevelLoaded(defaultLevel.getName()) && defaultLevel != this.defaultLevel)) {
            this.defaultLevel = defaultLevel;
        }
    }

    /**
     * @param name the level name
     * @return Is the world already loaded
     */
    public boolean isLevelLoaded(String name) {
        return this.getLevelByName(name) != null;
    }

    /**
     * Get world from world id, 0 OVERWORLD 1 NETHER 2 THE_END
     *
     * @param levelId world id
     * @return level level instance
     */
    public Level getLevel(int levelId) {
        if (this.levels.containsKey(levelId)) {
            return this.levels.get(levelId);
        }
        return null;
    }

    /**
     * Get world from world name, {@code overworld nether the_end}
     *
     * @param name world name
     * @return level instance
     */
    public Level getLevelByName(String name) {
        for (Level level : this.levelArray) {
            if (level.getName().equals(name)) {
                return level;
            }
        }
        return null;
    }

    public boolean unloadLevel(Level level) {
        return this.unloadLevel(level, false);
    }

    /**
     * Unloads the level
     *
     * @param level       the level
     * @param forceUnload Whether force to unload.
     * @return was the uninstallation successful
     */
    public boolean unloadLevel(Level level, boolean forceUnload) {
        if (level == this.getDefaultLevel() && !forceUnload) {
            throw new IllegalStateException("The default level cannot be unloaded while running, please switch levels.");
        }
        return level.unload(forceUnload);

    }

    public @Nullable LevelConfig getLevelConfig(String levelFolderName) {
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
            //verify the provider
            Class<? extends LevelProvider> provider = LevelProviderManager.getProvider(path);
            if (provider == null) {
                log.error(this.getLanguage().tr("nukkit.level.loadError", levelFolderName, "Unknown provider"));
                return null;
            }
            Map<Integer, LevelConfig.GeneratorConfig> map = new HashMap<>();
            //todo nether the_end overworld
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
     * Loads the selected level by its folder name
     *
     * @param levelFolderName the level folder name
     * @return whether load success
     */
    public boolean loadLevel(String levelFolderName) {
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
            String levelName = levelFolderName + (generators.size() > 1 ? entry.getValue().dimensionData().getSuffix() : "");
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
        if (tickCounter != 0) { // Update world enum when loading
            WorldCommand.WORLD_NAME_ENUM.updateSoftEnum();
        }
        return true;
    }

    /**
     * Generates a level with selected name and LevelConfig options.
     *
     * @param name The level name
     * @param levelConfig The level config
     * @return boolean
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
                log.error("The levelConfig is not exists under the {} path", path);
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
                String levelName = name + (levelConfig.generators().size() > 1 ? entry.getValue().dimensionData().getSuffix() : "");
                if (this.isLevelLoaded(levelName)) {
                    log.warn("level {} has already been loaded!", levelName);
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
    // endregion

    // region Ban, OP and Whitelist
    public BanList getNameBans() {
        return this.banByName;
    }

    public BanList getIPBans() {
        return this.banByIP;
    }

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

    public void addWhitelist(String name) {
        this.whitelist.set(name.toLowerCase(Locale.ENGLISH), true);
        this.whitelist.save(true);
    }

    public void removeWhitelist(String name) {
        this.whitelist.remove(name.toLowerCase(Locale.ENGLISH));
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

    // region configs

    public int getMaxPlayers() {
        return maxPlayers;
    }

    /**
     * Set the players count is allowed
     *
     * @param maxPlayers the max players
     */
    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
        this.getNetwork().getPong().maximumPlayerCount(maxPlayers).update(this.getNetwork());
    }

    /**
     * @return The server port
     */
    public int getPort() {
        return this.settings.baseSettings().port();
    }

    /**
     * @return The server view distance
     */
    public int getViewDistance() {
        return this.settings.gameplaySettings().viewDistance();
    }

    /**
     * @return The server ip
     */
    public String getIp() {
        return this.settings.baseSettings().ip();
    }

    /**
     * @return Does the server automatically save
     */
    public boolean getAutoSave() {
        return this.autoSave;
    }

    /**
     * Set server autosave
     *
     * @param autoSave Whether to save automatically
     */
    public void setAutoSave(boolean autoSave) {
        this.autoSave = autoSave;
        for (Level level : this.levelArray) {
            level.setAutoSave(this.autoSave);
        }
    }

    /**
     * Get the gamemode of the server
     *
     * @return gamemode id
     */
    public int getGamemode() {
        return this.settings.gameplaySettings().gamemode() & 0b11;

    }

    public boolean getForceGamemode() {
        return this.settings.gameplaySettings().forceGamemode();
    }

    /**
     * default {@code direct=false}
     *
     * @see #getGamemodeString(int, boolean)
     */
    public static String getGamemodeString(int mode) {
        return getGamemodeString(mode, false);
    }

    /**
     * Get game mode string from gamemode id.
     *
     * @param mode   gamemode id
     * @param direct If true, the string is returned directly, and if false, the hard-coded string representing the game mode is returned.
     * @return Gamemode string
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
     * Get gamemode from string
     *
     * @param gamemodeString A string representing the game mode, e.g., 0 for survival...
     * @return gamemode id
     */
    public static int getGamemodeFromString(String gamemodeString) {
        return switch (gamemodeString.trim().toLowerCase(Locale.ENGLISH)) {
            case "0", "survival", "s" -> Player.SURVIVAL;
            case "1", "creative", "c" -> Player.CREATIVE;
            case "2", "adventure", "a" -> Player.ADVENTURE;
            case "3", "spectator", "spc", "view", "v" -> Player.SPECTATOR;
            default -> -1;
        };
    }

    /**
     * Get game difficulty from string
     *
     * @param difficultyString A string representing the game difficulty, e.g., 0,peaceful...
     * @return game difficulty id
     */
    public static int getDifficultyFromString(String difficultyString) {
        return switch (difficultyString.trim().toLowerCase(Locale.ENGLISH)) {
            case "0", "peaceful", "p" -> 0;
            case "1", "easy", "e" -> 1;
            case "2", "normal", "n" -> 2;
            case "3", "hard", "h" -> 3;
            default -> -1;
        };
    }

    /**
     * Get server game difficulty
     *
     * @return game difficulty id
     */
    public int getDifficulty() {
        return this.settings.gameplaySettings().difficulty();
    }

    /**
     * Set server game difficulty
     *
     * @param difficulty game difficulty id
     */
    public void setDifficulty(int difficulty) {
        int value = difficulty;
        if (value < 0) value = 0;
        if (value > 3) value = 3;
        this.settings.gameplaySettings().difficulty(value);
    }

    /**
     * @return Whether to start server whitelist
     */
    public boolean hasWhitelist() {
        return this.settings.baseSettings().allowList();
    }

    /**
     * @return Get server birth point protection radius
     */
    public int getSpawnRadius() {
        return this.settings.playerSettings().spawnRadius();
    }

    /**
     * @return Whether the server is in hardcore mode
     */
    public boolean isHardcore() {
        return this.settings.gameplaySettings().hardcore();
    }

    /**
     * @return Get default gamemode
     */
    public int getDefaultGamemode() {
        if (this.defaultGamemode == Integer.MAX_VALUE) {
            this.defaultGamemode = this.getGamemode();
        }
        return this.defaultGamemode;
    }

    /**
     * Set the default gamemode for the server.
     *
     * @param defaultGamemode the default gamemode
     */
    public void setDefaultGamemode(int defaultGamemode) {
        this.defaultGamemode = defaultGamemode;
        this.getNetwork().getPong().gameType(Server.getGamemodeString(defaultGamemode, true)).update(this.getNetwork());
    }

    /**
     * @return Get server motd
     */
    public String getMotd() {
        return this.settings.baseSettings().motd();
    }

    /**
     * Set the motd of server.
     *
     * @param motd the motd content
     */
    public void setMotd(String motd) {
        this.settings.baseSettings().motd(motd);
        this.getNetwork().getPong().motd(motd).update(this.getNetwork());
    }

    /**
     * @return Get the server subheading
     */
    public String getSubMotd() {
        String subMotd = this.settings.baseSettings().subMotd();
        if (subMotd.isEmpty()) {
            subMotd = "powernukkitx.org";
        }
        return subMotd;
    }

    /**
     * Set the sub motd of server.
     *
     * @param subMotd the sub motd
     */
    public void setSubMotd(String subMotd) {
        this.settings.baseSettings().subMotd(subMotd);
        this.getNetwork().getPong().subMotd(subMotd).update(this.getNetwork());
    }

    /**
     * @return Whether to force the use of server resource pack
     */
    public boolean getForceResources() {
        return this.settings.gameplaySettings().forceResources();
    }

    /**
     * @return Whether to force the use of the server resource pack while allowing the loading of the client resource pack
     */
    public boolean getForceResourcesAllowOwnPacks() {
        return this.settings.gameplaySettings().allowClientPacks();
    }

    private LangCode mapInternalLang(String langName) {
        return switch (langName) {
            case "bra" -> LangCode.valueOf("pt_BR");
            case "chs" -> LangCode.valueOf("zh_CN");
            case "cht" -> LangCode.valueOf("zh_TW");
            case "cze" -> LangCode.valueOf("cs_CZ");
            case "deu" -> LangCode.valueOf("de_DE");
            case "fil" -> LangCode.valueOf("tl_PH");
            case "fin" -> LangCode.valueOf("fi_FI");
            case "eng" -> LangCode.valueOf("en_US");
            case "fra" -> LangCode.valueOf("fr_FR");
            case "idn" -> LangCode.valueOf("id_ID");
            case "jpn" -> LangCode.valueOf("ja_JP");
            case "kor" -> LangCode.valueOf("ko_KR");
            case "ltu" -> LangCode.valueOf("lt_LT");
            case "pol" -> LangCode.valueOf("pl_PL");
            case "rus" -> LangCode.valueOf("ru_RU");
            case "spa" -> LangCode.valueOf("es_ES");
            case "tur" -> LangCode.valueOf("tr_TR");
            case "ukr" -> LangCode.valueOf("uk_UA");
            case "vie" -> LangCode.valueOf("vi_VN");
            default -> throw new IllegalArgumentException();
        };
    }

    public BaseLang getLanguage() {
        return baseLang;
    }

    public LangCode getLanguageCode() {
        return baseLangCode;
    }

    public ServerSettings getSettings() {
        return settings;
    }

    public boolean isNetherAllowed() {
        return this.allowNether;
    }

    public boolean isTheEndAllowed() {
        return this.allowTheEnd;
    }

    public boolean isIgnoredPacket(Class<? extends DataPacket> clazz) {
        return this.getSettings().debugSettings().ignoredPackets().contains(clazz.getSimpleName());
    }

    public int getServerAuthoritativeMovement() {
        return serverAuthoritativeMovementMode;
    }
    // endregion

    // region threading

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

    public ForkJoinPool getComputeThreadPool() {
        return computeThreadPool;
    }

    public boolean allowVibrantVisuals() {
        return settings.gameplaySettings().allowVibrantVisuals();
    }

    public List<ExperimentEntry> getExperiments() {
        return experiments;
    }
  
  

    /** Allow plugins to override the default DP group UUID (e.g., when migrating from BDS). */
    public static void setDefaultDynamicPropertiesGroupUUID(String uuid) {
        if (uuid == null || !DP_UUID_CANON.matcher(uuid).matches()) {
            log.warn("DynamicProperties default group UUID rejected: '{}'", uuid);
            return;
        }
        DP_DEFAULT_GROUP_UUID = uuid.toLowerCase();
    }

    public static String getDynamicPropertyRoot()               { return DP_ROOT; }
    public static String getDefaultDynamicPropertiesGroupUUID() { return DP_DEFAULT_GROUP_UUID; }
    public static int    getDynamicPropertiesMaxStringBytes()   { return DP_MAX_STRING_BYTES; }
    public static double getDynamicPropertiesNumberAbsMax()     { return DP_NUMBER_ABS_MAX; }

    /**
     * Remove a DynamicProperty by key id.
     *
     * @param key the key id of the DynamicProperty
     */
    public Server removeDynamicProperty(String key) {
        LevelDBProvider provider = getWorldDynamicPropertiesProvider();
        if (provider == null) return this;

        CompoundTag root = provider.getWorldDynamicProperties();
        if (root == null) return this;

        if (!root.contains(DP_ROOT)) return this;
        CompoundTag dyn = root.getCompound(DP_ROOT);

        CompoundTag group = dyn.getCompound(DP_DEFAULT_GROUP_UUID);
        if (group == null || !group.contains(key)) return this;

        group.remove(key);
        saveWorldDynamicPropertiesGroup(provider, DP_DEFAULT_GROUP_UUID, group);
        return this;
    }

    /**
     * Remove all DynamicProperties in the world.
     */
    public Server clearDynamicProperties() {
        LevelDBProvider provider = getWorldDynamicPropertiesProvider();
        if (provider == null) return this;

        CompoundTag root = provider.getWorldDynamicProperties();
        if (root == null) root = new CompoundTag();

        CompoundTag dyn = root.getCompound(DP_ROOT);
        if (dyn == null) dyn = new CompoundTag();

        dyn.putCompound(DP_DEFAULT_GROUP_UUID, new CompoundTag());
        root.putCompound(DP_ROOT, dyn);

        provider.setWorldDynamicProperties(root);
        provider.setWorldDynamicPropertiesDirty(true);
        return this;
    }

    /**
     * Set a double int DynamicProperty.
     *
     * @param key the key id of the DynamicProperty
     * @param value the double int value of the DynamicProperty
     */
    public Server setDynamicProperty(String key, Double value) {
        if (value == null) return removeDynamicProperty(key);
        if (!isFiniteAndInRange(value)) {
            log.warn("DynamicProperty '{}' rejected: out of numeric bounds or non-finite (value={})", key, value);
            return this;
        }
        LevelDBProvider provider = getWorldDynamicPropertiesProvider();
        if (provider == null) return this;

        CompoundTag g = ensureWorldDynamicPropertiesGroup(provider, DP_DEFAULT_GROUP_UUID);
        g.putDouble(key, value);
        saveWorldDynamicPropertiesGroup(provider, DP_DEFAULT_GROUP_UUID, g);
        return this;
    }

    /**
     * Set an int DynamicProperty.
     *
     * @param key the key id of the DynamicProperty
     * @param value the int value of the DynamicProperty
     */
    public Server setDynamicProperty(String key, Integer value) {
        return setDynamicProperty(key, value == null ? null : value.doubleValue());
    }

    /**
     * Set an int DynamicProperty.
     *
     * @param key the key id of the DynamicProperty
     * @param value the int value of the DynamicProperty
     */
    public Server setDynamicProperty(String key, Float value) {
        return setDynamicProperty(key, value == null ? null : value.doubleValue());
    }

    /**
     * Set a boolean DynamicProperty.
     *
     * @param key the key id of the DynamicProperty
     * @param bool the bool value of the DynamicProperty
     */
    public Server setDynamicProperty(String key, Boolean bool) {
        if (bool == null) return removeDynamicProperty(key);
        LevelDBProvider provider = getWorldDynamicPropertiesProvider();
        if (provider == null) return this;

        CompoundTag g = ensureWorldDynamicPropertiesGroup(provider, DP_DEFAULT_GROUP_UUID);
        g.putBoolean(key, bool);
        saveWorldDynamicPropertiesGroup(provider, DP_DEFAULT_GROUP_UUID, g);
        return this;
    }

    /**
     * Set a string DynamicProperty.
     *
     * @param key the key id of the DynamicProperty
     * @param string the string value of the DynamicProperty
     */
    public Server setDynamicProperty(String key, String string) {
        if (string == null) return removeDynamicProperty(key);
        if (!fitsUtf8Limit(string)) {
            log.warn("DynamicProperty '{}' rejected: string exceeds {} UTF-8 bytes", key, DP_MAX_STRING_BYTES);
            return this;
        }
        LevelDBProvider provider = getWorldDynamicPropertiesProvider();
        if (provider == null) return this;

        CompoundTag g = ensureWorldDynamicPropertiesGroup(provider, DP_DEFAULT_GROUP_UUID);
        g.putString(key, string);
        saveWorldDynamicPropertiesGroup(provider, DP_DEFAULT_GROUP_UUID, g);
        return this;
    }

    /**
     * Set a Vec3 DynamicProperty.
     *
     * @param key the key id of the DynamicProperty
     * @param vec3 the vec3 value of the DynamicProperty
     */
    public Server setVec3DynamicProperty(String key, Vector3 vec3) {
        if (vec3 == null) return removeDynamicProperty(key);
        if (!isFiniteAndInRange(vec3.x) || !isFiniteAndInRange(vec3.y) || !isFiniteAndInRange(vec3.z)) {
            log.warn("DynamicProperty '{}' rejected: vec3 has component(s) out of bounds or non-finite (x={}, y={}, z={})", key, vec3.x, vec3.y, vec3.z);
            return this;
        }
        ListTag<FloatTag> list = new ListTag<>();
        list.add(new FloatTag((float) vec3.x));
        list.add(new FloatTag((float) vec3.y));
        list.add(new FloatTag((float) vec3.z));

        LevelDBProvider provider = getWorldDynamicPropertiesProvider();
        if (provider == null) return this;

        CompoundTag g = ensureWorldDynamicPropertiesGroup(provider, DP_DEFAULT_GROUP_UUID);
        g.putList(key, list);
        saveWorldDynamicPropertiesGroup(provider, DP_DEFAULT_GROUP_UUID, g);
        return this;
    }

    /**
     * Set a Vec3 DynamicProperty.
     *
     * @param key the key id of the DynamicProperty
     * @param xyz a map with keys "x","y","z" and numeric values, e.g. {x: 400, y: 60, z: 300}
     */
    public Server setVec3DynamicProperty(String key, Map<String, Number> xyz) {
        if (xyz == null) return removeDynamicProperty(key);

        Number nx = xyz.get("x"), ny = xyz.get("y"), nz = xyz.get("z");
        if (nx == null || ny == null || nz == null) {
            log.warn("DynamicProperty '{}' rejected: vec3 map must contain numeric keys 'x','y','z'", key);
            return this;
        }

        // Delegate to the Vector3 overload (keeps validation + storage consistent)
        return setVec3DynamicProperty(key, new Vector3(nx.doubleValue(), ny.doubleValue(), nz.doubleValue()));
    }

    /**
     * Get a double int DynamicProperty.
     *
     * @param key the key id of the DynamicProperty
     * @return the double int value or null if not available.
     */
    public Double getDoubleDynamicProperty(String key) {
        Tag t = findWorldDynamicPropertyTagInConfiguredGroup(key);
        if (t == null) return null;

        return switch (t) {
            case DoubleTag d -> d.data;
            case FloatTag  f -> (double) f.data;
            case IntTag    i -> (double) i.data;
            case LongTag   l -> (double) l.data;
            case ShortTag  s -> (double) s.data;
            case ByteTag   b -> (double) b.data;
            case StringTag s -> {
                try { yield Double.parseDouble(s.data.trim()); }
                catch (NumberFormatException ignored) { yield null; }
            }
            default -> null;
        };
    }

    /**
     * Get a double int DynamicProperty.
     *
     * @param key the key id of the DynamicProperty
     * @param defaultValue the default value to be returned if null.
     * @return the double int value or defaultValue if not available.
     */
    public Double getDoubleDynamicProperty(String key, double defaultValue) {
        Double d = getDoubleDynamicProperty(key);
        return (d != null) ? d : defaultValue;
    }

    /**
     * Get an int DynamicProperty.
     *
     * @param key the key id of the DynamicProperty
     * @return the int value or defaultValue if not available.
     */
    public Integer getIntDynamicProperty(String key) {
        Double d = getDoubleDynamicProperty(key);
        if (d == null) return null;
        return (int) Math.floor(d);
    }

    /**
     * Get an int DynamicProperty.
     *
     * @param key the key id of the DynamicProperty
     * @param defaultValue the default value to be returned if null.
     * @return the int value or defaultValue if not available.
     */
    public int getIntDynamicProperty(String key, int defaultValue) {
        Integer i = getIntDynamicProperty(key);
        return (i != null) ? i : defaultValue;
    }

    /**
     * Get a float DynamicProperty.
     *
     * @param key the key id of the DynamicProperty
     * @return the float value or null if not available.
     */
    public Float getFloatDynamicProperty(String key) {
        Double d = getDoubleDynamicProperty(key);
        if (d == null) return null;
        return d.floatValue();
    }

    /**
     * Get a float DynamicProperty.
     *
     * @param key the key id of the DynamicProperty
     * @param defaultValue the default value to be returned if null.
     * @return the float value or defaultValue if not available.
     */
    public float getFloatDynamicProperty(String key, float defaultValue) {
        Float f = getFloatDynamicProperty(key);
        return (f != null) ? f : defaultValue;
    }

    /**
     * Get a boolean DynamicProperty.
     *
     * @param key the key id of the DynamicProperty
     * @return the bool value or false if not available.
     */
    public Boolean getBoolDynamicProperty(String key) {
        Tag t = findWorldDynamicPropertyTagInConfiguredGroup(key);
        if (t == null) return null;
        if (t instanceof ByteTag)   return ((ByteTag) t).data != 0;
        Double d = getDoubleDynamicProperty(key);
        if (d != null) return d != 0.0;
        if (t instanceof StringTag) {
            String s = ((StringTag) t).data.trim().toLowerCase();
            if ("true".equals(s) || "1".equals(s))  return true;
            if ("false".equals(s) || "0".equals(s)) return false;
        }
        return null;
    }

    /**
     * Get a boolean DynamicProperty.
     *
     * @param key the key id of the DynamicProperty
     * @param defaultValue the default value to be returned if null.
     * @return the bool value or defaultValue if not available.
     */
    public boolean getBoolDynamicProperty(String key, boolean defaultValue) {
        Boolean b = getBoolDynamicProperty(key);
        return (b != null) ? b : defaultValue;
    }

    /**
     * Get a string DynamicProperty.
     *
     * @param key the key id of the DynamicProperty
     * @return the bool value or null if not available.
     */
    public String getStringDynamicProperty(String key) {
        Tag t = findWorldDynamicPropertyTagInConfiguredGroup(key);
        if (t == null) return null;

        return switch (t) {
            case StringTag s -> s.data;
            case DoubleTag d -> String.valueOf(d.data);
            case FloatTag  f -> String.valueOf(f.data);
            case IntTag    i -> String.valueOf(i.data);
            case LongTag   l -> String.valueOf(l.data);
            case ShortTag  s -> String.valueOf(s.data);
            case ByteTag   b -> String.valueOf(b.data);
            default -> null;
        };
    }

    /**
     * Get a string DynamicProperty.
     *
     * @param key the key id of the DynamicProperty
     * @param defaultValue the default value to be returned if null.
     * @return the string value or defaultValue if not available.
     */
    public String getStringDynamicProperty(String key, String defaultValue) {
        String s = getStringDynamicProperty(key);
        return (s != null) ? s : defaultValue;
    }

    /**
     * Get a vec3 DynamicProperty.
     *
     * @param key the key id of the DynamicProperty
     * @return the bool value or null if not available.
     */
    public Vector3 getVec3DynamicProperty(String key) {
        Tag t = findWorldDynamicPropertyTagInConfiguredGroup(key);
        if (t == null) return null;

        if (t instanceof ListTag<?> list &&
            list.size() == 3 &&
            list.get(0) instanceof FloatTag fx &&
            list.get(1) instanceof FloatTag fy &&
            list.get(2) instanceof FloatTag fz) {

            float x = fx.data;
            float y = fy.data;
            float z = fz.data;
            return new Vector3(x, y, z);
        }
        return null;
    }

    // Dynamic Properties Helpers start
    private static boolean isFiniteAndInRange(double v) {
        return !Double.isNaN(v) && !Double.isInfinite(v) && Math.abs(v) <= DP_NUMBER_ABS_MAX;
    }

    private static boolean fitsUtf8Limit(String s) {
        if (s == null) return false;
        int byteCount = s.getBytes(StandardCharsets.UTF_8).length;
        return byteCount <= DP_MAX_STRING_BYTES;
    }

    private LevelDBProvider getWorldDynamicPropertiesProvider() {
        Level level = this.getDefaultLevel();
        if (level == null) return null;

        LevelProvider provider = level.getProvider();
        if (!(provider instanceof LevelDBProvider ldb)) return null;

        return ldb;
    }

    private CompoundTag ensureWorldDynamicPropertiesGroup(LevelDBProvider provider, String groupId) {
        CompoundTag root = provider.getWorldDynamicProperties();
        if (root == null) root = new CompoundTag();

        CompoundTag dyn = root.getCompound(DP_ROOT);
        if (!root.contains(DP_ROOT) || dyn == null) {
            dyn = new CompoundTag();
            root.putCompound(DP_ROOT, dyn);
        }

        CompoundTag group = dyn.getCompound(groupId);
        if (group == null) group = new CompoundTag();

        dyn.putCompound(groupId, group);
        provider.setWorldDynamicProperties(root);
        return group;
    }


    private CompoundTag getWorldDynamicPropertiesGroup(LevelDBProvider provider, String groupId) {
        CompoundTag root = provider.getWorldDynamicProperties();
        if (root == null || !root.contains(DP_ROOT)) return null;
        CompoundTag dyn = root.getCompound(DP_ROOT);
        if (dyn == null) return null;
        return dyn.getCompound(groupId);
    }

    private void saveWorldDynamicPropertiesGroup(LevelDBProvider provider, String groupId, CompoundTag group) {
        CompoundTag root = provider.getWorldDynamicProperties();
        if (root == null) root = new CompoundTag();

        CompoundTag dyn = root.getCompound(DP_ROOT);
        if (!root.contains(DP_ROOT) || dyn == null) {
            dyn = new CompoundTag();
            root.putCompound(DP_ROOT, dyn);
        }

        dyn.putCompound(groupId, group);
        provider.setWorldDynamicProperties(root);
        provider.setWorldDynamicPropertiesDirty(true);
    }

    private Tag findWorldDynamicPropertyTagInConfiguredGroup(String key) {
        LevelDBProvider provider = getWorldDynamicPropertiesProvider();
        if (provider == null) return null;

        CompoundTag group = getWorldDynamicPropertiesGroup(provider, DP_DEFAULT_GROUP_UUID);
        if (group == null || !group.contains(key)) return null;
        return group.get(key);
    }
    // Dynamic Properties Helpers end

  
  
    // TODO: It will block NukkitConsole and cannot be turned off.
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
         * @throws NullPointerException if the pool is null
         */
        ComputeThread(ForkJoinPool pool, AtomicInteger threadCount) {
            super(pool);
            this.setName("ComputeThreadPool-thread-" + threadCount.getAndIncrement());
        }
    }

    private static class ComputeThreadPoolThreadFactory implements ForkJoinPool.ForkJoinWorkerThreadFactory {
        private static final AtomicInteger threadCount = new AtomicInteger(0);
        public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
            return new ComputeThread(pool, threadCount);
        }
    }

    // endregion

}
