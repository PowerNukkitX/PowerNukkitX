package cn.nukkit.level.generator.feature.ore;

public class GoldOreLowerGenerationFeature extends GoldOreMesaGenerationFeature {

    public static final String NAME = "minecraft:overworld_underground_gold_ore_lower_feature";

    @Override
    public int getClusterCount() {
        return 2;
    }

    @Override
    public boolean isRare() {
        return true;
    }

    @Override
    public int getMinHeight() {
        return -64;
    }

    @Override
    public int getMaxHeight() {
        return -48;
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
