package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.block.property.enums.OxidizationLevel;
import org.jetbrains.annotations.NotNull;

public class BlockCopperDoor extends BlockCopperDoorBase {
    public static final BlockProperties PROPERTIES = new BlockProperties(COPPER_DOOR, CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION, CommonBlockProperties.OPEN_BIT, CommonBlockProperties.UPPER_BLOCK_BIT, CommonBlockProperties.DOOR_HINGE_BIT);
    public static final BlockDefinition DEFINITION = BlockCopperDoorBase.DEFINITION.toBuilder()
            .hardness(3.0)
            .resistance(15)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCopperDoor() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCopperDoor(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public @NotNull OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.UNAFFECTED;
    }
}