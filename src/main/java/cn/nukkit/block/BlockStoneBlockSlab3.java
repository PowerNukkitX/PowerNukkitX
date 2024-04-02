package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.StoneSlabType3;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockStoneBlockSlab3 extends BlockSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties(STONE_BLOCK_SLAB3, CommonBlockProperties.MINECRAFT_VERTICAL_HALF, CommonBlockProperties.STONE_SLAB_TYPE_3);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockStoneBlockSlab3(BlockState blockstate) {
        super(blockstate, getDoubleBlockState(blockstate));
    }

    static BlockState getDoubleBlockState(BlockState blockState) {
        if (blockState == null) return BlockDoubleStoneBlockSlab3.PROPERTIES.getDefaultState();
        StoneSlabType3 propertyValue = blockState.getPropertyValue(CommonBlockProperties.STONE_SLAB_TYPE_3);
        return BlockDoubleStoneBlockSlab3.PROPERTIES.getBlockState(CommonBlockProperties.STONE_SLAB_TYPE_3, propertyValue);
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
        return slab.getId().equals(getId()) && getSlabType().equals(slab.getPropertyValue(CommonBlockProperties.STONE_SLAB_TYPE_3));
    }

    public StoneSlabType3 getSlabType() {
        return getPropertyValue(CommonBlockProperties.STONE_SLAB_TYPE_3);
    }

    public void setSlabType(StoneSlabType3 type) {
        setPropertyValue(CommonBlockProperties.STONE_SLAB_TYPE_3, type);
    }

    public Item toItem() {
        Block block = PROPERTIES.getBlockState(CommonBlockProperties.STONE_SLAB_TYPE_3.createValue(getSlabType())).toBlock();
        ItemBlock itemBlock = new ItemBlock(block);
        itemBlock.setBlockUnsafe(block);
        return itemBlock;
    }
}