package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockPoplarLog extends BlockLog {
    public static final BlockProperties PROPERTIES = new BlockProperties(POPLAR_LOG, CommonBlockProperties.PILLAR_AXIS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPoplarLog() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPoplarLog(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public BlockState getStrippedState() {
        return BlockStrippedPoplarLog.PROPERTIES.getDefaultState();
    }
}
