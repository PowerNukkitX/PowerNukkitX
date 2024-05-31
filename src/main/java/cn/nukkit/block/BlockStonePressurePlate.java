package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.AxisAlignedBB;
import org.jetbrains.annotations.NotNull;

public class BlockStonePressurePlate extends BlockPressurePlateBase {
    public static final BlockProperties $1 = new BlockProperties(STONE_PRESSURE_PLATE, CommonBlockProperties.REDSTONE_SIGNAL);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockStonePressurePlate() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockStonePressurePlate(BlockState blockstate) {
        super(blockstate);
        this.onPitch = 0.6f;
        this.offPitch = 0.5f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Stone Pressure Plate";
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
        return 6;
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
        AxisAlignedBB $2 = getCollisionBoundingBox();

        for (Entity entity : this.level.getCollidingEntities(bb)) {
            if (entity instanceof EntityLiving && entity.doesTriggerPressurePlate()) {
                return 15;
            }
        }

        return 0;
    }
}