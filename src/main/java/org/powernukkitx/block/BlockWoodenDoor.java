package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.block.definition.BlockDefinition;
import org.jetbrains.annotations.NotNull;

public class BlockWoodenDoor extends BlockDoor {
    public static final BlockProperties PROPERTIES = new BlockProperties(WOODEN_DOOR, CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION, CommonBlockProperties.OPEN_BIT, CommonBlockProperties.UPPER_BLOCK_BIT, CommonBlockProperties.DOOR_HINGE_BIT);
    public static final BlockDefinition DEFINITION = BlockDoor.DEFINITION.toBuilder()
            .hardness(3)
            .resistance(15)
            .toolType(ItemTool.TYPE_AXE)
            .toolTier(ItemTool.TIER_WOODEN)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWoodenDoor() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWoodenDoor(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    @Override
    public String getName() {
        return "Wood Door Block";
    }
}