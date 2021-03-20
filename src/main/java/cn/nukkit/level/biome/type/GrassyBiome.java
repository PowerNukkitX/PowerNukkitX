package cn.nukkit.level.biome.type;

import cn.nukkit.api.RemovedFromNewRakNet;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockDoublePlant;
import cn.nukkit.level.generator.populator.impl.PopulatorDoublePlant;
import cn.nukkit.level.generator.populator.impl.PopulatorGrass;
import cn.nukkit.level.generator.populator.impl.PopulatorMelon;
import cn.nukkit.level.generator.populator.impl.PopulatorPumpkin;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class GrassyBiome extends CoveredBiome {

    public GrassyBiome() {
        PopulatorGrass grass = new PopulatorGrass();
        grass.setBaseAmount(30);
        this.addPopulator(grass);

        PopulatorDoublePlant tallGrass = new PopulatorDoublePlant(BlockDoublePlant.TALL_GRASS);
        tallGrass.setBaseAmount(5);
        this.addPopulator(tallGrass);
        
        PopulatorMelon melon = new PopulatorMelon();
        melon.setBaseAmount(-1500);
        melon.setRandomAmount(1510);
        this.addPopulator(melon);
        
        PopulatorPumpkin pumpkin = new PopulatorPumpkin();
        pumpkin.setBaseAmount(-1500);
        pumpkin.setRandomAmount(1510);
        this.addPopulator(pumpkin);
    }

    @RemovedFromNewRakNet
    @Since("1.4.0.0-PN")
    @Override
    public int getSurfaceBlock(int y) {
        if (useNewRakNetSurface()) {
            return getSurfaceId(0, y, 0);
        }
        return GRASS;
    }

    @RemovedFromNewRakNet
    @Since("1.4.0.0-PN")
    @Override
    public int getGroundBlock(int y) {
        if (useNewRakNetGround()) {
            return getGroundId(0, y, 0) >> 4;
        }
        return DIRT;
    }
}
