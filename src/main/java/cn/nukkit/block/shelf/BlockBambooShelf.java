package cn.nukkit.block.shelf;

import cn.nukkit.block.BlockProperties;
import cn.nukkit.block.BlockState;
import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

/**
 * @author keksdev
 * @since 1.21.110
 */
public class BlockBambooShelf extends AbstractBlockShelf {
    public static final BlockProperties PROPERTIES = new BlockProperties(BAMBOO_SHELF, CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION, CommonBlockProperties.POWERED_SHELF_TYPE, CommonBlockProperties.POWERED_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBambooShelf() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBambooShelf(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Bamboo Shelf";
    }
}
