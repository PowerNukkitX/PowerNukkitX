package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

/**
 * @author xtypr
 * @since 2015/12/7
 */
public class BlockNetherBrick extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(NETHER_BRICK);

    public BlockNetherBrick() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockNetherBrick(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getName() {
        return "Nether Brick";
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
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
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 6;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
