package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockStrippedAcaciaLog extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:stripped_acacia_log", CommonBlockProperties.PILLAR_AXIS);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockStrippedAcaciaLog() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockStrippedAcaciaLog(BlockState blockstate) {
        super(blockstate);
    }
}