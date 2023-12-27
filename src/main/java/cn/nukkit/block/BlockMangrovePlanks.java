package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockMangrovePlanks extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:mangrove_planks");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMangrovePlanks() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMangrovePlanks(BlockState blockstate) {
        super(blockstate);
    }
}