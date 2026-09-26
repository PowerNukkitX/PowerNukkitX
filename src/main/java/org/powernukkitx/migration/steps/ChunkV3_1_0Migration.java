package org.powernukkitx.migration.steps;

import org.powernukkitx.migration.MigrationContext;
import org.powernukkitx.migration.MigrationFormat;
import org.powernukkitx.migration.MigrationStep;
import org.powernukkitx.migration.MigrationVersion;
import org.powernukkitx.migration.data.ChunkMigrationData;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.ListTag;

import java.io.IOException;

/**
 * Converts legacy chunk storage to the canonical 3.1.0 representation.
 * <p>
 * This includes legacy PNX metadata and scheduled ticks as well as legacy BDS formats which,
 * such as the one-byte BiomeState header and biome IDs.
 *
 * @author Curse
 */
public final class ChunkV3_1_0Migration implements MigrationStep<ChunkMigrationData> {

    @Override
    public MigrationFormat format() {
        return MigrationFormat.CHUNK;
    }

    @Override
    public MigrationVersion targetVersion() {
        return MigrationVersion.V3_1_0;
    }

    @Override
    public ChunkMigrationData migrate(MigrationContext context, ChunkMigrationData value) throws IOException {
        CompoundTag extraData = value.extraData();
        CompoundTag pendingTicks = value.pendingTicks();
        boolean pendingTicksPresent = value.pendingTicksPresent();
        byte[] biomeState = migrateBiomeState(value.biomeState(), value.chunkX(), value.chunkZ());

        if (extraData.contains("pendingScheduledTicks") && !pendingTicksPresent) {
            ListTag<CompoundTag> legacyTicks = extraData.getList("pendingScheduledTicks", CompoundTag.class);
            int expectedCount = legacyTicks.size();
            int convertedCount = value.scheduledTicks().size();

            if (expectedCount != convertedCount) {
                throw new IOException(
                        "Legacy scheduled tick conversion count mismatch in chunk [" +
                                value.chunkX() +
                                "," +
                                value.chunkZ() +
                                "]: expected " +
                                expectedCount +
                                ", converted " +
                                convertedCount
                );
            }

            if (convertedCount != 0) {
                ListTag<CompoundTag> tickList = new ListTag<>();

                for (ChunkMigrationData.ScheduledTick info : value.scheduledTicks()) {
                    tickList.add(
                            new CompoundTag()
                                    .putCompound("blockState", info.blockState())
                                    .putLong("time", Math.max(1L, info.delay()))
                                    .putInt("x", info.x())
                                    .putInt("y", info.y())
                                    .putInt("z", info.z())
                    );
                }

                pendingTicks = new CompoundTag()
                        .putInt("currentTick", 0)
                        .putList("tickList", tickList);
                pendingTicksPresent = true;
            }
        }

        extraData.remove("pendingScheduledTicks");
        extraData.remove("LightPopulated");

        if (extraData.contains("pendingNormalTickBlocks")) {
            ListTag<CompoundTag> pendingNormalTicks = extraData.getList("pendingNormalTickBlocks", CompoundTag.class);
            if (pendingNormalTicks.size() == 0) {
                extraData.remove("pendingNormalTickBlocks");
            }
        }

        return new ChunkMigrationData(
                value.chunkX(),
                value.chunkZ(),
                extraData,
                value.scheduledTicks(),
                pendingTicksPresent,
                pendingTicks,
                biomeState
        );
    }

    private static byte[] migrateBiomeState(byte[] data, int chunkX, int chunkZ) throws IOException {
        if (data == null) return null;

        if (data.length >= Short.BYTES) {
            int count = Byte.toUnsignedInt(data[0]) | (Byte.toUnsignedInt(data[1]) << Byte.SIZE);
            if (data.length == Short.BYTES + count * 3) {
                return data;
            }
        }

        if (data.length == 0) {
            throw new IOException("Invalid BiomeState storage in chunk [" + chunkX + "," + chunkZ + "]: empty value");
        }

        int legacyCount = Byte.toUnsignedInt(data[0]);
        int expectedLength = Byte.BYTES + legacyCount * 2;

        if (data.length != expectedLength) {
            throw new IOException(
                    "Invalid BiomeState storage in chunk [" + chunkX + "," + chunkZ +
                            "]: expected legacy length " + expectedLength + ", got " + data.length
            );
        }

        byte[] migrated = new byte[Short.BYTES + legacyCount * 3];
        migrated[0] = (byte) legacyCount;
        migrated[1] = 0;

        for (int i = 0; i < legacyCount; i++) {
            int sourceIndex = Byte.BYTES + i * 2;
            int targetIndex = Short.BYTES + i * 3;

            migrated[targetIndex] = data[sourceIndex];
            migrated[targetIndex + 1] = 0;
            migrated[targetIndex + 2] = data[sourceIndex + 1];
        }

        return migrated;
    }
}
