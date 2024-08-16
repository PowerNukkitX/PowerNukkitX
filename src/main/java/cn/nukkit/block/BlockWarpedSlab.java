package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockWarpedSlab extends BlockSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties(WARPED_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWarpedSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWarpedSlab(BlockState blockstate) {
        super(blockstate, WARPED_DOUBLE_SLAB);
    }

    @Override
    public String getSlabName() {
        return "Warped";
    }

    @Override
    public boolean canHarvestWithHand() {
        return true;
    }

    @Override
    public int getToolTier() {
        return 0;
    }

    @Override
    public boolean isSameType(BlockSlab slab) {
        return getId().equals(slab.getId());
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public double getResistance() {
        return 3;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this);
    }

}