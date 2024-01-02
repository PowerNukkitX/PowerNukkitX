package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockWeatheredCopperDoor extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties(WEATHERED_COPPER_DOOR, CommonBlockProperties.DIRECTION, CommonBlockProperties.DOOR_HINGE_BIT, CommonBlockProperties.OPEN_BIT, CommonBlockProperties.UPPER_BLOCK_BIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWeatheredCopperDoor() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWeatheredCopperDoor(BlockState blockstate) {
        super(blockstate);
    }
}