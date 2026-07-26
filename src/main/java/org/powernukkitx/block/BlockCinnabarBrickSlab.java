package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockCinnabarBrickSlab extends BlockSlab {

    public static final BlockProperties PROPERTIES = new BlockProperties(CINNABAR_BRICK_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    public BlockCinnabarBrickSlab(BlockState blockState) {
        super(blockState, CINNABAR_BRICK_DOUBLE_SLAB);
    }

    @Override
    public String getSlabName() {
        return "Cinnabar Brick";
    }

    @Override
    public boolean isSameType(BlockSlab slab) {
        return slab.getId().equals(this.getId());
    }

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
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
    public double getHardness(){
        return 1.5;
    }
}
