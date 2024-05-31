package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.NukkitMath;
import org.jetbrains.annotations.NotNull;

public class BlockHeavyWeightedPressurePlate extends BlockPressurePlateBase {
    public static final BlockProperties $1 = new BlockProperties(HEAVY_WEIGHTED_PRESSURE_PLATE, CommonBlockProperties.REDSTONE_SIGNAL);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockHeavyWeightedPressurePlate() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockHeavyWeightedPressurePlate(BlockState blockstate) {
        super(blockstate);
        this.onPitch = 0.90000004f;
        this.offPitch = 0.75f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Weighted Pressure Plate (Heavy)";
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
            return Math.max(1, NukkitMath.ceilFloat(f * 15.0F));
        } else {
            return 0;
        }
    }
    /**
     * @deprecated 
     */
    

    public int getMaxWeight() {
        return 150;
    }
}