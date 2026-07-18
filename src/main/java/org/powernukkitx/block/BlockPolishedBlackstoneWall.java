package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockPolishedBlackstoneWall extends BlockWallBase {
    public static final BlockProperties PROPERTIES = new BlockProperties(POLISHED_BLACKSTONE_WALL, CommonBlockProperties.WALL_CONNECTION_TYPE_EAST, CommonBlockProperties.WALL_CONNECTION_TYPE_NORTH, CommonBlockProperties.WALL_CONNECTION_TYPE_SOUTH, CommonBlockProperties.WALL_CONNECTION_TYPE_WEST, CommonBlockProperties.WALL_POST_BIT);
    public static final BlockDefinition DEFINITION = BlockWallBase.DEFINITION.toBuilder()
            .hardness(1.5)
            .resistance(6.0)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_WOODEN)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPolishedBlackstoneWall() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPolishedBlackstoneWall(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    public BlockPolishedBlackstoneWall(BlockState blockstate, BlockDefinition definition) {
        super(blockstate, definition);
    }

    @Override
    public String getName() {
        return "Polished Blackstone Wall";
    }

    }