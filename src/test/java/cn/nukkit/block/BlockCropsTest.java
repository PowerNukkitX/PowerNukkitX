package cn.nukkit.block;

import cn.nukkit.GameMockExtension;
import cn.nukkit.Server;
import cn.nukkit.TestEventHandler;
import cn.nukkit.TestPluginManager;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.event.block.BlockGrowEvent;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.response.FormResponseCustom;
import cn.nukkit.form.response.FormResponseData;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.ChunkSection;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.GameLoop;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;

import static cn.nukkit.TestUtils.serverTick;

@ExtendWith(GameMockExtension.class)
public class BlockCropsTest {
    @Test
    void testGrowWhenSkyLight(Level level, TestPluginManager testPluginManager) {
        testPluginManager.resetAll();
        GameLoop loop = GameLoop.builder().loopCountPerSec(20).onTick((d) -> {
            serverTick(Server.getInstance());
        }).build();
        Thread thread = new Thread(loop::startLoop);
        thread.start();

        IChunk chunk = level.getChunk(0, 0);
        chunk.batchProcess(unsafeChunk -> {
            ChunkSection[] sections = unsafeChunk.getSections();
            for (var s : sections) {
                if (s == null) continue;
                for (int i = 0; i < 16; i++) {
                    for (int j = 0; j < 16; j++) {
                        for (int k = 0; k < 16; k++) {
                            s.setBiomeId(i, j, k, 0);
                            s.setBlockState(i, j, k, BlockAir.STATE, 0);
                            s.setBlockState(i, j, k, BlockAir.STATE, 1);
                        }
                    }
                }
            }
        });
        level.setChunk(0, 0, chunk);
        level.syncGenerateChunk(0, 0);
        level.gameRules.setGameRule(GameRule.RANDOM_TICK_SPEED, 200);

        Thread currentThread = Thread.currentThread();

        AtomicBoolean success = new AtomicBoolean(false);
        testPluginManager.registerTestEventHandler(List.of(
                new TestEventHandler<BlockGrowEvent>() {
                    @Override
                    public void handle(BlockGrowEvent event) {
                        success.set(new Vector3(0, 6, 0).equals(event.getBlock()));
                        LockSupport.unpark(currentThread);
                    }
                }
        ));

        level.setBlock(0, 5, 0, BlockFarmland.PROPERTIES.getBlockState(CommonBlockProperties.MOISTURIZED_AMOUNT.createValue(7)).toBlock(), true, true);
        level.setBlock(0, 6, 0, BlockWheat.PROPERTIES.getDefaultState().toBlock(), true, true);
        level.setBlock(1, 4, 0, BlockFlowingWater.PROPERTIES.getDefaultState().toBlock(), true, true);

        LockSupport.park(currentThread);
        loop.stop();
        testPluginManager.resetAll();

        Assertions.assertTrue(success.get());
    }

    @Test
    void testGrowWhenBlockLight(Level level, TestPluginManager testPluginManager) {
        testPluginManager.resetAll();
        GameLoop loop = GameLoop.builder().loopCountPerSec(20).onTick((d) -> {
            serverTick(Server.getInstance());
        }).build();
        Thread thread = new Thread(loop::startLoop);
        thread.start();

        IChunk chunk = level.getChunk(0, 0);
        chunk.batchProcess(unsafeChunk -> {
            ChunkSection[] sections = unsafeChunk.getSections();
            for (var s : sections) {
                if (s == null) continue;
                for (int i = 0; i < 16; i++) {
                    for (int j = 0; j < 16; j++) {
                        for (int k = 0; k < 16; k++) {
                            s.setBiomeId(i, j, k, 0);
                            s.setBlockState(i, j, k, BlockAir.STATE, 0);
                            s.setBlockState(i, j, k, BlockAir.STATE, 1);
                        }
                    }
                }
            }
        });
        level.setChunk(0, 0, chunk);
        level.syncGenerateChunk(0, 0);
        level.gameRules.setGameRule(GameRule.RANDOM_TICK_SPEED, 200);

        Thread currentThread = Thread.currentThread();
        AtomicBoolean success = new AtomicBoolean(false);
        testPluginManager.registerTestEventHandler(List.of(
                new TestEventHandler<BlockGrowEvent>() {
                    @Override
                    public void handle(BlockGrowEvent event) {
                        success.set(new Vector3(0, 2, 0).equals(event.getBlock()));
                        LockSupport.unpark(currentThread);
                    }
                }
        ));

        level.setBlock(0, 1, 0, BlockAir.STATE.toBlock(), true, true);
        level.setBlock(0, 2, 0, BlockAir.STATE.toBlock(), true, true);
        level.setBlock(0, 1, 0, BlockFarmland.PROPERTIES.getBlockState(CommonBlockProperties.MOISTURIZED_AMOUNT.createValue(7)).toBlock(), true, true);
        level.setBlock(0, 2, 0, BlockWheat.PROPERTIES.getDefaultState().toBlock(), true, true);
        level.setBlock(1, 1, 0, BlockFlowingWater.PROPERTIES.getDefaultState().toBlock(), true, true);
        level.setBlock(0, 1, 1, BlockGlowstone.PROPERTIES.getDefaultState().toBlock(), true, true);
        level.setBlock(0, 2, 1, BlockGlowstone.PROPERTIES.getDefaultState().toBlock(), true, true);
        
        LockSupport.park(currentThread);
        loop.stop();
        testPluginManager.resetAll();

        Assertions.assertTrue(success.get());
    }
}
