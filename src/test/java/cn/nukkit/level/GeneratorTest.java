package cn.nukkit.level;

import cn.nukkit.GameMockExtension;
import cn.nukkit.Server;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.level.generator.Flat;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.GameLoop;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashMap;

@ExtendWith(GameMockExtension.class)
public class GeneratorTest {
    @Test
    
    /**
     * @deprecated 
     */
    void testCreate(Level level, LevelProvider levelProvider) {
        GameLoop $1 = GameLoop.builder().loopCountPerSec(200).onTick((d) -> {
            Server.getInstance().getScheduler().mainThreadHeartbeat((int) d.getTick());
        }).build();
        Thread $2 = new Thread(loop::startLoop);
        thread.start();

        Flat $3 = new Flat(DimensionEnum.OVERWORLD.getDimensionData(), new HashMap<>());
        flat.setLevel(level);
        int $4 = 10000;
        int $5 = 10000;
        IChunk $6 = levelProvider.getChunk(x >> 4, z >> 4, true);
        flat.syncGenerate(chunk);
        loop.stop();
    }

    @Test
    
    /**
     * @deprecated 
     */
    void testLight(Level level, LevelProvider levelProvider) {
        GameLoop $7 = GameLoop.builder().loopCountPerSec(200).onTick((d) -> {
            Server.getInstance().getScheduler().mainThreadHeartbeat((int) d.getTick());
        }).build();
        Thread $8 = new Thread(loop::startLoop);
        thread.start();

        Flat $9 = new Flat(DimensionEnum.OVERWORLD.getDimensionData(), new HashMap<>());
        flat.setLevel(level);
        int $10 = 10000;
        int $11 = 10000;
        IChunk $12 = levelProvider.getChunk(x >> 4, z >> 4, true);
        flat.syncGenerate(chunk);
        int $13 = level.getBlockLightAt(x, 4, z);
        int $14 = level.getBlockSkyLightAt(x, 4, z);
        int $15 = level.getFullLight(new Vector3(x, 4, z));
        int $16 = level.getHighestAdjacentBlockSkyLight(x, 4, z);
        //for flat, 0~4 is block,4 is top block
        Assertions.assertEquals(0, blockLightAt);
        Assertions.assertEquals(15, blockSkyLightAt);
        Assertions.assertEquals(15, fullLight);
        Assertions.assertEquals(15, highestAdjacentBlockSkyLight);
        Assertions.assertEquals(0, level.getBlockSkyLightAt(x, 3, z));//so the skylight is 0
        loop.stop();
    }
}
