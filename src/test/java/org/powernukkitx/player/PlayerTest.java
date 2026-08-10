package org.powernukkitx.player;

import org.powernukkitx.GameMockExtension;
import org.powernukkitx.TestPlayer;
import org.powernukkitx.level.Level;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.utils.GameLoop;
import org.cloudburstmc.protocol.bedrock.packet.MovePlayerPacket;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.powernukkitx.TestUtils.gameLoop0;
import static org.powernukkitx.TestUtils.resetPlayerStatus;

@ExtendWith(GameMockExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PlayerTest {

    /**
     * The fixture player never went through a real login, so {@code checkNetwork()} - and with
     * it the chunk manager tick - would bail out immediately.
     */
    private static void bringOnline(TestPlayer player) {
        player.loggedIn = true;
        doReturn(true).when(player.getSession()).isConnected();
    }

    @Test
    @Order(1)
    void test_player_teleport(TestPlayer player, Level level) {
        bringOnline(player);
        final TestPlayer p = player;
        resetPlayerStatus(p);
        p.level = level;
        p.setViewDistance(4);//view 4

        GameLoop loop = gameLoop0(p);

        p.teleport(new Vector3(10000, 6, 10000));

        int limit = 100;
        while (limit-- != 0) {
            try {
                if (level.isChunkLoaded(10000 >> 4, 10000 >> 4)) {
                    break;
                }
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException(e);
            }
        }
        loop.stop();
        //verify target chunk is load
        if (limit <= 0) {
            Assertions.fail("Chunks cannot be successfully loaded in 10s");
        }
        InOrder orderSendPk = Mockito.inOrder(p.getSession());
        orderSendPk.verify(p.getSession(), times(1)).sendPacket(any(MovePlayerPacket.class));
        resetPlayerStatus(p);
    }

    @Test
    @Order(2)
    void test_player_chunk_load(TestPlayer player) {
        bringOnline(player);
        final TestPlayer p = player;
        resetPlayerStatus(p);

        p.setViewDistance(4);//view 4
        p.setPosition(new Vector3(0, 100, 0));

        GameLoop loop = gameLoop0(p);

        int limit = 300;
        while (limit-- != 0) {
            try {
                if (p.getUsedChunks().size() >= 49) {
                    break;
                }
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException(e);
            }
        }
        loop.stop();

        if (limit <= 0) {
            resetPlayerStatus(p);
            Assertions.fail("Chunks cannot be successfully loaded in 30s,the number of chunks that are now loaded: " + p.getUsedChunks().size());
        }
        resetPlayerStatus(p);
    }

    @Test
    @Order(3)
    void test_player_chunk_unload(TestPlayer player, Level level) {
        bringOnline(player);
        resetPlayerStatus(player);

        player.setViewDistance(4);//view 4
        GameLoop loop = gameLoop0(player);

        player.setPosition(new Vector3(0, 100, 0));
        Thread thread = new Thread(loop::startLoop);
        thread.start();
        int limit = 100;
        while (limit-- != 0) {
            try {
                if (player.getUsedChunks().size() >= 49) {
                    break;
                }
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException(e);
            }
        }
        if (limit <= 0) {
            resetPlayerStatus(player);
            Assertions.fail("Chunks cannot be successfully loaded in 10s");
        }
        int limit2 = 300;
        player.setPosition(new Vector3(1000, 100, 1000));
        while (limit2-- != 0) {
            try {
                if (player.getUsedChunks().contains(Level.chunkHash(61, 61))) {
                    break;
                }
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException(e);
            }
        }
        if (limit2 == 0) {
            resetPlayerStatus(player);
            Assertions.fail("Chunks cannot be successfully unloaded in 10s, now have chunk %s".formatted(level.getChunks().size()));
        }
        loop.stop();
        // Level#doTick is not running here, so the periodic GC never drains the unload queue -
        // force it until the queue settles, the game loop thread may still be winding down.
        for (int i = 0; i < 20 && level.getChunks().containsKey(Level.chunkHash(1, 1)); i++) {
            level.unloadChunks(true);
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException(e);
            }
        }
        Assertions.assertTrue(level.getChunks().containsKey(0L), "spawn chunk 0,0 should keep load");
        Assertions.assertTrue(player.getUsedChunks().contains(Level.chunkHash(61, 61)), "the chunk 61,61 should be loaded for player");
        Assertions.assertFalse(level.getChunks().containsKey(Level.chunkHash(1, 1)), "This chunk 1,1 should not be loaded");

        resetPlayerStatus(player);
    }

}
