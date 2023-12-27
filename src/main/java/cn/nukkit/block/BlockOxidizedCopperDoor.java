package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockOxidizedCopperDoor extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:oxidized_copper_door", CommonBlockProperties.DIRECTION, CommonBlockProperties.DOOR_HINGE_BIT, CommonBlockProperties.OPEN_BIT, CommonBlockProperties.UPPER_BLOCK_BIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockOxidizedCopperDoor() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockOxidizedCopperDoor(BlockState blockstate) {
        super(blockstate);
    }
}