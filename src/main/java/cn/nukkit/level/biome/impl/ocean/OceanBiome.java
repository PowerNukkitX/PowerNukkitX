package cn.nukkit.level.biome.impl.ocean;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.level.biome.type.WateryBiome;
import cn.nukkit.level.generator.populator.impl.PopulatorDisk;
import cn.nukkit.level.generator.populator.impl.PopulatorKelp;
import cn.nukkit.level.generator.populator.impl.PopulatorSeagrass;

import java.util.Arrays;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class OceanBiome extends WateryBiome {

    public OceanBiome() {
        PopulatorDisk populatorDiskSand = new PopulatorDisk(1.0, BlockState.of(SAND), 2, 4, 2, Arrays.asList(BlockState.of(GRASS), BlockState.of(DIRT)));
        populatorDiskSand.setBaseAmount(3);
        this.addPopulator(populatorDiskSand);
        
        PopulatorDisk populatorDiskClay = new PopulatorDisk(1.0, BlockState.of(CLAY_BLOCK), 1, 2, 1, Arrays.asList(BlockState.of(DIRT), BlockState.of(CLAY_BLOCK)));
        populatorDiskClay.setBaseAmount(1);
        this.addPopulator(populatorDiskClay);
        
        PopulatorDisk populatorDiskGravel = new PopulatorDisk(1.0, BlockState.of(GRAVEL), 2, 3, 2, Arrays.asList(BlockState.of(GRASS), BlockState.of(DIRT)));
        populatorDiskGravel.setBaseAmount(1);
        this.addPopulator(populatorDiskGravel);
        
        PopulatorKelp populatorKelp = new PopulatorKelp();
        populatorKelp.setBaseAmount(-135);
        populatorKelp.setRandomAmount(180);
        this.addPopulator(populatorKelp);
        
        PopulatorSeagrass populatorSeagrass = new PopulatorSeagrass(0.3);
        populatorSeagrass.setBaseAmount(24);
        populatorSeagrass.setRandomAmount(24);
        this.addPopulator(populatorSeagrass);
        
        this.setBaseHeight(-1f);
        this.setHeightVariation(0.1f);
    }

    @Override
    public String getName() {
        return "Ocean";
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Override
    public int getGroundBlock(int y) {
        if (useNewRakNetGround()) {
            return getGroundId(0, y, 0) >> 4;
        }
        return GRAVEL;
    }
}
