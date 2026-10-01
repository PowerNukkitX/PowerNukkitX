package org.powernukkitx.block.shelf;

import org.powernukkitx.block.BlockProperties;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockPoplarShelf extends AbstractBlockShelf {
    public static final BlockProperties PROPERTIES = new BlockProperties(POPLAR_SHELF, CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION, CommonBlockProperties.POWERED_SHELF_TYPE, CommonBlockProperties.POWERED_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPoplarShelf() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPoplarShelf(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Poplar Shelf";
    }
}
