package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockBirchDoor extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:birch_door", CommonBlockProperties.DIRECTION, CommonBlockProperties.DOOR_HINGE_BIT, CommonBlockProperties.OPEN_BIT, CommonBlockProperties.UPPER_BLOCK_BIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBirchDoor() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBirchDoor(BlockState blockstate) {
        super(blockstate);
    }
}