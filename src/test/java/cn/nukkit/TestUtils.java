package cn.nukkit;

import cn.nukkit.level.PlayerChunkManager;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.GameLoop;

import java.lang.reflect.Field;

public class TestUtils {
    public static void setField(Class<?> clazz, Object target, String fieldName, Object value) {
        try {
            Field infoF = clazz.getDeclaredField(fieldName);
            infoF.setAccessible(true);
            infoF.set(target, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static GameLoop gameLoop(TestPlayer p) {
        GameLoop loop = GameLoop.builder().loopCountPerSec(100).onTick((d) -> {
            Server.getInstance().getScheduler().mainThreadHeartbeat((int) d.getTick());
            Server.getInstance().getNetwork().process();
            p.getLevel().subTick(d);
            p.checkNetwork();
        }).build();
        Thread thread = new Thread(loop::startLoop);
        thread.start();
        return loop;
    }

    public static void resetPlayerStatus(TestPlayer player) {
        player.level = GameMockExtension.level;
        player.setPosition(new Vector3(0, 100, 0));
        player.getPlayerChunkManager().getUsedChunks().clear();
        player.getPlayerChunkManager().getInRadiusChunks().clear();
        TestUtils.setField(PlayerChunkManager.class, player.getPlayerChunkManager(), "lastLoaderChunkPosHashed", Long.MAX_VALUE);
    }
}
