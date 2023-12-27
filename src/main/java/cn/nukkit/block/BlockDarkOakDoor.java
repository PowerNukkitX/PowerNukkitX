package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockDarkOakDoor extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:dark_oak_door", CommonBlockProperties.DIRECTION, CommonBlockProperties.DOOR_HINGE_BIT, CommonBlockProperties.OPEN_BIT, CommonBlockProperties.UPPER_BLOCK_BIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDarkOakDoor() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDarkOakDoor(BlockState blockstate) {
        super(blockstate);
    }
}