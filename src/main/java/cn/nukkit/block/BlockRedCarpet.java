package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockRedCarpet extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:red_carpet");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockRedCarpet() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockRedCarpet(BlockState blockstate) {
        super(blockstate);
    }
}