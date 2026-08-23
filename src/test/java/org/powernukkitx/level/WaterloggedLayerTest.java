package org.powernukkitx.level;

import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.block.BlockLiquid;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class WaterloggedLayerTest {

    static Level level;
    static boolean liquidFlowBefore;

    @BeforeAll
    static void boot() {
        ServerMockFixture.boot();
        level = ServerMockFixture.level;
        liquidFlowBefore = level.getGameplaySettings().enableLiquidFlow();
        level.getGameplaySettings().enableLiquidFlow(false);
    }

    @AfterAll
    static void restore() {
        level.getGameplaySettings().enableLiquidFlow(liquidFlowBefore);
    }

    @Test
    void orphanLiquidMovesBackToLayer0() {
        int x = 1000, y = 70, z = 1000;
        level.setBlock(x, y, z, 0, Block.get(BlockID.AIR), false, false);
        level.setBlock(x, y, z, 1, Block.get(BlockID.WATER), false, false);

        Assertions.assertTrue(BlockLiquid.normalizeWaterloggedLayer(level, x, y, z));
        Assertions.assertInstanceOf(BlockLiquid.class, level.getBlock(x, y, z, 0));
        Assertions.assertTrue(level.getBlock(x, y, z, 1).isAir());
    }

    @Test
    void liquidUnderABlockThatCannotBeWaterloggedIsDropped() {
        int x = 1002, y = 70, z = 1000;
        level.setBlock(x, y, z, 0, Block.get(BlockID.STONE), false, false);
        level.setBlock(x, y, z, 1, Block.get(BlockID.WATER), false, false);

        Assertions.assertTrue(BlockLiquid.normalizeWaterloggedLayer(level, x, y, z));
        Assertions.assertTrue(level.getBlock(x, y, z, 1).isAir());
    }

    @Test
    void clearingLayer0WithoutBlockUpdatesCannotStrandTheLiquid() {
        int x = 1004, y = 70, z = 1000;
        level.setBlock(x, y, z, 0, Block.get(BlockID.STONE), false, false);
        level.setBlock(x, y, z, 1, Block.get(BlockID.WATER), false, false);

        level.setBlock(x, y, z, 0, Block.get(BlockID.AIR), false, false);

        Assertions.assertInstanceOf(BlockLiquid.class, level.getBlock(x, y, z, 0));
        Assertions.assertTrue(level.getBlock(x, y, z, 1).isAir());
    }

    @Test
    void makingRoomForABlockAboutToBePlacedIsNotUndone() {
        int x = 1006, y = 70, z = 1000;
        level.setBlock(x, y, z, 0, Block.get(BlockID.WATER), false, false);
        level.setBlock(x, y, z, 1, Block.get(BlockID.WATER), false, false);
        level.setBlock(x, y, z, 0, Block.get(BlockID.AIR), false, false);

        Assertions.assertInstanceOf(BlockLiquid.class, level.getBlock(x, y, z, 1));

        level.setBlock(x, y, z, 0, Block.get(BlockID.OAK_SLAB), false, false);
        Assertions.assertInstanceOf(BlockLiquid.class, level.getBlock(x, y, z, 1));
    }

    @Test
    void legitimateWaterloggingIsLeftAlone() {
        int x = 1026, y = 70, z = 1024;
        level.setBlock(x, y, z, 0, Block.get(BlockID.OAK_FENCE), false, false);
        level.setBlock(x, y, z, 1, Block.get(BlockID.WATER), false, false);

        Assertions.assertFalse(BlockLiquid.normalizeWaterloggedLayer(level, x, y, z));
        Assertions.assertInstanceOf(BlockLiquid.class, level.getBlock(x, y, z, 1));
    }
}
