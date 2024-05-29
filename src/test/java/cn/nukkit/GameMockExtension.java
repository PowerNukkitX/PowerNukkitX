package cn.nukkit;

import cn.nukkit.block.BlockComposter;
import cn.nukkit.command.SimpleCommandMap;
import cn.nukkit.config.ServerSettings;
import cn.nukkit.config.YamlSnakeYamlConfigurer;
import cn.nukkit.dispenser.DispenseBehaviorRegister;
import cn.nukkit.entity.Attribute;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.entity.data.profession.Profession;
import cn.nukkit.event.server.QueryRegenerateEvent;
import cn.nukkit.inventory.HumanEnderChestInventory;
import cn.nukkit.inventory.HumanInventory;
import cn.nukkit.inventory.HumanOffHandInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.lang.BaseLang;
import cn.nukkit.level.DimensionEnum;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.LevelConfig;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.level.format.leveldb.LevelDBProvider;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.Network;
import cn.nukkit.network.connection.BedrockSession;
import cn.nukkit.network.process.DataPacketManager;
import cn.nukkit.network.protocol.types.PlayerInfo;
import cn.nukkit.permission.BanList;
import cn.nukkit.plugin.JavaPluginLoader;
import cn.nukkit.positiontracking.PositionTrackingService;
import cn.nukkit.registry.BlockRegistry;
import cn.nukkit.registry.Registries;
import cn.nukkit.scheduler.ServerScheduler;
import cn.nukkit.utils.ClientChainData;
import cn.nukkit.utils.collection.FreezableArrayManager;
import eu.okaeri.configs.ConfigManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Slf4j
public class GameMockExtension extends MockitoExtension {
    static BanList banList = mock(BanList.class);
    static TestPluginManager pluginManager;
    static SimpleCommandMap simpleCommandMap = mock(SimpleCommandMap.class);
    static ServerScheduler serverScheduler;
    static FreezableArrayManager freezableArrayManager;
    static Network network;
    public static Level level;

    final static Server server = mock(Server.class);
    final static GameMockExtension gameMockExtension;
    final static BlockRegistry BLOCK_REGISTRY;
    final static TestPlayer player;

