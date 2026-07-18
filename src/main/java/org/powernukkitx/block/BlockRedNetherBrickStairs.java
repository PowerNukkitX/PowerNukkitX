package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockRedNetherBrickStairs extends BlockStairs {
    public static final BlockProperties PROPERTIES = new BlockProperties(RED_NETHER_BRICK_STAIRS, CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION);
    public static final BlockDefinition DEFINITION = BlockStairs.DEFINITION.toBuilder()
            .hardness(2)
            .resistance(30)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_WOODEN)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockRedNetherBrickStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockRedNetherBrickStairs(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Red Nether Brick Stairs";
    }

}