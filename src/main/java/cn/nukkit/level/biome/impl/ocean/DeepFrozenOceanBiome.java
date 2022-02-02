package cn.nukkit.level.biome.impl.ocean;

/**
 * @author GoodLucky777
 */
public class DeepFrozenOceanBiome extends FrozenOceanBiome {

    public DeepFrozenOceanBiome() {
        // TODO: Add Iceberg
        
        this.setBaseHeight(-1.8f);
        this.setHeightVariation(0.1f);
    }
    
    @Override
    public String getName() {
        return "Deep Frozen Ocean";
    }
}
