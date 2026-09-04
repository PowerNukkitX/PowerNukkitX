package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockDeepslateBricks extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(DEEPSLATE_BRICKS);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(3.5)
            .resistance(6)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_WOODEN)
            .canHarvestWithHand(false)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDeepslateBricks() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDeepslateBricks(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Deepslate Bricks";
    }

    }