package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockWaxedCopperTrapdoor extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties(WAXED_COPPER_TRAPDOOR, CommonBlockProperties.DIRECTION, CommonBlockProperties.OPEN_BIT, CommonBlockProperties.UPSIDE_DOWN_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWaxedCopperTrapdoor() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWaxedCopperTrapdoor(BlockState blockstate) {
        super(blockstate);
    }
}