package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;


public class BlockBambooMosaic extends BlockSolid {
    public BlockBambooMosaic() {
    }

    public int getId() {
        return BAMBOO_MOSAIC;
    }

    public String getName() {
        return "Bamboo Mosaic";
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 15;
    }

    @Override
    public int getBurnAbility() {
        return 20;
    }

    @Override
    public int getBurnChance() {
        return 5;
    }
}