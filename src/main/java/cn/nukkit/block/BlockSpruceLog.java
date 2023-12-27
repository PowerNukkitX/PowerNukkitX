package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockSpruceLog extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:spruce_log", CommonBlockProperties.PILLAR_AXIS);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSpruceLog() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSpruceLog(BlockState blockstate) {
        super(blockstate);
    }
}