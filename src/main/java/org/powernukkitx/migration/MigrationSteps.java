package org.powernukkitx.migration;

import com.google.common.base.Preconditions;
import org.powernukkitx.migration.steps.ChunkV3_1_0Migration;
import org.powernukkitx.migration.steps.LevelDBActorV3_1_0Migration;
import org.powernukkitx.migration.steps.LevelDBBlockEntityV3_1_0Migration;
import org.powernukkitx.migration.steps.PlayerV3_1_0Migration;
import org.powernukkitx.migration.steps.PositionTrackingV3_1_0Migration;
import org.powernukkitx.migration.steps.ScoreboardV3_1_0Migration;
import org.powernukkitx.migration.steps.StructureV3_1_0Migration;
import org.powernukkitx.migration.steps.WorldStorageV3_1_0Migration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Defines the built-in versioned migration steps available to the migration service.
 *
 * @author Curse
 */
public final class MigrationSteps {
    private static final List<MigrationStep<?>> STEPS = List.of(
            new WorldStorageV3_1_0Migration(),
            new ChunkV3_1_0Migration(),
            new LevelDBBlockEntityV3_1_0Migration(),
            new LevelDBActorV3_1_0Migration(),
            new PlayerV3_1_0Migration(),
            new PositionTrackingV3_1_0Migration(),
            new ScoreboardV3_1_0Migration(),
            new StructureV3_1_0Migration()
    );
    private static final Map<MigrationFormat, List<MigrationStep<?>>> STEPS_BY_FORMAT = createStepsByFormat();

    private MigrationSteps() {
    }

    /**
     * Returns the registered migration steps for the specified format.
     */
    public static List<MigrationStep<?>> getSteps(MigrationFormat format) {
        return STEPS_BY_FORMAT.get(format);
    }

    /**
     * Returns the first registered migration version for the specified format.
     */
    public static MigrationVersion getFirstVersion(MigrationFormat format) {
        return getSteps(format).getFirst().targetVersion();
    }

    /**
     * Returns the latest registered migration version for the specified format.
     */
    public static MigrationVersion getLatestVersion(MigrationFormat format) {
        return getSteps(format).getLast().targetVersion();
    }

    /**
     * Parses and validates a persisted migration version against the latest registered version.
     */
    public static MigrationVersion parseStoredVersion(MigrationFormat format, String value) throws IOException {
        MigrationVersion version;
        try {
            version = MigrationVersion.parse(value);
        } catch (IllegalArgumentException e) {
            throw new IOException("Invalid " + format + " migration version: " + value, e);
        }

        MigrationVersion supportedVersion = getLatestVersion(format);
        if (version.compareTo(supportedVersion) > 0) {
            throw new IOException(format + " storage version " + version + " is newer than supported version " + supportedVersion);
        }

        return version;
    }

    private static Map<MigrationFormat, List<MigrationStep<?>>> createStepsByFormat() {
        Map<MigrationFormat, List<MigrationStep<?>>> stepsByFormat = new EnumMap<>(MigrationFormat.class);
        for (MigrationFormat format : MigrationFormat.values()) {
            stepsByFormat.put(format, new ArrayList<>());
        }

        for (MigrationStep<?> step : STEPS) {
            List<MigrationStep<?>> steps = stepsByFormat.get(step.format());
            Preconditions.checkState(steps.stream().noneMatch(existing -> existing.targetVersion().equals(step.targetVersion())),
                    "Duplicate migration for %s %s", step.format(), step.targetVersion());
            steps.add(step);
        }

        for (MigrationFormat format : MigrationFormat.values()) {
            List<MigrationStep<?>> steps = stepsByFormat.get(format);
            Preconditions.checkState(steps.size() != 0, "No migration steps registered for %s", format);
            steps.sort((a, b) -> a.targetVersion().compareTo(b.targetVersion()));
            stepsByFormat.put(format, List.copyOf(steps));
        }

        return stepsByFormat;
    }
}
