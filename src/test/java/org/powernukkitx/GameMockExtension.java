package org.powernukkitx;

import org.powernukkitx.level.Level;
import org.powernukkitx.level.format.LevelProvider;
import org.powernukkitx.network.Network;
import org.powernukkitx.registry.BlockRegistry;
import org.powernukkitx.registry.Registries;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;

/**
 * JUnit extension that boots the shared {@link ServerMockFixture} and injects the
 * fixture's server, level and player into test methods.
 * <p>
 * Supported parameter types: {@link GameMockExtension}, {@link Server}, {@link Level},
 * {@link LevelProvider}, {@link BlockRegistry}, {@link TestPlayer} and
 * {@link TestPluginManager}.
 * <p>
 * Deliberately not built on {@code MockitoExtension}: the fixture mocks are shared and
 * long-lived, so per-test strict-stub validation would flag their stubs as unnecessary for
 * every test that doesn't happen to touch them. Test classes that want {@code @Mock} can
 * add {@code MockitoExtension} alongside this one.
 */
public class GameMockExtension implements ParameterResolver {

    private static final AtomicBoolean running = new AtomicBoolean(true);

    static {
        ServerMockFixture.boot();
    }

    public Server getServer() {
        return ServerMockFixture.server;
    }

    public Level getLevel() {
        return ServerMockFixture.level;
    }

    public TestPlayer getPlayer() {
        return PlayerFixture.get();
    }

    public Network getNetwork() {
        return ServerMockFixture.network;
    }

    public void stopNetworkTickLoop() {
        running.set(false);
    }

    /**
     * Drives {@link Network#process()} on a daemon thread and parks the caller until
     * {@link #stopNetworkTickLoop()} is called - used by the network tests that need a
     * live packet pump while they assert.
     */
    public void mockNetworkTickLoop() {
        final Thread main = Thread.currentThread();
        running.set(true);
        Thread t = new Thread(() -> {
            while (running.get()) {
                try {
                    ServerMockFixture.network.process();
                } catch (Exception ignore) {
                }
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            LockSupport.unpark(main);
        }, "game-mock-network-tick");
        t.setDaemon(true);
        t.start();
        LockSupport.park();
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext context)
            throws ParameterResolutionException {
        return isSupported(parameterContext.getParameter().getType());
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext context)
            throws ParameterResolutionException {
        Class<?> type = parameterContext.getParameter().getType();
        if (type == GameMockExtension.class) {
            return this;
        } else if (type == Server.class) {
            return ServerMockFixture.server;
        } else if (type == Level.class) {
            return ServerMockFixture.level;
        } else if (type == LevelProvider.class) {
            return ServerMockFixture.level.getProvider();
        } else if (type == BlockRegistry.class) {
            return Registries.BLOCK;
        } else if (type == TestPlayer.class) {
            return PlayerFixture.get();
        } else if (type == TestPluginManager.class) {
            return ServerMockFixture.pluginManager;
        }
        throw new ParameterResolutionException("Unsupported parameter type: " + type);
    }

    private static boolean isSupported(Class<?> type) {
        return type == GameMockExtension.class
                || type == Server.class
                || type == Level.class
                || type == LevelProvider.class
                || type == BlockRegistry.class
                || type == TestPlayer.class
                || type == TestPluginManager.class;
    }
}
