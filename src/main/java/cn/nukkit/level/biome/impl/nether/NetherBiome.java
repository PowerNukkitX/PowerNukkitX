package cn.nukkit.level.biome.impl.nether;

import cn.nukkit.level.biome.Biome;

public abstract class NetherBiome extends Biome {

    @Override
    public boolean canRain() {
        return false;
    }


    @Override
    public boolean isDry() {
        return true;
    }

    public abstract int getCoverBlock();

    public abstract int getMiddleBlock();
}
