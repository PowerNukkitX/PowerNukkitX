package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockExposedCopperDoor extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:exposed_copper_door", CommonBlockProperties.DIRECTION, CommonBlockProperties.DOOR_HINGE_BIT, CommonBlockProperties.OPEN_BIT, CommonBlockProperties.UPPER_BLOCK_BIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockExposedCopperDoor() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockExposedCopperDoor(BlockState blockstate) {
        super(blockstate);
    }
}