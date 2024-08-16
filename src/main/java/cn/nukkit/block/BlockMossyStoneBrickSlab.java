package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.StoneSlabType;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockMossyStoneBrickSlab extends BlockSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties(MOSSY_STONE_BRICK_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    public BlockMossyStoneBrickSlab(BlockState blockState) {
        super(blockState, MOSSY_STONE_BRICK_DOUBLE_SLAB);
    }

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getSlabName() {
        return "Mossy Stone Brick";
    }

    @Override
    public boolean isSameType(BlockSlab slab) {
        return this.getId().equals(slab.getId());
    }

    @Override
    public double getHardness() {
        return 1.5;
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