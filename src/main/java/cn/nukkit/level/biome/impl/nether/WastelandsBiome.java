package cn.nukkit.level.biome.impl.nether;

import cn.nukkit.level.generator.Nether;

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
