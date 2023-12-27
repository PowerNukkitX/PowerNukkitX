package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockStrippedMangroveLog extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:stripped_mangrove_log", CommonBlockProperties.PILLAR_AXIS);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockStrippedMangroveLog() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockStrippedMangroveLog(BlockState blockstate) {
        super(blockstate);
    }
}