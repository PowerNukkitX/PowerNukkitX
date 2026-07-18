package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;


public class BlockBlackstone extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(BLACKSTONE);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(1.5)
            .resistance(6)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_WOODEN)
            .build();

    public BlockBlackstone() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBlackstone(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    public BlockBlackstone(BlockState blockState, BlockDefinition definition) {
        super(blockState, definition);
    }

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getName() {
        return "Blackstone";
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    
    }
