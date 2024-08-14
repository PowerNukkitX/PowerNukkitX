package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockLightBlock4 extends BlockLightBlock0 {
    public static final BlockProperties PROPERTIES = new BlockProperties(LIGHT_BLOCK_4);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLightBlock4() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLightBlock4(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public int getLightLevel() {
        return 4;
    }

}