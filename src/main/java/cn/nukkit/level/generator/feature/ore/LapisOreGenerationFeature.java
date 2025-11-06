package cn.nukkit.level.generator.feature.ore;

public class LapisOreGenerationFeature extends LapisOreBuriedGenerationFeature {

    public static final String NAME = "minecraft:overworld_underground_lapis_ore_feature";

    @Override
    public int getClusterCount() {
        return 2;
    }


    @Override
    public int getMinHeight() {
        return -32;
    }

    @Override
    public int getMaxHeight() {
        return 32;
    }

    @Override
    public ConcentrationType getConcentration() {
        return ConcentrationType.TRIANGLE;
    }

    @Override
    public String name() {
        return NAME;
    }
}
