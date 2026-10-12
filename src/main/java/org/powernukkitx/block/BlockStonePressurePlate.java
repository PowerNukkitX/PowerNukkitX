package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityLiving;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.math.AxisAlignedBB;
import org.jetbrains.annotations.NotNull;

public class BlockStonePressurePlate extends BlockPressurePlateBase {
    public static final BlockProperties PROPERTIES = new BlockProperties(STONE_PRESSURE_PLATE, CommonBlockProperties.REDSTONE_SIGNAL);
    public static final BlockDefinition DEFINITION = BlockPressurePlateBase.DEFINITION.toBuilder()
            .hardness(0.5D)
            .resistance(6)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_WOODEN)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockStonePressurePlate() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockStonePressurePlate(BlockState blockstate) {
        super(blockstate, DEFINITION);
        this.onPitch = 0.6f;
        this.offPitch = 0.5f;
    }

    @Override
    public String getName() {
        return "Stone Pressure Plate";
    }

    @Override
    protected int computeRedstoneStrength() {
        AxisAlignedBB bb = getCollisionBoundingBox();

        for (Entity entity : this.level.getCollidingEntities(bb)) {
            if (entity instanceof EntityLiving && entity.doesTriggerPressurePlate()) {
                return 15;
            }
        }

        return 0;
    }
}