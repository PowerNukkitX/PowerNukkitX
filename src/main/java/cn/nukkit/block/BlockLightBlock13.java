package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockLightBlock13 extends BlockLightBlock0 {
    public static final BlockProperties PROPERTIES = new BlockProperties(LIGHT_BLOCK_13);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLightBlock13() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLightBlock13(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public int getLightLevel() {
        return 13;
    }

}