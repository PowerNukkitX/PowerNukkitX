package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockRawCopperBlock extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:raw_copper_block");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockRawCopperBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockRawCopperBlock(BlockState blockstate) {
        super(blockstate);
    }
}