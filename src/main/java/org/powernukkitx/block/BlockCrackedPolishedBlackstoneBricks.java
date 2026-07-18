package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.jetbrains.annotations.NotNull;

public class BlockCrackedPolishedBlackstoneBricks extends BlockBlackstone {
    public static final BlockProperties PROPERTIES = new BlockProperties(CRACKED_POLISHED_BLACKSTONE_BRICKS);
    public static final BlockDefinition DEFINITION = BlockBlackstone.DEFINITION.toBuilder()
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCrackedPolishedBlackstoneBricks() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCrackedPolishedBlackstoneBricks(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Cracked Polished Blackstone Bricks";
    }

    }