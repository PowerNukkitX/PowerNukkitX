package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockMangroveDoor extends BlockWoodenDoor {
    public static final BlockProperties PROPERTIES = new BlockProperties(MANGROVE_DOOR, CommonBlockProperties.DIRECTION, CommonBlockProperties.DOOR_HINGE_BIT, CommonBlockProperties.OPEN_BIT, CommonBlockProperties.UPPER_BLOCK_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMangroveDoor() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMangroveDoor(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Mangrove Door Block";
    }
}