package org.powernukkitx.level.village;

import org.junit.jupiter.api.Test;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.nbt.tag.LongTag;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VillageRaidTest {
    @Test
    void bedrockStatesAndGroupNumbersRoundTripWithoutTranslation() {
        for (int state = 0; state <= 6; state++) {
            int status = state == 5 ? 1 : 0;
            CompoundTag root = bedrockRaid(state, status, 0, 123);

            VillageRaid raid = VillageRaid.fromCompound(root);
            CompoundTag saved = raid.toCompound().getCompound("Raid");

            assertEquals(state, raid.state());
            assertEquals(state == 6, raid.isFinished());
            assertEquals(state, saved.getInt("State"));
            assertEquals(status, saved.getInt("Status"));
            assertEquals(0, saved.getByte("GroupNum"));
            assertEquals(123L, saved.getLong("Ticks"));
        }
    }

    @Test
    void importedFirstGroupAdvancesToSecondGroupAfterCompletion() {
        VillageRaid active = VillageRaid.fromCompound(bedrockRaid(3, 0, 0, 0));
        assertEquals(List.of(42L), active.raiders());
        VillageRaid next = active.withRaiders(List.of()).completeGroup();

        assertEquals(1, next.groupNumber());
        assertEquals(VillageRaid.STATE_PREPARATION, next.state());
        assertEquals(1, next.toCompound().getCompound("Raid").getByte("GroupNum"));
    }

    @Test
    void finalGroupAdvancesToRewardsForEveryDifficulty() {
        for (int groups : new int[]{3, 5, 7}) {
            CompoundTag root = bedrockRaid(3, 0, groups - 1, 0);
            root.getCompound("Raid").putByte("NumGroups", groups);
            VillageRaid next = VillageRaid.fromCompound(root).withRaiders(List.of()).completeGroup();

            assertEquals(groups, next.groupNumber());
            assertEquals(VillageRaid.STATE_AWARDING_REWARDS, next.state());
        }
    }

    @Test
    void victoryAndLossEnterResultStateBeforeFinishing() {
        for (int status : new int[]{VillageRaid.STATUS_VICTORY, VillageRaid.STATUS_LOSS}) {
            VillageRaid result = VillageRaid.starting(0, 3, new Vector3()).withTicks(100).withStatus(status);
            assertEquals(5, result.state());
            assertEquals(status, result.status());
            assertEquals(0, result.ticks());
            assertFalse(result.isFinished());
            assertTrue(result.withState(VillageRaid.STATE_FINISHED).isFinished());
        }
        assertEquals(6, VillageRaid.starting(0, 3, new Vector3())
                .withStatus(VillageRaid.STATUS_STOPPED).state());
    }

    static CompoundTag bedrockRaid(int state, int status, int group, long ticks) {
        return new CompoundTag().putCompound("Raid", new CompoundTag()
                .putInt("State", state).putInt("Status", status)
                .putByte("GroupNum", group).putByte("NumGroups", 3).putByte("NumRaiders", 1)
                .putList("Raiders", new ListTag<LongTag>().add(new LongTag(42L)))
                .putByte("SpawnFails", 0).putLong("GameTick", 100).putLong("Ticks", ticks)
                .putFloat("SpawnX", 10).putFloat("SpawnY", 64).putFloat("SpawnZ", 20)
                .putFloat("TotalMaxHealth", 24));
    }

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
    void healthSnapshotsSurviveStateChangesAndResetForANewGroup() {
        VillageRaid raid = VillageRaid.starting(0, 3, new Vector3()).withGroup(1, List.of(1L), 24f);
        raid.raiderHealth().put(1L, 12f);
        VillageRaid next = raid.withTicks(2).withRaiders(List.of(1L))
                .withState(VillageRaid.STATE_GROUP_IN_PLAY).withSpawnPoint(new Vector3(), 1);
        assertEquals(12f, next.raiderHealth().get(1L).floatValue());
        assertEquals(12f, next.withStatus(VillageRaid.STATUS_LOSS).raiderHealth().get(1L).floatValue());
        assertEquals(0, next.withGroup(2, List.of(2L), 24f).raiderHealth().size());
    }

    @Test
    void zeroMaximumHealthHasNoProgress() {
        VillageRaid raid = VillageRaid.starting(0, 3, new Vector3())
                .withGroup(1, List.of(1L), 0f);

        assertEquals(0f, raid.bossBarProgress(20f));
    }
}
