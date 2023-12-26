package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockWarpedPressurePlate extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:warped_pressure_plate", CommonBlockProperties.REDSTONE_SIGNAL);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWarpedPressurePlate() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWarpedPressurePlate(BlockState blockstate) {
        super(blockstate);
    }
}