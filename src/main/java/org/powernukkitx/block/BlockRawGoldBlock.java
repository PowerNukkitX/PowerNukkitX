package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockRawGoldBlock extends BlockRaw {
    public static final BlockProperties PROPERTIES = new BlockProperties(RAW_GOLD_BLOCK);
    public static final BlockDefinition DEFINITION = BlockRaw.DEFINITION.toBuilder()
            .toolTier(ItemTool.TIER_IRON)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockRawGoldBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockRawGoldBlock(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Block of Raw Gold";
    }

    }