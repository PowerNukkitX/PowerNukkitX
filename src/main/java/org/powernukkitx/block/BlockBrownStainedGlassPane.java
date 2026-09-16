package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.utils.DyeColor;
import org.jetbrains.annotations.NotNull;

public class BlockBrownStainedGlassPane extends BlockGlassPaneStained {
    public static final BlockProperties PROPERTIES = new BlockProperties(BROWN_STAINED_GLASS_PANE, CommonBlockProperties.CONNECTION_EAST, CommonBlockProperties.CONNECTION_NORTH, CommonBlockProperties.CONNECTION_SOUTH, CommonBlockProperties.CONNECTION_WEST);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBrownStainedGlassPane() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBrownStainedGlassPane(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.BROWN;
    }
}
