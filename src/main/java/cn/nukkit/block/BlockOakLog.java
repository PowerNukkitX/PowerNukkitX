package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockOakLog extends BlockLog {
    public static final BlockProperties PROPERTIES = new BlockProperties(OAK_LOG, CommonBlockProperties.PILLAR_AXIS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockOakLog() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockOakLog(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public BlockState getStrippedState() {
        return BlockStrippedOakLog.PROPERTIES.getDefaultState();
    }
}