package cn.nukkit.entity.projectile;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockID;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.passive.EntityPig;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.powernukkit.tests.api.MockEntity;
import org.powernukkit.tests.api.MockLevel;
import org.powernukkit.tests.junit.jupiter.PowerNukkitExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author joserobjr
 * @since 2021-12-15
 */
@PowerNukkitOnly
@Since("FUTURE")
@ExtendWith(PowerNukkitExtension.class)
class EntityThrownTridentTest {
    @MockLevel
    Level level;

    @MockEntity(position = {0, 64, 0})
    EntityPig pig;

    EntityThrownTrident trident;

    @BeforeEach
    void setUp() {
        level.setBlockStateAt(0, 64, 0, BlockState.of(BlockID.STILL_WATER));
        trident = new EntityThrownTrident(level.getChunk(0, 0), Entity.getDefaultNBT(new Vector3(0, 64, 0)));
    }

    @Test
    void backwardIsCreative() {
        trident = new EntityThrownTrident(level.getChunk(0, 0), Entity.getDefaultNBT(new Vector3(0, 64, 0))
                .putBoolean("isCreative", true));
        assertTrue(trident.isCreative());

        trident = new EntityThrownTrident(level.getChunk(0, 0), Entity.getDefaultNBT(new Vector3(0, 64, 0))
                .putBoolean("isCreative", false));
        assertFalse(trident.isCreative());

        trident = new EntityThrownTrident(level.getChunk(0, 0), Entity.getDefaultNBT(new Vector3(0, 64, 0))
                .putBoolean("isCreative", true)
                .putByte("pickup", EntityProjectile.PICKUP_NONE));
        assertFalse(trident.isCreative());
    }

    @Test
    void pickupMode() {
        trident.setPickupMode(EntityProjectile.PICKUP_CREATIVE);
        assertEquals(EntityProjectile.PICKUP_CREATIVE, trident.getPickupMode());
    }

    @Test
    void alreadyCollided() {
        trident.alreadyCollided = true;
        trident.onCollideWithEntity(pig);
        verify(pig, times(0)).attack(any(EntityDamageEvent.class));
    }

    @Test
    void saveLoad() {
        trident.trident = Item.get(ItemID.TRIDENT);
        trident.trident.addEnchantment(Enchantment.getEnchantment(Enchantment.ID_MENDING));
        Item itemTrident = trident.trident.clone();
        trident.setPickupMode(EntityProjectile.PICKUP_ANY);
        trident.saveNBT();
        EntityThrownTrident other = new EntityThrownTrident(level.getChunk(0, 0), trident.namedTag.copy());
        assertEquals(EntityProjectile.PICKUP_ANY, trident.getPickupMode());
        assertEquals(itemTrident, other.trident);
        assertEquals(0.05f, trident.getGravity());
    }
}
