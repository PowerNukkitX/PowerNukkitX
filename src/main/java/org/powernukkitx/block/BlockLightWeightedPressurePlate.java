package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.math.NukkitMath;
import org.jetbrains.annotations.NotNull;

public class BlockLightWeightedPressurePlate extends BlockPressurePlateBase {
    public static final BlockProperties PROPERTIES = new BlockProperties(LIGHT_WEIGHTED_PRESSURE_PLATE, CommonBlockProperties.REDSTONE_SIGNAL);
    public static final BlockDefinition DEFINITION = BlockPressurePlateBase.DEFINITION.toBuilder()
            .hardness(0.5D)
            .resistance(2.5D)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_WOODEN)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLightWeightedPressurePlate() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLightWeightedPressurePlate(BlockState blockstate) {
        super(blockstate, DEFINITION);
        this.onPitch = 0.90000004f;
        this.offPitch = 0.75f;
    }

    @Override
    public String getName() {
        return "Weighted Pressure Plate (Light)";
    }

    @Override
    protected int computeRedstoneStrength() {
        int count = Math.min(this.level.getCollidingEntities(getCollisionBoundingBox()).length, this.getMaxWeight());

        if (count > 0) {
            float f = (float) Math.min(this.getMaxWeight(), count) / (float) this.getMaxWeight();
            return NukkitMath.ceilFloat(f * 15.0F);
        } else {
            return 0;
        }
    }

    public int getMaxWeight() {
        return 15;
    }
}