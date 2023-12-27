package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockYellowConcrete extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:yellow_concrete");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockYellowConcrete() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockYellowConcrete(BlockState blockstate) {
        super(blockstate);
    }
}