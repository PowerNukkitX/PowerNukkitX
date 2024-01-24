package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.NukkitMath;
import org.jetbrains.annotations.NotNull;

public class BlockLightWeightedPressurePlate extends BlockPressurePlateBase {
    public static final BlockProperties PROPERTIES = new BlockProperties(LIGHT_WEIGHTED_PRESSURE_PLATE, CommonBlockProperties.REDSTONE_SIGNAL);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLightWeightedPressurePlate() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLightWeightedPressurePlate(BlockState blockstate) {
        super(blockstate);
        this.onPitch = 0.90000004f;
        this.offPitch = 0.75f;
    }

    @Override
    public String getName() {
        return "Weighted Pressure Plate (Light)";
    }

    @Override
    public double getHardness() {
        return 0.5D;
    }

    @Override
    public double getResistance() {
        return 2.5D;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
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