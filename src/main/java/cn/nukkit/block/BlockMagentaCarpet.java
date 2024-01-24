package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockMagentaCarpet extends BlockCarpet {
    public static final BlockProperties PROPERTIES = new BlockProperties(MAGENTA_CARPET);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMagentaCarpet() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMagentaCarpet(BlockState blockstate) {
        super(blockstate);
    }
}