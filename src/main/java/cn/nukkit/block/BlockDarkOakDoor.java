package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockDarkOakDoor extends BlockWoodenDoor {
    public static final BlockProperties PROPERTIES = new BlockProperties(DARK_OAK_DOOR, CommonBlockProperties.DIRECTION, CommonBlockProperties.DOOR_HINGE_BIT, CommonBlockProperties.OPEN_BIT, CommonBlockProperties.UPPER_BLOCK_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDarkOakDoor() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDarkOakDoor(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Dark Oak Door Block";
    }
}