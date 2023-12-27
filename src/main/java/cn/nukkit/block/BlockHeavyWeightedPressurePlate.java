package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockHeavyWeightedPressurePlate extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:heavy_weighted_pressure_plate", CommonBlockProperties.REDSTONE_SIGNAL);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockHeavyWeightedPressurePlate() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockHeavyWeightedPressurePlate(BlockState blockstate) {
        super(blockstate);
    }
}