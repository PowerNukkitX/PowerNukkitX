package cn.nukkit;

import cn.nukkit.level.PlayerChunkManager;
import cn.nukkit.math.Vector3;

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

    public static void resetPlayerStatus(TestPlayer player) {
        player.level = GameMockExtension.level;
        player.setPosition(new Vector3(0, 100, 0));
        player.getPlayerChunkManager().getUsedChunks().clear();
        player.getPlayerChunkManager().getInRadiusChunks().clear();
        TestUtils.setField(PlayerChunkManager.class, player.getPlayerChunkManager(), "lastLoaderChunkPosHashed", Long.MAX_VALUE);
    }
}
