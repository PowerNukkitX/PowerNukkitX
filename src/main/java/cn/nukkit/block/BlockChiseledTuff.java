package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockChiseledTuff extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(CHISELED_TUFF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockChiseledTuff() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockChiseledTuff(BlockState blockstate) {
        super(blockstate);
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