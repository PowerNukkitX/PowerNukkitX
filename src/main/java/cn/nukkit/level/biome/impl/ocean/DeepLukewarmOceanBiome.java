package cn.nukkit.level.biome.impl.ocean;

import cn.nukkit.level.generator.populator.impl.PopulatorKelp;
import cn.nukkit.level.generator.populator.impl.PopulatorSeagrass;

/**
 * @author GoodLucky777
 */
public class DeepLukewarmOceanBiome extends LukewarmOceanBiome {

    public DeepLukewarmOceanBiome() {
        PopulatorKelp populatorKelp = new PopulatorKelp();
        populatorKelp.setBaseAmount(-175);
        populatorKelp.setRandomAmount(210);
        this.addPopulator(populatorKelp);
        
        PopulatorSeagrass populatorSeagrass = new PopulatorSeagrass(0.8);
        populatorSeagrass.setBaseAmount(40);
        populatorSeagrass.setBaseAmount(40);
        this.addPopulator(populatorSeagrass);
        
        this.setBaseHeight(-1.8f);
        this.setHeightVariation(0.1f);
    }
    
    @Override
    public String getName() {
        return "Deep Lukewarm Ocean";
    }
}
