package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockSpruceDoor extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:spruce_door", CommonBlockProperties.DIRECTION, CommonBlockProperties.DOOR_HINGE_BIT, CommonBlockProperties.OPEN_BIT, CommonBlockProperties.UPPER_BLOCK_BIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSpruceDoor() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSpruceDoor(BlockState blockstate) {
        super(blockstate);
    }
}