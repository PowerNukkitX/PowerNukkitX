package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;


public class BlockNetheriteBlock extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(NETHERITE_BLOCK);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(50)
            .resistance(1200)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_DIAMOND)
            .build();
    public BlockNetheriteBlock() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockNetheriteBlock(BlockState blockState) {
        super(blockState, DEFINITION);
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
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public boolean isLavaResistant() {
        return true;
    }
}
