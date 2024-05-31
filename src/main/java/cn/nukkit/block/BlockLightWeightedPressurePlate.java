package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.NukkitMath;
import org.jetbrains.annotations.NotNull;

public class BlockLightWeightedPressurePlate extends BlockPressurePlateBase {
    public static final BlockProperties $1 = new BlockProperties(LIGHT_WEIGHTED_PRESSURE_PLATE, CommonBlockProperties.REDSTONE_SIGNAL);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockLightWeightedPressurePlate() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockLightWeightedPressurePlate(BlockState blockstate) {
        super(blockstate);
        this.onPitch = 0.90000004f;
        this.offPitch = 0.75f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Weighted Pressure Plate (Light)";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 0.5D;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 2.5D;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    
    /**
     * @deprecated 
     */
    protected int computeRedstoneStrength() {
        int $2 = Math.min(this.level.getCollidingEntities(getCollisionBoundingBox()).length, this.getMaxWeight());

        if (count > 0) {
            $3loat $1 = (float) Math.min(this.getMaxWeight(), count) / (float) this.getMaxWeight();
            return NukkitMath.ceilFloat(f * 15.0F);
        } else {
            return 0;
        }
    }
    /**
     * @deprecated 
     */
    

    public int getMaxWeight() {
        return 15;
    }
}