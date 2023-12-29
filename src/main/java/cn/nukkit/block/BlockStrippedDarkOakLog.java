package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockStrippedDarkOakLog extends BlockLog {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:stripped_dark_oak_log", CommonBlockProperties.PILLAR_AXIS);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockStrippedDarkOakLog() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockStrippedDarkOakLog(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public BlockState getStrippedState() {
        return BlockStrippedAcaciaLog.PROPERTIES.getDefaultState();
    }
}