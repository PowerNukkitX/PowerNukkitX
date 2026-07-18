package org.powernukkitx.block.copper.bars;

import org.powernukkitx.block.*;
import org.jetbrains.annotations.NotNull;


/**
 * @author keksdev
 * @since 1.21.110
 */
public class BlockWaxedCopperBars extends BlockCopperBars {
    public static final BlockProperties PROPERTIES = new BlockProperties(WAXED_COPPER_BARS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWaxedCopperBars() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWaxedCopperBars(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Waxed Copper Bars";
    }

    @Override
    public boolean isWaxed() {
        return true;
    }
}
