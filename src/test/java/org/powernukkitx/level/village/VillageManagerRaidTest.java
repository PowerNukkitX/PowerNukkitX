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
    void invalidVillageStopsItsRaidInTheSameTick() {
        Level level = mock(Level.class);
        VillageManager manager = new VillageManager(level);
        Village village = manager.createVillage(new BlockVector3(0, 64, 0));
        village.setRaid(VillageRaid.starting(0, 3, new Vector3()).withGroup(1, List.of(42L), 24f));
        EntityPillager raider = raider(village);
        when(level.getEntityByUniqueId(42L)).thenReturn(raider);

        manager.tick(1);

        assertNull(village.raid());
        verify(raider).setRaiding(false);
        verify(raider).setPersistent(false);
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
