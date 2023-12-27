package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockCopperOre extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:copper_ore");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCopperOre() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCopperOre(BlockState blockstate) {
        super(blockstate);
    }
}