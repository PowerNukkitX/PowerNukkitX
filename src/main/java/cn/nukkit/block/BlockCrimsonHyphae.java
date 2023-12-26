package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockCrimsonHyphae extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:crimson_hyphae", CommonBlockProperties.PILLAR_AXIS);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCrimsonHyphae() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCrimsonHyphae(BlockState blockstate) {
        super(blockstate);
    }
}