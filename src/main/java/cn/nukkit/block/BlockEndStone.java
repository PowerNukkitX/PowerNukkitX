package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

/**
 * @author xtypr
 * @since 2015/12/1
 */
public class BlockEndStone extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(END_STONE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockEndStone() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockEndStone(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getName() {
        return "End Stone";
    }

    @Override
    public double getHardness() {
        return 3;
    }

    @Override
    public double getResistance() {
        return 9;
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
