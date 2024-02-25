package cn.nukkit;

import cn.nukkit.block.BlockComposter;
import cn.nukkit.command.SimpleCommandMap;
import cn.nukkit.dispenser.DispenseBehaviorRegister;
import cn.nukkit.entity.Attribute;
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
import cn.nukkit.permission.BanList;
import cn.nukkit.player.info.PlayerInfo;
import cn.nukkit.plugin.JavaPluginLoader;
import cn.nukkit.plugin.PluginManager;
import cn.nukkit.registry.BlockRegistry;
import cn.nukkit.registry.Registries;
import cn.nukkit.scheduler.ServerScheduler;
import cn.nukkit.utils.ClientChainData;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.collection.FreezableArrayManager;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.UUID;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GameMockExtension extends MockitoExtension {
    final static Server server = mock(Server.class);
    static BanList banList = mock(BanList.class);
    static BaseLang baseLang = mock(BaseLang.class);
    static PluginManager pluginManager;
    static SimpleCommandMap simpleCommandMap = mock(SimpleCommandMap.class);
    static Config config;
    static ServerScheduler serverScheduler;
    static FreezableArrayManager freezableArrayManager;
    static Network network;
    static QueryRegenerateEvent queryRegenerateEvent;
    static MockedStatic<Server> serverMockedStatic;
    final static GameMockExtension gameMockExtension;
    final static BlockRegistry BLOCK_REGISTRY;
    final static Player player;
    static Level level;

    static {
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
        Registries.GENERATOR.init();
        Registries.RECIPE.init();
        Registries.EFFECT.init();
        Attribute.init();
        BlockComposter.init();
        DispenseBehaviorRegister.init();
        BLOCK_REGISTRY = Registries.BLOCK;
        config = new Config(new File("src/test/resources/default-nukkit.yml"));
        try {
            FieldUtils.writeDeclaredField(server, "config", config, true);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        when(server.getConfig()).thenReturn(config);
        when(server.getConfig(anyString())).thenCallRealMethod();
        when(server.getConfig(any(), any())).thenCallRealMethod();
        serverScheduler = new ServerScheduler();
        when(server.getScheduler()).thenReturn(serverScheduler);

        when(banList.getEntires()).thenReturn(new LinkedHashMap<>());
        when(server.getIPBans()).thenReturn(banList);
        BaseLang lang = new BaseLang("eng", "src/main/resources/language");
        when(server.getLanguage()).thenReturn(lang);
        when(server.getApiVersion()).thenReturn("1.0.0");

        when(simpleCommandMap.getCommands()).thenReturn(Collections.emptyMap());
        pluginManager = new PluginManager(server, simpleCommandMap);
        pluginManager.registerInterface(JavaPluginLoader.class);
        when(server.getPluginManager()).thenReturn(pluginManager);

        freezableArrayManager = new FreezableArrayManager(
                server.getConfig("memory-compression.enable", true),
                server.getConfig("memory-compression.slots", 32),
                server.getConfig("memory-compression.default-temperature", 32),
                server.getConfig("memory-compression.threshold.freezing-point", 0),
                server.getConfig("memory-compression.threshold.absolute-zero", -256),
                server.getConfig("memory-compression.threshold.boiling-point", 1024),
                server.getConfig("memory-compression.heat.melting", 16),
                server.getConfig("memory-compression.heat.single-operation", 1),
                server.getConfig("memory-compression.heat.batch-operation", 32));
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
        queryRegenerateEvent = new QueryRegenerateEvent(server);
        when(server.getQueryInformation()).thenReturn(queryRegenerateEvent);
        when(server.getNetwork()).thenCallRealMethod();
        when(server.isEnableSnappy()).thenCallRealMethod();
        when(server.getAutoSave()).thenReturn(false);
        when(server.getTick()).thenReturn(1);
        when(server.getViewDistance()).thenReturn(4);
        when(server.getRecipeRegistry()).thenCallRealMethod();
        ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
        when(server.getComputeThreadPool()).thenReturn(pool);
        when(server.getCommandMap()).thenReturn(simpleCommandMap);
        when(server.getScoreboardManager()).thenReturn(null);
        when(server.getChunkUnloadDelay()).thenReturn(100);
        doNothing().when(server).sendRecipeList(any());
        try {
            FieldUtils.writeDeclaredField(server, "levelArray", Level.EMPTY_ARRAY, true);
            FieldUtils.writeDeclaredField(server, "autoSave", false, true);
            FieldUtils.writeDeclaredField(server, "tickAverage", new float[]{20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20}, true);
            FieldUtils.writeDeclaredField(server, "useAverage", new float[]{20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20}, true);
            network = new Network(server);
            network.setPong("PNX");
            FieldUtils.writeDeclaredField(server, "network", network, true);
            FieldUtils.writeDeclaredStaticField(Server.class, "instance", server, true);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        gameMockExtension = new GameMockExtension();
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
        doNothing().when(serverSession).sendPacketImmediately(any());
        doNothing().when(serverSession).sendPacket(any());
        player = new Player(serverSession, info);
        player.loggedIn = true;
        player.username = "test";
        player.setInventories(new Inventory[]{
                new HumanInventory(player),
                new HumanOffHandInventory(player),
                new HumanEnderChestInventory(player)
        });
        PlayerHandle playerHandle = new PlayerHandle(player);
        playerHandle.addDefaultWindows();
        try {
            FieldUtils.writeDeclaredField(player, "foodData", new PlayerFood(player, 20, 20), true);
            FileUtils.copyDirectory(new File("src/test/resources/level"), new File("src/test/resources/newlevel"));
            level = new Level(Server.getInstance(), "newlevel", "src/test/resources/newlevel",
                    1, LevelDBProvider.class, new LevelConfig.GeneratorConfig("flat", 114514, DimensionEnum.OVERWORLD.getDimensionData(), new HashMap<>()));
            level.initLevel();

            player.level = level;
            player.setPosition(new Vector3(0, 100, 0));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Thread t = new Thread(() -> {
            level.close();
            try {
                FileUtils.deleteDirectory(new File("src/test/resources/newlevel"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        Runtime.getRuntime().addShutdownHook(t);
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext context) throws ParameterResolutionException {
        boolean b = super.supportsParameter(parameterContext, context);
        return b || parameterContext.getParameter().getType() == GameMockExtension.class ||
                parameterContext.getParameter().getType().equals(BlockRegistry.class)
                || parameterContext.getParameter().getType().equals(LevelProvider.class)
                || parameterContext.getParameter().getType().equals(Player.class)
                || parameterContext.getParameter().getType().equals(Level.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext context) throws ParameterResolutionException {
        if (parameterContext.getParameter().getType() == GameMockExtension.class) {
            return gameMockExtension;
        }
        if (parameterContext.getParameter().getType().equals(BlockRegistry.class)) {
            return BLOCK_REGISTRY;
        }
        if (parameterContext.getParameter().getType().equals(LevelProvider.class)) {
            return level.getProvider();
        }
        if (parameterContext.getParameter().getType().equals(Level.class)) {
            return level;
        }
        if (parameterContext.getParameter().getType().equals(Player.class)) {
            return player;
        }
        return super.resolveParameter(parameterContext, context);
    }

    final static AtomicBoolean running = new AtomicBoolean(true);

    public void stop() {
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
