package org.powernukkitx.migration.steps;

import org.junit.jupiter.api.Test;
import org.powernukkitx.block.BlockAir;
import org.powernukkitx.level.format.ChunkSection;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.migration.MigrationContext;
import org.powernukkitx.migration.data.PositionTrackingMigrationData;
import org.powernukkitx.migration.leveldb.LevelDBMigrationChunkSerializer;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.nbt.tag.Tag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

 /**
  * Covers valid and orphaned legacy PNX Lodestone tracking references.
  *
  * @author Curse
  */
class PositionTrackingV3_1_0MigrationTest {

    @Test
    void remapsValidLodestoneHandle() throws IOException {
        CompoundTag lodestone = lodestone(2);
        lodestone.putInt("trackingHandler", 2);

        boolean changed = PositionTrackingV3_1_0Migration.remapLegacyPnxBlockEntity(
                lodestone, 10, 100, Set.of(2, 5));

        assertTrue(changed);
        assertEquals(102, lodestone.getInt("trackingHandle"));
        assertEquals(102, lodestone.getInt("trackingHandler"));
    }

    @Test
    void clearsOutOfRangeLodestoneWithoutDeletingIt() throws IOException {
        CompoundTag lodestone = lodestone(8);
        lodestone.putString("CustomData", "preserve");

        boolean changed = PositionTrackingV3_1_0Migration.remapLegacyPnxBlockEntity(
                lodestone, 5, 100, Set.of(2));

        assertTrue(changed);
        assertEquals("Lodestone", lodestone.getString("id"));
        assertEquals(997, lodestone.getInt("x"));
        assertEquals(63, lodestone.getInt("y"));
        assertEquals(-3246, lodestone.getInt("z"));
        assertEquals("preserve", lodestone.getString("CustomData"));
        assertFalse(lodestone.contains("trackingHandle"));
    }

    @Test
    void clearsInRangeHandleWithoutSourceRecord() throws IOException {
        CompoundTag lodestone = lodestone(5);

        assertTrue(PositionTrackingV3_1_0Migration.remapLegacyPnxBlockEntity(
                lodestone, 10, 100, Set.of(2, 8)));
        assertFalse(lodestone.contains("trackingHandle"));
    }

    @Test
    void clearsOrphanedNestedCompassWithoutDeletingItem() throws IOException {
        CompoundTag compass = compass(10);
        compass.getCompound("tag").putString("CustomData", "preserve");

        CompoundTag nested = new CompoundTag()
                .putList("Contents", new ListTag<CompoundTag>(Tag.TAG_Compound).add(compass));

        CompoundTag chest = new CompoundTag()
                .putString("id", "Chest")
                .putCompound("Nested", nested);

        boolean changed = PositionTrackingV3_1_0Migration.remapLegacyPnxBlockEntity(
                chest, 5, 100, Set.of(2));

        assertTrue(changed);
        assertEquals(0, compass.getCompound("tag").getInt("trackingHandle"));
        assertEquals("preserve", compass.getCompound("tag").getString("CustomData"));
        assertEquals("minecraft:lodestone_compass", compass.getString("Name"));
        assertEquals(1, nested.getList("Contents").size());
    }

    @Test
    void preservesZeroAndRemapsValidCompass() throws IOException {
        CompoundTag untracked = compass(0);
        CompoundTag tracked = compass(2);

        PositionTrackingV3_1_0Migration.remapLegacyPnxItems(
                untracked, 10, 100, Set.of(2));
        PositionTrackingV3_1_0Migration.remapLegacyPnxItems(
                tracked, 10, 100, Set.of(2));

        assertEquals(0, untracked.getCompound("tag").getInt("trackingHandle"));
        assertEquals(102, tracked.getCompound("tag").getInt("trackingHandle"));
    }

    @Test
    void preservesDisabledSourceRecordDuringImport() throws IOException {
        var source = new PositionTrackingMigrationData(
                PositionTrackingMigrationData.Source.LEGACY_PNX,
                List.of(new PositionTrackingMigrationData.Entry(
                        2, 0, "world", 997, 63, -3246, (byte) 1)),
                10, 100, -1, List.of(), 0, 0
        );

        var migrated = new PositionTrackingV3_1_0Migration().migrate(new MigrationContext(null), source);

        assertEquals(102, migrated.canonicalEntries().getFirst().handle());
        assertEquals(1, migrated.canonicalEntries().getFirst().status());
        assertEquals(110, migrated.migratedLastId());
    }

