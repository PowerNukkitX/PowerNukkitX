package cn.nukkit;

import cn.nukkit.command.SimpleCommandMap;
import cn.nukkit.lang.BaseLang;
import cn.nukkit.level.Level;
import cn.nukkit.network.Network;
import cn.nukkit.network.RakNetInterface;
import cn.nukkit.permission.BanEntry;
import cn.nukkit.permission.BanList;
import cn.nukkit.plugin.PluginManager;
import cn.nukkit.registry.BlockRegistry;
import cn.nukkit.scheduler.ServerScheduler;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.collection.FreezableArrayManager;
import com.sun.jna.internal.ReflectionUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;

public class GameMockExtension extends MockitoExtension {
    static Server server = Mockito.mock(Server.class);
    static BanList banList = Mockito.mock(BanList.class);
    static BaseLang baseLang = Mockito.mock(BaseLang.class);
    static PluginManager pluginManager;
    static SimpleCommandMap simpleCommandMap;
    static Config config;
    static ServerScheduler serverScheduler;
    static FreezableArrayManager freezableArrayManager;
    static Network network;

    static {
        config = new Config(new File("src/test/resources/default-nukkit.yml"));
        serverScheduler = new ServerScheduler();
        Mockito.when(server.getConfig()).thenReturn(config);
        Mockito.when(server.getScheduler()).thenReturn(serverScheduler);

        Mockito.when(banList.getEntires()).thenReturn(new LinkedHashMap<>());
        Mockito.when(server.getIPBans()).thenReturn(banList);
        Mockito.when(server.getLanguage()).thenReturn(baseLang);
        Mockito.when(server.getApiVersion()).thenReturn("1.0.0");

        ArgumentCaptor<String> trs = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String[]> tra = ArgumentCaptor.forClass(String[].class);
        ArgumentCaptor<Object[]> tro = ArgumentCaptor.forClass(Object[].class);
        Mockito.doCallRealMethod().when(baseLang).tr(Mockito.anyString());
        Mockito.when(baseLang.tr(trs.capture(), tra.capture())).thenReturn("mock tra " + trs.getValue() + " " + Arrays.toString(tra.getValue()));
        Mockito.when(baseLang.tr(trs.capture(), tro.capture())).thenReturn("mock tro " + trs.getValue() + " " + Arrays.toString(tro.getValue()));

        simpleCommandMap = new SimpleCommandMap(server);
        pluginManager = new PluginManager(server, simpleCommandMap);
        Mockito.when(server.getPluginManager()).thenReturn(pluginManager);

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
        Mockito.when(server.getFreezableArrayManager()).thenReturn(freezableArrayManager);

        try {
            FieldUtils.writeField(server, "levelArray", Level.EMPTY_ARRAY);
            FieldUtils.writeField(server, "autoSave", false);
            FieldUtils.writeField(server, "tickAverage", new float[]{20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20});
            FieldUtils.writeField(server, "useAverage", new float[]{20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20});
            network = new Network(server);
            network.setName("PNX");
            network.setSubName("TEST MOCK");
            network.registerInterface(new RakNetInterface(server));
            FieldUtils.writeField(server, "network", network);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        Mockito.when(server.getNetwork()).thenReturn(network);

        Mockito.doCallRealMethod().when(server).start();
        try (MockedStatic<Server> mockedStatic = Mockito.mockStatic(Server.class)) {
            mockedStatic.when(Server::getInstance).thenReturn(server);
        }
    }
}
