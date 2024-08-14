package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockPolishedTuffDoubleSlab extends BlockDoubleSlabBase {
    public static final BlockProperties PROPERTIES = new BlockProperties(POLISHED_TUFF_DOUBLE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPolishedTuffDoubleSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPolishedTuffDoubleSlab(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Polished Tuff Double Slab";
    }

    @Override
    public String getSlabName() {
        return "Polished Tuff";
    }

    @Override
    public BlockState getSingleSlab() {
        return BlockPolishedTuffSlab.PROPERTIES.getDefaultState();
    }

    @Override
    public double getResistance() {
        return 6;
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