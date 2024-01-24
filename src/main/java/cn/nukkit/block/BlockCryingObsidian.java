package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockCryingObsidian extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(CRYING_OBSIDIAN);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCryingObsidian() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCryingObsidian(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Crying Obsidian";
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
    public int getLightLevel() {
        return 10;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_DIAMOND;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public boolean canBePulled() {
        return false;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

}