package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockMelonBlock extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:melon_block");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMelonBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMelonBlock(BlockState blockstate) {
        super(blockstate);
    }
}