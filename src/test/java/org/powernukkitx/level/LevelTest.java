package org.powernukkitx.level;

import org.powernukkitx.GameMockExtension;
import org.powernukkitx.TestPlayer;
import org.powernukkitx.block.BlockDiamondBlock;
import org.powernukkitx.block.BlockDirt;
import org.powernukkitx.block.BlockOakWood;
import org.powernukkitx.block.BlockObserver;
import org.powernukkitx.block.BlockRedstoneWire;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.utils.GameLoop;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.powernukkitx.TestUtils.gameLoop0;
import static org.powernukkitx.TestUtils.resetPlayerStatus;

@ExtendWith(GameMockExtension.class)
public class LevelTest {
//
//    @Test
//    void test_getRedstonePower(Level level) {
//        level.setBlockStateAt(0, 0, 0, BlockObserver.PROPERTIES.getBlockState(
//                        CommonBlockProperties.POWERED_BIT.createValue(true),
//                        CommonBlockProperties.MINECRAFT_FACING_DIRECTION.createValue(BlockFace.WEST)//West is the direction of the observer's face, and the opposite side of the observer's direction can output power
//                )
//        );
//        //For example, when a redstone is in the east of the observer
//        level.setBlockStateAt(1, 0, 0, BlockRedstoneWire.PROPERTIES.getDefaultState());
//        //judge if there is a redstone power source in the west of the block
//        Assertions.assertEquals(15, level.getRedstonePower(new Vector3(1, 0, 0).getSide(BlockFace.WEST), BlockFace.WEST));
//        Assertions.assertEquals(0, level.getRedstonePower(new Vector3(1, 0, 0).getSide(BlockFace.EAST), BlockFace.EAST));//observer cant output on east
//
//        level.setBlockStateAt(1, 0, 0, BlockOakWood.PROPERTIES.getDefaultState());
//        Assertions.assertEquals(15, level.getRedstonePower(new Vector3(1, 0, 0), BlockFace.WEST));//wood be strong power with observer
//    }

}
