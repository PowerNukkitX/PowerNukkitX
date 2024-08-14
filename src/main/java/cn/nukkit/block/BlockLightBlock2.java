package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockLightBlock2 extends BlockLightBlock0 {
    public static final BlockProperties PROPERTIES = new BlockProperties(LIGHT_BLOCK_2);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLightBlock2() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLightBlock2(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public int getLightLevel() {
        return 2;
    }

}