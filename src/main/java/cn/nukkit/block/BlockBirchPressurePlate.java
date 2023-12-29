package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockBirchPressurePlate extends BlockWoodenPressurePlate {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:birch_pressure_plate", CommonBlockProperties.REDSTONE_SIGNAL);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBirchPressurePlate() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBirchPressurePlate(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Birch Pressure Plate";
    }
}