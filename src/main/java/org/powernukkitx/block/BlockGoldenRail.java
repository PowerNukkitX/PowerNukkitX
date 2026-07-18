package org.powernukkitx.block;

import org.powernukkitx.utils.RedstoneComponent;
import org.jetbrains.annotations.NotNull;

import static org.powernukkitx.block.property.CommonBlockProperties.RAIL_DATA_BIT;
import static org.powernukkitx.block.property.CommonBlockProperties.RAIL_DIRECTION_6;

public class BlockGoldenRail extends BlockRailPowerable implements RedstoneComponent {
    public static final BlockProperties PROPERTIES = new BlockProperties(GOLDEN_RAIL, RAIL_DATA_BIT, RAIL_DIRECTION_6);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockGoldenRail() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockGoldenRail(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Powered Rail";
    }
}