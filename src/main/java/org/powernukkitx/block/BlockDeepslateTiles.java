package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockDeepslateTiles extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(DEEPSLATE_TILES);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(3.5)
            .resistance(6)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_WOODEN)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDeepslateTiles() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDeepslateTiles(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    public BlockDeepslateTiles(BlockState blockstate, BlockDefinition definition) {
        super(blockstate, definition);
    }

    @Override
    public String getName() {
        return "Deepslate Tiles";
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    
    }