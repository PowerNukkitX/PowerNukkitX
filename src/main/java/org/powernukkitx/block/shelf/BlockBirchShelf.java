package org.powernukkitx.block.shelf;

import org.powernukkitx.block.BlockProperties;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

/**
 * @author keksdev
 * @since 1.21.110
 */
public class BlockBirchShelf extends AbstractBlockShelf {
    public static final BlockProperties PROPERTIES = new BlockProperties(BIRCH_SHELF, CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION, CommonBlockProperties.POWERED_SHELF_TYPE, CommonBlockProperties.POWERED_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBirchShelf() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBirchShelf(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Birch Shelf";
    }
}
