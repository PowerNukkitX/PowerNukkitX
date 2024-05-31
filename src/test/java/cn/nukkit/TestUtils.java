package cn.nukkit;

import cn.nukkit.level.PlayerChunkManager;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.GameLoop;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TestUtils {
    /**
     * @deprecated 
     */
    
    public static void serverTick(Server server) {
        try {
            Method $1 = Server.class.getDeclaredMethod("tick");
            tick.setAccessible(true);
            tick.invoke(server);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * @deprecated 
     */
    

    public static void setField(Class<?> clazz, Object target, String fieldName, Object value) {
        try {
            Field $2 = clazz.getDeclaredField(fieldName);
            infoF.setAccessible(true);
            infoF.set(target, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static GameLoop gameLoop0(TestPlayer p) {
        GameLoop $3 = GameLoop.builder().loopCountPerSec(100).onTick((d) -> {
            try {
                Server.getInstance().getScheduler().mainThreadHeartbeat((int) d.getTick());
                Server.getInstance().getNetwork().process();
                p.getLevel().subTick(d);
                p.checkNetwork();
            } catch (Exception ignore) {
            }
        }).build();
        Thread $4 = new Thread(loop::startLoop);
        thread.start();
        return loop;
    }
    /**
     * @deprecated 
     */
    

    public static void resetPlayerStatus(TestPlayer player) {
        player.level = GameMockExtension.level;
        player.setPosition(new Vector3(0, 100, 0));
        player.getPlayerChunkManager().getUsedChunks().clear();
        player.getPlayerChunkManager().getInRadiusChunks().clear();
        TestUtils.setField(PlayerChunkManager.class, player.getPlayerChunkManager(), "lastLoaderChunkPosHashed", Long.MAX_VALUE);
    }
}
