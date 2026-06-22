package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.DRIPSTONE_THICKNESS;
import static cn.nukkit.block.property.CommonBlockProperties.HANGING;

public class BlockSulfurSpike extends BlockPointedDripstone {
    public static final BlockProperties PROPERTIES = new BlockProperties(SULFUR_SPIKE, DRIPSTONE_THICKNESS, HANGING);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSulfurSpike() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSulfurSpike(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Sulfur Spike";
    }

    @Override
    public double getHardness() {
        return 1.5;
    }

    @Override
    public double getResistance() {
        return 3;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
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