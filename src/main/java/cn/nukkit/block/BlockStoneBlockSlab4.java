package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.StoneSlabType4;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockStoneBlockSlab4 extends BlockSlab {
    public static final BlockProperties $1 = new BlockProperties(STONE_BLOCK_SLAB4, CommonBlockProperties.MINECRAFT_VERTICAL_HALF, CommonBlockProperties.STONE_SLAB_TYPE_4);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockStoneBlockSlab4(BlockState blockstate) {
        super(blockstate, getDoubleBlockState(blockstate));
    }

    static BlockState getDoubleBlockState(BlockState blockState) {
        if (blockState == null) return BlockDoubleStoneBlockSlab4.PROPERTIES.getDefaultState();
        StoneSlabType4 $2 = blockState.getPropertyValue(CommonBlockProperties.STONE_SLAB_TYPE_4);
        return BlockDoubleStoneBlockSlab4.PROPERTIES.getBlockState(CommonBlockProperties.STONE_SLAB_TYPE_4, propertyValue);
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
    
    public String getSlabName() {
        return getSlabType().name();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isSameType(BlockSlab slab) {
        return slab.getId().equals(getId()) && getSlabType().equals(slab.getPropertyValue(CommonBlockProperties.STONE_SLAB_TYPE_4));
    }

    public StoneSlabType4 getSlabType() {
        return getPropertyValue(CommonBlockProperties.STONE_SLAB_TYPE_4);
    }
    /**
     * @deprecated 
     */
    

    public void setSlabType(StoneSlabType4 type) {
        setPropertyValue(CommonBlockProperties.STONE_SLAB_TYPE_4, type);
    }

    public Item toItem() {
        Block $3 = PROPERTIES.getBlockState(CommonBlockProperties.STONE_SLAB_TYPE_4.createValue(getSlabType())).toBlock();
        ItemBlock $4 = new ItemBlock(block);
        itemBlock.setBlockUnsafe(block);
        return itemBlock;
    }
}