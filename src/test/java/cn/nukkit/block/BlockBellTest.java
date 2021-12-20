package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockentity.BlockEntityBell;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.powernukkit.tests.api.MockLevel;
import org.powernukkit.tests.api.MockPlayer;
import org.powernukkit.tests.junit.jupiter.PowerNukkitExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author joserobjr
 * @since 2021-12-20
 */
@PowerNukkitOnly
@Since("FUTURE")
@ExtendWith(PowerNukkitExtension.class)
class BlockBellTest {
    @MockLevel
    Level level;

    @MockPlayer
    Player player;

    @Test
    void testPlacement() {
        BlockBell bell = new BlockBell();
        bell.level = level;
        bell.x = 0;
        bell.y = 101;
        bell.z = 0;
        Block grass = Block.get(BlockID.GRASS);
        Block tallGrass = Block.get(BlockID.TALL_GRASS);
        level.setBlock(0, 100, 0, grass, true, false);
        level.setBlock(0, 101, 0, tallGrass, true, false);

        assertTrue(bell.place(Item.getBlock(BlockID.BELL), tallGrass, tallGrass, BlockFace.NORTH, 0, 0, 0, player));
        BlockEntityBell entityBell = bell.getBlockEntity();
        assertNotNull(entityBell);
        assertTrue(entityBell.isBlockEntityValid());
        assertNotNull(entityBell.spawnExceptions);
    }
}
