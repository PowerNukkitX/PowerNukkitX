package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockMangroveSlab extends BlockSlab {
    public static final BlockProperties $1 = new BlockProperties(MANGROVE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockMangroveSlab() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockMangroveSlab(BlockState blockstate) {
        super(blockstate, MANGROVE_DOUBLE_SLAB);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return (isOnTop() ? "Upper " : "") + getSlabName() + " Wood Slab";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getSlabName() {
        return "Mangrove";
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

}