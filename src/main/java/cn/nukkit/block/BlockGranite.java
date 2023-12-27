package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockGranite extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:granite");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockGranite() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockGranite(BlockState blockstate) {
        super(blockstate);
    }
}