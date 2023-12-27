package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockRedConcrete extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:red_concrete");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockRedConcrete() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockRedConcrete(BlockState blockstate) {
        super(blockstate);
    }
}