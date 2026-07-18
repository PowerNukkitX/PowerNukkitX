package org.powernukkitx.education.block;

import org.powernukkitx.block.*;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockUnderwaterTNT extends BlockTnt {
    public static final BlockProperties PROPERTIES = new BlockProperties(UNDERWATER_TNT, CommonBlockProperties.EXPLODE_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockUnderwaterTNT() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockUnderwaterTNT(BlockState blockstate) {
        super(blockstate);
    }
}