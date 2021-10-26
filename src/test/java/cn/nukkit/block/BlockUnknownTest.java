package cn.nukkit.block;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.powernukkit.tests.junit.jupiter.PowerNukkitExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(PowerNukkitExtension.class)
class BlockUnknownTest {
    BlockUnknown block;
    @Test
    void constructor() {
        block = new BlockUnknown(1, (Number) null);
        assertEquals(0, block.getExactIntStorage());

        block = new BlockUnknown(1, null);
        assertEquals(0, block.getExactIntStorage());

        block = new BlockUnknown(1, 2);
        assertEquals(2, block.getExactIntStorage());

        block = new BlockUnknown(1, 2000000000L);
        assertEquals(2000000000L, block.getDataStorage());
    }
}
