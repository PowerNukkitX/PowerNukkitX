package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockLapisBlock extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(LAPIS_BLOCK);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(3)
            .resistance(5)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_STONE)
            .canHarvestWithHand(false)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLapisBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLapisBlock(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Lapis Lazuli Block";
    }

    }