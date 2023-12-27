package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockActivatorRail extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:activator_rail", CommonBlockProperties.RAIL_DATA_BIT, CommonBlockProperties.RAIL_DIRECTION_6);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockActivatorRail() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockActivatorRail(BlockState blockstate) {
        super(blockstate);
    }
}