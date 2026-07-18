package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockStonecutter extends BlockSolid {

    public static final BlockProperties PROPERTIES = new BlockProperties(STONECUTTER);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(3.5)
            .resistance(17.5)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_WOODEN)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockStonecutter() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockStonecutter(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Stonecutter";
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public int getWaterloggingLevel() {
        return 1;
    }
}
