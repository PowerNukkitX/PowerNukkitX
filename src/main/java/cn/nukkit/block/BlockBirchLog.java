package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockBirchLog extends BlockLog {
    public static final BlockProperties PROPERTIES = new BlockProperties(BIRCH_LOG, CommonBlockProperties.PILLAR_AXIS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBirchLog() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBirchLog(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public BlockState getStrippedState() {
        return BlockStrippedBirchLog.PROPERTIES.getDefaultState();
    }
}