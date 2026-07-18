package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockSandstoneStairs extends BlockStairs {
    public static final BlockProperties PROPERTIES = new BlockProperties(SANDSTONE_STAIRS, CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION);
    public static final BlockDefinition DEFINITION = BlockStairs.DEFINITION.toBuilder()
            .hardness(0.8)
            .resistance(4)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_WOODEN)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSandstoneStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSandstoneStairs(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Sandstone Stairs";
    }

}