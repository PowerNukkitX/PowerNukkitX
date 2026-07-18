package org.powernukkitx.level.generator;

import org.powernukkitx.level.generator.populator.Populator;

public abstract class GenerateFeature extends Populator {

    public abstract String name();

    public String identifier() {
        return name();
    }

    public abstract void apply(ChunkGenerateContext context);
}
