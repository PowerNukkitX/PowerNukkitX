package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockStrippedSpruceLog extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:stripped_spruce_log", CommonBlockProperties.PILLAR_AXIS);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockStrippedSpruceLog() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockStrippedSpruceLog(BlockState blockstate) {
        super(blockstate);
    }
}