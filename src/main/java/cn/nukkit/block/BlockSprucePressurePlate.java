package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockSprucePressurePlate extends BlockWoodenPressurePlate {
    public static final BlockProperties $1 = new BlockProperties(SPRUCE_PRESSURE_PLATE, CommonBlockProperties.REDSTONE_SIGNAL);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockSprucePressurePlate() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockSprucePressurePlate(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Spruce Pressure Plate";
    }

}