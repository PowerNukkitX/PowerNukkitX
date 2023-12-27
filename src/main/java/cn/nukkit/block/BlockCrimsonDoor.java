package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockCrimsonDoor extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:crimson_door", CommonBlockProperties.DIRECTION, CommonBlockProperties.DOOR_HINGE_BIT, CommonBlockProperties.OPEN_BIT, CommonBlockProperties.UPPER_BLOCK_BIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCrimsonDoor() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCrimsonDoor(BlockState blockstate) {
        super(blockstate);
    }
}