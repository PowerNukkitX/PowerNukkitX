package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockGoldOre extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:gold_ore");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockGoldOre() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockGoldOre(BlockState blockstate) {
        super(blockstate);
    }
}