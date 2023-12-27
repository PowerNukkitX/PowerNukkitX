package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockBlackShulkerBox extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:black_shulker_box");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBlackShulkerBox() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBlackShulkerBox(BlockState blockstate) {
        super(blockstate);
    }
}