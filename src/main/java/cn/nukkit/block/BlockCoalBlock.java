package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockCoalBlock extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:coal_block");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCoalBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCoalBlock(BlockState blockstate) {
        super(blockstate);
    }
}