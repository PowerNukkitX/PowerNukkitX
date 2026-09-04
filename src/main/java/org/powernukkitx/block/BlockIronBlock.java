package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockIronBlock extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(IRON_BLOCK);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(5)
            .resistance(10)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_STONE)
            .canHarvestWithHand(false)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockIronBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockIronBlock(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Iron Block";
    }

    }