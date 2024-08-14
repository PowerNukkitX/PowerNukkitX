package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockLightBlock3 extends BlockLightBlock0 {
    public static final BlockProperties PROPERTIES = new BlockProperties(LIGHT_BLOCK_3);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLightBlock3() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLightBlock3(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public int getLightLevel() {
        return 3;
    }

}