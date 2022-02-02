package cn.nukkit.level.biome.impl.ocean;

import cn.nukkit.level.generator.populator.impl.PopulatorKelp;
import cn.nukkit.level.generator.populator.impl.PopulatorSeagrass;

/**
 * @author GoodLucky777
 */
public class DeepColdOceanBiome extends ColdOceanBiome {

    public DeepColdOceanBiome() {
        PopulatorKelp populatorKelp = new PopulatorKelp();
        populatorKelp.setBaseAmount(-135);
        populatorKelp.setRandomAmount(180);
        this.addPopulator(populatorKelp);
        
        PopulatorSeagrass populatorSeagrass = new PopulatorSeagrass(0.8);
        populatorSeagrass.setBaseAmount(20);
        populatorSeagrass.setBaseAmount(20);
        this.addPopulator(populatorSeagrass);
        
        this.setBaseHeight(-1.8f);
        this.setHeightVariation(0.1f);
    }
    
    @Override
    public String getName() {
        return "Deep Cold Ocean";
    }
}
