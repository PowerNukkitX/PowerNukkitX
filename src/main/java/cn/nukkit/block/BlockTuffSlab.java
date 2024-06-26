package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockTuffSlab extends BlockSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties(TUFF_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockTuffSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockTuffSlab(BlockState blockstate) {
        super(blockstate, TUFF_DOUBLE_SLAB);
    }

    @Override
    public String getSlabName() {
        return "Tuff";
    }

    @Override
    public boolean isSameType(BlockSlab slab) {
        return getId().equals(slab.getId());
    }

    @Override
    public double getHardness() {
        return 1.5;
    }

    @Override
    public double getResistance() {
        return 6;
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