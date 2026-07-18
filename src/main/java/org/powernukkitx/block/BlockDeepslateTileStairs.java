package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockDeepslateTileStairs extends BlockStairs {
    public static final BlockProperties PROPERTIES = new BlockProperties(DEEPSLATE_TILE_STAIRS, CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION);
    public static final BlockDefinition DEFINITION = BlockStairs.DEFINITION.toBuilder()
            .hardness(3.5)
            .resistance(6)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_WOODEN)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDeepslateTileStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDeepslateTileStairs(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Deepslate Tile Stairs";
    }

    }