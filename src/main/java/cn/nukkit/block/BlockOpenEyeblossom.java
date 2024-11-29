package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockOpenEyeblossom extends BlockClosedEyeblossom {
    public static final BlockProperties PROPERTIES = new BlockProperties(OPEN_EYEBLOSSOM);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockOpenEyeblossom() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockOpenEyeblossom(BlockState blockstate) {
        super(blockstate);
    }

}