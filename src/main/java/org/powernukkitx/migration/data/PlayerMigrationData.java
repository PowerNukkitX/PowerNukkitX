package org.powernukkitx.migration.data;

import org.powernukkitx.nbt.tag.CompoundTag;

/**
 * Carries canonical player NBT and PNX-specific supplemental data through versioned player migration steps.
 *
 * @author Curse
 */
public record PlayerMigrationData(CompoundTag player, CompoundTag pnxExtra) {
}
