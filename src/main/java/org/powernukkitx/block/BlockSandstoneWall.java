package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemBlock;
import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockSandstoneWall extends BlockWallBase {

    public static final BlockProperties PROPERTIES = new BlockProperties(
            SANDSTONE_WALL,
            CommonBlockProperties.WALL_CONNECTION_TYPE_EAST,
            CommonBlockProperties.WALL_CONNECTION_TYPE_NORTH,
            CommonBlockProperties.WALL_CONNECTION_TYPE_SOUTH,
            CommonBlockProperties.WALL_CONNECTION_TYPE_WEST,
            CommonBlockProperties.WALL_POST_BIT
    );
    public static final BlockDefinition DEFINITION = BlockWallBase.DEFINITION.toBuilder()
            .hardness(0.8)
            .resistance(0.8)
            .toolTier(ItemTool.TIER_WOODEN)
            .build();

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSandstoneWall() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSandstoneWall(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Sandstone Wall";
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this.getProperties().getDefaultState().toBlock());
    }
}
