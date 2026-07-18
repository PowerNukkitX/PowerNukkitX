package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockCrackedDeepslateBricks extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(CRACKED_DEEPSLATE_BRICKS);
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

    public BlockCrackedDeepslateBricks() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCrackedDeepslateBricks(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Cracked Deepslate Bricks";
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    
    }