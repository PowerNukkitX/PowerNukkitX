package org.powernukkitx.level.village;

import org.junit.jupiter.api.Test;
import org.powernukkitx.math.Vector3;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VillageRaidTest {
    @Test
    void progressTracksDamageBeforeAnyRaiderDies() {
        VillageRaid raid = VillageRaid.starting(0, 3, new Vector3())
                .withGroup(1, List.of(1L, 2L), 100f);

        assertEquals(0.75f, raid.bossBarProgress(75f));
        assertEquals(0.25f, raid.withRaiders(List.of(2L)).bossBarProgress(25f));
        assertEquals(0f, raid.bossBarProgress(0f));
        assertEquals(1f, raid.bossBarProgress(150f));
    }

    @Test
    void preparationProgressDoesNotDependOnRaiderHealth() {
        VillageRaid raid = VillageRaid.starting(0, 3, new Vector3())
                .withTicks(VillageManager.RAID_PREPARATION_TIME / 2);

        assertEquals(0.5f, raid.bossBarProgress(0f));
        assertEquals(1f, raid.withState(VillageRaid.STATE_PICKING_SPAWN_POINT).bossBarProgress(0f));
    }

    @Test
    void retriesSurviveSpawnSelectionAndPersistenceUntilAGroupSpawns() {
        VillageRaid raid = VillageRaid.starting(0, 3, new Vector3())
                .withSpawnPoint(new Vector3(1, 64, 2), 4);
        VillageRaid spawning = raid.withSpawnPoint(new Vector3(3, 64, 4), raid.spawnFails())
                .withState(VillageRaid.STATE_SPAWNING_GROUP);
        VillageRaid restored = VillageRaid.fromCompound(spawning.toCompound());

        assertEquals(4, restored.spawnFails());
        assertEquals(VillageRaid.STATE_SPAWNING_GROUP, restored.state());
        assertEquals(0, restored.withGroup(1, List.of(1L), 24f).spawnFails());
    }

    @Test
    void zeroMaximumHealthHasNoProgress() {
        VillageRaid raid = VillageRaid.starting(0, 3, new Vector3())
                .withGroup(1, List.of(1L), 0f);

        assertEquals(0f, raid.bossBarProgress(20f));
    }
}
