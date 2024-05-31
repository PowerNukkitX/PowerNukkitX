package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockMangrovePressurePlate extends BlockWoodenPressurePlate {
    public static final BlockProperties $1 = new BlockProperties(MANGROVE_PRESSURE_PLATE, CommonBlockProperties.REDSTONE_SIGNAL);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockMangrovePressurePlate() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockMangrovePressurePlate(BlockState blockstate) {
        super(blockstate);
        this.onPitch = 0.8f;
        this.offPitch = 0.7f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Mangrove Pressure Plate";
    }
}