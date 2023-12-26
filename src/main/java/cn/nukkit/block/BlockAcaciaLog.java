package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockAcaciaLog extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:acacia_log", CommonBlockProperties.PILLAR_AXIS);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockAcaciaLog() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockAcaciaLog(BlockState blockstate) {
        super(blockstate);
    }
}