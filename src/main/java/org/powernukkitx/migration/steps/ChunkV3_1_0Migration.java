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
 * Converts legacy PNX chunk metadata and scheduled tick storage to the canonical 3.1.0 representation.
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
                pendingTicks
        );
    }
}
