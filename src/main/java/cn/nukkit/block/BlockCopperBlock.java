package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockCopperBlock extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:copper_block");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCopperBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCopperBlock(BlockState blockstate) {
        super(blockstate);
    }
}