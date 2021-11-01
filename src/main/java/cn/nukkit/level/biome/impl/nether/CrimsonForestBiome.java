package cn.nukkit.level.biome.impl.nether;

import cn.nukkit.level.generator.populator.impl.nether.CrimsonFungiTreePopulator;
import cn.nukkit.level.generator.populator.impl.nether.CrimsonGrassesPopulator;
import cn.nukkit.level.generator.populator.impl.nether.CrimsonWeepingVinesPopulator;

public class CrimsonForestBiome extends NetherBiome {

    public CrimsonForestBiome() {
        this.addPopulator(new CrimsonFungiTreePopulator());
        this.addPopulator(new CrimsonGrassesPopulator());
        this.addPopulator(new CrimsonWeepingVinesPopulator());
    }

    @Override
    public String getName() {
        return "Crimson Forest";
    }

    @Override
    public int getCoverBlock() {
        return CRIMSON_NYLIUM;
    }

    @Override
    public int getMiddleBlock() {
        return NETHERRACK;
    }
}
