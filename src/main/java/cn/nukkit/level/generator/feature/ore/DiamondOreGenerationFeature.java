package cn.nukkit.level.generator.feature.ore;

public class DiamondOreGenerationFeature extends DiamondOreSquareGenerationFeature {

    public static final String NAME = "minecraft:overworld_underground_diamond_ore_feature";

    @Override
    public int getClusterCount() {
        return 4;
    }

    @Override
    public int getClusterSize() {
        return 7;
    }

    @Override
    public int getMaxHeight() {
        return 16;
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
