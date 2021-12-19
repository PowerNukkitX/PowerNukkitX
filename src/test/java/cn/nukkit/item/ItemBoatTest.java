package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.entity.item.EntityBoat;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.powernukkit.tests.api.MockLevel;
import org.powernukkit.tests.api.MockPlayer;
import org.powernukkit.tests.junit.jupiter.PowerNukkitExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author joserobjr
 * @since 2021-12-19
 */
@PowerNukkitOnly
@Since("FUTURE")
@ExtendWith(PowerNukkitExtension.class)
class ItemBoatTest {
    ItemBoat item;

    @MockLevel
    Level level;

    @MockPlayer(position = {0, 66, 0})
    Player player;

    @Test
    void defaultName() {
        item = new ItemBoat(1000);
        assertEquals("Boat", item.getName());
    }

    @Test
    void onActivate() {
        level.setBlock(0, 64 , 0, Block.get(BlockID.STILL_WATER), true, false);
        Block water = level.getBlock(0, 64, 0);
        item = new ItemBoat(2);
        item.onActivate(level, player, water.up(), water, BlockFace.UP, 0.5, 0.5, 0.5);
        final Optional<EntityBoat> boat = Arrays.stream(level.getEntities()).filter(EntityBoat.class::isInstance).map(EntityBoat.class::cast).findFirst();
        assertTrue(boat.isPresent(), "The boat did not spawn");
        assertEquals(2, boat.get().namedTag.getInt("Variant"));
    }
}
