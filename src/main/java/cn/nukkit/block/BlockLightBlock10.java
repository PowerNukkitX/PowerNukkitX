package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockLightBlock10 extends BlockLightBlock0 {
    public static final BlockProperties PROPERTIES = new BlockProperties(LIGHT_BLOCK_10);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLightBlock10() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLightBlock10(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public int getLightLevel() {
        return 10;
    }

}