package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockNetherSprouts extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:nether_sprouts");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockNetherSprouts() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockNetherSprouts(BlockState blockstate) {
        super(blockstate);
    }
}