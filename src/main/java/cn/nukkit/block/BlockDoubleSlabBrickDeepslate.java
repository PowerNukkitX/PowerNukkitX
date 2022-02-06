package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nonnull;

/**
 * @autor GoodLucky777
 */
@PowerNukkitOnly
@Since("FUTURE")
public class BlockDoubleSlabBrickDeepslate extends BlockDoubleSlabBase {

    @PowerNukkitOnly
    @Since("FUTURE")
    public BlockDoubleSlabBrickDeepslate() {
        this(0);
    }
    
    @PowerNukkitOnly
    @Since("FUTURE")
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
    
    @Nonnull
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
    
    @Override
    public BlockColor getColor() {
        return BlockColor.DEEPSLATE_GRAY;
    }
}
