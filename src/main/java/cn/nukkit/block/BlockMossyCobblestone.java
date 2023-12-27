package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockMossyCobblestone extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:mossy_cobblestone");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMossyCobblestone() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMossyCobblestone(BlockState blockstate) {
        super(blockstate);
    }
}