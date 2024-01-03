package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.StoneSlabType3;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockStoneBlockSlab3 extends BlockSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties(STONE_BLOCK_SLAB3, CommonBlockProperties.MINECRAFT_VERTICAL_HALF, CommonBlockProperties.STONE_SLAB_TYPE_3);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockStoneBlockSlab3() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockStoneBlockSlab3(BlockState blockstate) {
        super(blockstate, DOUBLE_STONE_BLOCK_SLAB3);
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

}