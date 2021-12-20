package cn.nukkit.entity.item;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockID;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.passive.EntityPig;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.BubbleParticle;
import cn.nukkit.level.particle.WaterParticle;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.AddEntityPacket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.powernukkit.tests.api.MockEntity;
import org.powernukkit.tests.api.MockLevel;
import org.powernukkit.tests.junit.jupiter.PowerNukkitExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author joserobjr
 * @since 2021-12-13
 */
@PowerNukkitOnly
@Since("FUTURE")
@ExtendWith(PowerNukkitExtension.class)
class EntityFishingHookTest {
    @MockLevel
    Level level;

    @MockEntity(type = EntityPig.class)
    EntityPig pig;

    EntityFishingHook fishingHook;

    @BeforeEach
    void setUp() {
        level.setBlockStateAt(0, 63, 0, BlockState.of(BlockID.STILL_WATER));
        fishingHook = new EntityFishingHook(level.getChunk(0, 0), Entity.getDefaultNBT(new Vector3(0, 64, 0)));
    }

    @Test
    void overAgedFishingHook() {
        fishingHook = new EntityFishingHook(level.getChunk(0, 0), Entity.getDefaultNBT(new Vector3(0, 64, 0))
                .putShort("Age", 1));
        assertTrue(fishingHook.closed);
    }

    @Test
    void canCollide() {
        fishingHook.canCollide = false;
        assertFalse(fishingHook.canCollide());

        fishingHook.canCollide = true;
        assertTrue(fishingHook.canCollide());
    }

    @Test
    void setTarget() {
        fishingHook.setTarget(1);
        assertEquals(1L, fishingHook.getDataPropertyLong(Entity.DATA_TARGET_EID));
        assertFalse(fishingHook.canCollide);
        fishingHook.setTarget(0);
        assertEquals(0L, fishingHook.getDataPropertyLong(Entity.DATA_TARGET_EID));
        assertTrue(fishingHook.canCollide);
    }

    @Test
    void spawnPacket() {
        Player player = mock(Player.class);
        fishingHook.spawnTo(player);
        verify(player).dataPacket(any(AddEntityPacket.class));
    }

    @Test
    void spawnFish() {
        assertNull(fishingHook.fish);
        fishingHook.spawnFish();
        assertNotNull(fishingHook.fish);
    }

    @Test
    void attractFish() {
        fishingHook.level = mock(Level.class);
        fishingHook.fish = new Vector3(100, fishingHook.y, 100);
        int attempts = 0;
        do {
            if (attempts++ == 1000) {
                fail();
            }
        } while (!fishingHook.attractFish());

        verify(fishingHook.level, atLeastOnce()).addParticle(any(WaterParticle.class));
    }

    @Test
    void checkLureNoRod() {
        assertEquals(120, fishingHook.waitChance);
        fishingHook.checkLure();
        assertEquals(120, fishingHook.waitChance);
    }

    @Test
    void checkLureNoLure() {
        fishingHook.rod = Item.get(ItemID.FISHING_ROD);
        assertEquals(120, fishingHook.waitChance);
        fishingHook.checkLure();
        assertEquals(120, fishingHook.waitChance);
    }

    @Test
    void checkLureLv1() {
        fishingHook.rod = Item.get(ItemID.FISHING_ROD);
        fishingHook.rod.addEnchantment(Enchantment.getEnchantment(Enchantment.ID_LURE));
        assertEquals(120, fishingHook.waitChance);
        fishingHook.checkLure();
        assertEquals(120 - 25, fishingHook.waitChance);
    }

    @Test
    void checkLureLv2() {
        fishingHook.rod = Item.get(ItemID.FISHING_ROD);
        fishingHook.rod.addEnchantment(Enchantment.getEnchantment(Enchantment.ID_LURE).setLevel(2));
        assertEquals(120, fishingHook.waitChance);
        fishingHook.checkLure();
        assertEquals(120 - 50, fishingHook.waitChance);
    }

    @Test
    void onCollideWithEntity() {
        assertNotEquals(0, pig.getId());
        fishingHook.onCollideWithEntity(pig);
        assertEquals(pig.getId(), fishingHook.getDataPropertyLong(Entity.DATA_TARGET_EID));
    }

    @Test
    void fishBites() {
        fishingHook.level = mock(Level.class);
        fishingHook.fishBites();
        verify(fishingHook.level, times(5)).addParticle(any(BubbleParticle.class));
    }
}
