package cn.nukkit.level.generator.populator.nether;

public class AncientDebrisLargePopulator extends AncientDebrisSmallPopulator {

    public static final String NAME = "nether_ancientdebris_large";

    @Override
    public int getClusterCount() {
        return 2;
    }

    @Override
    public int getClusterSize() {
        return 3;
    }

    @Override
    public int getMaxHeight() {
        return 23;
    }

    @Override
    public String name() {
        return NAME;
    }
}
