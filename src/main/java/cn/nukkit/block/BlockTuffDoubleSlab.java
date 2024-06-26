package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockTuffDoubleSlab extends BlockDoubleSlabBase {
    public static final BlockProperties PROPERTIES = new BlockProperties(TUFF_DOUBLE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockTuffDoubleSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockTuffDoubleSlab(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getSlabName() {
        return "Tuff";
    }

    @Override
    public BlockState getSingleSlab() {
        return BlockTuffSlab.PROPERTIES.getDefaultState();
    }

    @Override
    public String getName() {
        return "Tuff Double Slab";
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
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}