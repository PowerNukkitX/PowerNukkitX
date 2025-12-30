package cn.nukkit.block.shelf;

import cn.nukkit.block.BlockProperties;
import cn.nukkit.block.BlockState;
import cn.nukkit.block.copper.lantern.BlockCopperLantern;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.OxidizationLevel;
import org.jetbrains.annotations.NotNull;

/**
 * @author keksdev
 * @since 1.21.110
 */
public class BlockOakShelf extends AbstractBlockShelf {
    public static final BlockProperties PROPERTIES = new BlockProperties(OAK_SHELF, CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION, CommonBlockProperties.POWERED_SHELF_TYPE, CommonBlockProperties.POWERED_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockOakShelf() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockOakShelf(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Oak Shelf";
    }
}
