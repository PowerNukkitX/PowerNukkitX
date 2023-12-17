package cn.nukkit.block;

import cn.nukkit.item.ItemTool;


public class BlockWallBlackstone extends BlockWallBase {


    public BlockWallBlackstone() {
        this(0);
    }


    public BlockWallBlackstone(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Blackstone Wall";
    }

    @Override
    public int getId() {
        return BLACKSTONE_WALL;
    }

    @Override
    public double getResistance() {
        return 6;
    }

    @Override
    public double getHardness() {
        return 1.5;
    }


    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }
}
