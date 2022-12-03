package cn.nukkit.level.biome.impl.ocean;

import cn.nukkit.level.generator.populator.impl.PopulatorKelp;
import cn.nukkit.level.generator.populator.impl.PopulatorSeagrass;

/**
 * @author DaPorkchop_ (Nukkit Project)
 */
public class DeepOceanBiome extends OceanBiome {

    public DeepOceanBiome() {
        PopulatorKelp populatorKelp = new PopulatorKelp();
        populatorKelp.setBaseAmount(-135);
        populatorKelp.setRandomAmount(180);
        this.addPopulator(populatorKelp);

        PopulatorSeagrass populatorSeagrass = new PopulatorSeagrass(0.8);
        populatorSeagrass.setBaseAmount(24);
        populatorSeagrass.setBaseAmount(24);
        this.addPopulator(populatorSeagrass);

        //TODO: Add Ocean Monuments

        this.setBaseHeight(-1.8f);
        this.setHeightVariation(0.1f);
    }

    @Override
    public String getName() {
        return "Deep Ocean";
    }
}
