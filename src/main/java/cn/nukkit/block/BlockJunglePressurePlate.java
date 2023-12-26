package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockJunglePressurePlate extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:jungle_pressure_plate", CommonBlockProperties.REDSTONE_SIGNAL);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockJunglePressurePlate() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockJunglePressurePlate(BlockState blockstate) {
        super(blockstate);
    }
}