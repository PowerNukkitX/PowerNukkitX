package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockCryingObsidian extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(CRYING_OBSIDIAN);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(50)
            .resistance(1200)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_DIAMOND)
            .lightEmission(10)
            .canBePushed(false)
            .canBePulled(false)
            .canHarvestWithHand(false)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCryingObsidian() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCryingObsidian(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Crying Obsidian";
    }

}