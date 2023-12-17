package cn.nukkit.block;

import cn.nukkit.item.ItemTool;


public class BlockAncientDebris extends BlockSolid {


    public BlockAncientDebris() {
        // Does nothing
    }

    @Override
    public int getId() {
        return ANCIENT_DEBRIS;
    }

    @Override
    public String getName() {
        return "Ancient Derbris";
    }


    @Override
    public int getToolTier() {
        return ItemTool.TIER_DIAMOND;
    }
    
    @Override
    public double getResistance() {
        return 1200;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public double getHardness() {
        return 30;
    }


    @Override
    public boolean isLavaResistant() {
        return true;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
