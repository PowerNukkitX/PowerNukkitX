package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.StoneSlabType;
import cn.nukkit.item.ItemTool;

public abstract class BlockStoneBlockSlab extends BlockSlab {
    public BlockStoneBlockSlab(BlockState blockstate) {
        super(blockstate, getDoubleBlockState(blockstate));
    }

    static BlockState getDoubleBlockState(BlockState blockState) {
        if (blockState == null) return BlockDoubleStoneBlockSlab.PROPERTIES.getDefaultState();
        StoneSlabType propertyValue = blockState.getPropertyValue(CommonBlockProperties.STONE_SLAB_TYPE);
        return BlockDoubleStoneBlockSlab.PROPERTIES.getBlockState(CommonBlockProperties.STONE_SLAB_TYPE, propertyValue);
    }

    @Override
    public String getSlabName() {
        return getSlabType().name();
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

    @Override
    public boolean isSameType(BlockSlab slab) {
        return slab.getId().equals(getId()) && getSlabType().equals(slab.getPropertyValue(CommonBlockProperties.STONE_SLAB_TYPE));
    }

    public abstract StoneSlabType getSlabType();
}