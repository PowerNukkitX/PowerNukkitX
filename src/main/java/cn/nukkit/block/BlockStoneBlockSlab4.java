package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.StoneSlabType4;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockStoneBlockSlab4 extends BlockSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties(STONE_BLOCK_SLAB4, CommonBlockProperties.MINECRAFT_VERTICAL_HALF, CommonBlockProperties.STONE_SLAB_TYPE_4);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockStoneBlockSlab4() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockStoneBlockSlab4(BlockState blockstate) {
        super(blockstate, DOUBLE_STONE_BLOCK_SLAB4);
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
    public String getSlabName() {
        return getSlabType().name();
    }

    @Override
    public boolean isSameType(BlockSlab slab) {
        return slab.getId().equals(getId()) && getSlabType().equals(slab.getPropertyValue(CommonBlockProperties.STONE_SLAB_TYPE_4));
    }

    public StoneSlabType4 getSlabType() {
        return getPropertyValue(CommonBlockProperties.STONE_SLAB_TYPE_4);
    }

    public void setSlabType(StoneSlabType4 type) {
        setPropertyValue(CommonBlockProperties.STONE_SLAB_TYPE_4, type);
    }

}