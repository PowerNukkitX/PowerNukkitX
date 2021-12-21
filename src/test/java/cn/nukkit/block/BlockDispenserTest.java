package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityDispenser;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.powernukkit.tests.api.MockLevel;
import org.powernukkit.tests.api.MockPlayer;
import org.powernukkit.tests.junit.jupiter.PowerNukkitExtension;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author joserobjr
 * @since 2021-12-20
 */
@PowerNukkitOnly
@Since("FUTURE")
@ExtendWith(PowerNukkitExtension.class)
class BlockDispenserTest {
    @MockLevel
    Level level;

    @MockPlayer
    Player player;

    @Test
    void testPlacement() {
        BlockDispenser dispenser = new BlockDispenser();
        dispenser.level = level;
        dispenser.x = 0;
        dispenser.y = 101;
        dispenser.z = 0;
        Block grass = Block.get(BlockID.GRASS);
        Block tallGrass = Block.get(BlockID.TALL_GRASS);
        level.setBlock(0, 100, 0, grass, true, false);
        level.setBlock(0, 101, 0, tallGrass, true, false);

        assertTrue(dispenser.place(Item.getBlock(BlockID.DISPENSER), tallGrass, tallGrass, BlockFace.NORTH, 0, 0, 0, player));
        BlockEntityDispenser entityDispenser = (BlockEntityDispenser) dispenser.getBlockEntity();
        assertNotNull(entityDispenser);
        assertTrue(entityDispenser.isBlockEntityValid());
        assertNotNull(entityDispenser.getInventory());
        assertEquals(BlockEntity.DISPENSER, entityDispenser.getSpawnCompound().getString("id"));
    }
}
