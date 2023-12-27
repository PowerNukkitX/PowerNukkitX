package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
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