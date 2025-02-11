package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.OxidizationLevel;
import org.jetbrains.annotations.NotNull;

public class BlockExposedCopperDoor extends BlockCopperDoorBase {
    public static final BlockProperties PROPERTIES = new BlockProperties(EXPOSED_COPPER_DOOR, CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION, CommonBlockProperties.OPEN_BIT, CommonBlockProperties.UPPER_BLOCK_BIT, CommonBlockProperties.DOOR_HINGE_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockExposedCopperDoor() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockExposedCopperDoor(BlockState blockstate) {
        super(blockstate);
    }


    @Override
    public @NotNull OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.EXPOSED;
    }
}