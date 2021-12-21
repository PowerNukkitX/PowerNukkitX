package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityDropper;
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
class BlockDropperTest {
    @MockLevel
    Level level;

    @MockPlayer
    Player player;

    @Test
    void testPlacement() {
        BlockDropper dropper = new BlockDropper();
        dropper.level = level;
        dropper.x = 0;
        dropper.y = 101;
        dropper.z = 0;
        Block grass = Block.get(BlockID.GRASS);
        Block tallGrass = Block.get(BlockID.TALL_GRASS);
        level.setBlock(0, 100, 0, grass, true, false);
        level.setBlock(0, 101, 0, tallGrass, true, false);

        assertTrue(dropper.place(Item.getBlock(BlockID.DROPPER), tallGrass, tallGrass, BlockFace.NORTH, 0, 0, 0, player));
        BlockEntityDropper entityDropper = (BlockEntityDropper) dropper.getBlockEntity();
        assertNotNull(entityDropper);
        assertTrue(entityDropper.isBlockEntityValid());
        assertNotNull(entityDropper.getInventory());
        assertEquals(BlockEntity.DISPENSER, entityDropper.getSpawnCompound().getString("id"));
    }
}
