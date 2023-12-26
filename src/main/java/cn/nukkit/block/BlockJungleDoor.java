package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockJungleDoor extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:jungle_door", CommonBlockProperties.DIRECTION, CommonBlockProperties.DOOR_HINGE_BIT, CommonBlockProperties.OPEN_BIT, CommonBlockProperties.UPPER_BLOCK_BIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockJungleDoor() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockJungleDoor(BlockState blockstate) {
        super(blockstate);
    }
}