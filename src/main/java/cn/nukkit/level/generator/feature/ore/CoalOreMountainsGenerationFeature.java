package cn.nukkit.level.generator.feature.ore;

public class CoalOreMountainsGenerationFeature extends CoalOreUpperGenerationFeature {

    public static final String NAME = "minecraft:mountains_underground_coal_ore_feature";

    @Override
    public int getClusterCount() {
        return 20;
    }

    @Override
    public int getMinHeight() {
        return 128;
    }

    @Override
    public int getMaxHeight() {
        return 156;
    }

    @Override
    public String name() {
        return NAME;
    }
}
