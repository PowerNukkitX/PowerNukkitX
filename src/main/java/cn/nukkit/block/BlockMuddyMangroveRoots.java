package cn.nukkit.block;

import cn.nukkit.item.ItemTool;

public class BlockMuddyMangroveRoots extends BlockSolid {

    public BlockMuddyMangroveRoots() {
    }

    @Override
    public String getName() {
        return "Muddy Mangrove Roots";
    }

    @Override
    public int getId() {
        return MUDDY_MANGROVE_ROOTS;
    }

    @Override
    public double getHardness() {
        return 0.7;
    }

    @Override
    public double getResistance() {
        return 0.7;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TYPE_SHOVEL;
    }

    @Override
    public int getToolType() {
        return ItemTool.AIR;
    }

    @Override
    public boolean isTransparent() {
        return true;
    }
}
