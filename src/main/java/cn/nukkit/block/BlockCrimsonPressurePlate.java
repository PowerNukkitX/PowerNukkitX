package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockCrimsonPressurePlate extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:crimson_pressure_plate", CommonBlockProperties.REDSTONE_SIGNAL);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCrimsonPressurePlate() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCrimsonPressurePlate(BlockState blockstate) {
        super(blockstate);
    }
}