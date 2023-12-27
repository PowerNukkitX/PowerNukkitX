package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockChiseledPolishedBlackstone extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:chiseled_polished_blackstone");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockChiseledPolishedBlackstone() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockChiseledPolishedBlackstone(BlockState blockstate) {
        super(blockstate);
    }
}