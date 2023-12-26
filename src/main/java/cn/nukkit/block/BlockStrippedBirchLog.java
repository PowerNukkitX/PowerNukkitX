package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockStrippedBirchLog extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:stripped_birch_log", CommonBlockProperties.PILLAR_AXIS);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockStrippedBirchLog() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockStrippedBirchLog(BlockState blockstate) {
        super(blockstate);
    }
}