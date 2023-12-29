package cn.nukkit.block;


import cn.nukkit.block.property.CommonBlockProperties;

public class BlockBambooPressurePlate extends BlockWoodenPressurePlate {
    public static final BlockProperties PROPERTIES = new BlockProperties(BAMBOO_PRESSURE_PLATE, CommonBlockProperties.REDSTONE_SIGNAL);

    public BlockBambooPressurePlate(BlockState blockState) {
        super(blockState);
    }

    public String getName() {
        return "Bamboo Pressure Plate";
    }
}