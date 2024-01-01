package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockPolishedTuff extends BlockTuff {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:polished_tuff");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPolishedTuff() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPolishedTuff(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Polished Tuff";
    }
}