    static {
        try (MockedStatic<Server> serverMockedStatic = Mockito.mockStatic(Server.class)) {
            serverMockedStatic.when(Server::getInstance).thenReturn(server);

            Registries.PACKET.init();
            Registries.ENTITY.init();
            Profession.init();
            Registries.BLOCKENTITY.init();
            Registries.BLOCKSTATE_ITEMMETA.init();
            Registries.BLOCK.init();
            Enchantment.init();
            Registries.ITEM_RUNTIMEID.init();
            Registries.POTION.init();
            Registries.ITEM.init();
            Registries.CREATIVE.init();
            Registries.BIOME.init();
            Registries.FUEL.init();
            Registries.GENERATE_STAGE.init();
            Registries.GENERATOR.init();
            Registries.RECIPE.init();
            Registries.EFFECT.init();
            Attribute.init();
            BlockComposter.init();
            DispenseBehaviorRegister.init();
            BLOCK_REGISTRY = Registries.BLOCK;

            serverScheduler = new ServerScheduler();
            when(server.getScheduler()).thenReturn(serverScheduler);
            when(banList.getEntires()).thenReturn(new LinkedHashMap<>());
            when(server.getIPBans()).thenReturn(banList);
            when(server.getLanguage()).thenReturn(new BaseLang("eng", "src/main/resources/language"));
            final ServerSettings serverSettings = ConfigManager.create(ServerSettings.class, it -> {
                it.withConfigurer(new YamlSnakeYamlConfigurer());
                it.withBindFile("nukkit.yml");
                it.withRemoveOrphans(true);
                it.saveDefaults();
                it.load(true);
            });
            when(server.getSettings()).thenReturn(serverSettings);
            when(server.getApiVersion()).thenReturn("1.0.0");
            when(simpleCommandMap.getCommands()).thenReturn(Collections.emptyMap());

            pluginManager = new TestPluginManager(server, simpleCommandMap);
            pluginManager.registerInterface(JavaPluginLoader.class);
            when(server.getPluginManager()).thenReturn(pluginManager);
            pluginManager.loadInternalPlugin();

            freezableArrayManager = new FreezableArrayManager(
                    server.getSettings().freezeArraySettings().enable(),
                    server.getSettings().freezeArraySettings().slots(),
                    server.getSettings().freezeArraySettings().defaultTemperature(),
                    server.getSettings().freezeArraySettings().freezingPoint(),
                    server.getSettings().freezeArraySettings().absoluteZero(),
                    server.getSettings().freezeArraySettings().boilingPoint(),
                    server.getSettings().freezeArraySettings().melting(),
                    server.getSettings().freezeArraySettings().singleOperation(),
                    server.getSettings().freezeArraySettings().batchOperation());
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
            when(server.getRecipeRegistry()).thenCallRealMethod();

            ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
            when(server.getComputeThreadPool()).thenReturn(pool);
            when(server.getCommandMap()).thenReturn(simpleCommandMap);
            when(server.getScoreboardManager()).thenReturn(null);
            try {
                final PositionTrackingService positionTrackingService = new PositionTrackingService(new File(Nukkit.DATA_PATH, "services/position_tracking_db"));
                when(server.getPositionTrackingService()).thenReturn(positionTrackingService);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            doNothing().when(server).sendRecipeList(any());
            try {
                FieldUtils.writeDeclaredField(server, "levelArray", Level.EMPTY_ARRAY, true);
                FieldUtils.writeDeclaredField(server, "autoSave", false, true);
                FieldUtils.writeDeclaredField(server, "tickAverage", new float[]{20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20}, true);
                FieldUtils.writeDeclaredField(server, "useAverage", new float[]{20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20}, true);
                network = new Network(server);
                FieldUtils.writeDeclaredField(server, "network", network, true);
                FieldUtils.writeDeclaredStaticField(Server.class, "instance", server, true);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    //mock player
    static {
        BedrockSession serverSession = mock(BedrockSession.class);
        PlayerInfo info = new PlayerInfo(
                "test",
                UUID.randomUUID(),
                null,
                mock(ClientChainData.class)
        );
        final DataPacketManager dataPacketManager = new DataPacketManager();
        when(serverSession.getDataPacketManager()).thenReturn(dataPacketManager);
        doNothing().when(serverSession).sendPacketImmediately(any());
        doNothing().when(serverSession).sendPacket(any());
        player = new TestPlayer(serverSession, info);
        player.adventureSettings = new AdventureSettings(player);
        player.loggedIn = true;
        player.spawned = true;
        TestUtils.setField(Player.class, player, "info", new PlayerInfo("test", UUID.nameUUIDFromBytes(new byte[]{1, 2, 3}), mock(Skin.class), mock(ClientChainData.class)));
        player.temporalVector = new Vector3(0, 100, 0);
        player.setInventories(new Inventory[]{
                new HumanInventory(player),
                new HumanOffHandInventory(player),
                new HumanEnderChestInventory(player)
        });
        PlayerHandle playerHandle = new PlayerHandle(player);
        playerHandle.addDefaultWindows();
        TestUtils.setField(Player.class, player, "foodData", new PlayerFood(player, 20, 20));
        try {
            FileUtils.copyDirectory(new File("src/test/resources/level"), new File("src/test/resources/newlevel"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        level = new Level(Server.getInstance(), "newlevel", "src/test/resources/newlevel",
                1, LevelDBProvider.class, new LevelConfig.GeneratorConfig("flat", 114514, false, LevelConfig.AntiXrayMode.LOW, true, DimensionEnum.OVERWORLD.getDimensionData(), new HashMap<>()));
        level.initLevel();

        HashMap<Integer, Level> map = new HashMap<>();
        map.put(1, level);
        when(server.getLevels()).thenReturn(map);

        Map<InetSocketAddress, Player> players = new HashMap<>();
        players.put(new InetSocketAddress("127.0.0.1", 63333), player);
        TestUtils.setField(Server.class, server, "players", players);

        player.level = level;
        player.setPosition(new Vector3(0, 100, 0));

        Thread t = new Thread(() -> {
            level.close();
            try {
                File file1 = Path.of("services").toFile();
                if (file1.exists()) {
                    FileUtils.deleteDirectory(file1);
                }
                File file2 = Path.of("src/test/resources/newlevel").toFile();
                if (file2.exists()) {
                    FileUtils.deleteDirectory(file2);
                }
                File file3 = Path.of("config.yml").toFile();
                if (file3.exists()) {
                    FileUtils.delete(file3);
                }
                System.out.println("TEST END!!!!!");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        Runtime.getRuntime().addShutdownHook(t);

        gameMockExtension = new GameMockExtension();
    }

    private MockedStatic<Server> serverMockedStatic;

    @Override
    public void beforeEach(ExtensionContext context) {
        serverMockedStatic = Mockito.mockStatic(Server.class);
        serverMockedStatic.when(Server::getInstance).thenReturn(server);
        super.beforeEach(context);
    }

    @Override
    public void afterEach(ExtensionContext context) {
        serverMockedStatic.close();
        super.afterEach(context);
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext context) throws ParameterResolutionException {
        boolean b = super.supportsParameter(parameterContext, context);
        return b || parameterContext.getParameter().getType() == GameMockExtension.class
                || parameterContext.getParameter().getType().equals(BlockRegistry.class)
                || parameterContext.getParameter().getType().equals(LevelProvider.class)
                || parameterContext.getParameter().getType().equals(TestPlayer.class)
                || parameterContext.getParameter().getType().equals(TestPluginManager.class)
                || parameterContext.getParameter().getType().equals(Level.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext context) throws ParameterResolutionException {
        if (parameterContext.getParameter().getType() == GameMockExtension.class) {
            return gameMockExtension;
        } else if (parameterContext.getParameter().getType().equals(BlockRegistry.class)) {
            return BLOCK_REGISTRY;
        } else if (parameterContext.getParameter().getType().equals(LevelProvider.class)) {
            return level.getProvider();
        } else if (parameterContext.getParameter().getType().equals(Level.class)) {
            return level;
        } else if (parameterContext.getParameter().getType().equals(TestPlayer.class)) {
            return player;
        } else if (parameterContext.getParameter().getType().equals(TestPluginManager.class)) {
            return pluginManager;
        }
        return super.resolveParameter(parameterContext, context);
    }

    final static AtomicBoolean running = new AtomicBoolean(true);

    public void stopNetworkTickLoop() {
        running.set(false);
    }

    public void mockNetworkTickLoop() {
        final Thread main = Thread.currentThread();
        Thread t = new Thread(() -> {
            while (running.get()) {
                try {
                    network.process();
                } catch (Exception ignore) {
                }
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            LockSupport.unpark(main);
        });
        t.setDaemon(true);
        t.start();
        LockSupport.park();
    }
}
