package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockMovingBlock extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:moving_block");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMovingBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMovingBlock(BlockState blockstate) {
        super(blockstate);
    }
}