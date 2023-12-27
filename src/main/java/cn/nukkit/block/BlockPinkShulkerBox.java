package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockPinkShulkerBox extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:pink_shulker_box");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPinkShulkerBox() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPinkShulkerBox(BlockState blockstate) {
        super(blockstate);
    }
}