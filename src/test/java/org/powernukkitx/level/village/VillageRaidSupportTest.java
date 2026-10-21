package org.powernukkitx.level.village;

import org.junit.jupiter.api.Test;
import org.powernukkitx.Player;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.entity.ai.memory.MemoryStorage;
import org.powernukkitx.entity.mob.*;
import org.powernukkitx.level.Level;
import org.powernukkitx.math.AxisAlignedBB;
import org.powernukkitx.math.BlockVector3;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.cloudburstmc.protocol.bedrock.data.SoundEvent;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VillageRaidSupportTest {
    @Test
    void existingMobsCanSupplyAWaveEvenWhenNoNewActorSpawns() {
        Fixture f = new Fixture();
        EntityPillager recruit = f.actor(EntityPillager.class, 43, Entity.PILLAGER, false);
        when(f.level.fastNearbyEntities(any(AxisAlignedBB.class), isNull(), eq(false))).thenReturn(List.of(recruit));
        f.village.setRaid(VillageRaid.starting(0, 3, new Vector3()).withState(VillageRaid.STATE_SPAWNING_GROUP));
        try (var entities = mockStatic(Entity.class)) {
            f.tick(1);
        }

        assertEquals(VillageRaid.STATE_GROUP_IN_PLAY, f.village.raid().state());
        assertEquals(List.of(43L), f.village.raid().raiders());
        assertEquals(0, f.village.raid().groupNumber());
        assertTrue(f.manager.isRaider(recruit));
        verify(recruit).setRaiding(true);
        verify(recruit).setPersistent(true);
    }

    @Test
    void recruitsAllFiveTypesOnceAndPersistsTheirHealth() {
        Fixture f = new Fixture();
        EntityPillager pillager = f.actor(EntityPillager.class, 43, Entity.PILLAGER, false);
        EntityVindicator vindicator = f.actor(EntityVindicator.class, 44, Entity.VINDICATOR, false);
        EntityRavager ravager = f.actor(EntityRavager.class, 45, Entity.RAVAGER, false);
        EntityWitch witch = f.actor(EntityWitch.class, 46, Entity.WITCH, false);
        EntityEvocationIllager evoker = f.actor(EntityEvocationIllager.class, 47, Entity.EVOCATION_ILLAGER, false);
        when(f.level.fastNearbyEntities(any(AxisAlignedBB.class), any(Entity.class), eq(false)))
                .thenReturn(List.of(pillager, vindicator, ravager, witch, evoker, pillager));

        f.tick(20);
        f.tick(40);

        assertEquals(List.of(42L, 43L, 44L, 45L, 46L, 47L), f.village.raid().raiders());
        assertEquals(6, f.village.raid().numberOfRaiders());
        assertEquals(144f, f.village.raid().totalMaxHealth());
        VillageRaid saved = VillageRaid.fromCompound(f.village.raid().toCompound());
        assertEquals(6, saved.raiderHealth().size());
        assertEquals(12f, saved.raiderHealth().get(46L));
        assertEquals(new Vector3(0.5, 64, 0.5), witch.getMemoryStorage().get(CoreMemoryTypes.RAID_TARGET));
        verify(pillager, times(1)).setPersistent(true);
    }

    @Test
    void doesNotStealForeignRaidersOrRecruitDeadBabyAndCelebratingActors() {
        Fixture f = new Fixture();
        EntityPillager foreign = f.actor(EntityPillager.class, 43, Entity.PILLAGER, false);
        foreign.getNbt().putString("RaidVillageUUID", UUID.randomUUID().toString());
        EntityPillager dead = f.actor(EntityPillager.class, 44, Entity.PILLAGER, false);
        when(dead.isAlive()).thenReturn(false);
        EntityPillager baby = f.actor(EntityPillager.class, 45, Entity.PILLAGER, false);
        when(baby.isBaby()).thenReturn(true);
        EntityWitch celebrating = f.actor(EntityWitch.class, 46, Entity.WITCH, false);
        celebrating.getMemoryStorage().put(CoreMemoryTypes.CELEBRATING, true);
        EntityZombie zombie = f.actor(EntityZombie.class, 47, Entity.ZOMBIE, false);
        when(f.level.fastNearbyEntities(any(AxisAlignedBB.class), any(Entity.class), eq(false)))
                .thenReturn(List.of(foreign, dead, baby, celebrating, zombie));

        f.tick(20);

        assertEquals(List.of(42L), f.village.raid().raiders());
        assertFalse(f.manager.isRaider(foreign));
        assertFalse(f.manager.isRaider(dead));
        assertFalse(f.manager.isRaider(zombie));
        verify(foreign, never()).setPersistent(true);
    }

    @Test
    void helpHornRepeatsAtALivingRaiderEveryThirtySeconds() {
        Fixture f = new Fixture();
        for (int tick = 1; tick < 600; tick++) f.tick(tick);
        verify(f.level, never()).addLevelSoundEvent(any(Vector3.class), eq(SoundEvent.RAID_HORN));
        f.tick(600);
        verify(f.level, times(1)).addLevelSoundEvent(f.member, SoundEvent.RAID_HORN);
        for (int tick = 601; tick <= 1200; tick++) f.tick(tick);
        verify(f.level, times(2)).addLevelSoundEvent(f.member, SoundEvent.RAID_HORN);
    }

    @Test
    void eliminatingARaiderRestartsTheHelpDelay() {
        Fixture f = new Fixture();
        EntityPillager other = f.actor(EntityPillager.class, 43, Entity.PILLAGER, true);
        f.village.setRaid(f.village.raid().withGroup(0, List.of(42L, 43L), 48));
        for (int tick = 1; tick < 600; tick++) f.tick(tick);
        when(other.isAlive()).thenReturn(false);
        f.tick(600);
        for (int tick = 601; tick < 1200; tick++) f.tick(tick);
        verify(f.level, never()).addLevelSoundEvent(any(Vector3.class), eq(SoundEvent.RAID_HORN));
        f.tick(1200);
        verify(f.level).addLevelSoundEvent(f.member, SoundEvent.RAID_HORN);
    }

    @Test
    void unloadedRaidersDoNotProduceAHornOrLoadChunks() {
        Fixture f = new Fixture();
        when(f.level.getEntityByUniqueId(42)).thenReturn(null);
        for (int tick = 1; tick <= 600; tick++) f.tick(tick);
        assertEquals(List.of(42L), f.village.raid().raiders());
        verify(f.level, never()).addLevelSoundEvent(any(Vector3.class), eq(SoundEvent.RAID_HORN));
        verify(f.level, never()).getChunk(anyInt(), anyInt());
    }

    private static class Fixture {
        final Level level = mock(Level.class);
        final VillageManager manager = new VillageManager(level);
        final Village village = manager.createVillage(new BlockVector3(0, 64, 0));
        final EntityPillager member;

        Fixture() {
            manager.addDweller(village.uuid(), new VillageDwellers.Actor(1L, new BlockVector3(), 0L, null));
            village.pois().poi().add(new VillagePoiGroup(1L,
                    List.of(new VillagePoi(PoiType.HOME, new BlockVector3(0, 64, 0)))));
            Player player = mock(Player.class);
            when(player.getX()).thenReturn(0.5);
            when(player.getY()).thenReturn(0.5);
            when(player.getZ()).thenReturn(0.5);
            when(level.getPlayers()).thenReturn(Map.of(7L, player));
            member = actor(EntityPillager.class, 42, Entity.PILLAGER, true);
            village.setRaid(VillageRaid.starting(0, 3, new Vector3()).withGroup(0, List.of(42L), 24));
        }

        <T extends EntityIntelligent> T actor(Class<T> type, long id, String identifier, boolean associated) {
            T actor = mock(type);
            when(actor.getIdentifier()).thenReturn(identifier);
            when(actor.getLevel()).thenReturn(level);
            when(actor.uniqueIdLong()).thenReturn(id);
            when(actor.getNbt()).thenReturn(new CompoundTag());
            when(actor.getMemoryStorage()).thenReturn(new MemoryStorage(actor));
            when(actor.isAlive()).thenReturn(true);
            when(actor.getMaxHealth()).thenReturn(24);
            when(actor.getHealth()).thenReturn(12f);
            when(actor.asBlockVector3()).thenReturn(new BlockVector3(0, 64, 0));
            when(level.getEntityByUniqueId(id)).thenReturn(actor);
            if (associated) actor.getNbt().putString("RaidVillageUUID", village.uuid().toString());
            return actor;
        }

        void tick(int tick) {
            when(level.getCurrentTick()).thenReturn((long) tick);
            manager.tick(tick);
        }
    }
}
