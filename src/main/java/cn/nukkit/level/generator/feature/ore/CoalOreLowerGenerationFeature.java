package cn.nukkit.level.generator.feature.ore;

public class CoalOreLowerGenerationFeature extends CoalOreUpperGenerationFeature {

    public static final String NAME = "minecraft:overworld_underground_coal_ore_lower_feature";

    @Override
    public int getClusterCount() {
        return 20;
    }

    @Override
    public int getMinHeight() {
        return 0;
    }

    @Override
    public int getMaxHeight() {
        return 192;
    }

    @Override
    public ConcentrationType getConcentration() {
        return ConcentrationType.TRIANGLE;
    }

    @Override
    public float getSkipAir() {
        return 0.5f;
    }

    @Override
    public String name() {
        return NAME;
    }
}
