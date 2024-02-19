package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockSoulSoil extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(SOUL_SOIL);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSoulSoil() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSoulSoil(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Soul Soil";
    }

    @Override
    public double getHardness() {
        return 1;
    }

    @Override
    public double getResistance() {
        return 1;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_SHOVEL;
    }

    @Override
    public boolean canHarvestWithHand() {
        return true;
    }

    @Override
    public boolean isSoulSpeedCompatible() {
        return true;
    }
}
