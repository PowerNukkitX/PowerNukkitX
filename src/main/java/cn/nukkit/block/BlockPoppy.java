package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockPoppy extends BlockFlower {
    public static final BlockProperties PROPERTIES = new BlockProperties(POPPY);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPoppy() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockPoppy(BlockState blockstate) {
        super(blockstate);
    }

}