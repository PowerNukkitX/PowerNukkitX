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
    void player_teleport(Player player, Level level) {
        player.setViewDistance(4);//view 4
        GameLoop loop = GameLoop.builder().loopCountPerSec(20).onTick((d) -> {
            Server.getInstance().getNetwork().process();
            Server.getInstance().getScheduler().mainThreadHeartbeat((int) d.getTick());
            player.checkNetwork();
        }).build();
        Thread thread = new Thread(loop::startLoop);
        thread.start();
        player.teleport(new Vector3(10000, 6, 10000));
        Assertions.assertTrue(level.isChunkLoaded(10000 >> 4, 10000 >> 4));//verify target chunk is load
        InOrder orderSendPk = Mockito.inOrder(player.getSession());
        orderSendPk.verify(player.getSession(), times(1)).sendPacket(any(MovePlayerPacket.class));
        loop.stop();
    }

    @Test
    @Order(2)
    void player_chunk_load(Player player, Level level) {
        player.setViewDistance(4);//view 4
        GameLoop loop = GameLoop.builder().loopCountPerSec(20).onTick((d) -> {
            Server.getInstance().getScheduler().mainThreadHeartbeat((int) d.getTick());
            level.subTick(d);
            player.checkNetwork();
        }).build();
        Thread thread = new Thread(loop::startLoop);
        thread.start();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        loop.stop();
        Assertions.assertEquals(49, player.getUsedChunks().size());
    }

    @Test
    @Order(3)
    void player_chunk_unload(Player player, Level level) {
        player.setViewDistance(4);//view 4
        GameLoop loop = GameLoop.builder().loopCountPerSec(20).onTick((d) -> {
            Server.getInstance().getScheduler().mainThreadHeartbeat((int) d.getTick());
            level.subTick(d);
            player.checkNetwork();
        }).build();
        Thread thread = new Thread(loop::startLoop);
        thread.start();
        try {
            Thread.sleep(2000);
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
        loop.stop();
        //Some chunks may need to be processed next tick by doLevelGarbageCollection, and I don't want to wait too long for the test
        Assertions.assertTrue(49 <= level.getChunks().size() && level.getChunks().size() <= 51);
        Assertions.assertTrue(level.getChunks().containsKey(0L), "spawn chunk should keep load");
        Assertions.assertTrue(player.getUsedChunks().contains(Level.chunkHash(61, 61)), "the chunk should be loaded for player");
        Assertions.assertFalse(level.getChunks().containsKey(Level.chunkHash(1, 1)), "This chunk should not be loaded");
    }

    @Test
    void test_setNameTag(Player player){
        String oldNameTag = player.getNameTag();
        player.setNameTag("test nameTag");
        String newNameTag = player.getNameTag();
        System.out.println("Old name tag: " + oldNameTag);
        System.out.println("New name tag: " + newNameTag);
    }
}
