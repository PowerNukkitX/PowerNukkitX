package cn.nukkit.level.generator.feature.ore;

public class CopperOreDripstoneCaveGenerationFeature extends CopperOreGenerationFeature {

    public static final String NAME = "minecraft:dripstone_caves_copper_ore_feature";

    @Override
    public int getClusterSize() {
        return 20;
    }

    @Override
    public String name() {
        return NAME;
    }
}
