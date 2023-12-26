package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockWarpedDoor extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:warped_door", CommonBlockProperties.DIRECTION, CommonBlockProperties.DOOR_HINGE_BIT, CommonBlockProperties.OPEN_BIT, CommonBlockProperties.UPPER_BLOCK_BIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWarpedDoor() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWarpedDoor(BlockState blockstate) {
        super(blockstate);
    }
}