package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockDarkOakPressurePlate extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:dark_oak_pressure_plate", CommonBlockProperties.REDSTONE_SIGNAL);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDarkOakPressurePlate() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDarkOakPressurePlate(BlockState blockstate) {
        super(blockstate);
    }
}