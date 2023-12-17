package cn.nukkit.block;

import cn.nukkit.item.ItemTool;

/**
 * @author GoodLucky777
 */


public class BlockStairsBrickDeepslate extends BlockStairs {


    public BlockStairsBrickDeepslate() {
        this(0);
    }


    public BlockStairsBrickDeepslate(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return DEEPSLATE_BRICK_STAIRS;
    }
    
    @Override
    public String getName() {
        return "Deepslate Brick Stairs";
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
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }
    
    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
