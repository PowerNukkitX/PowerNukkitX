package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockLightBlock12 extends BlockLightBlock0 {
    public static final BlockProperties PROPERTIES = new BlockProperties(LIGHT_BLOCK_12);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLightBlock12() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLightBlock12(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public int getLightLevel() {
        return 12;
    }

}