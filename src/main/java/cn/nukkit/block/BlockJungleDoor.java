package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockJungleDoor extends BlockWoodenDoor {
    public static final BlockProperties PROPERTIES = new BlockProperties(JUNGLE_DOOR, CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION, CommonBlockProperties.OPEN_BIT, CommonBlockProperties.UPPER_BLOCK_BIT, CommonBlockProperties.DOOR_HINGE_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockJungleDoor() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockJungleDoor(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Jungle Door Block";
    }
}