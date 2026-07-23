package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockPolishedBlackstoneBrickStairs extends BlockPolishedBlackstoneStairs {
    public static final BlockProperties PROPERTIES = new BlockProperties(POLISHED_BLACKSTONE_BRICK_STAIRS, CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION);
    public static final BlockDefinition DEFINITION = BlockPolishedBlackstoneStairs.DEFINITION.toBuilder()
            .hardness(1.5)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPolishedBlackstoneBrickStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPolishedBlackstoneBrickStairs(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Polished Blackstone Brick Stairs";
    }

    }