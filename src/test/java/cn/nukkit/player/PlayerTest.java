package cn.nukkit.player;

import cn.nukkit.GameMockExtension;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.MovePlayerPacket;
import cn.nukkit.utils.GameLoop;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

@ExtendWith(GameMockExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PlayerTest {

    @Test
    @Order(1)
    void test_player_teleport(Player player, Level level) {
        player.level = level;
        player.setViewDistance(4);//view 4
        GameLoop loop = GameLoop.builder().loopCountPerSec(20).onTick((d) -> {
            Server.getInstance().getNetwork().process();
            Server.getInstance().getScheduler().mainThreadHeartbeat((int) d.getTick());
            player.checkNetwork();
        }).build();
        Thread thread = new Thread(loop::startLoop);
        thread.start();
        player.teleport(new Vector3(10000, 6, 10000));

        int limit = 10;
        while (limit-- != 0) {
            try {
                Thread.sleep(1000);
                if (level.isChunkLoaded(10000 >> 4, 10000 >> 4)) {
                    break;
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        loop.stop();
        //verify target chunk is load
        if (limit <= 0) {
            Assertions.fail("Chunks cannot be successfully loaded in 10s");
        }
        InOrder orderSendPk = Mockito.inOrder(player.getSession());
        orderSendPk.verify(player.getSession(), times(1)).sendPacket(any(MovePlayerPacket.class));
        player.setPosition(new Vector3(0, 100, 0));
    }

    @Test
    @Order(2)
    void test_player_chunk_load(Player player, Level level) {
        player.level = level;
        player.setViewDistance(4);//view 4
        GameLoop loop = GameLoop.builder().loopCountPerSec(20).onTick((d) -> {
            Server.getInstance().getScheduler().mainThreadHeartbeat((int) d.getTick());
            level.subTick(d);
            player.checkNetwork();
        }).build();
        Thread thread = new Thread(loop::startLoop);
        thread.start();
        int limit = 10;
        while (limit-- != 0) {
            try {
                Thread.sleep(1000);
                if (49 == player.getUsedChunks().size()) {
                    break;
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        loop.stop();
        if (limit <= 0) {
            Assertions.fail("Chunks cannot be successfully loaded in 10s");
        }
    }

    @Test
    @Order(3)
    void test_player_chunk_unload(Player player, Level level) {
        player.level = level;
        player.setViewDistance(4);//view 4
        GameLoop loop = GameLoop.builder().loopCountPerSec(20).onTick((d) -> {
            Server.getInstance().getScheduler().mainThreadHeartbeat((int) d.getTick());
            level.subTick(d);
            player.checkNetwork();
        }).build();
        Thread thread = new Thread(loop::startLoop);
        thread.start();
        int limit = 10;
        while (limit-- != 0) {
            try {
                Thread.sleep(1000);
                if (49 == player.getUsedChunks().size()) {
                    break;
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        if (limit <= 0) {
            Assertions.fail("Chunks cannot be successfully loaded in 10s");
        }
        int limit2 = 10;
        player.setPosition(new Vector3(1000, 100, 1000));
        while (limit2-- != 0) {
            try {
                Thread.sleep(1000);
                //Some chunks may need to be processed next tick by doLevelGarbageCollection, and I don't want to wait too long for the test
                if (49 <= level.getChunks().size() && level.getChunks().size() <= 51) {
                    break;
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        if (limit2 == 0) {
            Assertions.fail("Chunks cannot be successfully unloaded in 10s");
        }
        loop.stop();
        Assertions.assertTrue(level.getChunks().containsKey(0L), "spawn chunk should keep load");
        Assertions.assertTrue(player.getUsedChunks().contains(Level.chunkHash(61, 61)), "the chunk should be loaded for player");
        Assertions.assertFalse(level.getChunks().containsKey(Level.chunkHash(1, 1)), "This chunk should not be loaded");
        player.setPosition(new Vector3(0, 100, 0));
    }

}
