package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockJungleLog extends BlockLog {
    public static final BlockProperties PROPERTIES = new BlockProperties(JUNGLE_LOG, CommonBlockProperties.PILLAR_AXIS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockJungleLog() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockJungleLog(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public BlockState getStrippedState() {
        return BlockStrippedJungleLog.PROPERTIES.getDefaultState();
    }
}