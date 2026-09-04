package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;


public class BlockNetherWartBlock extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(NETHER_WART_BLOCK);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(1)
            .resistance(5)
            .toolType(ItemTool.TYPE_HOE)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockNetherWartBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockNetherWartBlock(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Nether Wart Block";
    }

}
