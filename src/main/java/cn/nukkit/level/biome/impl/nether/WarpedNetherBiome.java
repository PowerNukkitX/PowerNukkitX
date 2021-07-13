package cn.nukkit.level.biome.impl.nether;

import cn.nukkit.level.generator.populator.impl.nether.WarpedFungiPopulator;

public class WarpedNetherBiome extends NetherBiome {

    public WarpedNetherBiome() {
        this.addPopulator(new WarpedFungiPopulator());
    }

    @Override
    public String getName() {
        return "Warped Forest";
    }

    @Override
    public int getCoverBlock() {
        return WARPED_NYLIUM;
    }

    @Override
    public int getMiddleBlock() {
        return NETHERRACK;
    }
}
