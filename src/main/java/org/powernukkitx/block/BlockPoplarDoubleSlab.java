package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockPoplarDoubleSlab extends BlockDoubleWoodenSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties(POPLAR_DOUBLE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPoplarDoubleSlab(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getSlabName() {
        return "Poplar";
    }

    @Override
    public BlockState getSingleSlab() {
        return BlockPoplarSlab.PROPERTIES.getDefaultState();
    }
}
