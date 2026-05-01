package cn.nukkit.level.generator.feature.terrain;

public class CaveExtraUndergroundFeature extends CaveGenerateFeature {

    public static final String NAME = "minecraft:cave_extra_underground";

    @Override
    public String name() {
        return NAME;
    }

    @Override
    protected float getCaveProbability() {
        return 0.07F;
    }

    @Override
    protected int getCaveMaxY() {
        return 47;
    }
}
