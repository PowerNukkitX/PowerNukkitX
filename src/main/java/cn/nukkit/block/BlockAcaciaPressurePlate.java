package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockAcaciaPressurePlate extends BlockWoodenPressurePlate {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:acacia_pressure_plate", CommonBlockProperties.REDSTONE_SIGNAL);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockAcaciaPressurePlate() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockAcaciaPressurePlate(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Acacia Pressure Plate";
    }
}