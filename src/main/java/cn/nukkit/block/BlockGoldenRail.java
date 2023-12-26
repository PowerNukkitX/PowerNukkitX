package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockGoldenRail extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:golden_rail", CommonBlockProperties.RAIL_DATA_BIT, CommonBlockProperties.RAIL_DIRECTION_6);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockGoldenRail() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockGoldenRail(BlockState blockstate) {
        super(blockstate);
    }
}