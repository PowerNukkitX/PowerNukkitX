package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockTuffBrickDoubleSlab extends BlockDoubleSlabBase {
    public static final BlockProperties PROPERTIES = new BlockProperties(TUFF_BRICK_DOUBLE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockTuffBrickDoubleSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockTuffBrickDoubleSlab(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Tuff Brick Double Slab";
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public String getSlabName() {
        return "Tuff Brick";
    }

    @Override
    public BlockState getSingleSlab() {
        return BlockTuffBrickSlab.PROPERTIES.getDefaultState();
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }
}