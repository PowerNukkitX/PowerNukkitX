package cn.nukkit.block;

import cn.nukkit.utils.RedstoneComponent;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.RAIL_DATA_BIT;
import static cn.nukkit.block.property.CommonBlockProperties.RAIL_DIRECTION_6;

public class BlockActivatorRail extends BlockRailPowerable implements RedstoneComponent {
    public static final BlockProperties PROPERTIES = new BlockProperties(ACTIVATOR_RAIL, RAIL_DATA_BIT, RAIL_DIRECTION_6);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockActivatorRail() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockActivatorRail(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Activator Rail";
    }
}