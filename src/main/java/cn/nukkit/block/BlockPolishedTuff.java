package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockPolishedTuff extends BlockTuff {
    public static final BlockProperties PROPERTIES = new BlockProperties(POLISHED_TUFF);

    @Override
    @NotNull public BlockProperties getProperties() {
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