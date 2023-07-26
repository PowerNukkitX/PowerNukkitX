package cn.nukkit.block;

import static org.junit.jupiter.api.Assertions.assertThrows;

import cn.nukkit.blockstate.BlockState;
import cn.nukkit.blockstate.exception.InvalidBlockStateException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.powernukkit.tests.api.MockServer;
import org.powernukkit.tests.junit.jupiter.PowerNukkitExtension;

@ExtendWith(PowerNukkitExtension.class)
@MockServer(callsRealMethods = false)
class BlockLeaves2Test {
    @Test
    void issue482() {
        BlockState current = BlockState.of(BlockID.LEAVES2, 11);
        assertThrows(InvalidBlockStateException.class, current::getBlock);
    }
}
