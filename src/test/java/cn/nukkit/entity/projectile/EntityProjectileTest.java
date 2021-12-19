package cn.nukkit.entity.projectile;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockID;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityCombustByEntityEvent;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.powernukkit.tests.api.MockLevel;
import org.powernukkit.tests.api.MockPlayer;
import org.powernukkit.tests.junit.jupiter.PowerNukkitExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

/**
 * @author joserobjr
 * @since 2021-12-15
 */
@PowerNukkitOnly
@Since("FUTURE")
@ExtendWith(PowerNukkitExtension.class)
class EntityProjectileTest {
    @MockPlayer(position = {0, 64, 0})
    Player player;

    @MockLevel
    Level level;

    TestProjectile projectile;
    @BeforeEach
    void setUp() {
        level.setBlockStateAt(0, 63, 0, BlockState.of(BlockID.STONE));
        projectile = new TestProjectile(level.getChunk(0, 0), Entity.getDefaultNBT(new Vector3(0, 64, 0)), player);
    }

    @Test
    void getDamage() {
        projectile.motionX = 0.5;
        projectile.motionY = -0.005;
        projectile.motionZ = 0.5;
        double damage = projectile.getResultDamage();
        assertEquals(damage, projectile.getResultDamage(player));
        assertEquals(3, damage);
    }

    @Test
    void fire() {
        projectile.fireTicks = 20;
        projectile.onCollideWithEntity(player);
        verify(Server.getInstance().getPluginManager()).callEvent(any(EntityCombustByEntityEvent.class));
        verify(player).setOnFire(eq(5));
    }

    @SuppressWarnings("deprecation")
    @Test
    void noAge() {
        assertTrue(projectile.getHasAge());
        assertTrue(projectile.hasAge());

        projectile.setAge(false);
        projectile.namedTag.remove("Age");
        projectile.saveNBT();
        assertFalse(projectile.getHasAge());
        assertFalse(projectile.hasAge());
        assertFalse(projectile.namedTag.contains("Age"));

        projectile.setHasAge(true);
        projectile.namedTag.remove("Age");
        projectile.saveNBT();
        assertTrue(projectile.getHasAge());
        assertTrue(projectile.hasAge());
        assertTrue(projectile.namedTag.contains("Age"));

        projectile.setHasAge(false);
        assertFalse(projectile.getHasAge());
        assertFalse(projectile.hasAge());

        projectile.setAge(true);
        assertTrue(projectile.getHasAge());
        assertTrue(projectile.hasAge());
    }

    public static class TestProjectile extends EntityProjectile {
        public TestProjectile(FullChunk chunk, CompoundTag nbt) {
            super(chunk, nbt);
        }

        public TestProjectile(FullChunk chunk, CompoundTag nbt, Entity shootingEntity) {
            super(chunk, nbt, shootingEntity);
        }

        @Override
        public int getNetworkId() {
            return 1;
        }

        @Override
        protected double getDamage() {
            return 3;
        }
    }
}
