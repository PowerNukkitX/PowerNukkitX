package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockLapisBlock extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:lapis_block");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLapisBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLapisBlock(BlockState blockstate) {
        super(blockstate);
    }
}