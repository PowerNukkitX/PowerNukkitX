package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockLightBlock15 extends BlockLightBlock0 {
    public static final BlockProperties PROPERTIES = new BlockProperties(LIGHT_BLOCK_15);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLightBlock15() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLightBlock15(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public int getLightLevel() {
        return 15;
    }

}