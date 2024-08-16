package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockSmoothRedSandstoneSlab extends BlockSlab {

    public static final BlockProperties PROPERTIES = new BlockProperties(SMOOTH_RED_SANDSTONE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    public BlockSmoothRedSandstoneSlab(BlockState blockState) {
        super(blockState, SMOOTH_RED_SANDSTONE_DOUBLE_SLAB);
    }

    @Override
    public String getSlabName() {
        return "Smooth Red Sandstone";
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
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
    public boolean isSameType(BlockSlab slab) {
        return slab.getId().equals(this.getId());
    }

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }
}
