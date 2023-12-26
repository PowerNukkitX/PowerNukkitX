package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockBirchPressurePlate extends Block {
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
}