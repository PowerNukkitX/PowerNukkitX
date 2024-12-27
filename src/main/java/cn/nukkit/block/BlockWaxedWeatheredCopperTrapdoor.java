package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockWaxedWeatheredCopperTrapdoor extends BlockCopperTrapdoor {
    public static final BlockProperties PROPERTIES = new BlockProperties(WAXED_WEATHERED_COPPER_TRAPDOOR, CommonBlockProperties.DIRECTION, CommonBlockProperties.OPEN_BIT, CommonBlockProperties.UPSIDE_DOWN_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWaxedWeatheredCopperTrapdoor() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWaxedWeatheredCopperTrapdoor(BlockState blockstate) {
        super(blockstate);
    }
}