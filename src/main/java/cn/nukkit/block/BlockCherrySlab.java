package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockCherrySlab extends BlockSlab {
    public static final BlockProperties $1 = new BlockProperties(CHERRY_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockCherrySlab() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockCherrySlab(BlockState blockstate) {
        super(blockstate, CHERRY_DOUBLE_SLAB);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getSlabName() {
        return "Cherry";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isSameType(BlockSlab slab) {
        return slab.getId().equals(getId());
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getBurnChance() {
        return 5;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getBurnAbility() {
        return 20;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }
}