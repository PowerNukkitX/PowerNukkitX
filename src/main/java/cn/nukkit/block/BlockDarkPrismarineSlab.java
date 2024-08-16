package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockDarkPrismarineSlab extends BlockSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties(DARK_PRISMARINE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    public BlockDarkPrismarineSlab(BlockState blockState) {
        super(blockState, DARK_PRISMARINE_DOUBLE_SLAB);
    }

    @Override
    public String getSlabName() {
        return "Dark Prismarine Slab";
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
}
