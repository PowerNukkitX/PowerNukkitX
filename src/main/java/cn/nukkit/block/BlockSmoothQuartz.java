package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockSmoothQuartz extends BlockSandstone {
    public static final BlockProperties PROPERTIES = new BlockProperties(SMOOTH_QUARTZ, CommonBlockProperties.PILLAR_AXIS);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSmoothQuartz() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSmoothQuartz(BlockState blockstate) {
        super(blockstate);
    }
}
