package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.StoneSlabType2;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockStoneBlockSlab2 extends BlockSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties(STONE_BLOCK_SLAB2, CommonBlockProperties.MINECRAFT_VERTICAL_HALF, CommonBlockProperties.STONE_SLAB_TYPE_2);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockStoneBlockSlab2(BlockState blockstate) {
        super(blockstate, getDoubleBlockState(blockstate));
    }

    static BlockState getDoubleBlockState(BlockState blockState) {
        if (blockState == null) return BlockDoubleStoneBlockSlab2.PROPERTIES.getDefaultState();
        StoneSlabType2 propertyValue = blockState.getPropertyValue(CommonBlockProperties.STONE_SLAB_TYPE_2);
        return BlockDoubleStoneBlockSlab2.PROPERTIES.getBlockState(CommonBlockProperties.STONE_SLAB_TYPE_2, propertyValue);
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
        return slab.getId().equals(getId()) && getSlabType().equals(slab.getPropertyValue(CommonBlockProperties.STONE_SLAB_TYPE_2));
    }

    public StoneSlabType2 getSlabType() {
        return getPropertyValue(CommonBlockProperties.STONE_SLAB_TYPE_2);
    }

    public void setSlabType(StoneSlabType2 type) {
        setPropertyValue(CommonBlockProperties.STONE_SLAB_TYPE_2, type);
    }

    public Item toItem() {
        Block block = PROPERTIES.getBlockState(CommonBlockProperties.STONE_SLAB_TYPE_2.createValue(getSlabType())).toBlock();
        ItemBlock itemBlock = new ItemBlock(block);
        itemBlock.setBlockUnsafe(block);
        return itemBlock;
    }
}