package org.powernukkitx.level.generator.populator;

import org.powernukkitx.level.generator.ChunkGenerateContext;

public interface PopulatorStructure {

    default boolean shouldGenerateStructures(ChunkGenerateContext context) {
        return (boolean) context.getGenerator().getSettings().getOrDefault("structures", true);
    }

}
