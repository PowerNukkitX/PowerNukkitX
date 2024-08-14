package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockLightBlock7 extends BlockLightBlock0 {
    public static final BlockProperties PROPERTIES = new BlockProperties(LIGHT_BLOCK_7);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLightBlock7() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLightBlock7(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public int getLightLevel() {
        return 7;
    }

}