package cn.nukkit.level.generator;

import cn.nukkit.level.generator.populator.Populator;

public abstract class GenerateFeature extends Populator {

    public abstract String name();

    public String identifier() {
        return name();
    }

    public abstract void apply(ChunkGenerateContext context);
}
