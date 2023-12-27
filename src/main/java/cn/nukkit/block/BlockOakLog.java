package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockOakLog extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:oak_log", CommonBlockProperties.PILLAR_AXIS);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockOakLog() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockOakLog(BlockState blockstate) {
        super(blockstate);
    }
}