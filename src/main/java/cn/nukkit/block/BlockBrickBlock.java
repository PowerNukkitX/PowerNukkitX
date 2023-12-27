package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockBrickBlock extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:brick_block");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBrickBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBrickBlock(BlockState blockstate) {
        super(blockstate);
    }
}