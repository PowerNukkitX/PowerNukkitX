package cn.nukkit.level.biome.impl.nether;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.generator.object.ore.OreType;
import cn.nukkit.level.generator.populator.impl.PopulatorOre;

public class BasaltDeltasBiome extends NetherBiome {

    public BasaltDeltasBiome() {
        this.addPopulator(new PopulatorOre(BlockID.BASALT, new OreType[]{
                new OreType(Block.get(BlockID.BLACKSTONE), 2, 128, 0, 128, BASALT)
        }));
    }

    @Override
    public String getName() {
        return "Basalt Deltas";
    }

    @Override
    public int getCoverBlock() {
        return BASALT;
    }

    @Override
    public int getMiddleBlock() {
        return BASALT;
    }
}
