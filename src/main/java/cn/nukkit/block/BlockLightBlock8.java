package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockLightBlock8 extends BlockLightBlock0 {
    public static final BlockProperties PROPERTIES = new BlockProperties(LIGHT_BLOCK_8);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLightBlock8() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLightBlock8(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public int getLightLevel() {
        return 8;
    }

}