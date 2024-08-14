package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockLightBlock5 extends BlockLightBlock0 {
    public static final BlockProperties PROPERTIES = new BlockProperties(LIGHT_BLOCK_5);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLightBlock5() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLightBlock5(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public int getLightLevel() {
        return 5;
    }

}