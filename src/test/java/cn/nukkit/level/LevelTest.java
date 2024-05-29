package cn.nukkit.level;

import cn.nukkit.GameMockExtension;
import cn.nukkit.TestPlayer;
import cn.nukkit.block.BlockDiamondBlock;
import cn.nukkit.block.BlockDirt;
import cn.nukkit.block.BlockOakWood;
import cn.nukkit.block.BlockObserver;
import cn.nukkit.block.BlockRedstoneWire;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.GameLoop;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static cn.nukkit.TestUtils.gameLoop0;
import static cn.nukkit.TestUtils.resetPlayerStatus;

@ExtendWith(GameMockExtension.class)
public class LevelTest {

    @Test
    void test_getRedstonePower(Level level) {
        level.setBlockStateAt(0, 0, 0, BlockObserver.PROPERTIES.getBlockState(
                        CommonBlockProperties.POWERED_BIT.createValue(true),
                        CommonBlockProperties.MINECRAFT_FACING_DIRECTION.createValue(BlockFace.WEST)//West is the direction of the observer's face, and the opposite side of the observer's direction can output power
                )
        );
        //For example, when a redstone is in the east of the observer
        level.setBlockStateAt(1, 0, 0, BlockRedstoneWire.PROPERTIES.getDefaultState());
        //judge if there is a redstone power source in the west of the block
        Assertions.assertEquals(15, level.getRedstonePower(new Vector3(1, 0, 0).getSide(BlockFace.WEST), BlockFace.WEST));
        Assertions.assertEquals(0, level.getRedstonePower(new Vector3(1, 0, 0).getSide(BlockFace.EAST), BlockFace.EAST));//observer cant output on east

        level.setBlockStateAt(1, 0, 0, BlockOakWood.PROPERTIES.getDefaultState());
        Assertions.assertEquals(15, level.getRedstonePower(new Vector3(1, 0, 0), BlockFace.WEST));//wood be strong power with observer
    }

    @Test
    void test_regenerateChunk(TestPlayer player) {
        final TestPlayer p = player;
        resetPlayerStatus(p);

        p.level.setAutoSave(true);
        p.setPosition(new Vector3(0, 100, 0));
        IChunk chunk = p.getChunk();
        chunk.setBlockState(0, 3, 0, BlockDirt.PROPERTIES.getDefaultState());
        Assertions.assertEquals(chunk.getBlockState(0, 3, 0), BlockDirt.PROPERTIES.getDefaultState());
        chunk.setBlockState(0, 3, 0, BlockDiamondBlock.PROPERTIES.getDefaultState());
        Assertions.assertEquals(chunk.getBlockState(0, 3, 0), BlockDiamondBlock.PROPERTIES.getDefaultState());

        GameLoop gameLoop = gameLoop0(p);
        int limit1 = 100;
        while (limit1-- != 0) {
            try {
                if (49 == p.getUsedChunks().size()) {
                    break;
                }
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        if (limit1 <= 0) {
            p.level.setAutoSave(false);
            resetPlayerStatus(p);
            Assertions.fail("Chunk cannot be regenerate in 10s, chunk size " + p.getUsedChunks().size());
        }
        p.getLevel().regenerateChunk(0, 0);

        int limit = 100;
        while (limit-- != 0) {
            try {
                if (p.getUsedChunks().contains(Level.chunkHash(0, 0))) {
                    break;
                }
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        if (limit <= 0) {
            p.level.setAutoSave(false);
            resetPlayerStatus(p);
            Assertions.fail("Chunk cannot be regenerate in 10s");
        }

        Assertions.assertEquals(BlockDirt.PROPERTIES.getDefaultState(), p.getChunk().getBlockState(0, 3, 0));
        p.level.setAutoSave(false);

        gameLoop.stop();
        resetPlayerStatus(p);
    }
}
