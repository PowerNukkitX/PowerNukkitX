package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockPaleOakLog extends BlockLog {
    public static final BlockProperties PROPERTIES = new BlockProperties(PALE_OAK_LOG, CommonBlockProperties.PILLAR_AXIS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPaleOakLog() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPaleOakLog(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public BlockState getStrippedState() {
        return BlockStrippedPaleOakLog.PROPERTIES.getDefaultState();
    }
}