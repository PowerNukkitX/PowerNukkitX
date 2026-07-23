package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockEmeraldBlock extends BlockSolid {
    
    public static final BlockProperties PROPERTIES = new BlockProperties(EMERALD_BLOCK);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(5)
            .resistance(30)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_IRON)
            .canHarvestWithHand(false)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockEmeraldBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockEmeraldBlock(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Emerald Block";
    }

    }