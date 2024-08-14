package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockLightBlock6 extends BlockLightBlock0 {
    public static final BlockProperties PROPERTIES = new BlockProperties(LIGHT_BLOCK_6);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLightBlock6() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLightBlock6(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public int getLightLevel() {
        return 6;
    }

}