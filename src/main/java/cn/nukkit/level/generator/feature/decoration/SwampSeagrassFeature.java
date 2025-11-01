package cn.nukkit.level.generator.feature.decoration;

public class SwampSeagrassFeature extends SeagrassRiverGenerateFeature {

    public static final String NAME = "minecraft:scatter_swamp_seagrass_feature";

    public float getTallSeagrassProbability() {
        return 0.6f;
    }

    @Override
    public int getBase() {
        return 48;
    }

    @Override
    public String name() {
        return super.name();
    }
}
