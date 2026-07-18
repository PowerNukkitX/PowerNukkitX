package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockCobbledDeepslate extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(COBBLED_DEEPSLATE);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(3.5)
            .resistance(6.0)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_WOODEN)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCobbledDeepslate() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCobbledDeepslate(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Cobbled Deepslate";
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}