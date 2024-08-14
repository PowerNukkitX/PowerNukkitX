package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockLightBlock9 extends BlockLightBlock0 {
    public static final BlockProperties PROPERTIES = new BlockProperties(LIGHT_BLOCK_9);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLightBlock9() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLightBlock9(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public int getLightLevel() {
        return 9;
    }

}