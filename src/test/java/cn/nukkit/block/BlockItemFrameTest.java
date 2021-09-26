package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockproperty.value.TallGrassType;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
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
 * @since 2021-09-25
 */
@ExtendWith(PowerNukkitExtension.class)
class BlockItemFrameTest {
    @MockLevel
    Level level;

    @MockPlayer
    Player player;

    @Test
    void testPlaceOnTallGrass() {
        BlockItemFrame itemFrame = new BlockItemFrame();
        itemFrame.setLevel(level);

        Block grass = Block.get(BlockID.GRASS);
        Block tallGrass = Block.get(BlockID.TALL_GRASS);
        level.setBlock(0, 100, 0, grass, true, false);
        level.setBlock(0, 101, 0, tallGrass, true, false);

        assertTrue(itemFrame.place(Item.get(ItemID.ITEM_FRAME), tallGrass, tallGrass, BlockFace.NORTH, 0, 0, 0, player));
        assertEquals(BlockFace.UP, itemFrame.getBlockFace());


        tallGrass = tallGrass.clone();
        Block tallGrassTop = BlockState.of(BlockID.TALL_GRASS).withProperty(BlockTallGrass.TALL_GRASS_TYPE, TallGrassType.TALL).getBlock();
        level.setBlock(2, 100, 0, grass.clone(), true, false);
        level.setBlock(2, 101, 0, tallGrass, true, false);
        level.setBlock(2, 103, 0, tallGrassTop, true, false);
        assertFalse(itemFrame.place(Item.get(ItemID.ITEM_FRAME), tallGrassTop, tallGrassTop, BlockFace.NORTH, 0, 0, 0, player));

        Block wall = Block.get(BlockID.COBBLE_WALL);
        level.setBlock(2, 100, 0, wall, true, false);
        assertTrue(itemFrame.place(Item.get(ItemID.ITEM_FRAME), tallGrass, tallGrass, BlockFace.NORTH, 0, 0, 0, player));
        assertTrue(itemFrame.place(Item.get(ItemID.ITEM_FRAME), wall.north(), wall, BlockFace.SOUTH, 0, 0, 0, player));
    }

    @Test
    void testStoringMap() {
        BlockItemFrame block = new BlockItemFrame();

        assertFalse(block.isStoringMap());
        block.setStoringMap(true);
        assertTrue(block.isStoringMap());
        block.setStoringMap(false);
        assertFalse(block.isStoringMap());
    }

    @Test
    void testStoringPhoto() {
        BlockItemFrame block = new BlockItemFrame();

        assertFalse(block.isStoringMap());
        block.setStoringMap(true);
        assertTrue(block.isStoringMap());
        block.setStoringMap(false);
        assertFalse(block.isStoringMap());
    }
}
