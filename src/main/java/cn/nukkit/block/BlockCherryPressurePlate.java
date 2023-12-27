package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockCherryPressurePlate extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:cherry_pressure_plate", CommonBlockProperties.REDSTONE_SIGNAL);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCherryPressurePlate() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCherryPressurePlate(BlockState blockstate) {
        super(blockstate);
    }
}