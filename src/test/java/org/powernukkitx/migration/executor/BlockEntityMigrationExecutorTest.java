package org.powernukkitx.migration.executor;

import org.cloudburstmc.nbt.NBTOutputStream;
import org.cloudburstmc.nbt.NbtUtils;
import org.junit.jupiter.api.Test;
import org.powernukkitx.migration.leveldb.LevelDBMigrationChunkSerializer;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.nbt.tag.Tag;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests recovery of over-depth Chest NBT during LevelDB migrations.
 *
 * @author Curse
 */
class BlockEntityMigrationExecutorTest {

    @Test
    void recoversOverDepthChestWithoutRemovingOtherBlockEntities() throws IOException {
        CompoundTag chest = createChest(-10579, 35, -1741);
        CompoundTag bundle = createBundle();
        chest.putList("Items", new ListTag<CompoundTag>(Tag.TAG_Compound).add(bundle));

        for (int i = 0; i < 5; i++) {
            CompoundTag nested = createBundle();
            bundle.putCompound("tag", new CompoundTag()
                    .putList("storage_item_component_content",
                            new ListTag<CompoundTag>(Tag.TAG_Compound).add(nested)));
            bundle = nested;
        }

        CompoundTag neighbor = new CompoundTag()
                .putString("id", "Furnace")
                .putInt("x", -10578)
                .putInt("y", 35)
                .putInt("z", -1741)
                .putInt("BurnTime", 120);

        byte[] original = serialize(chest, neighbor);
        assertThrows(IllegalArgumentException.class,
                () -> LevelDBMigrationChunkSerializer.readLittleEndianCompounds(original));

        var recovery = BlockEntityMigrationExecutor.readBlockEntitiesWithChestRecovery(original, 0, -662, -109);
        assertTrue(recovery.changed());
        assertEquals(2, recovery.blockEntities().size());

        CompoundTag restored = recovery.blockEntities().get(0);
        assertEquals("Chest", restored.getString("id"));
        assertEquals(-10579, restored.getInt("x"));
        assertEquals(35, restored.getInt("y"));
        assertEquals(-1741, restored.getInt("z"));
        assertEquals(0, restored.getList("Items").size());
        assertEquals(0, restored.getByte("Findable"));

        CompoundTag preserved = recovery.blockEntities().get(1);
        assertEquals("Furnace", preserved.getString("id"));
        assertEquals(120, preserved.getInt("BurnTime"));

        byte[] migrated = LevelDBMigrationChunkSerializer.writeLittleEndianCompounds(recovery.blockEntities());
        assertEquals(2, LevelDBMigrationChunkSerializer.readLittleEndianCompounds(migrated).size());
    }

    @Test
    void preservesValidChestNbt() throws IOException {
        CompoundTag chest = createChest(-10579, 35, -1741);
        chest.putList("Items", new ListTag<>(Tag.TAG_Compound));

        var recovery = BlockEntityMigrationExecutor.readBlockEntitiesWithChestRecovery(
                serialize(chest), 0, -662, -109);

        assertFalse(recovery.changed());
        assertEquals(1, recovery.blockEntities().size());
        assertEquals("Chest", recovery.blockEntities().get(0).getString("id"));
    }

    @Test
    void rejectsOverDepthNonChestWithoutDiscardingIt() throws IOException {
        CompoundTag furnace = new CompoundTag()
                .putString("id", "Furnace")
                .putInt("x", -10579)
                .putInt("y", 35)
                .putInt("z", -1741);

        CompoundTag current = furnace;
        for (int i = 0; i < 18; i++) {
            CompoundTag nested = new CompoundTag();
            current.putCompound("Nested", nested);
            current = nested;
        }
        current.putInt("Value", 1);

        byte[] original = serialize(furnace);
        IOException exception = assertThrows(IOException.class,
                () -> BlockEntityMigrationExecutor.readBlockEntitiesWithChestRecovery(original, 0, -662, -109));

        assertTrue(exception.getMessage().contains("Furnace"));
        assertThrows(IllegalArgumentException.class,
                () -> LevelDBMigrationChunkSerializer.readLittleEndianCompounds(original));
    }

    @Test
    void rejectsOverDepthChestWithInvalidCoordinates() throws IOException {
        CompoundTag chest = createChest(-10560, 35, -1741);
        CompoundTag current = chest;
        for (int i = 0; i < 18; i++) {
            CompoundTag nested = new CompoundTag();
            current.putCompound("Nested", nested);
            current = nested;
        }
        current.putInt("Value", 1);

        byte[] original = serialize(chest);
        assertThrows(IOException.class,
                () -> BlockEntityMigrationExecutor.readBlockEntitiesWithChestRecovery(original, 0, -662, -109));
    }

    private static CompoundTag createChest(int x, int y, int z) {
        return new CompoundTag()
                .putString("id", "Chest")
                .putInt("x", x)
                .putInt("y", y)
                .putInt("z", z);
    }

    private static CompoundTag createBundle() {
        return new CompoundTag()
                .putString("Name", "minecraft:bundle")
                .putByte("Count", 1)
                .putShort("Damage", 0);
    }

    private static byte[] serialize(CompoundTag... tags) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (NBTOutputStream writer = NbtUtils.createWriterLE(output)) {
            for (CompoundTag tag : tags) {
                writer.writeTag(tag.toNetwork(), 64);
            }
        }
        return output.toByteArray();
    }
}
