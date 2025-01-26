package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockClosedEyeblossom extends BlockFlower {
    public static final BlockProperties PROPERTIES = new BlockProperties(CLOSED_EYEBLOSSOM);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockClosedEyeblossom() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockClosedEyeblossom(BlockState blockstate) {
        super(blockstate);
    }

}