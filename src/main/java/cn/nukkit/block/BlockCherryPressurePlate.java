package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockCherryPressurePlate extends BlockWoodenPressurePlate {
    public static final BlockProperties $1 = new BlockProperties(CHERRY_PRESSURE_PLATE, CommonBlockProperties.REDSTONE_SIGNAL);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockCherryPressurePlate() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockCherryPressurePlate(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Cherry Pressure Plate";
    }
}