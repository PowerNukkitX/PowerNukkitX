package cn.nukkit.player;

import cn.nukkit.GameMockExtension;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.LevelDBProvider;
import cn.nukkit.math.Vector3;
import cn.nukkit.registry.Registries;
import com.google.common.annotations.VisibleForTesting;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@ExtendWith(GameMockExtension.class)
public class PlayerTest {
    static final Method processChunkRequest;

    static {
        try {
            processChunkRequest = Level.class.getDeclaredMethod("processChunkRequest");
            processChunkRequest.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void player_chunk_load(Player player, Level level) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        player.setViewDistance(4);//view 4
        scheduler.scheduleAtFixedRate(() -> {
            player.checkNetwork();
            try {
                processChunkRequest.invoke(level);
            } catch (Exception ignore) {
            }
        }, 0L, 50, TimeUnit.MILLISECONDS);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertEquals(49, player.getUsedChunks().size());
        scheduler.shutdown();
    }

    @Test
    void player_chunk_unload(Player player, Level level) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        player.setViewDistance(4);//view 4
        AtomicLong t = new AtomicLong(1);
        scheduler.scheduleAtFixedRate(() -> {
            try {
                player.checkNetwork();
                processChunkRequest.invoke(level);
                if (t.getAndAdd(1) % 100 == 0) {
                    level.asyncChunkGarbageCollection();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0L, 50, TimeUnit.MILLISECONDS);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertEquals(49, player.getUsedChunks().size());
        player.setPosition(new Vector3(1000, 100, 1000));

        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        //Some chunks may need to be processed next tick by asyncChunkGarbageCollection, and I don't want to wait too long for the test
        Assertions.assertTrue(49 <= level.getChunks().size() && level.getChunks().size() <= 51);
        Assertions.assertTrue(level.getChunks().containsKey(0L), "spawn chunk should keep load");
        Assertions.assertTrue(player.getUsedChunks().contains(Level.chunkHash(61, 61)), "the chunk should be loaded for player");
        Assertions.assertFalse(level.getChunks().containsKey(Level.chunkHash(1, 1)), "This chunk should not be loaded");
        scheduler.shutdown();
    }

    @Test
    void player_chunk_unload2(Player player, Level level) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        player.setViewDistance(4);//view 4
        AtomicLong t = new AtomicLong(1);
        scheduler.scheduleAtFixedRate(() -> {
            try {
                player.checkNetwork();
                processChunkRequest.invoke(level);
                if (t.getAndAdd(1) % 100 == 0) {
                    level.asyncChunkGarbageCollection();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0L, 50, TimeUnit.MILLISECONDS);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertEquals(49, player.getUsedChunks().size());
        player.setPosition(new Vector3(1000, 100, 1000));

        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        //Some chunks may need to be processed next tick by asyncChunkGarbageCollection, and I don't want to wait too long for the test
        Assertions.assertTrue(49 <= level.getChunks().size() && level.getChunks().size() <= 51);
        Assertions.assertTrue(level.getChunks().containsKey(0L), "spawn chunk should keep load");
        Assertions.assertTrue(player.getUsedChunks().contains(Level.chunkHash(61, 61)), "the chunk should be loaded for player");
        Assertions.assertFalse(level.getChunks().containsKey(Level.chunkHash(1, 1)), "This chunk should not be loaded");
        scheduler.shutdown();
    }
}
