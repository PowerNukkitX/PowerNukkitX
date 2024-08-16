package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockPolishedGraniteSlab extends BlockSlab {

    public static final BlockProperties PROPERTIES = new BlockProperties(POLISHED_GRANITE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    public BlockPolishedGraniteSlab(BlockState blockState) {
        super(blockState, POLISHED_GRANITE_DOUBLE_SLAB);
    }

    @Override
    public String getSlabName() {
        return "Polished Granite";
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

    @Override
    public double getHardness(){
        return 1.5;
    }
}
