package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockStrippedJungleLog extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:stripped_jungle_log", CommonBlockProperties.PILLAR_AXIS);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockStrippedJungleLog() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockStrippedJungleLog(BlockState blockstate) {
        super(blockstate);
    }
}