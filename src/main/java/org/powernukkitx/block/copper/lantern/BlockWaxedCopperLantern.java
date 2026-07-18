package org.powernukkitx.block.copper.lantern;

import org.powernukkitx.block.BlockProperties;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

/**
 * @author keksdev
 * @since 1.21.110
 */
public class BlockWaxedCopperLantern extends BlockCopperLantern {
    public static final BlockProperties PROPERTIES = new BlockProperties(WAXED_COPPER_LANTERN, CommonBlockProperties.HANGING);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWaxedCopperLantern() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWaxedCopperLantern(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Waxed Copper Lantern";
    }

    @Override
    public boolean isWaxed() {
        return true;
    }
}
