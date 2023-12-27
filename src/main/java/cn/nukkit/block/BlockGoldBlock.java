package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockGoldBlock extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:gold_block");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockGoldBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockGoldBlock(BlockState blockstate) {
        super(blockstate);
    }
}