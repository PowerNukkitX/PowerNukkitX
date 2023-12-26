package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockWarpedHyphae extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:warped_hyphae", CommonBlockProperties.PILLAR_AXIS);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWarpedHyphae() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWarpedHyphae(BlockState blockstate) {
        super(blockstate);
    }
}