package cn.nukkit.level.generator.feature.ore;

public class DiamondOreBuriedGenerationFeature extends DiamondOreGenerationFeature {

    public static final String NAME = "minecraft:overworld_underground_diamond_ore_buried_feature";

    @Override
    public int getClusterCount() {
        return 8;
    }

    @Override
    public int getClusterSize() {
        return 4;
    }

    @Override
    public float getSkipAir() {
        return 1f;
    }

    @Override
    public String name() {
        return NAME;
    }
}
