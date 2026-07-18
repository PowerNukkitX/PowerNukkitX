package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockSoulSoil extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(SOUL_SOIL);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(1)
            .resistance(1)
            .toolType(ItemTool.TYPE_SHOVEL)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSoulSoil() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSoulSoil(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Soul Soil";
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
