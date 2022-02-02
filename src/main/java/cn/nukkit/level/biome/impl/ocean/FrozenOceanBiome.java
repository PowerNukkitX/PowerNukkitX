package cn.nukkit.level.biome.impl.ocean;

import cn.nukkit.blockstate.BlockState;
import cn.nukkit.level.generator.populator.impl.PopulatorDisk;

import java.util.Arrays;

/**
 * @author GoodLucky777
 */
public class FrozenOceanBiome extends OceanBiome {

    public FrozenOceanBiome() {
        PopulatorDisk populatorDiskSand = new PopulatorDisk(1.0, BlockState.of(SAND), 2, 4, 2, Arrays.asList(BlockState.of(GRASS), BlockState.of(DIRT)));
        populatorDiskSand.setBaseAmount(3);
        this.addPopulator(populatorDiskSand);
        
        PopulatorDisk populatorDiskClay = new PopulatorDisk(1.0, BlockState.of(CLAY_BLOCK), 1, 2, 1, Arrays.asList(BlockState.of(DIRT), BlockState.of(CLAY_BLOCK)));
        populatorDiskClay.setBaseAmount(1);
        this.addPopulator(populatorDiskClay);
        
        PopulatorDisk populatorDiskGravel = new PopulatorDisk(1.0, BlockState.of(GRAVEL), 2, 3, 2, Arrays.asList(BlockState.of(GRASS), BlockState.of(DIRT)));
        populatorDiskGravel.setBaseAmount(1);
        this.addPopulator(populatorDiskGravel);
        
        // TODO: Add Iceberg, ice and snow
        
        this.setBaseHeight(-1f);
        this.setHeightVariation(0.1f);
    }
    
    @Override
    public String getName() {
        return "Frozen Ocean";
    }
    
    @Override
    public boolean isFreezing() {
        return true;
    }
    
    @Override
    public boolean canRain() {
        return false;
    }
}
