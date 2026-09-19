package org.powernukkitx.migration.data;

import org.powernukkitx.nbt.tag.CompoundTag;

import java.nio.file.Path;

/**
 * Carries one structure root and its storage path through versioned structure migration steps.
 *
 * @author Curse
 */
public record StructureMigrationData(CompoundTag root, Path path) {
}
