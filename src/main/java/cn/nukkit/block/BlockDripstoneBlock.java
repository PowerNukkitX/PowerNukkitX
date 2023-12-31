package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockDripstoneBlock extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties(DRIPSTONE_BLOCK);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDripstoneBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDripstoneBlock(BlockState blockstate) {
        super(blockstate);
    }
}