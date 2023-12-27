package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockDarkOakLog extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:dark_oak_log", CommonBlockProperties.PILLAR_AXIS);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDarkOakLog() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDarkOakLog(BlockState blockstate) {
        super(blockstate);
    }
}