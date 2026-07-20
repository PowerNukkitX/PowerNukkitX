package org.powernukkitx.block.shelf;

import org.powernukkitx.block.BlockProperties;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

/**
 * @author keksdev
 * @since 1.21.110
 */
public class BlockWarpedShelf extends AbstractBlockShelf {
    public static final BlockProperties PROPERTIES = new BlockProperties(WARPED_SHELF, CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION, CommonBlockProperties.POWERED_SHELF_TYPE, CommonBlockProperties.POWERED_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWarpedShelf() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWarpedShelf(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Warped Shelf";
    }
}
