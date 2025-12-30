package cn.nukkit.level.generator.feature.ore;



public class IronOreSmallGenerationFeature extends IronOreMiddleGenerationFeature {

    public static final String NAME = "minecraft:overworld_underground_iron_ore_small_feature";

    @Override
    public int getClusterSize() {
        return 4;
    }

    @Override
    public int getMinHeight() {
        return -64;
    }

    @Override
    public int getMaxHeight() {
        return 72;
    }

    @Override
    public ConcentrationType getConcentration() {
        return ConcentrationType.UNIFORM;
    }

    @Override
    public String name() {
        return NAME;
    }
}
