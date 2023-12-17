package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;


public class BlockStairsCherry extends BlockStairs {

    public BlockStairsCherry() {
        this(0);
    }

    public BlockStairsCherry(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return CHERRY_STAIRS;
    }

    @Override
    public String getName() {
        return "Cherry Wood Stairs";
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