package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.math.AxisAlignedBB;
import org.jetbrains.annotations.NotNull;

public class BlockWoodenPressurePlate extends BlockPressurePlateBase {
    public static final BlockProperties PROPERTIES = new BlockProperties(WOODEN_PRESSURE_PLATE, CommonBlockProperties.REDSTONE_SIGNAL);
    public static final BlockDefinition DEFINITION = BlockPressurePlateBase.DEFINITION.toBuilder()
            .hardness(0.5D)
            .resistance(0.5D)
            .toolType(ItemTool.TYPE_AXE)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWoodenPressurePlate() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWoodenPressurePlate(BlockState blockstate) {
        super(blockstate, DEFINITION);
        this.onPitch = 0.8f;
        this.offPitch = 0.7f;
    }

    public BlockWoodenPressurePlate(BlockState blockstate, BlockDefinition definition) {
        super(blockstate, definition);
        this.onPitch = 0.8f;
        this.offPitch = 0.7f;
    }

    @Override
    public String getName() {
        return "Oak Pressure Plate";
    }

    @Override
    protected int computeRedstoneStrength() {
        AxisAlignedBB bb = getCollisionBoundingBox();

        for (Entity entity : this.level.getCollidingEntities(bb)) {
            if (entity.doesTriggerPressurePlate()) {
                return 15;
            }
        }

        return 0;
    }
}