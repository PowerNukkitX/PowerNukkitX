package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.StoneSlabType;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockStoneBlockSlab extends BlockSlab {
    public static final BlockProperties $1 = new BlockProperties(STONE_BLOCK_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF, CommonBlockProperties.STONE_SLAB_TYPE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockStoneBlockSlab(BlockState blockstate) {
        super(blockstate, getDoubleBlockState(blockstate));
    }

    static BlockState getDoubleBlockState(BlockState blockState) {
        if (blockState == null) return BlockDoubleStoneBlockSlab.PROPERTIES.getDefaultState();
        StoneSlabType $2 = blockState.getPropertyValue(CommonBlockProperties.STONE_SLAB_TYPE);
        return BlockDoubleStoneBlockSlab.PROPERTIES.getBlockState(CommonBlockProperties.STONE_SLAB_TYPE, propertyValue);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getSlabName() {
        return getSlabType().name();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isSameType(BlockSlab slab) {
        return slab.getId().equals(getId()) && getSlabType().equals(slab.getPropertyValue(CommonBlockProperties.STONE_SLAB_TYPE));
    }

    public StoneSlabType getSlabType() {
        return getPropertyValue(CommonBlockProperties.STONE_SLAB_TYPE);
    }
    /**
     * @deprecated 
     */
    

    public void setSlabType(StoneSlabType type) {
        setPropertyValue(CommonBlockProperties.STONE_SLAB_TYPE, type);
    }

    public Item toItem() {
        Block $3 = PROPERTIES.getBlockState(CommonBlockProperties.STONE_SLAB_TYPE.createValue(getSlabType())).toBlock();
        ItemBlock $4 = new ItemBlock(block);
        itemBlock.setBlockUnsafe(block);
        return itemBlock;
    }
}