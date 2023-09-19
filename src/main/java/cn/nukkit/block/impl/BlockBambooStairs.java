package cn.nukkit.block.impl;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockStairs;

@PowerNukkitXOnly
@Since("1.20.0-r2")
public class BlockBambooStairs extends BlockStairs {
    public BlockBambooStairs() {
        this(0);
    }

    public BlockBambooStairs(int meta) {
        super(meta);
    }

    public int getId() {
        return BAMBOO_STAIRS;
    }

    public String getName() {
        return "Bamboo Stairs";
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 3;
    }

    @Override
    public int getBurnChance() {
        return 5;
    }

    @Override
    public int getBurnAbility() {
        return 20;
    }
}
