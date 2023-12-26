package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockStrippedWarpedHyphae extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:stripped_warped_hyphae", CommonBlockProperties.PILLAR_AXIS);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockStrippedWarpedHyphae() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockStrippedWarpedHyphae(BlockState blockstate) {
        super(blockstate);
    }
}