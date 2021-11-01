package cn.nukkit.level.biome.impl.nether;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.generator.object.ore.OreType;
import cn.nukkit.level.generator.populator.impl.PopulatorOre;
import cn.nukkit.level.generator.populator.impl.nether.BasaltDeltaLavaPopulator;
import cn.nukkit.level.generator.populator.impl.nether.BasaltDeltaMagmaPopulator;
import cn.nukkit.level.generator.populator.impl.nether.BasaltDeltaPillarPopulator;

public class BasaltDeltasBiome extends NetherBiome {

    public BasaltDeltasBiome() {
        this.addPopulator(new PopulatorOre(BlockID.BASALT, new OreType[]{
                new OreType(Block.get(BlockID.BLACKSTONE), 4, 128, 0, 128, BASALT)
        }));
        this.addPopulator(new BasaltDeltaLavaPopulator());
        this.addPopulator(new BasaltDeltaMagmaPopulator());
        this.addPopulator(new BasaltDeltaPillarPopulator());
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
