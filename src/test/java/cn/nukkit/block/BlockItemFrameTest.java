package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.powernukkit.tests.junit.jupiter.PowerNukkitExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author joserobjr
 * @since 2021-09-25
 */
@ExtendWith(PowerNukkitExtension.class)
class BlockItemFrameTest {
    @Mock
    Level level;

    @Mock
    Player player;

    @Test @Disabled //TODO Fix
    void testPlaceOnTallGrass() {
        BlockItemFrame itemFrame = new BlockItemFrame();
        itemFrame.setLevel(level);

        Block grass = mock(BlockGrass.class, InvocationOnMock::callRealMethod);
        Block tallGrass = mock(BlockTallGrass.class, InvocationOnMock::callRealMethod);
        grass.y = 100;
        tallGrass.y = 101;
        grass.level = level;
        tallGrass.level = level;
        when(tallGrass.down()).thenReturn(grass);

        assertTrue(itemFrame.place(Item.get(ItemID.ITEM_FRAME), tallGrass, tallGrass, BlockFace.NORTH, 0, 0, 0, player));
        itemFrame = (BlockItemFrame) level.getBlock(0, 101, 0);
        assertEquals(BlockFace.UP, itemFrame.getBlockFace());
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
