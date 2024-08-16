package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.StoneSlabType;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockSandstoneSlab extends BlockSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties(SANDSTONE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    public BlockSandstoneSlab(BlockState blockState) {
        super(blockState, SANDSTONE_DOUBLE_SLAB);
    }

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getSlabName() {
        return "Sandstone";
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
}