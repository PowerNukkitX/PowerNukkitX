package cn.nukkit.level.generator.feature.ore;

public class DiamondOreLargeGenerationFeature extends DiamondOreGenerationFeature {

    public static final String NAME = "minecraft:overworld_underground_diamond_ore_large_feature";

    @Override
    public int getClusterCount() {
        return 9;
    }

    @Override
    public int getClusterSize() {
        return 12;
    }

    @Override
    public float getSkipAir() {
        return 0.7f;
    }

    @Override
    public boolean isRare() {
        return true;
    }

    @Override
    public String name() {
        return NAME;
    }
}
