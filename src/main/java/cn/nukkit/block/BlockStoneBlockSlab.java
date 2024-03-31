package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.StoneSlabType;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockStoneBlockSlab extends BlockSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties(STONE_BLOCK_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF, CommonBlockProperties.STONE_SLAB_TYPE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

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

    public StoneSlabType getSlabType() {
        return getPropertyValue(CommonBlockProperties.STONE_SLAB_TYPE);
    }

    public void setSlabType(StoneSlabType type) {
        setPropertyValue(CommonBlockProperties.STONE_SLAB_TYPE, type);
    }

    public Item toItem() {
        ItemBlock itemBlock = new ItemBlock(this);
        itemBlock.setBlockUnsafe(this);
        return itemBlock;
    }
}