package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockSmoothStone extends BlockSolid {

    public static final BlockProperties PROPERTIES = new BlockProperties(SMOOTH_STONE);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(2.0)
            .resistance(10)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_WOODEN)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSmoothStone() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSmoothStone(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    @Override
    public String getName() {
        return "Smooth Stone";
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

}
