package cn.nukkit.event.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockBell;
import cn.nukkit.block.BlockID;
import cn.nukkit.blockproperty.value.AttachmentType;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.level.Level;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.powernukkit.tests.api.MockLevel;
import org.powernukkit.tests.api.MockPlayer;
import org.powernukkit.tests.junit.jupiter.PowerNukkitExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * @author joserobjr
 * @since 2021-12-19
 */
@PowerNukkitOnly
@Since("FUTURE")
@ExtendWith(PowerNukkitExtension.class)
class BellRingEventTest {
    @MockLevel
    Level level;

    @MockPlayer
    Player player;

    BellRingEvent event;
    BlockBell bell;

    @BeforeEach
    void setUp() {
        level.setBlockStateAt(0, 1, 0, BlockState.of(BlockID.STONE));
        level.setBlockStateAt(0, 2, 0, BlockState.of(BlockID.BELL).withProperty(BlockBell.ATTACHMENT_TYPE, AttachmentType.STANDING));
        bell = (BlockBell) level.getBlock(0, 2, 0);
    }

    @Test
    void construction() {
        event = new BellRingEvent(bell, BellRingEvent.RingCause.HUMAN_INTERACTION, player);
        assertEquals(bell, event.getBlock());
        assertSame(player, event.getEntity());
        assertEquals(BellRingEvent.RingCause.HUMAN_INTERACTION, event.getCause());
    }
}
