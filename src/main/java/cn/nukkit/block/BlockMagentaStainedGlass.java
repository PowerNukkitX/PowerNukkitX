package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockMagentaStainedGlass extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:magenta_stained_glass");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMagentaStainedGlass() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMagentaStainedGlass(BlockState blockstate) {
        super(blockstate);
    }
}