package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockSpruceDoor extends BlockWoodenDoor {
    public static final BlockProperties PROPERTIES = new BlockProperties(SPRUCE_DOOR, CommonBlockProperties.DIRECTION, CommonBlockProperties.DOOR_HINGE_BIT, CommonBlockProperties.OPEN_BIT, CommonBlockProperties.UPPER_BLOCK_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSpruceDoor() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSpruceDoor(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Spruce Door Block";
    }
}