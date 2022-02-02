package cn.nukkit.level.biome.impl.ocean;

import cn.nukkit.level.generator.populator.impl.PopulatorSeagrass;

/**
 * @author GoodLucky777
 */
public class DeepWarmOceanBiome extends WarmOceanBiome {

    public DeepWarmOceanBiome() {
        PopulatorSeagrass populatorSeagrass = new PopulatorSeagrass(0.8);
        populatorSeagrass.setBaseAmount(40);
        populatorSeagrass.setBaseAmount(40);
        this.addPopulator(populatorSeagrass);
        
        this.setBaseHeight(-1.8f);
        this.setHeightVariation(0.1f);
    }
    
    @Override
    public String getName() {
        return "Deep Warm Ocean";
    }
}
