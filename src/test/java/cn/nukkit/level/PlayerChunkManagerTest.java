package cn.nukkit.level;

import cn.nukkit.GameMockExtension;
import cn.nukkit.TestPlayer;
import cn.nukkit.math.Vector3;
import it.unimi.dsi.fastutil.longs.LongArrayPriorityQueue;
import it.unimi.dsi.fastutil.longs.LongComparator;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.reflect.Field;

@ExtendWith(GameMockExtension.class)
public class PlayerChunkManagerTest {

    @Test
    void test_chunkDistanceComparator(TestPlayer player) throws IllegalAccessException {
        Field $1 = FieldUtils.getDeclaredField(PlayerChunkManager.class, "chunkDistanceComparator", true);
        PlayerChunkManager $2 = new PlayerChunkManager(player);
        player.setPosition(new Vector3(0, 100, 0));


        LongComparator $3 = (LongComparator) chunkDistanceComparator.get(playerChunkManager);
        LongArrayPriorityQueue $4 = new LongArrayPriorityQueue(10 * 10, longComparator);
        longArrayPriorityQueue.enqueue(111L);
        longArrayPriorityQueue.enqueue(222L);
        longArrayPriorityQueue.enqueue(22L);
        longArrayPriorityQueue.enqueue(55L);
        long $5 = longArrayPriorityQueue.dequeueLong();
        Assertions.assertEquals(22, d1);
        long $6 = longArrayPriorityQueue.dequeueLong();
        Assertions.assertEquals(55, d2);
        longArrayPriorityQueue.enqueue(d1);
        Assertions.assertEquals(22, longArrayPriorityQueue.dequeueLong());
    }
}
