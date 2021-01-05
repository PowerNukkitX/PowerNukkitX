package cn.nukkit.level.biome.impl.river;

import cn.nukkit.blockstate.BlockState;
import cn.nukkit.level.generator.populator.impl.PopulatorDisk;
import cn.nukkit.level.generator.populator.impl.WaterIcePopulator;

import java.util.Arrays;

/**
 * @author DaPorkchop_ (Nukkit Project)
 */
public class FrozenRiverBiome extends RiverBiome {

    public FrozenRiverBiome() {
        PopulatorDisk populatorDiskSand = new PopulatorDisk(1.0, BlockState.of(SAND), 2, 4, 2, Arrays.asList(BlockState.of(GRASS), BlockState.of(DIRT)));
        populatorDiskSand.setBaseAmount(3);
        addPopulator(populatorDiskSand);
        
        PopulatorDisk populatorDiskClay = new PopulatorDisk(1.0, BlockState.of(CLAY_BLOCK), 1, 2, 1, Arrays.asList(BlockState.of(DIRT), BlockState.of(CLAY_BLOCK)));
        populatorDiskClay.setBaseAmount(1);
        addPopulator(populatorDiskClay);
        
        PopulatorDisk populatorDiskGravel = new PopulatorDisk(1.0, BlockState.of(GRAVEL), 2, 3, 2, Arrays.asList(BlockState.of(GRASS), BlockState.of(DIRT)));
        populatorDiskGravel.setBaseAmount(1);
        addPopulator(populatorDiskGravel);
        
        WaterIcePopulator ice = new WaterIcePopulator();
        this.addPopulator(ice);
        
        this.setBaseHeight(-0.5f);
        this.setHeightVariation(0f);
    }

    @Override
    public String getName() {
        return "Frozen River";
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
