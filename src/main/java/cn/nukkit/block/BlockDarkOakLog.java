package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockDarkOakLog extends BlockLog {
    public static final BlockProperties PROPERTIES = new BlockProperties(DARK_OAK_LOG, CommonBlockProperties.PILLAR_AXIS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDarkOakLog() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDarkOakLog(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public BlockState getStrippedState() {
        return BlockStrippedDarkOakLog.PROPERTIES.getDefaultState();
    }
}