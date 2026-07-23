package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.jetbrains.annotations.NotNull;

public class BlockPolishedBlackstoneBricks extends BlockPolishedBlackstone {
    public static final BlockProperties PROPERTIES = new BlockProperties(POLISHED_BLACKSTONE_BRICKS);
    public static final BlockDefinition DEFINITION = BlockPolishedBlackstone.DEFINITION.toBuilder()
            .hardness(1.5)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPolishedBlackstoneBricks() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPolishedBlackstoneBricks(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Polished Blackstone Bricks";
    }

    }