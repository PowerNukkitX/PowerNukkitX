package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockPurpleShulkerBox extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:purple_shulker_box");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPurpleShulkerBox() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPurpleShulkerBox(BlockState blockstate) {
        super(blockstate);
    }
}