package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.ItemTool;
import cn.nukkit.item.customitem.ItemCustomTool;
import org.jetbrains.annotations.NotNull;

public class BlockSmoothSandstoneSlab extends BlockSlab {

    public static final BlockProperties PROPERTIES = new BlockProperties(SMOOTH_SANDSTONE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    public BlockSmoothSandstoneSlab(BlockState blockState) {
        super(blockState, SMOOTH_SANDSTONE_DOUBLE_SLAB);
    }

    @Override
    public String getSlabName() {
        return "Smooth Sandstone";
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
