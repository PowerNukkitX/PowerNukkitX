package cn.nukkit.level.biome.impl.nether;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.generator.object.ore.OreType;
import cn.nukkit.level.generator.populator.impl.PopulatorOre;
import cn.nukkit.level.generator.populator.impl.nether.PopulatorGroundSoulFire;
import cn.nukkit.level.generator.populator.impl.nether.PopulatorSoulsandFossils;

public class SoulSandValleyBiome extends NetherBiome {

    public SoulSandValleyBiome() {
        this.addPopulator(new PopulatorSoulsandFossils());
        this.addPopulator(new PopulatorOre(BlockID.SOUL_SAND, new OreType[]{
                new OreType(Block.get(SOUL_SOIL), 3, 128, 0, 128, SOUL_SAND)
        }));
        this.addPopulator(new PopulatorGroundSoulFire());
    }

    @Override
    public String getName() {
        return "Soulsand Valley";
    }

    @Override
    public int getCoverBlock() {
        return SOUL_SAND;
    }

    @Override
    public int getMiddleBlock() {
        return SOUL_SAND;
    }
}
