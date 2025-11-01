package cn.nukkit.level.generator.feature.ore;


public class RedstoneOreLowerGenerationFeature extends RedstoneOreGenerationFeature {

    public static final String NAME = "minecraft:overworld_underground_redstone_ore_lower_feature";

    @Override
    public int getClusterCount() {
        return 8;
    }

    @Override
    public int getMaxHeight() {
        return -32;
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
