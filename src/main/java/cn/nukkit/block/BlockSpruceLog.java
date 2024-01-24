package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockSpruceLog extends BlockLog {
    public static final BlockProperties PROPERTIES = new BlockProperties(SPRUCE_LOG, CommonBlockProperties.PILLAR_AXIS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSpruceLog() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSpruceLog(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public BlockState getStrippedState() {
        return BlockStrippedSpruceLog.PROPERTIES.getDefaultState();
    }
}