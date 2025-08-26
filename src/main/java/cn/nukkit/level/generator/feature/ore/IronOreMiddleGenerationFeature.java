package cn.nukkit.level.generator.feature.ore;



public class IronOreMiddleGenerationFeature extends IronOreUpperGenerationFeature {

    public static final String NAME = "minecraft:overworld_underground_iron_ore_middle_feature";

    @Override
    public int getClusterCount() {
        return 10;
    }

    @Override
    public int getClusterSize() {
        return 10;
    }

    @Override
    public int getMinHeight() {
        return -24;
    }

    @Override
    public int getMaxHeight() {
        return 56;
    }

    @Override
    public String name() {
        return NAME;
    }
}