    @Test
    void rejectsMalformedAndConflictingReferences() {
        CompoundTag negative = lodestone(-1);
        assertThrows(IOException.class, () -> PositionTrackingV3_1_0Migration.remapLegacyPnxBlockEntity(
                negative, 10, 100, Set.of(2)));

        CompoundTag conflicting = lodestone(2).putInt("trackingHandler", 3);
        assertThrows(IOException.class, () -> PositionTrackingV3_1_0Migration.remapLegacyPnxBlockEntity(
                conflicting, 10, 100, Set.of(2, 3)));

        CompoundTag malformed = lodestone(2).putString("trackingHandle", "invalid");
        assertThrows(IOException.class, () -> PositionTrackingV3_1_0Migration.remapLegacyPnxBlockEntity(
                malformed, 10, 100, Set.of(2)));
    }

    @Test
    void persistsNestedTrackingRemapWhenItIsTheOnlyChange() throws IOException {
        CompoundTag compass = compass(3);
        CompoundTag blockEntity = new CompoundTag()
                .putString("id", "Bed")
                .putInt("x", 0)
                .putInt("y", 64)
                .putInt("z", 0)
                .putInt("BlockEntityVersion", 0)
                .putList("Items", new ListTag<CompoundTag>(Tag.TAG_Compound).add(compass));

        MigrationContext context = new MigrationContext(null);
        context.setLegacyPositionTrackingRemap(3, 10, Set.of(3));

        IChunk chunk = mock(IChunk.class);
        when(chunk.getBlockState(0, 64, 0)).thenReturn(BlockAir.STATE);
        when(chunk.getSections()).thenReturn(new ChunkSection[0]);

        var data = new LevelDBBlockEntityV3_1_0Migration.Data(
                new ArrayList<>(List.of(blockEntity)), chunk, null, false, new HashSet<>());
        var migrated = new LevelDBBlockEntityV3_1_0Migration().migrate(context, data);

        assertTrue(migrated.changed());

        byte[] saved = LevelDBMigrationChunkSerializer.writeLittleEndianCompounds(migrated.blockEntities());
        CompoundTag reloaded = LevelDBMigrationChunkSerializer.readLittleEndianCompounds(saved).getFirst();
        CompoundTag reloadedCompass = reloaded.getList("Items", CompoundTag.class).get(0);

        assertEquals(13, reloadedCompass.getCompound("tag").getInt("trackingHandle"));
    }

    @Test
    void preservesNativeBdsActorCompassReferences() throws IOException {
        assertEquals(2, migrateActorCompass(false, 2));
        assertEquals(10, migrateActorCompass(false, 10));
    }

    @Test
    void remapsOnlyLegacyPnxActorCompassReferences() throws IOException {
        assertEquals(102, migrateActorCompass(true, 2));
        assertEquals(0, migrateActorCompass(true, 10));
    }

    private static int migrateActorCompass(boolean legacyPnxStorage, int handle) throws IOException {
        MigrationContext context = new MigrationContext(null);
        context.setLegacyPositionTrackingRemap(5, 100, Set.of(2));

        CompoundTag actor = new CompoundTag()
                .putString("identifier", "minecraft:armor_stand")
                .putCompound("TestNested", compass(handle));

        var source = new LevelDBActorV3_1_0Migration.Data(
                actor, 0, 1L, new HashMap<>(), new ArrayList<>(), new HashMap<>(), legacyPnxStorage, true);

        var migrated = new LevelDBActorV3_1_0Migration().migrate(context, source);
        assertTrue(migrated.keep());

        return migrated.actor().getCompound("TestNested").getCompound("tag").getInt("trackingHandle");
    }

    private static CompoundTag lodestone(int handle) {
        return new CompoundTag()
                .putString("id", "Lodestone")
                .putInt("x", 997)
                .putInt("y", 63)
                .putInt("z", -3246)
                .putInt("trackingHandle", handle);
    }

    private static CompoundTag compass(int handle) {
        return new CompoundTag()
                .putString("Name", "minecraft:lodestone_compass")
                .putByte("Count", 1)
                .putShort("Damage", 0)
                .putCompound("tag", new CompoundTag().putInt("trackingHandle", handle));
    }
}
