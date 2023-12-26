package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockStrippedCrimsonHyphae extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:stripped_crimson_hyphae", CommonBlockProperties.PILLAR_AXIS);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockStrippedCrimsonHyphae() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockStrippedCrimsonHyphae(BlockState blockstate) {
        super(blockstate);
    }
}