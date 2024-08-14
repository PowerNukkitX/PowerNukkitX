package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockLightBlock11 extends BlockLightBlock0 {
    public static final BlockProperties PROPERTIES = new BlockProperties(LIGHT_BLOCK_11);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLightBlock11() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLightBlock11(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public int getLightLevel() {
        return 11;
    }

}