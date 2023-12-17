package cn.nukkit.block;

import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

/**
 * @autor GoodLucky777
 */


public class BlockDoubleSlabBrickDeepslate extends BlockDoubleSlabBase {


    public BlockDoubleSlabBrickDeepslate() {
        this(0);
    }


    protected BlockDoubleSlabBrickDeepslate(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return DEEPSLATE_BRICK_DOUBLE_SLAB;
    }
    
    @Override
    public int getSingleSlabId() {
        return DEEPSLATE_BRICK_SLAB;
    }
    
    @Override
    public String getSlabName() {
        return "Double Deepslate Brick Slab";
    }

    @NotNull
    @Override
    public BlockProperties getProperties() {
        return BlockSlab.SIMPLE_SLAB_PROPERTIES;
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
