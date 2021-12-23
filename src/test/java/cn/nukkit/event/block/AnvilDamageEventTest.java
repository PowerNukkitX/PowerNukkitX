package cn.nukkit.event.block;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAnvil;
import cn.nukkit.block.BlockID;
import cn.nukkit.blockproperty.value.AnvilDamage;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.inventory.transaction.CraftingTransaction;
import cn.nukkit.level.Level;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.powernukkit.tests.api.MockLevel;
import org.powernukkit.tests.api.MockPlayer;
import org.powernukkit.tests.junit.jupiter.PowerNukkitExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author joserobjr
 * @since 2021-12-11
 */
@ExtendWith(PowerNukkitExtension.class)
class AnvilDamageEventTest {

    AnvilDamageEvent event;

    @MockLevel
    Level level;

    @MockPlayer
    Player player;

    CraftingTransaction fakeTransaction;

    @BeforeEach
    void setUp() {
        fakeTransaction = new CraftingTransaction(player, Collections.emptyList());
        level.setBlockIdAt(0, 1, 0, BlockID.STONE);
        level.setBlockIdAt(0, 2, 0, BlockID.ANVIL);
        Block block = level.getBlock(0, 2, 0);
        event = new AnvilDamageEvent(
                block,
                BlockState.of(BlockID.ANVIL).withProperty(BlockAnvil.DAMAGE, AnvilDamage.SLIGHTLY_DAMAGED),
                player,
                fakeTransaction,
                AnvilDamageEvent.DamageCause.USE
        );
    }

    @Test
    void constructor2() {
        Block block = level.getBlock(0, 2, 0);
        BlockState before = block.getCurrentState();
        Block result = block.clone();
        result.setPropertyValue(BlockAnvil.DAMAGE, AnvilDamage.VERY_DAMAGED);
        BlockState after = result.getCurrentState();
        event = new AnvilDamageEvent(
                block,
                result,
                player,
                fakeTransaction,
                AnvilDamageEvent.DamageCause.USE
        );
        assertEquals(before, event.getOldBlockState());
        assertEquals(after, event.getNewBlockState());
    }

    @SuppressWarnings("deprecation")
    @Test
    void constructor3() {
        Block block = level.getBlock(0, 2, 0);
        BlockState before = block.getCurrentState();
        Block result = block.clone();
        result.setPropertyValue(BlockAnvil.DAMAGE, AnvilDamage.VERY_DAMAGED);
        BlockState after = result.getCurrentState();
        event = new AnvilDamageEvent(
                block,
                before.getLegacyDamage(),
                after.getLegacyDamage(),
                AnvilDamageEvent.DamageCause.USE,
                player
        );
        assertEquals(before, event.getOldBlockState());
        assertEquals(after, event.getNewBlockState());
    }

    @Test
    void getHandlers() {
        assertNotNull(AnvilDamageEvent.getHandlers());
    }

    @Test
    void getTransaction() {
        assertSame(fakeTransaction, event.getTransaction());
    }

    @Test
    void getDamageCause() {
        assertEquals(AnvilDamageEvent.DamageCause.USE, event.getDamageCause());
    }

    @SuppressWarnings("deprecation")
    @Test
    void getOldDamage() {
        assertEquals(0, event.getOldDamage());
    }

    @Test
    void getOldAnvilDamage() {
        assertEquals(AnvilDamage.UNDAMAGED, event.getOldAnvilDamage());
    }

    @Test
    void getOldBlockState() {
        assertEquals(BlockState.of(BlockID.ANVIL).withProperty(BlockAnvil.DAMAGE, AnvilDamage.UNDAMAGED), event.getOldBlockState());
    }

    @Test
    void getNewBlockState() {
        assertEquals(BlockState.of(BlockID.ANVIL).withProperty(BlockAnvil.DAMAGE, AnvilDamage.SLIGHTLY_DAMAGED), event.getNewBlockState());
    }

    @Test
    void getNewState() {
        assertEquals(event.getNewBlockState().getBlock(level, 0, 2, 0), event.getNewState());
    }

    @SuppressWarnings("deprecation")
    @Test
    void getNewDamage() {
        assertEquals(AnvilDamage.SLIGHTLY_DAMAGED.ordinal(), event.getNewDamage());
    }

    @Test
    void setNewBlockState() {
        BlockState anvil = BlockState.of(BlockID.ANVIL);
        assertEquals(anvil.withProperty(BlockAnvil.DAMAGE, AnvilDamage.SLIGHTLY_DAMAGED), event.getNewBlockState());
        event.setNewBlockState(anvil.withProperty(BlockAnvil.DAMAGE, AnvilDamage.VERY_DAMAGED));
        assertEquals(anvil.withProperty(BlockAnvil.DAMAGE, AnvilDamage.VERY_DAMAGED), event.getNewBlockState());
    }

    @Test
    void setNewDamage() {
        BlockState anvil = BlockState.of(BlockID.ANVIL);
        assertEquals(anvil.withProperty(BlockAnvil.DAMAGE, AnvilDamage.SLIGHTLY_DAMAGED), event.getNewBlockState());
        event.setNewDamage(AnvilDamage.VERY_DAMAGED.ordinal());
        assertEquals(anvil.withProperty(BlockAnvil.DAMAGE, AnvilDamage.VERY_DAMAGED), event.getNewBlockState());
    }

    @Test
    void setNewState() {
        BlockState anvil = BlockState.of(BlockID.ANVIL);
        assertEquals(anvil.withProperty(BlockAnvil.DAMAGE, AnvilDamage.SLIGHTLY_DAMAGED), event.getNewBlockState());
        event.setNewState(anvil.withProperty(BlockAnvil.DAMAGE, AnvilDamage.VERY_DAMAGED).getBlock(level, 0, 2, 0));
        assertEquals(anvil.withProperty(BlockAnvil.DAMAGE, AnvilDamage.VERY_DAMAGED), event.getNewBlockState());
    }

    @Test
    void getCause() {
        assertEquals(AnvilDamageEvent.DamageCause.USE, event.getDamageCause());
    }

    @Test
    void getPlayer() {
        assertSame(player, event.getPlayer());
    }

    @SuppressWarnings("deprecation")
    @Test
    void nonAnvil() {
        level.setBlockStateAt(1, 2, 3, BlockState.of(BlockID.STONE));
        Block block = level.getBlock(1, 2, 3);
        event = new AnvilDamageEvent(block, BlockState.of(BlockID.GLASS), null, null, AnvilDamageEvent.DamageCause.FALL);
        assertEquals(0, event.getOldDamage());
        assertEquals(0, event.getNewDamage());
        assertNull(event.getOldAnvilDamage());

        event.setNewDamage(1);
        assertEquals(0, event.getNewDamage());
        assertNull(event.getOldAnvilDamage());

        assertEquals(AnvilDamageEvent.DamageCause.FALL, event.getCause());
    }
}
