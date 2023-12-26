package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockStonePressurePlate extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:stone_pressure_plate", CommonBlockProperties.REDSTONE_SIGNAL);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockStonePressurePlate() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockStonePressurePlate(BlockState blockstate) {
        super(blockstate);
    }
}