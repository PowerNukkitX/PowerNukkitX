package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockDeepslateDiamondOre extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:deepslate_diamond_ore");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDeepslateDiamondOre() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDeepslateDiamondOre(BlockState blockstate) {
        super(blockstate);
    }
}