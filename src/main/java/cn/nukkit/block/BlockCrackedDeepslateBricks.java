package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockCrackedDeepslateBricks extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(CRACKED_DEEPSLATE_BRICKS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCrackedDeepslateBricks() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCrackedDeepslateBricks(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Cracked Deepslate Bricks";
    }

    @Override
    public double getHardness() {
        return 3.5;
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
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }
}