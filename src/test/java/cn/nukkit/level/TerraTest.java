package cn.nukkit.level;

import cn.nukkit.GameMockExtension;
import cn.nukkit.Server;
import cn.nukkit.TestPlayer;
import cn.nukkit.level.format.LevelConfig;
import cn.nukkit.level.format.leveldb.LevelDBProvider;
import cn.nukkit.level.generator.terra.PNXPlatform;
import cn.nukkit.utils.GameLoop;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;

import static cn.nukkit.TestUtils.gameLoop;
import static cn.nukkit.TestUtils.resetPlayerStatus;

@ExtendWith(GameMockExtension.class)
public class TerraTest {
    static Level level;

    @BeforeAll
    static void before() {
        PNXPlatform instance = PNXPlatform.getInstance();
        HashMap<String, Object> objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.put("pack", "overworld");
        level = new Level(Server.getInstance(), "terra", "src/test/resources/terra",
                1, LevelDBProvider.class, new LevelConfig.GeneratorConfig("terra", 114514, false, LevelConfig.AntiXrayMode.LOW, true, DimensionEnum.OVERWORLD.getDimensionData(), objectObjectHashMap));
    }


    /**
     * It is used to test whether the chunk in the player's current position can be loaded normally
     * after teleporting in the Terra generator
     */
    @Test
    void test_terra(TestPlayer player) {
        final TestPlayer p = player;
        resetPlayerStatus(p);

        p.level = level;
        p.getLevel().initLevel();
        p.setViewDistance(1);

        GameLoop loop = gameLoop(p);

        int limit = 100;
        while (limit-- != 0) {
            try {
                if (p.getPlayerChunkManager().getUsedChunks().size() >= 5) {
                    break;
                }
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        if (limit <= 0) {
            resetPlayerStatus(p);
            Assertions.fail("Chunks cannot be successfully loaded in 10s");
        }

        //teleport
        p.teleport(p.getLocation().setComponents(10000, 100, 10000));

        int limit2 = 1000;
        while (limit2-- != 0) {
            try {
                Thread.sleep(100);
                if (p.chunk != null && p.chunk.getChunkState().canSend() && p.chunk.getX() == 625 && p.chunk.getZ() == 625) {
                    break;
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        if (limit2 == 0) {
            resetPlayerStatus(p);
            Assertions.fail("Players are unable to load Terra generator chunks normally");
        }
        loop.stop();
        resetPlayerStatus(p);
    }

    @SneakyThrows
    @AfterAll
    static void after() {
        level.close();
        File file2 = Path.of("src/test/resources/terra").toFile();
        if (file2.exists()) {
            FileUtils.deleteDirectory(file2);
        }
    }
}
