package org.powernukkitx.entity.ai.executor;

import org.junit.jupiter.api.Test;
import org.powernukkitx.Server;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityID;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.entity.ai.memory.MemoryStorage;
import org.powernukkitx.entity.ai.sensor.RaidHealingSensor;
import org.powernukkitx.entity.item.EntitySplashPotion;
import org.powernukkitx.entity.mob.EntityPillager;
import org.powernukkitx.entity.mob.EntityWitch;
import org.powernukkitx.event.entity.ProjectileLaunchEvent;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Location;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.village.VillageManager;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.plugin.PluginManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RaidHealingExecutorTest {
    @Test
    void choosesHealingAtTwoHeartsAndRegenerationAboveIt() {
        Fixture f = new Fixture();
        RaidHealingExecutor executor = new RaidHealingExecutor();
        for (float health : new float[]{1f, 4f, 4.5f, 24f}) {
            when(f.ally.getHealthCurrent()).thenReturn(health);
            assertEquals(health <= 4 ? 21 : 28, executor.getPotionEffect(f.witch));
        }
    }

    @Test
    void sensorExcludesOtherWitchesAndNonRaidersAndHonorsRangeAndCooldown() {
        Fixture f = new Fixture();
        EntityWitch otherWitch = mock(EntityWitch.class);
        EntityPillager outsider = mock(EntityPillager.class);
        when(f.manager.isRaider(otherWitch)).thenReturn(true);
        f.memory.clear(CoreMemoryTypes.RAID_HEAL_TARGET);
        when(f.level.getEntities(f.witch, 10, false)).thenReturn(List.of(f.witch, otherWitch, outsider, f.ally));
        RaidHealingSensor sensor = new RaidHealingSensor();
        sensor.sense(f.witch);
        assertSame(f.ally, f.memory.get(CoreMemoryTypes.RAID_HEAL_TARGET));

        f.memory.put(CoreMemoryTypes.RAID_HEAL_COOLDOWN, 200L);
        when(f.level.getCurrentTick()).thenReturn(199L);
        sensor.sense(f.witch);
        assertNull(f.memory.get(CoreMemoryTypes.RAID_HEAL_TARGET));
        when(f.level.getCurrentTick()).thenReturn(200L);
        sensor.sense(f.witch);
        assertSame(f.ally, f.memory.get(CoreMemoryTypes.RAID_HEAL_TARGET));
        when(f.witch.distanceSquared(f.ally)).thenReturn(101d);
        sensor.sense(f.witch);
        assertNull(f.memory.get(CoreMemoryTypes.RAID_HEAL_TARGET));
    }

    @Test
    void executorStopsIfEitherActorLeavesTheRaidOrTargetIsOutOfSight() {
        Fixture f = new Fixture();
        RaidHealingExecutor executor = new RaidHealingExecutor();
        when(f.manager.isRaider(f.ally)).thenReturn(false);
        assertFalse(executor.execute(f.witch));
        when(f.manager.isRaider(f.ally)).thenReturn(true);
        when(f.manager.isRaider(f.witch)).thenReturn(false);
        assertFalse(executor.execute(f.witch));
        when(f.manager.isRaider(f.witch)).thenReturn(true);
        when(f.witch.hasLineOfSight(f.ally)).thenReturn(false);
        assertFalse(executor.execute(f.witch));
    }

    @Test
    void throwsARealSupportPotionThenLosesTargetForTenSeconds() {
        checkThrow(false);
    }

    @Test
    void cancelledSupportProjectileIsNotSpawned() {
        checkThrow(true);
    }

    private void checkThrow(boolean cancel) {
        Fixture f = new Fixture();
        Server server = mock(Server.class);
        PluginManager plugins = mock(PluginManager.class);
        when(server.getPluginManager()).thenReturn(plugins);
        when(f.level.getCurrentTick()).thenReturn(50L);
        when(f.ally.getHealthCurrent()).thenReturn(4f);
        when(f.witch.getLocation()).thenReturn(new Location(0, 64, 0, f.level));
        when(f.witch.getDirectionVector()).thenReturn(new Vector3(0, 0, 1));
        f.witch.level = f.level;
        EntitySplashPotion projectile = mock(EntitySplashPotion.class);
        var nbt = new java.util.ArrayList<CompoundTag>();
        if (cancel) doAnswer(invocation -> {
            ((ProjectileLaunchEvent) invocation.getArgument(0)).setCancelled();
            return null;
        }).when(plugins).callEvent(any(ProjectileLaunchEvent.class));

        try (var servers = mockStatic(Server.class); var entities = mockStatic(Entity.class)) {
            servers.when(Server::getInstance).thenReturn(server);
            entities.when(() -> Entity.createEntity(eq(EntityID.SPLASH_POTION), isNull(IChunk.class), any(CompoundTag.class)))
                    .thenAnswer(invocation -> {
                        nbt.add(invocation.getArgument(2));
                        return projectile;
                    });
            new RaidHealingExecutor().throwPotion(f.witch);
        }

        assertEquals(21, nbt.getFirst().getInt("PotionId"));
        assertSame(f.witch, projectile.shootingEntity);
        assertNull(f.memory.get(CoreMemoryTypes.RAID_HEAL_TARGET));
        assertEquals(250L, f.memory.get(CoreMemoryTypes.RAID_HEAL_COOLDOWN));
        verify(plugins).callEvent(any(ProjectileLaunchEvent.class));
        if (cancel) {
            verify(projectile).kill();
            verify(projectile, never()).spawnToAll();
        } else verify(projectile).spawnToAll();
    }

    private static class Fixture {
        final Level level = mock(Level.class);
        final VillageManager manager = mock(VillageManager.class);
        final EntityWitch witch = mock(EntityWitch.class);
        final EntityPillager ally = mock(EntityPillager.class);
        final MemoryStorage memory = new MemoryStorage(witch);

        Fixture() {
            when(witch.getLevel()).thenReturn(level);
            when(witch.getMemoryStorage()).thenReturn(memory);
            when(level.getVillageManager()).thenReturn(manager);
            when(manager.isRaider(witch)).thenReturn(true);
            when(manager.isRaider(ally)).thenReturn(true);
            when(witch.distanceSquared(ally)).thenReturn(100d);
            when(witch.hasLineOfSight(ally)).thenReturn(true);
            memory.put(CoreMemoryTypes.RAID_HEAL_TARGET, ally);
        }
    }
}
