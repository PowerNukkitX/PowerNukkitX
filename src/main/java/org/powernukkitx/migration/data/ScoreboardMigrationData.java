package org.powernukkitx.migration.data;

import org.powernukkitx.nbt.tag.CompoundTag;

import java.util.Map;

/**
 * Carries canonical and legacy scoreboard state through versioned scoreboard migration steps.
 *
 * @author Curse
 */
public record ScoreboardMigrationData(
        CompoundTag scoreboard,
        Map<String, Object> legacyScoreboards,
        Map<String, String> legacyDisplay,
        long legacyLastUniqueId
) {
}
