package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockPoplarSlab extends BlockWoodenSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties(POPLAR_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPoplarSlab(BlockState blockstate) {
        super(blockstate, POPLAR_DOUBLE_SLAB);
    }

    @Override
    public String getSlabName() {
        return "Poplar";
    }
}
