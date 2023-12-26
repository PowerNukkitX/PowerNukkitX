package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockBirchLog extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:birch_log", CommonBlockProperties.PILLAR_AXIS);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBirchLog() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBirchLog(BlockState blockstate) {
        super(blockstate);
    }
}