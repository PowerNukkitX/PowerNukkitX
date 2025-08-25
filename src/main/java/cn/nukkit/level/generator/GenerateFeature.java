package cn.nukkit.level.generator;

public abstract class GenerateFeature {

    public abstract String name();

    public abstract void apply(ChunkGenerateContext context);

}
