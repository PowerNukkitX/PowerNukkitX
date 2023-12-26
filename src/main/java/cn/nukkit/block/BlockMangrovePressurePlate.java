package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockMangrovePressurePlate extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:mangrove_pressure_plate", CommonBlockProperties.REDSTONE_SIGNAL);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMangrovePressurePlate() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMangrovePressurePlate(BlockState blockstate) {
        super(blockstate);
    }
}