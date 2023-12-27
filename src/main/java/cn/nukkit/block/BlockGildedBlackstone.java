package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockGildedBlackstone extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:gilded_blackstone");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockGildedBlackstone() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockGildedBlackstone(BlockState blockstate) {
        super(blockstate);
    }
}