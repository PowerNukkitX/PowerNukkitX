package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockStrippedCherryLog extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:stripped_cherry_log", CommonBlockProperties.PILLAR_AXIS);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockStrippedCherryLog() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockStrippedCherryLog(BlockState blockstate) {
        super(blockstate);
    }
}