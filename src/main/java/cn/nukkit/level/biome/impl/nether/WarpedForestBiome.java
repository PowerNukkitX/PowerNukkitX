package cn.nukkit.level.biome.impl.nether;

import cn.nukkit.level.generator.populator.impl.nether.WarpedFungiTreePopulator;
import cn.nukkit.level.generator.populator.impl.nether.WarpedGrassesPopulator;
import cn.nukkit.level.generator.populator.impl.nether.WarpedTwistingVinesPopulator;

public class WarpedForestBiome extends NetherBiome {

    public WarpedForestBiome() {
        this.addPopulator(new WarpedFungiTreePopulator());
        this.addPopulator(new WarpedGrassesPopulator());
        this.addPopulator(new WarpedTwistingVinesPopulator());
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
