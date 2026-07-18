package org.powernukkitx;

import org.powernukkitx.block.BlockComposter;
import org.powernukkitx.block.dispenser.DispenseBehaviorRegister;
import org.powernukkitx.command.SimpleCommandMap;
import org.powernukkitx.config.ServerSettings;
import org.powernukkitx.config.YamlSnakeYamlConfigurer;
import org.powernukkitx.entity.Attribute;
import org.powernukkitx.entity.data.profession.Profession;
import org.powernukkitx.event.server.QueryRegenerateEvent;
import org.powernukkitx.item.enchantment.Enchantment;
import org.powernukkitx.lang.BaseLang;
import org.powernukkitx.level.DimensionEnum;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.format.LevelConfig;
import org.powernukkitx.level.format.leveldb.LevelDBProvider;
import org.powernukkitx.network.Network;
import org.powernukkitx.permission.BanList;
import org.powernukkitx.plugin.JavaPluginLoader;
import org.powernukkitx.positiontracking.PositionTrackingService;
import org.powernukkitx.registry.Registries;
import org.powernukkitx.scheduler.ServerScheduler;
import org.powernukkitx.utils.collection.FreezableArrayManager;
import eu.okaeri.configs.ConfigManager;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.ForkJoinPool;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Minimal but real server + level fixture. Boots every registry, wires a heavily
 * mocked {@link Server} (installed as the singleton) and opens a real LevelDB world
 * from the test resources. Enough runtime to construct entities, block entities and
 * exercise block behaviour that needs a level - none of which the pure unit tests reach.
 * <p>
 * Modelled on the project's original (disabled) GameMockExtension, rebuilt against the
 * current API.
 */
public final class ServerMockFixture {

    public static final Server server;
    public static final Level level;

    private ServerMockFixture() {
    }

