package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockLightBlock1 extends BlockLightBlock0 {
    public static final BlockProperties PROPERTIES = new BlockProperties(LIGHT_BLOCK_1);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLightBlock1() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLightBlock1(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public int getLightLevel() {
        return 1;
    }

}