package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockLightWeightedPressurePlate extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:light_weighted_pressure_plate", CommonBlockProperties.REDSTONE_SIGNAL);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLightWeightedPressurePlate() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLightWeightedPressurePlate(BlockState blockstate) {
        super(blockstate);
    }
}