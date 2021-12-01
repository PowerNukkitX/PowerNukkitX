package cn.nukkit.blockstate;

import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockWall;
import cn.nukkit.math.BlockFace;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.powernukkit.tests.junit.jupiter.PowerNukkitExtension;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(PowerNukkitExtension.class)
class BlockStateRegistryTest {
    @Test
    void getKnownBlockStateIdByRuntimeIdAndViceVersa() {
        BlockWall wall = new BlockWall();
        wall.setWallType(BlockWall.WallType.DIORITE);
        wall.setWallPost(true);
        wall.setConnection(BlockFace.NORTH, BlockWall.WallConnectionType.TALL);
        val runtimeId = wall.getRuntimeId();
        val stateId = wall.getStateId();
        val minimalistStateId = wall.getMinimalistStateId();
        val legacyStateId = wall.getLegacyStateId();
        val unknownStateId = wall.getPersistenceName()+";unknown="+wall.getDataStorage();

        assertEquals(stateId, BlockStateRegistry.getKnownBlockStateIdByRuntimeId(runtimeId));
        assertEquals(runtimeId, BlockStateRegistry.getKnownRuntimeIdByBlockStateId(stateId));
        assertEquals(runtimeId, BlockStateRegistry.getKnownRuntimeIdByBlockStateId(minimalistStateId));
        assertEquals(runtimeId, BlockStateRegistry.getKnownRuntimeIdByBlockStateId(legacyStateId));
        assertEquals(runtimeId, BlockStateRegistry.getKnownRuntimeIdByBlockStateId(unknownStateId));
        assertEquals(BlockID.COBBLE_WALL, BlockStateRegistry.getBlockIdByRuntimeId(runtimeId));
    }

    @Test
    void getBlockIdByRuntimeId() {
        assertThrows(NoSuchElementException.class, ()-> BlockStateRegistry.getBlockIdByRuntimeId(999999999));
        int runtimeId = BlockStateRegistry.getKnownRuntimeIdByBlockStateId("minecraft:sculk_shrieker;active=1");
        assertEquals(716, BlockStateRegistry.getBlockIdByRuntimeId(runtimeId));
    }
}
