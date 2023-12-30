package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockBrownCarpet extends BlockCarpet {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:brown_carpet");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBrownCarpet() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBrownCarpet(BlockState blockstate) {
        super(blockstate);
    }
}