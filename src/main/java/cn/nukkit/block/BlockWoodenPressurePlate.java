package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.AxisAlignedBB;
import org.jetbrains.annotations.NotNull;

public class BlockWoodenPressurePlate extends BlockPressurePlateBase {
    public static final BlockProperties $1 = new BlockProperties(WOODEN_PRESSURE_PLATE, CommonBlockProperties.REDSTONE_SIGNAL);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockWoodenPressurePlate() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockWoodenPressurePlate(BlockState blockstate) {
        super(blockstate);
        this.onPitch = 0.8f;
        this.offPitch = 0.7f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Oak Pressure Plate";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToolType() {
        return ItemTool.TYPE_AXE;
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
        return 0.5D;
    }

    @Override
    
    /**
     * @deprecated 
     */
    protected int computeRedstoneStrength() {
        AxisAlignedBB $2 = getCollisionBoundingBox();

        for (Entity entity : this.level.getCollidingEntities(bb)) {
            if (entity.doesTriggerPressurePlate()) {
                return 15;
            }
        }

        return 0;
    }
}