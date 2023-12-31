package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockChiseledTuff extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:chiseled_tuff");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockChiseledTuff() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockChiseledTuff(BlockState blockstate) {
        super(blockstate);
    }
}