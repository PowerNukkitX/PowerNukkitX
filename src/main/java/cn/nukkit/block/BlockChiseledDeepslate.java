package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockChiseledDeepslate extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:chiseled_deepslate");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockChiseledDeepslate() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockChiseledDeepslate(BlockState blockstate) {
        super(blockstate);
    }
}