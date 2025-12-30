package cn.nukkit.level.generator.feature.ore;

public class GoldOreGenerationFeature extends GoldOreMesaGenerationFeature {

    public static final String NAME = "minecraft:overworld_underground_gold_ore_feature";

    @Override
    public int getClusterCount() {
        return 4;
    }

    @Override
    public int getMinHeight() {
        return -64;
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
    public float getSkipAir() {
        return 0.5f;
    }

    @Override
    public String name() {
        return NAME;
    }
}
