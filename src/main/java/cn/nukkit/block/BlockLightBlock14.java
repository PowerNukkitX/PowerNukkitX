package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockLightBlock14 extends BlockLightBlock0 {
    public static final BlockProperties PROPERTIES = new BlockProperties(LIGHT_BLOCK_14);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLightBlock14() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLightBlock14(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public int getLightLevel() {
        return 14;
    }

}