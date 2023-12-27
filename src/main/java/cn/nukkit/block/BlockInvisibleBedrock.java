package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockInvisibleBedrock extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:invisible_bedrock");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockInvisibleBedrock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockInvisibleBedrock(BlockState blockstate) {
        super(blockstate);
    }
}