package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockJungleLog extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:jungle_log", CommonBlockProperties.PILLAR_AXIS);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockJungleLog() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockJungleLog(BlockState blockstate) {
        super(blockstate);
    }
}