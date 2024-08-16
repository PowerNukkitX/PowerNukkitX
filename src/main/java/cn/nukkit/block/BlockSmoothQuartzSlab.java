package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockSmoothQuartzSlab extends BlockSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties(SMOOTH_QUARTZ_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    public BlockSmoothQuartzSlab(BlockState blockState) {
        super(blockState, SMOOTH_QUARTZ_DOUBLE_SLAB);
    }

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getSlabName() {
        return "Smooth Quartz";
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
        return this.getId().equals(slab.getId());
    }
}