package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockWaxedCopperDoor extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:waxed_copper_door", CommonBlockProperties.DIRECTION, CommonBlockProperties.DOOR_HINGE_BIT, CommonBlockProperties.OPEN_BIT, CommonBlockProperties.UPPER_BLOCK_BIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWaxedCopperDoor() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWaxedCopperDoor(BlockState blockstate) {
        super(blockstate);
    }
}