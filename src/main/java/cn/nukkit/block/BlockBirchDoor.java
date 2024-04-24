package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockBirchDoor extends BlockWoodenDoor {
    public static final BlockProperties PROPERTIES = new BlockProperties(BIRCH_DOOR, CommonBlockProperties.DIRECTION, CommonBlockProperties.DOOR_HINGE_BIT, CommonBlockProperties.OPEN_BIT, CommonBlockProperties.UPPER_BLOCK_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBirchDoor() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBirchDoor(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Birch Door Block";
    }
}