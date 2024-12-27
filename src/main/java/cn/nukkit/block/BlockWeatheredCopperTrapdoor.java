package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockWeatheredCopperTrapdoor extends BlockCopperTrapdoor {
    public static final BlockProperties PROPERTIES = new BlockProperties(WEATHERED_COPPER_TRAPDOOR, CommonBlockProperties.DIRECTION, CommonBlockProperties.OPEN_BIT, CommonBlockProperties.UPSIDE_DOWN_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWeatheredCopperTrapdoor() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWeatheredCopperTrapdoor(BlockState blockstate) {
        super(blockstate);
    }
}