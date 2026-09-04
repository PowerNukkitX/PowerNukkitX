package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;


public class BlockShroomlight extends BlockTransparent {
    public static final BlockProperties PROPERTIES = new BlockProperties(SHROOMLIGHT);
    public static final BlockDefinition DEFINITION = TRANSPARENT.toBuilder()
            .hardness(1)
            .resistance(1)
            .toolType(ItemTool.TYPE_HOE)
            .lightEmission(15)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockShroomlight() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockShroomlight(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    @Override
    public String getName() {
        return "Shroomlight";
    }

}
