package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockOrangeWool extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:orange_wool");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockOrangeWool() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockOrangeWool(BlockState blockstate) {
        super(blockstate);
    }
}