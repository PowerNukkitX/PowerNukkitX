package org.powernukkitx;

import org.powernukkitx.level.Level;
import org.powernukkitx.level.PlayerChunkManager;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.utils.GameLoop;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SuppressWarnings("PMD.AvoidAccessibilityAlteration")
public class TestUtils {
    public static void serverTick(Server server) {
        try {
            Method tick = Server.class.getDeclaredMethod("tick");
            tick.setAccessible(true);
            tick.invoke(server);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void setField(Class<?> clazz, Object target, String fieldName, Object value) {
        try {
            Field infoF = clazz.getDeclaredField(fieldName);
            infoF.setAccessible(true);
            infoF.set(target, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static GameLoop gameLoop0(TestPlayer p) {
        GameLoop loop = GameLoop.builder().loopCountPerSec(100).onTick((d) -> {
            try {
                p.getLevel().getScheduler().mainThreadHeartbeat((int) d.getTick());
                Server.getInstance().getNetwork().process();
                p.getLevel().subTick(d);
                p.checkNetwork();
            } catch (Exception ignore) {
            }
        }).build();
        Thread thread = new Thread(loop::startLoop);
        thread.start();
        return loop;
    }

    /**
     * Puts the shared fixture player back at spawn with a clean chunk pipeline. The chunk
     * manager is replaced rather than cleared because its pending-load futures and send queues
     * would otherwise leak between tests.
     */
    public static void resetPlayerStatus(TestPlayer player) {
        player.setLevel(ServerMockFixture.level);
        player.setPosition(new Vector3(0, 100, 0));
        for (long hash : player.getPlayerChunkManager().getUsedChunks()) {
            player.getLevel().unregisterChunkLoader(player, Level.getHashX(hash), Level.getHashZ(hash));
        }
        setField(Player.class, player, "playerChunkManager", new PlayerChunkManager(player));
        player.getLevel().unloadChunks(true);
    }
}
