package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockMagentaShulkerBox extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:magenta_shulker_box");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMagentaShulkerBox() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMagentaShulkerBox(BlockState blockstate) {
        super(blockstate);
    }
}