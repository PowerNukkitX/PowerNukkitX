package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockID;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.entity.projectile.EntityThrownTrident;
import cn.nukkit.event.Event;
import cn.nukkit.event.entity.EntityShootBowEvent;
import cn.nukkit.event.entity.ProjectileLaunchEvent;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Level;
import cn.nukkit.plugin.PluginManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.powernukkit.tests.api.MockLevel;
import org.powernukkit.tests.api.MockPlayer;
import org.powernukkit.tests.junit.jupiter.PowerNukkitExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

/**
 * @author joserobjr
 * @since 2021-12-20
 */
@PowerNukkitOnly
@Since("FUTURE")
@ExtendWith(PowerNukkitExtension.class)
class ItemTridentTest {
    @MockLevel
    Level level;

    @MockPlayer(position = {0, 65, 0})
    Player player;

    ItemTrident item;

    @BeforeEach
    void setUp() {
        level.setBlockStateAt(0, 64, 0, BlockState.of(BlockID.STONE));
        item = new ItemTrident();
    }

    @Test
    void onReleaseRiptide() {
        item.addEnchantment(Enchantment.getEnchantment(Enchantment.ID_TRIDENT_RIPTIDE));
        assertTrue(item.onRelease(player, 20));
        assertEquals(0, item.getDamage());
    }

    @Test
    void onReleaseCreative() {
        player.setGamemode(Player.CREATIVE);
        assertTrue(player.isCreative());

        assertTrue(item.onRelease(player, 20));
        Optional<EntityThrownTrident> optTrident = Arrays.stream(level.getEntities()).filter(EntityThrownTrident.class::isInstance).map(EntityThrownTrident.class::cast).findFirst();
        assertTrue(optTrident.isPresent());
        assertTrue(optTrident.get().isCreative());
    }

    @Test
    void onReleaseCancelBow() {
        PluginManager pluginManager = Server.getInstance().getPluginManager();
        doAnswer(call -> {
            ((Event)call.getArgument(0)).setCancelled();
            return null;
        }).when(pluginManager).callEvent(any(EntityShootBowEvent.class));
        assertTrue(item.onRelease(player, 20));
        Optional<EntityThrownTrident> optTrident = Arrays.stream(level.getEntities()).filter(EntityThrownTrident.class::isInstance).map(EntityThrownTrident.class::cast).findFirst();
        assertFalse(optTrident.isPresent());
    }

    @Test
    void onReleaseCancelProjectLaunch() {
        PluginManager pluginManager = Server.getInstance().getPluginManager();
        doAnswer(call -> {
            ((Event)call.getArgument(0)).setCancelled();
            return null;
        }).when(pluginManager).callEvent(any(ProjectileLaunchEvent.class));
        assertTrue(item.onRelease(player, 20));
        Optional<EntityThrownTrident> optTrident = Arrays.stream(level.getEntities()).filter(EntityThrownTrident.class::isInstance).map(EntityThrownTrident.class::cast).findFirst();
        assertFalse(optTrident.isPresent());
    }
}
