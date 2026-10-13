package org.powernukkitx.level.generator;

/**
 * Declares the neighboring chunk state required by a generation stage.
 *
 * @author Curse
 */
public enum ChunkGenerationDependency {
    NONE,
    NEIGHBORHOOD_PRESENT,
    NEIGHBORHOOD_GENERATED,
    NEIGHBORHOOD_STATE_8
}
