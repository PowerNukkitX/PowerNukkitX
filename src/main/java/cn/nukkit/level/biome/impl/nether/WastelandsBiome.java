package cn.nukkit.level.biome.impl.nether;

public class WastelandsBiome extends NetherBiome {
    @Override
    public String getName() {
        return "Wastelands";
    }

    @Override
    public int getCoverBlock() {
        return NETHERRACK;
    }

    @Override
    public int getMiddleBlock() {
        return NETHERRACK;
    }
}
