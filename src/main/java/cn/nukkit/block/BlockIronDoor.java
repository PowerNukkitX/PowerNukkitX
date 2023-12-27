package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockIronDoor extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:iron_door", CommonBlockProperties.DIRECTION, CommonBlockProperties.DOOR_HINGE_BIT, CommonBlockProperties.OPEN_BIT, CommonBlockProperties.UPPER_BLOCK_BIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockIronDoor() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockIronDoor(BlockState blockstate) {
        super(blockstate);
    }
}