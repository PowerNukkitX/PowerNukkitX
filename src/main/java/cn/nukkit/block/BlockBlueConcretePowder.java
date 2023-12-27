package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockBlueConcretePowder extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:blue_concrete_powder");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBlueConcretePowder() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBlueConcretePowder(BlockState blockstate) {
        super(blockstate);
    }
}