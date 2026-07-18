package org.powernukkitx.block.shelf;

import org.powernukkitx.block.BlockProperties;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

/**
 * @author keksdev
 * @since 1.21.110
 */
public class BlockDarkOakShelf extends AbstractBlockShelf {
    public static final BlockProperties PROPERTIES = new BlockProperties(DARK_OAK_SHELF, CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION, CommonBlockProperties.POWERED_SHELF_TYPE, CommonBlockProperties.POWERED_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDarkOakShelf() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDarkOakShelf(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Dark Oak Shelf";
    }
}
