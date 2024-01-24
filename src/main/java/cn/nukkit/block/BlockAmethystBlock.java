package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockAmethystBlock extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(AMETHYST_BLOCK);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockAmethystBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockAmethystBlock(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Amethyst Block";
    }

    @Override
    public double getHardness() {
        return 1.5;
    }

    @Override
    public double getResistance() {
        return 1.5;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_IRON;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public boolean isLavaResistant() {
        return true;
    }
}