package cn.nukkit.player;

import cn.nukkit.GameMockExtension;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.LevelDBProvider;
import cn.nukkit.registry.Registries;
import com.google.common.annotations.VisibleForTesting;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@ExtendWith(GameMockExtension.class)
public class PlayerTest {
    @Test
    void player_chunk_load(Player player, Level level) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            player.checkNetwork();
            for (var l : player.getUsedChunks()) {
                long x = Level.getHashX(l);
                long z = Level.getHashZ(l);
                System.out.println("loaded chunk " + x + " " + z);
            }
        }, 0L, 50, TimeUnit.MILLISECONDS);
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
