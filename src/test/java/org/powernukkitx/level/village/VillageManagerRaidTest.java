package org.powernukkitx.level.village;

import org.junit.jupiter.api.Test;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.entity.ai.memory.MemoryStorage;
import org.powernukkitx.entity.mob.EntityPillager;
import org.powernukkitx.level.Level;
import org.powernukkitx.math.BlockVector3;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VillageManagerRaidTest {
    @Test
    void reloadRestoresNavigationForAnActiveRaid() {
        Level level = mock(Level.class);
        VillageManager manager = new VillageManager(level);
        Village village = manager.createVillage(new BlockVector3(0, 64, 0));
        village.setRaid(VillageRaid.starting(0, 3, new Vector3()).withGroup(1, List.of(42L), 24f));
        EntityPillager raider = raider(village);

        manager.restoreRaider(raider);

        assertEquals(new Vector3(0.5, 64, 0.5), raider.getMemoryStorage().get(CoreMemoryTypes.RAID_TARGET));
    }

    @Test
    void removingVillageClearsLoadedRaiderAndRaidState() {
        Level level = mock(Level.class);
        VillageManager manager = new VillageManager(level);
        Village village = manager.createVillage(new BlockVector3(0, 64, 0));
        village.setRaid(VillageRaid.starting(0, 3, new Vector3()).withGroup(1, List.of(42L), 24f));
        village.raiderPositions().put(42L, new BlockVector3(1, 64, 1));
        village.setBellRingTick(100);
        EntityPillager raider = raider(village);
        when(level.getEntityByUniqueId(42L)).thenReturn(raider);
        manager.restoreRaider(raider);

        assertTrue(manager.removeVillage(village));

        assertNull(village.raid());
        assertTrue(village.raiderPositions().isEmpty());
        assertEquals(0, village.bellRingTick());
        assertNull(raider.getMemoryStorage().get(CoreMemoryTypes.RAID_TARGET));
        assertEquals("", raider.getNbt().getString("RaidVillageUUID"));
        verify(raider).setRaiding(false);
        verify(raider).setPersistent(false);
    }

    @Test
    void reloadClearsRaidStateForARaiderWhoseVillageWasRemoved() {
        Level level = mock(Level.class);
        VillageManager manager = new VillageManager(level);
        Village village = manager.createVillage(new BlockVector3(0, 64, 0));
        EntityPillager raider = raider(village);
        manager.removeVillage(village.uuid());

        manager.restoreRaider(raider);

        assertNull(raider.getMemoryStorage().get(CoreMemoryTypes.RAID_TARGET));
        verify(raider).setRaiding(false);
        verify(raider).setPersistent(false);
    }

    @Test
    void invalidVillageLosesItsRaidAndStartsRaiderCelebration() {
        Level level = mock(Level.class);
        VillageManager manager = new VillageManager(level);
        Village village = manager.createVillage(new BlockVector3(0, 64, 0));
        village.setRaid(VillageRaid.starting(0, 3, new Vector3()).withGroup(1, List.of(42L), 24f));
        EntityPillager raider = raider(village);
        when(level.getEntityByUniqueId(42L)).thenReturn(raider);

        when(level.getPlayers()).thenReturn(new java.util.HashMap<>());
        manager.tick(1);

        assertEquals(VillageRaid.STATUS_LOSS, village.raid().status());
        assertTrue(raider.getMemoryStorage().get(CoreMemoryTypes.CELEBRATING));
        verify(raider).setRaiding(false);
        verify(raider).setPersistent(false);
        for (int tick = 0; tick <= VillageManager.CELEBRATION_DURATION; tick++) manager.tick(tick + 2);
        assertNull(village.raid());
    }

    @Test
    void removingDwellerUpdatesPopulationWithoutRemovingUnloadedNeighbours() {
        Level level = mock(Level.class);
        VillageManager manager = new VillageManager(level);
        Village village = manager.createVillage(new BlockVector3(0, 64, 0));
        manager.addDweller(village.uuid(), new VillageDwellers.Actor(1L, new BlockVector3(), 0L, null));
        manager.addDweller(village.uuid(), new VillageDwellers.Actor(2L, new BlockVector3(), 0L, null));

        manager.removeDweller(1L);
        manager.removeDweller(1L);
        assertEquals(1L, village.population());
        assertTrue(manager.isDweller(2L));
        manager.removeDweller(2L);
        assertEquals(0L, village.population());
        assertFalse(village.isValid());
    }

    @Test
    void snapshotKeepsDamagedRaiderHealthAcrossPersistence() {
        Level level = mock(Level.class);
        VillageManager manager = new VillageManager(level);
        Village village = manager.createVillage(new BlockVector3(0, 64, 0));
        village.setRaid(VillageRaid.starting(0, 3, new Vector3()).withGroup(1, List.of(42L), 24f));
        EntityPillager raider = raider(village);
        when(raider.getHealth()).thenReturn(9f);

        manager.rememberRaiderHealth(raider);
        VillageRaid restored = VillageRaid.fromCompound(village.raid().toCompound());
        assertEquals(9f, restored.raiderHealth().get(42L).floatValue());
        assertEquals(0.375f, restored.bossBarProgress(restored.raiderHealth().get(42L)));
    }

    @Test
    void bossBarKeepsUnloadedRaiderHealthAndRefreshesItOnReload() {
        Level level = mock(Level.class);
        VillageManager manager = new VillageManager(level);
        Village village = manager.createVillage(new BlockVector3(0, 64, 0));
        manager.addDweller(village.uuid(), new VillageDwellers.Actor(1L, new BlockVector3(), 0L, null));
        village.pois().poi().add(new VillagePoiGroup(1L,
                List.of(new VillagePoi(PoiType.HOME, new BlockVector3(0, 64, 0)))));
        village.setRaid(VillageRaid.starting(0, 3, new Vector3()).withGroup(1, List.of(42L), 24f));
        EntityPillager raider = raider(village);
        when(raider.isAlive()).thenReturn(true);
        when(raider.getHealth()).thenReturn(12f);
        when(level.getEntityByUniqueId(42L)).thenReturn(raider);
        org.powernukkitx.Player player = mock(org.powernukkitx.Player.class);
        when(player.getX()).thenReturn(0.5);
        when(player.getY()).thenReturn(0.5);
        when(player.getZ()).thenReturn(0.5);
        when(player.isConnected()).thenReturn(true);
        when(player.getLevel()).thenReturn(level);
        when(player.getUniqueId()).thenReturn(java.util.UUID.randomUUID());
        when(level.getPlayers()).thenReturn(java.util.Map.of(7L, player));

        manager.tick(1);
        when(level.getEntityByUniqueId(42L)).thenReturn(null);
        manager.tick(2);
        assertEquals(List.of(42L), village.raid().raiders());
        when(level.getEntityByUniqueId(42L)).thenReturn(raider);
        when(raider.getHealth()).thenReturn(6f);
        manager.tick(3);

        var bars = org.mockito.ArgumentCaptor.forClass(org.powernukkitx.utils.DummyBossBar.class);
        verify(player, times(3)).createBossBar(bars.capture());
        assertEquals(50f, bars.getAllValues().get(0).getLength());
        assertEquals(50f, bars.getAllValues().get(1).getLength());
        assertEquals(25f, bars.getAllValues().get(2).getLength());
    }

    @Test
    void reloadedRaiderCelebratesDuringDefeat() {
        Level level = mock(Level.class);
        VillageManager manager = new VillageManager(level);
        Village village = manager.createVillage(new BlockVector3(0, 64, 0));
        village.setRaid(VillageRaid.starting(0, 3, new Vector3())
                .withGroup(1, List.of(42L), 24f).withStatus(VillageRaid.STATUS_LOSS));
        EntityPillager raider = raider(village);

        manager.restoreRaider(raider);
        assertTrue(raider.getMemoryStorage().get(CoreMemoryTypes.CELEBRATING));
        assertNull(raider.getMemoryStorage().get(CoreMemoryTypes.RAID_TARGET));
    }

    @Test
    void clearingManagerTerminatesItsRaids() {
        Level level = mock(Level.class);
        VillageManager manager = new VillageManager(level);
        Village village = manager.createVillage(new BlockVector3(0, 64, 0));
        village.setRaid(VillageRaid.starting(0, 3, new Vector3()));

        manager.clear();

        assertTrue(manager.getVillages().isEmpty());
        assertNull(village.raid());
    }

    private static EntityPillager raider(Village village) {
        EntityPillager raider = mock(EntityPillager.class);
        when(raider.uniqueIdLong()).thenReturn(42L);
        when(raider.getNbt()).thenReturn(new CompoundTag().putString("RaidVillageUUID", village.uuid().toString()));
        when(raider.getMemoryStorage()).thenReturn(new MemoryStorage(raider));
        return raider;
    }
}
