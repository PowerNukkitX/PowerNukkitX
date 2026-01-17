package cn.nukkit.block;

import cn.nukkit.block.definition.BlockDefinitions;
import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockWoodenDoor extends BlockDoor {
    public static final BlockProperties PROPERTIES = new BlockProperties(WOODEN_DOOR, CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION, CommonBlockProperties.OPEN_BIT, CommonBlockProperties.UPPER_BLOCK_BIT, CommonBlockProperties.DOOR_HINGE_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWoodenDoor() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWoodenDoor(BlockState blockstate) {
        super(blockstate, BlockDefinitions.WOODEN_DOOR);
    }

    @Override
    public String getName() {
        return "Wood Door Block";
    }
}