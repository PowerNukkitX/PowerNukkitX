package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockTuffBrickSlab extends BlockSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties(TUFF_BRICK_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockTuffBrickSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockTuffBrickSlab(BlockState blockstate) {
        super(blockstate, TUFF_BRICK_DOUBLE_SLAB);
    }

    @Override
    public String getSlabName() {
        return "Tuff Brick";
    }

    @Override
    public String getName() {
        return "Tuff Brick Slab";
    }

    @Override
    public double getResistance() {
        return 6;
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
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

}