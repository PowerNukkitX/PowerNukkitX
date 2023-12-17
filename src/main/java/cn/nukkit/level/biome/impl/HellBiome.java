package cn.nukkit.level.biome.impl;

import cn.nukkit.level.biome.Biome;

@Deprecated

public class HellBiome extends Biome {

    public HellBiome() {
    }

    @Override
    public String getName() {
        return "Hell";
    }

    @Override
    public boolean canRain() {
        return false;
    }


    @Override
    public boolean isDry() {
        return true;
    }
}
