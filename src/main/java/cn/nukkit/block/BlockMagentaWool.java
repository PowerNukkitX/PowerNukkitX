package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockMagentaWool extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:magenta_wool");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMagentaWool() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMagentaWool(BlockState blockstate) {
        super(blockstate);
    }
}