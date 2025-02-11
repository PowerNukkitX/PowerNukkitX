package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.OxidizationLevel;
import org.jetbrains.annotations.NotNull;

public class BlockCopperDoor extends BlockCopperDoorBase {
    public static final BlockProperties PROPERTIES = new BlockProperties(COPPER_DOOR, CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION, CommonBlockProperties.OPEN_BIT, CommonBlockProperties.UPPER_BLOCK_BIT, CommonBlockProperties.DOOR_HINGE_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCopperDoor() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCopperDoor(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public double getHardness() {
        return 3.0;
    }

    @Override
    public double getResistance() {
        return 15;
    }

    @Override
    public @NotNull OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.UNAFFECTED;
    }
}