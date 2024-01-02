package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockCopperDoor extends BlockDoor {
    public static final BlockProperties PROPERTIES = new BlockProperties(COPPER_DOOR, CommonBlockProperties.DIRECTION, CommonBlockProperties.DOOR_HINGE_BIT, CommonBlockProperties.OPEN_BIT, CommonBlockProperties.UPPER_BLOCK_BIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCopperDoor() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCopperDoor(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public double getHardness() {
        return 3.0;
    }

    @Override
    public double getResistance() {
        return 15;
    }
}