package org.powernukkitx.block.shelf;

import org.powernukkitx.block.BlockProperties;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockMushroomShelf extends AbstractBlockShelf {
    public static final BlockProperties PROPERTIES = new BlockProperties(SHELF_MUSHROOM, CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION, CommonBlockProperties.POWERED_SHELF_TYPE, CommonBlockProperties.POWERED_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMushroomShelf() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMushroomShelf(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Mushroom Shelf";
    }
}
