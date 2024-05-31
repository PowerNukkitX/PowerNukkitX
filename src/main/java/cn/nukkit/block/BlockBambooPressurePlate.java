package cn.nukkit.block;


import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockBambooPressurePlate extends BlockWoodenPressurePlate {
    public static final BlockProperties $1 = new BlockProperties(BAMBOO_PRESSURE_PLATE, CommonBlockProperties.REDSTONE_SIGNAL);
    /**
     * @deprecated 
     */
    

    public BlockBambooPressurePlate(BlockState blockState) {
        super(blockState);
    }
    /**
     * @deprecated 
     */
    

    public String getName() {
        return "Bamboo Pressure Plate";
    }

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }
}