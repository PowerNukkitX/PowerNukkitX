package cn.nukkit.block;

import cn.nukkit.item.ItemTool;

/**
 * @author GoodLucky777
 */


public class BlockStairsDeepslatePolished extends BlockStairs {


    public BlockStairsDeepslatePolished() {
        this(0);
    }


    public BlockStairsDeepslatePolished(BlockState blockstate) {
        super(blockstate);
    }
    
    @Override
    public int getId() {
        return POLISHED_DEEPSLATE_STAIRS;
    }
    
    @Override
    public String getName() {
        return "Polished Deepslate Stairs";
    }
    
    @Override
    public double getHardness() {
        return 3.5;
    }
    
    @Override
    public double getResistance() {
        return 6;
    }
    
    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
    
    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }
    
    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

}