    static {
        // Another test in the same fork may have left Mockito state dirty (unfinished
        // stubbing / inline-mock registrations). Flush it so our when(...) calls bind to
        // the right invocations and don't inherit a foreign mock's behaviour.
        try {
            org.mockito.Mockito.framework().clearInlineMocks();
        } catch (Throwable ignore) {
        }
        try {
            org.mockito.Mockito.validateMockitoUsage();
        } catch (Throwable ignore) {
        }

        Registries.ENTITY.init();
        Profession.init();
        Registries.BLOCKENTITY.init();
        Registries.BLOCK.init();
        Enchantment.init();
        Registries.ITEM_RUNTIMEID.init();
        Registries.POTION.init();
        Registries.ITEM.init();
        Registries.BIOME.init();
        Registries.FUEL.init();
        Registries.GENERATE_STAGE.init();
        Registries.GENERATOR.init();
        Registries.EFFECT.init();
        Attribute.init();
        BlockComposter.init();
        DispenseBehaviorRegister.init();

        server = mock(Server.class);
        BanList banList = mock(BanList.class);
        SimpleCommandMap simpleCommandMap = mock(SimpleCommandMap.class);

        ServerScheduler serverScheduler = new ServerScheduler();
        when(server.getScheduler()).thenReturn(serverScheduler);
        when(banList.getEntires()).thenReturn(new LinkedHashMap<>());
        when(server.getIPBans()).thenReturn(banList);
        when(server.getLanguage()).thenReturn(new BaseLang("eng", "src/main/resources/language"));

        try {
            FieldUtils.writeDeclaredStaticField(Server.class, "instance", server, true);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        final ServerSettings serverSettings = ConfigManager.create(ServerSettings.class, it -> {
            it.withConfigurer(new YamlSnakeYamlConfigurer());
            it.withBindFile("nukkit.yml");
            it.withRemoveOrphans(true);
            it.saveDefaults();
            it.load(true);
        });
        when(server.getSettings()).thenReturn(serverSettings);
        when(server.getApiVersion()).thenReturn("1.0.0");
        when(simpleCommandMap.getCommands()).thenReturn(java.util.Collections.emptyMap());

        TestPluginManager pluginManager = new TestPluginManager(server, simpleCommandMap);
        pluginManager.registerInterface(JavaPluginLoader.class);
        when(server.getPluginManager()).thenReturn(pluginManager);
        pluginManager.loadInternalPlugin();

        FreezableArrayManager freezableArrayManager = new FreezableArrayManager(
                serverSettings.performanceSettings().enable(),
                serverSettings.performanceSettings().slots(),
                serverSettings.performanceSettings().defaultTemperature(),
                serverSettings.performanceSettings().freezingPoint(),
                serverSettings.performanceSettings().absoluteZero(),
                serverSettings.performanceSettings().boilingPoint(),
                serverSettings.performanceSettings().melting(),
                serverSettings.performanceSettings().singleOperation(),
                serverSettings.performanceSettings().batchOperation());
        when(server.getFreezableArrayManager()).thenReturn(freezableArrayManager);

        when(server.getMotd()).thenReturn("PNX");
        when(server.getOnlinePlayers()).thenReturn(new HashMap<>());
        when(server.getGamemode()).thenReturn(1);
        when(server.getName()).thenReturn("PNX");
        when(server.getNukkitVersion()).thenReturn("1.0.0");
        when(server.getGitCommit()).thenReturn("1.0.0");
        when(server.getMaxPlayers()).thenReturn(100);
        when(server.hasWhitelist()).thenReturn(false);
        when(server.getPort()).thenReturn(19132);
        when(server.getIp()).thenReturn("127.0.0.1");

        final QueryRegenerateEvent queryRegenerateEvent = new QueryRegenerateEvent(server);
        when(server.getQueryInformation()).thenReturn(queryRegenerateEvent);
        when(server.getNetwork()).thenCallRealMethod();
        when(server.getAutoSave()).thenReturn(false);
        when(server.getTick()).thenReturn(1);
        when(server.getViewDistance()).thenReturn(4);
        when(server.getBaseTps()).thenReturn(20);
        when(server.getLevelTickExecutor()).thenReturn(java.util.concurrent.Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "fixture-level-tick");
            t.setDaemon(true);
            return t;
        }));

        ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
        when(server.getComputeThreadPool()).thenReturn(pool);
        when(server.getCommandMap()).thenReturn(simpleCommandMap);
        when(server.getScoreboardManager()).thenReturn(null);
        try {
            final PositionTrackingService positionTrackingService =
                    new PositionTrackingService(new File(PowerNukkitX.DATA_PATH,
                            "services/position_tracking_db_" + ProcessHandle.current().pid()));
            when(server.getPositionTrackingService()).thenReturn(positionTrackingService);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        doNothing().when(server).sendRecipeList(any());

        // Unique per JVM fork - LevelDB takes a process file lock, so a fixed dir would
        // collide when Gradle runs test workers in parallel.
        final String levelName = "newlevel_fixture_" + ProcessHandle.current().pid() + "_" + System.nanoTime();
        final File levelDir = new File("src/test/resources/" + levelName);
        try {
            FieldUtils.writeDeclaredField(server, "levelArray", Level.EMPTY_ARRAY, true);
            FieldUtils.writeDeclaredField(server, "autoSave", false, true);
            Network network = new Network(server);
            FieldUtils.writeDeclaredField(server, "network", network, true);

            FileUtils.copyDirectory(new File("src/test/resources/level"), levelDir);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> FileUtils.deleteQuietly(levelDir)));

        level = new Level(server, levelName, levelDir.getPath(),
                1, LevelDBProvider.class,
                new LevelConfig.GeneratorConfig("flat", 114514L, false, LevelConfig.AntiXrayMode.LOW,
                        true, DimensionEnum.OVERWORLD.getDimensionData(), new HashMap<>()));
        level.initLevel();

        HashMap<Integer, Level> levels = new HashMap<>();
        levels.put(1, level);
        when(server.getLevels()).thenReturn(levels);
        when(server.getDefaultLevel()).thenReturn(level);
    }

    /** Force class-load / static-init. */
    public static void boot() {
    }
}
