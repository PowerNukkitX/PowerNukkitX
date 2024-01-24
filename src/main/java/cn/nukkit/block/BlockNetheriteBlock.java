package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;


public class BlockNetheriteBlock extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(NETHERITE_BLOCK);
    public BlockNetheriteBlock() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockNetheriteBlock(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getName() {
        return "Netherite Block";
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
    public double getHardness() {
        return 50;
    }

    @Override
    public double getResistance() {
        return 1200;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_DIAMOND;
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
