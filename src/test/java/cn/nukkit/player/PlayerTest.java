package cn.nukkit.player;

import cn.nukkit.GameMockExtension;
import cn.nukkit.PlayerFood;
import cn.nukkit.TestPlayer;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemFood;
import cn.nukkit.item.ItemID;
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

import static cn.nukkit.TestUtils.gameLoop0;
import static cn.nukkit.TestUtils.resetPlayerStatus;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

@ExtendWith(GameMockExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PlayerTest {

    @Test
    @Order(1)
    void test_player_teleport(TestPlayer player, Level level) {
        final TestPlayer p = player;
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
                throw new RuntimeException(e);
            }
        }
        loop.stop();
        //verify target chunk is load
        if (limit <= 0) {
            Assertions.fail("Chunks cannot be successfully loaded in 10s");
        }
        InOrder orderSendPk = Mockito.inOrder(p.getSession());
        orderSendPk.verify(p.getSession(), times(1)).sendPacket(any(MovePlayerPacket.class));
        p.setPosition(new Vector3(0, 100, 0));
    }

    @Test
    @Order(2)
    void test_player_chunk_load(TestPlayer player) {
        final TestPlayer p = player;
        resetPlayerStatus(p);

        p.setViewDistance(4);//view 4
        p.setPosition(new Vector3(0, 100, 0));

        GameLoop loop = gameLoop0(p);

        int limit = 300;
        while (limit-- != 0) {
            try {
                if (49 == p.getUsedChunks().size()) {
                    break;
                }
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
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
        resetPlayerStatus(player);

        player.setViewDistance(4);//view 4
        GameLoop loop = gameLoop0(player);

        player.setPosition(new Vector3(0, 100, 0));
        Thread thread = new Thread(loop::startLoop);
        thread.start();
        int limit = 100;
        while (limit-- != 0) {
            try {
                if (49 == player.getUsedChunks().size()) {
                    break;
                }
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
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
                if (50 == player.getUsedChunks().size()) {
                    break;
                }
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        if (limit2 == 0) {
            resetPlayerStatus(player);
            Assertions.fail("Chunks cannot be successfully unloaded in 10s, now have chunk %s".formatted(level.getChunks().size()));
        }
        loop.stop();
        Assertions.assertTrue(level.getChunks().containsKey(0L), "spawn chunk 0,0 should keep load");
        Assertions.assertTrue(player.getUsedChunks().contains(Level.chunkHash(61, 61)), "the chunk 61,61 should be loaded for player");
        Assertions.assertFalse(level.getChunks().containsKey(Level.chunkHash(1, 1)), "This chunk 1,1 should not be loaded");

        resetPlayerStatus(player);
    }

    @Test
    @Order(4)
    void test_player_food(TestPlayer player) {
        PlayerFood food = player.getFoodData();

        Assertions.assertSame(player, food.getPlayer());
        Assertions.assertEquals(20, food.getFood());
        Assertions.assertEquals(20, food.getMaxFood());
        Assertions.assertEquals(20, food.getSaturation());

        player.setSprinting(true);
        food.setFood(6);
        Assertions.assertFalse(player.isSprinting(), "Player should not sprint with food <= 6"); // Player should not sprint with food <= 6
        Assertions.assertTrue(food.isHungry());

        food.setSaturation(10);
        Assertions.assertEquals(6, food.getSaturation(), "Saturation should not exceed food level.");

        ItemFood apple = (ItemFood) Item.get(ItemID.APPLE);
        food.addFood(apple);

        Assertions.assertEquals(10, food.getFood(), "6 + 4 = 10");
        Assertions.assertEquals(8.4, Math.round(food.getSaturation() * 10) / 10.0F, 0.0001D, "6 + 2.4 = 8.4");

        food.setEnabled(false);
        Assertions.assertFalse(food.isEnabled(), "Food should be disabled.");

        double exhaustion = food.getExhaustion();
        food.exhaust(100);
        Assertions.assertEquals(exhaustion, food.getExhaustion(), "Food is disabled: Exhaustion should not change.");

        food.reset();
        Assertions.assertEquals(20, food.getFood());
        Assertions.assertEquals(20, food.getMaxFood());
        Assertions.assertEquals(20, food.getSaturation());
        Assertions.assertEquals(0, food.getFoodTickTimer());

        resetPlayerStatus(player);
    }
}
