package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockRedNetherBrickSlab extends BlockSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties(RED_NETHER_BRICK_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    public BlockRedNetherBrickSlab(BlockState blockState) {
        super(blockState, RED_NETHER_BRICK_DOUBLE_SLAB);
    }

    @Override
    public String getSlabName() {
        return "Red Nether Brick";
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
