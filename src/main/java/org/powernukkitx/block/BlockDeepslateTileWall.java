package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockDeepslateTileWall extends BlockWallBase {
    public static final BlockProperties PROPERTIES = new BlockProperties(DEEPSLATE_TILE_WALL, CommonBlockProperties.WALL_CONNECTION_TYPE_EAST, CommonBlockProperties.WALL_CONNECTION_TYPE_NORTH, CommonBlockProperties.WALL_CONNECTION_TYPE_SOUTH, CommonBlockProperties.WALL_CONNECTION_TYPE_WEST, CommonBlockProperties.WALL_POST_BIT);
    public static final BlockDefinition DEFINITION = BlockWallBase.DEFINITION.toBuilder()
            .hardness(3.5)
            .resistance(6)
            .toolTier(ItemTool.TIER_WOODEN)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDeepslateTileWall() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDeepslateTileWall(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Deepslate Tile Wall";
    }

    }